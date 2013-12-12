package com.death.likegraph;

import java.util.ArrayList;
import java.util.Collections;

import helpers.CheckinDialog;
import helpers.ImageLoader;
import helpers.LinkDialog;
import helpers.PhotoDialog;
import helpers.PostTimeComparator;
import helpers.StatusDialog;
import helpers.VideoDialog;
import models.Checkin;
import models.Friend;
import models.Link;
import models.Photo;
import models.Post;
import models.Status;
import models.Video;
import adapters.PostAdapter;
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
	private ArrayList<Post> posts;
	
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
		posts = postsDatabaseAdapter.getLikedPostsByFriend(friend.getId());
		Collections.sort(posts, new PostTimeComparator());
		
		final PostAdapter statusesAdapter = new PostAdapter(getApplicationContext(), posts);
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
		Post post = posts.get(pos);
		if(post instanceof Status)
		{
			new StatusDialog(ViewFriendActivity.this, (Status) post).show();
		}
		else if(post instanceof Photo)
		{
			new PhotoDialog(ViewFriendActivity.this, (Photo) post).show();
		}
		else if(post instanceof Link)
		{
			new LinkDialog(ViewFriendActivity.this, (Link) post).show();
		}
		else if(post instanceof Checkin)
		{
			new CheckinDialog(ViewFriendActivity.this, (Checkin) post).show();
		}
		else if(post instanceof Video)
		{
			new VideoDialog(ViewFriendActivity.this, (Video) post).show();
		}
	}
}
