package com.death.likegraph;

import java.util.ArrayList;

import helpers.ImageLoader;
import helpers.StatusDialog;
import models.Friend;
import models.Status;
import adapters.StatusAdapter;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewFriendActivity extends ListActivity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private Friend friend;
	private ArrayList<Status> statuses;
	
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
		statuses = postsDatabaseAdapter.getLikedStatusesByFriend(friend.getId());
		
		final StatusAdapter statusesAdapter = new StatusAdapter(getApplicationContext(), statuses);
		setListAdapter(statusesAdapter);
		
		TextView name = (TextView) findViewById(R.id.friend_name);
		ImageView picture = (ImageView) findViewById(R.id.friend_picture);
		TextView likes = (TextView) findViewById(R.id.friend_likecount);
		
		name.setText(friend.getName());
		likes.setText("Posts of yours liked: " + postsDatabaseAdapter.getUserLikes(friend.getName()));
		new ImageLoader(picture, friend.getPicture(), R.drawable.com_facebook_profile_picture_blank_portrait).execute();
	}
	
	public void onListItemClick(ListView l, View v, int pos, long id)
	{
		new StatusDialog(ViewFriendActivity.this, statuses.get(pos)).show();
	}
}
