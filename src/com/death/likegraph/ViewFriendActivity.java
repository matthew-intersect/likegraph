package com.death.likegraph;

import helpers.ImageLoader;
import models.Friend;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewFriendActivity extends ListActivity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private Friend friend;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_friend);
		
		postsDatabaseAdapter = new PostsDatabaseAdapter(this);
		postsDatabaseAdapter = postsDatabaseAdapter.open();
		
		Bundle extras = getIntent().getExtras();
		long friendId = extras.getLong("friend_id");
		friend = postsDatabaseAdapter.getFriend(friendId);
		
		TextView name = (TextView) findViewById(R.id.friend_name);
		ImageView picture = (ImageView) findViewById(R.id.friend_picture);
		
		name.setText(friend.getName());
		new ImageLoader(picture, friend.getPicture()).execute();
	}
}
