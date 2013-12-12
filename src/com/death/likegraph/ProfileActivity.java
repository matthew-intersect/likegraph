package com.death.likegraph;

import models.Friend;
import models.Post;
import models.Status;
import models.Photo;
import models.Link;
import models.Checkin;
import models.Video;
import helpers.CheckinDialog;
import helpers.ImageLoader;
import helpers.LinkDialog;
import helpers.PhotoDialog;
import helpers.StatusDialog;
import helpers.VideoDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private SharedPreferences sharedPrefs;
	
	private Post topPost;
	private TextView name, totalLikes, uniqueLikes, topPostLikes, selfLikes, topLiker;
	private ImageView picture;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.profile);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    postsDatabaseAdapter = new PostsDatabaseAdapter(this);
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
	    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    name = (TextView) findViewById(R.id.username);
	    picture = (ImageView) findViewById(R.id.user_picture);
	    totalLikes = (TextView) findViewById(R.id.total_likes);
	    uniqueLikes = (TextView) findViewById(R.id.unique_likes);
	    topPostLikes = (TextView) findViewById(R.id.top_post);
	    selfLikes = (TextView) findViewById(R.id.self_likes);
	    topLiker = (TextView) findViewById(R.id.top_liker);
	    
	    name.setText(sharedPrefs.getString("global_user_name", "F"));
	    totalLikes.setText("Total likes received: " + postsDatabaseAdapter.getTotalLikes());
	    uniqueLikes.setText("Unique likes received: " + postsDatabaseAdapter.getTotalUniqueLikes());
	    topPost = postsDatabaseAdapter.getTopPost();
	    topPostLikes.setText("Most likes received for a post: " + topPost.getLikeCount());
	    selfLikes.setText("Self Likes (Vanity count): " + postsDatabaseAdapter.getUserLikes(sharedPrefs.getString("global_user_name", "")));
	    final Friend topFriend = postsDatabaseAdapter.getTopFriend();
	    topLiker.setText("Most likes from friend: " + topFriend.getCount() + " (" + topFriend.getName() + ")");
	    new ImageLoader(picture, sharedPrefs.getString("global_user_pic", ""), R.drawable.com_facebook_profile_picture_blank_portrait).execute();
	    
	    topPostLikes.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(topPost instanceof Status)
				{
					new StatusDialog(ProfileActivity.this, (Status) topPost).show();
				}
				else if(topPost instanceof Photo)
				{
					new PhotoDialog(ProfileActivity.this, (Photo) topPost).show();
				}
				else if(topPost instanceof Link)
				{
					new LinkDialog(ProfileActivity.this, (Link) topPost).show();
				}
				else if(topPost instanceof Checkin)
				{
					new CheckinDialog(ProfileActivity.this, (Checkin) topPost).show();
				}
				else if(topPost instanceof Video)
				{
					new VideoDialog(ProfileActivity.this, (Video) topPost).show();
				}
			}
		});
	
	    topLiker.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent friend = new Intent(ProfileActivity.this, ViewFriendActivity.class);
				friend.putExtra("friend_id", topFriend.getId());
				startActivity(friend);
			}
		});
	}

}
