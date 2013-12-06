package com.death.likegraph;

import helpers.ImageLoader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private SharedPreferences sharedPrefs;
	
	private TextView name, totalLikes, uniqueLikes;
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
	    
	    name.setText(sharedPrefs.getString("global_user_name", "F"));
	    totalLikes.setText("Total likes received: " + postsDatabaseAdapter.getTotalLikes());
	    uniqueLikes.setText("Unique likes received: " + postsDatabaseAdapter.getTotalUniqueLikes());
	    new ImageLoader(picture, sharedPrefs.getString("global_user_pic", "")).execute();
	}

}
