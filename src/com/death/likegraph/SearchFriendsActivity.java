package com.death.likegraph;

import java.util.ArrayList;

import models.Friend;

import adapters.SearchFriendAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class SearchFriendsActivity extends ListActivity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private ArrayList<Friend> friends;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_search_list);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.search_friends_action_bar, null);
		actionBar.setCustomView(v);
		
		postsDatabaseAdapter = new PostsDatabaseAdapter(this);
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
		friends = postsDatabaseAdapter.getAllFriends();
		
		final SearchFriendAdapter friendsAdapter = new SearchFriendAdapter(getApplicationContext(), friends);
		setListAdapter(friendsAdapter);
		EditText searchBox = (EditText) v.findViewById(R.id.searchBox);
		
		searchBox.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void afterTextChanged(Editable text)
			{
				friends = postsDatabaseAdapter.searchFriends(text.toString());
				friendsAdapter.updateFriends(friends);
				friendsAdapter.notifyDataSetChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
			{
			}
		});
	}
	
	public void onListItemClick(ListView l, View v, int pos, long id)
	{
		Intent friend = new Intent(SearchFriendsActivity.this, ViewFriendActivity.class);
		friend.putExtra("friend_id", friends.get(pos).getId());
		startActivity(friend);
	}
}
