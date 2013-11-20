package com.death.likegraph;

import java.util.Arrays;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.HttpMethod;
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
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private static final String TAG = "MainFragment";
	int offset = 0;
	
	private ProgressDialog dialog;
	private Button fetchData, createGraph, rankFriends, searchFriends;
	
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
	    View view = inflater.inflate(R.layout.main, container, false);

	    postsDatabaseAdapter = new PostsDatabaseAdapter(getActivity());
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    fetchData = (Button) view.findViewById(R.id.fetchData);
	    createGraph = (Button) view.findViewById(R.id.createGraph);
	    rankFriends = (Button) view.findViewById(R.id.rankFriends);
	    searchFriends = (Button) view.findViewById(R.id.searchFriends);
	    authButton.setFragment(this);
	    authButton.setReadPermissions(Arrays.asList("read_stream", "user_status", "user_checkins", "user_photos", "user_videos"));
	    
	    fetchData.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog = new ProgressDialog(getActivity());
				dialog.setMessage("Fetching data...");
		        dialog.show();
		        postsDatabaseAdapter.clearTables();
		        batchStatiiRequest();
			}
		});
	    
	    createGraph.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent graph = new Intent(v.getContext(), LikeGraphActivity.class);
				startActivity(graph);
			}
		});
	    
	    rankFriends.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent rank = new Intent(v.getContext(), RankFriendsActivity.class);
				startActivity(rank);
			}
		});
	    
	    searchFriends.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent searchFriends = new Intent(v.getContext(), SearchFriendsActivity.class);
				startActivity(searchFriends);
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
	    	rankFriends.setVisibility(View.VISIBLE);
	    	searchFriends.setVisibility(View.VISIBLE);
	    }
	    else if (state.isClosed())
	    {
	        Log.i(TAG, "Logged out...");
	        fetchData.setVisibility(View.INVISIBLE);
	        createGraph.setVisibility(View.INVISIBLE);
	        rankFriends.setVisibility(View.INVISIBLE);
	        searchFriends.setVisibility(View.INVISIBLE);
	    }
	}
	
	public void batchStatiiRequest()
	{
		dialog.setMessage("Fetching statuses...");
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
				if(postsDatabaseAdapter.getNumberOfStatii()==offset)
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
		dialog.setMessage("Fetching links...");
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
				if(postsDatabaseAdapter.getNumberOfLinks()==offset)
				{
					batchLinksRequest();
				}
				else
				{
					offset = 0;
					batchCheckinsRequest();
				}
			}
		});
		requestBatch.executeAsync();
	}
	
	public void batchCheckinsRequest()
	{
		dialog.setMessage("Fetching checkins...");
		RequestBatch requestBatch = new RequestBatch();
		for(int i=0;i<10;i++)
		{
			Request req = new Request(Session.getActiveSession(), "/me/checkins", null, null, new Request.Callback()
			{
				public void onCompleted(Response response)
				{
					processCheckinsResponse(response);
				}
			});
			Bundle params = new Bundle();
			params.putInt("limit", 25);
			params.putInt("offset", offset);
			offset+=25;
			params.putString("fields", "id,message,created_time,likes,place,from");
			req.setParameters(params);
			requestBatch.add(req);
		}
		requestBatch.addCallback(new RequestBatch.Callback()
		{
			@Override
			public void onBatchCompleted(RequestBatch batch)
			{
				if(postsDatabaseAdapter.getNumberOfCheckins()==offset)
				{
					batchCheckinsRequest();
				}
				else
				{
					offset = 0;
					batchPhotosRequest();
				}
			}
		});
		requestBatch.executeAsync();
	}
	
	public void batchPhotosRequest()
	{
		dialog.setMessage("Fetching photos...");
		RequestBatch requestBatch = new RequestBatch();
		for(int i=0;i<10;i++)
		{
			Request req = new Request(Session.getActiveSession(), "/me/photos", null, null, new Request.Callback()
			{
				public void onCompleted(Response response)
				{
					processPhotosResponse(response);
				}
			});
			Bundle params = new Bundle();
			params.putString("type", "uploaded");
			params.putInt("limit", 25);
			params.putInt("offset", offset);
			offset+=25;
			params.putString("fields", "id,name,created_time,likes,source,from");
			req.setParameters(params);
			requestBatch.add(req);
		}
		requestBatch.addCallback(new RequestBatch.Callback()
		{
			@Override
			public void onBatchCompleted(RequestBatch batch)
			{
				if(postsDatabaseAdapter.getNumberOfPhotos()==offset)
				{
					batchPhotosRequest();
				}
				else
				{
					offset = 0;
					batchVideosRequest();
				}
			}
		});
		requestBatch.executeAsync();
	}
	
	public void batchVideosRequest()
	{
		dialog.setMessage("Fetching videos...");
		RequestBatch requestBatch = new RequestBatch();
		for(int i=0;i<10;i++)
		{
			Request req = new Request(Session.getActiveSession(), "/me/videos", null, null, new Request.Callback()
			{
				public void onCompleted(Response response)
				{
					processVideosResponse(response);
				}
			});
			Bundle params = new Bundle();
			params.putString("type", "uploaded");
			params.putInt("limit", 25);
			params.putInt("offset", offset);
			offset+=25;
			params.putString("fields", "id,name,created_time,likes,source,from,description,picture");
			req.setParameters(params);
			requestBatch.add(req);
		}
		requestBatch.addCallback(new RequestBatch.Callback()
		{
			@Override
			public void onBatchCompleted(RequestBatch batch)
			{
				if(postsDatabaseAdapter.getNumberOfVideos()==offset)
				{
					batchVideosRequest();
				}
				else
				{
					offset = 0;
					fetchFriends();
				}
			}
		});
		requestBatch.executeAsync();
	}
	
	public void fetchFriends()
	{
		dialog.setMessage("Fetching friends...");
		String fqlQuery = "SELECT uid, name, pic FROM user WHERE uid IN " +
	              "(SELECT uid2 FROM friend WHERE uid1 = me())";
		Bundle params = new Bundle();
        params.putString("q", fqlQuery);
        Request request = new Request(Session.getActiveSession(), "/fql", params,
        		HttpMethod.GET, new Request.Callback()
        {         
            public void onCompleted(Response response)
            {
                GraphObject data = response.getGraphObject();
        		JSONArray friendsData = (JSONArray) data.getProperty("data");
        		for(int i=0;i<friendsData.length();i++)
        		{
        			JSONObject item;
        			try
        			{
        				item = (JSONObject) friendsData.get(i);
        				long id = Long.parseLong(item.getString("uid"));
        				String name = item.getString("name");
        				String link = item.getString("pic");
        				postsDatabaseAdapter.addFriend(id, name, link);
        			}
        			catch (JSONException e)
        			{
        				e.printStackTrace();
        			}
        		}
                dialog.dismiss();
            }                  
    	});
        Request.executeBatchAsync(request);
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
				postsDatabaseAdapter.addStatus(Long.parseLong(item.getString("id")), date, item.getString("message"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						postsDatabaseAdapter.addLike(like.getString("name"), Long.parseLong(item.getString("id")));
					}
					JSONObject paging = (JSONObject) likeObject.getJSONObject("paging");
					if(paging.has("next"))
					{
						JSONObject cursors = paging.getJSONObject("cursors");
						makeLikeRequest(Long.parseLong(item.getString("id")), cursors.getString("after"));
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
				postsDatabaseAdapter.addLink(Long.parseLong(item.getString("id")), date, message, item.getString("link"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						postsDatabaseAdapter.addLike(like.getString("name"), Long.parseLong(item.getString("id")));
					}
					JSONObject paging = (JSONObject) likeObject.getJSONObject("paging");
					if(paging.has("next"))
					{
						JSONObject cursors = paging.getJSONObject("cursors");
						makeLikeRequest(Long.parseLong(item.getString("id")), cursors.getString("after"));
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void processCheckinsResponse(Response response)
	{
		GraphObject data = response.getGraphObject();
		JSONArray checkinsData = (JSONArray) data.getProperty("data");
		for(int i=0;i<checkinsData.length();i++)
		{
			JSONObject item;
			try
			{
				item = (JSONObject) checkinsData.get(i);
				JSONObject from = (JSONObject) item.getJSONObject("from");
				JSONObject place = (JSONObject) item.getJSONObject("place");
				String postedDate = item.getString("created_time");
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
				long date = parser.parseDateTime(postedDate).getMillis();
				String message = (item.has("message")) ? item.getString("message") : "";
				postsDatabaseAdapter.addCheckin(Long.parseLong(item.getString("id")), date, from.getString("name"), message, place.getString("name"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						postsDatabaseAdapter.addLike(like.getString("name"), Long.parseLong(item.getString("id")));
					}
					JSONObject paging = (JSONObject) likeObject.getJSONObject("paging");
					if(paging.has("next"))
					{
						JSONObject cursors = paging.getJSONObject("cursors");
						makeLikeRequest(Long.parseLong(item.getString("id")), cursors.getString("after"));
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void processPhotosResponse(Response response)
	{
		GraphObject data = response.getGraphObject();
		JSONArray photosData = (JSONArray) data.getProperty("data");
		for(int i=0;i<photosData.length();i++)
		{
			JSONObject item;
			try
			{
				item = (JSONObject) photosData.get(i);
				JSONObject from = (JSONObject) item.getJSONObject("from");
				String postedDate = item.getString("created_time");
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
				long date = parser.parseDateTime(postedDate).getMillis();
				String message = (item.has("name")) ? item.getString("name") : "";
				postsDatabaseAdapter.addPhoto(Long.parseLong(item.getString("id")), date, from.getString("name"), message, item.getString("source"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						postsDatabaseAdapter.addLike(like.getString("name"), Long.parseLong(item.getString("id")));
					}
					JSONObject paging = (JSONObject) likeObject.getJSONObject("paging");
					if(paging.has("next"))
					{
						JSONObject cursors = paging.getJSONObject("cursors");
						makeLikeRequest(Long.parseLong(item.getString("id")), cursors.getString("after"));
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void processVideosResponse(Response response)
	{
		GraphObject data = response.getGraphObject();
		JSONArray videosData = (JSONArray) data.getProperty("data");
		for(int i=0;i<videosData.length();i++)
		{
			JSONObject item;
			try
			{
				item = (JSONObject) videosData.get(i);
				JSONObject from = (JSONObject) item.getJSONObject("from");
				String postedDate = item.getString("created_time");
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
				long date = parser.parseDateTime(postedDate).getMillis();
				String message = (item.has("name")) ? item.getString("name") : "";
				String description = (item.has("description")) ? item.getString("description") : "";
				String source = (item.has("source")) ? item.getString("source") : "";
				postsDatabaseAdapter.addVideo(Long.parseLong(item.getString("id")), date, from.getString("name"), message, description, source, item.getString("picture"));
				if(item.has("likes"))
				{
					JSONObject likeObject = (JSONObject) item.getJSONObject("likes");
					JSONArray likeData = likeObject.getJSONArray("data");
					JSONObject like;
					for(int j=0;j<likeData.length();j++)
					{
						like = likeData.getJSONObject(j);
						postsDatabaseAdapter.addLike(like.getString("name"), Long.parseLong(item.getString("id")));
					}
					JSONObject paging = (JSONObject) likeObject.getJSONObject("paging");
					if(paging.has("next"))
					{
						JSONObject cursors = paging.getJSONObject("cursors");
						makeLikeRequest(Long.parseLong(item.getString("id")), cursors.getString("after"));
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void makeLikeRequest(final long id, String next)
	{
		Request req = new Request(Session.getActiveSession(), "/"+String.valueOf(id)+"/likes", null, null, new Request.Callback()
		{
			public void onCompleted(Response response)
			{
				GraphObject data = response.getGraphObject();
				JSONArray likeData = (JSONArray) data.getProperty("data");
				JSONObject like;
				try
				{
					for(int j=0;j<likeData.length();j++)
					{
							like = likeData.getJSONObject(j);
							postsDatabaseAdapter.addLike(like.getString("name"), id);
					}
					JSONObject paging = (JSONObject) data.getProperty("paging");
					if(paging.has("next"))
					{
						JSONObject cursors = paging.getJSONObject("cursors");
						makeLikeRequest(id, cursors.getString("after"));
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		});
		Bundle params = new Bundle();
		params.putInt("limit", 25);
		params.putString("after", next);
		req.setParameters(params);
		req.executeAsync();
	}
}