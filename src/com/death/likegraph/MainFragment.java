package com.death.likegraph;

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment
{
	private UiLifecycleHelper uiHelper;
	private StatiiDatabaseAdapter statiiDatabaseAdapter;
	private static final String TAG = "MainFragment";
	int offset = 0;
	
	private ProgressDialog dialog;
	private Button fetchData, createGraph;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, 
	        Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    statiiDatabaseAdapter = new StatiiDatabaseAdapter(getActivity());
		statiiDatabaseAdapter = statiiDatabaseAdapter.open();
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    fetchData = (Button) view.findViewById(R.id.fetchData);
	    createGraph = (Button) view.findViewById(R.id.createGraph);
	    authButton.setFragment(this);
	    authButton.setReadPermissions(Arrays.asList("read_stream"));
	    
	    fetchData.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				dialog = new ProgressDialog(getActivity());
				dialog.setMessage("Fetching data...");
		        dialog.show();
		        statiiDatabaseAdapter.clearTables();
		        batchStatiiRequest();
			}
		});
	    
	    return view;
	}
	
	@Override
	public void onResume()
	{
	    super.onResume();
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null && (session.isOpened() || session.isClosed()) )
	    {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause()
	{
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy()
	{
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
	    @Override
	    public void call(Session session, SessionState state, Exception exception)
	    {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
	    if (state.isOpened())
	    {
	    	Log.i(TAG, "Logged in...");
	    	fetchData.setVisibility(View.VISIBLE);
	    	createGraph.setVisibility(View.VISIBLE);
	    }
	    else if (state.isClosed())
	    {
	        Log.i(TAG, "Logged out...");
	        fetchData.setVisibility(View.INVISIBLE);
	        createGraph.setVisibility(View.INVISIBLE);
	    }
	}
	
	public void processStatiiResponse(Response response)
	{
		GraphObject data = response.getGraphObject();
		JSONArray statiiData = (JSONArray) data.getProperty("data");
		for(int i=0;i<statiiData.length();i++)
		{
			JSONObject item;
			try
			{
				item = (JSONObject) statiiData.get(i);
				String postedDate = item.getString("updated_time");
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
				long date = parser.parseDateTime(postedDate).getMillis();
				statiiDatabaseAdapter.addStatus(item.getLong("id"), date, item.getString("message"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						statiiDatabaseAdapter.addLike(like.getString("name"), item.getLong("id"));
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void processLinksResponse(Response response)
	{
		GraphObject data = response.getGraphObject();
		JSONArray linksData = (JSONArray) data.getProperty("data");
		for(int i=0;i<linksData.length();i++)
		{
			JSONObject item;
			try
			{
				item = (JSONObject) linksData.get(i);
				String postedDate = item.getString("created_time");
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
				long date = parser.parseDateTime(postedDate).getMillis();
				String message = (item.has("message")) ? item.getString("message") : "";
				statiiDatabaseAdapter.addLink(item.getLong("id"), date, message, item.getString("link"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						statiiDatabaseAdapter.addLike(like.getString("name"), item.getLong("id"));
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void batchStatiiRequest()
	{
		RequestBatch requestBatch = new RequestBatch();
		for(int i=0;i<10;i++)
		{
			Request req = new Request(Session.getActiveSession(), "/me/statuses", null, null, new Request.Callback()
			{
	            public void onCompleted(Response response)
	            {
	            	processStatiiResponse(response);
	            }
	        });
			Bundle params = new Bundle();
			params.putInt("limit", 25);
			params.putInt("offset", offset);
			offset+=25;
			params.putString("fields", "id,message,updated_time,likes");
			req.setParameters(params);
			requestBatch.add(req);
		}
		requestBatch.addCallback(new RequestBatch.Callback()
		{
			@Override
			public void onBatchCompleted(RequestBatch batch)
			{
				if(statiiDatabaseAdapter.getNumberOfStatii()==offset)
				{
					batchStatiiRequest();
				}
				else
				{
					offset = 0;
					batchLinksRequest();
				}
			}
		});
		requestBatch.executeAsync();
	}
	
	public void batchLinksRequest()
	{
		RequestBatch requestBatch = new RequestBatch();
		for(int i=0;i<10;i++)
		{
			Request req = new Request(Session.getActiveSession(), "/me/links", null, null, new Request.Callback()
			{
	            public void onCompleted(Response response)
	            {
	            	processLinksResponse(response);
	            }
	        });
			Bundle params = new Bundle();
			params.putInt("limit", 25);
			params.putInt("offset", offset);
			offset+=25;
			params.putString("fields", "id,message,created_time,likes,link");
			req.setParameters(params);
			requestBatch.add(req);
		}
		requestBatch.addCallback(new RequestBatch.Callback()
		{
			@Override
			public void onBatchCompleted(RequestBatch batch)
			{
				if(statiiDatabaseAdapter.getNumberOfLinks()==offset)
				{
					batchStatiiRequest();
				}
				else
				{
					offset = 0;
//					batchCheckinsRequest();
				}
				dialog.dismiss();
			}
		});
		requestBatch.executeAsync();
	}
	
	public void rankFriends()
	{
		ArrayList<Friend> ranks = statiiDatabaseAdapter.getRankedLikes();
		for(Friend f: ranks)
		{
			System.out.println(f.getName() + ": " + f.getCount());
		}
	}
	
}