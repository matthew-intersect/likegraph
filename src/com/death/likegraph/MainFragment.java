package com.death.likegraph;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;

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
	
	private Button fetchData;
	
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
	    authButton.setFragment(this);
	    authButton.setReadPermissions(Arrays.asList("user_status"));
	    
	    fetchData.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				fetchData();
				rankFriends();
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
	    }
	    else if (state.isClosed())
	    {
	        Log.i(TAG, "Logged out...");
	        fetchData.setVisibility(View.INVISIBLE);
	    }
	}
	
	private void fetchData()
	{
		statiiDatabaseAdapter.clearTables();
		Request req = Request.newGraphPathRequest(Session.getActiveSession(), "/me/statuses", new Request.Callback()
		{
			@Override
			public void onCompleted(Response response)
			{
				processResponse(response);
				iterateResponse(response);
			}
		});
		Bundle params = new Bundle();
		params.putInt("offset", offset);
		params.putString("fields", "id,message,updated_time,likes");
		req.setParameters(params);
		Request.executeBatchAsync(req);
	}
	
	public void processResponse(Response response)
	{
		GraphObject data = response.getGraphObject();
		JSONArray statiiData = (JSONArray) data.getProperty("data");
		for(int i=0;i<statiiData.length();i++)
		{
			JSONObject item;
			try
			{
				item = (JSONObject) statiiData.get(i);
				statiiDatabaseAdapter.addStatus(item.getLong("id"), 1, item.getString("message"));
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
	
	public void iterateResponse(Response response)
	{
		Request req = Request.newGraphPathRequest(Session.getActiveSession(), "/me/statuses", new Request.Callback()
		{
			@Override
			public void onCompleted(Response response)
			{
				processResponse(response);
				iterateResponse(response);
			}
		});
		Bundle params = new Bundle();
		offset+=25;
		params.putInt("offset", offset);
		params.putString("fields", "id,message,updated_time,likes");
		req.setParameters(params);
		if(offset==statiiDatabaseAdapter.numberOfStatii())
			Request.executeBatchAsync(req);
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