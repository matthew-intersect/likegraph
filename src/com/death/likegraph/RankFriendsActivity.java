package com.death.likegraph;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import models.Friend;

public class RankFriendsActivity extends ListActivity implements OnScrollListener
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	
	private FriendRankAdapter friendRankAdapter;
	private ArrayList<Friend> friendRanks;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rank_friends_list);
        
        postsDatabaseAdapter = new PostsDatabaseAdapter(this);
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
	    friendRanks = postsDatabaseAdapter.getRankedLikes();
	    friendRankAdapter = new FriendRankAdapter(getApplicationContext(), friendRanks);
	    
	    setListAdapter(friendRankAdapter);
	    getListView().setOnScrollListener(this);
	    
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		
		if(loadMore && totalItemCount != 100)
		{
			friendRankAdapter.increaseLoadCount();
			friendRankAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){}
}
