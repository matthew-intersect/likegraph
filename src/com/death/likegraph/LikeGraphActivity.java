package com.death.likegraph;

import helpers.ImageLoader;
import helpers.PostLikesComparator;

import java.util.ArrayList;
import java.util.Collections;

import models.Post;
import models.Status;
import models.Photo;
import models.Checkin;
import models.Link;
import models.Video;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LikeGraphActivity extends Activity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private SharedPreferences sharedPrefs;
	
	private ArrayList<Post> posts;
	
	private XYSeriesRenderer renderer;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset dataset;
	
	private LinearLayout layout;
	private View graph;
	private CheckBox checkStatii, checkLinks, checkCheckins, checkPhotos, checkVideos;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.graph);

	    postsDatabaseAdapter = new PostsDatabaseAdapter(this);
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
	    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    layout = (LinearLayout) findViewById(R.id.sub_layout);
	    checkStatii = (CheckBox) findViewById(R.id.check_statii);
	    checkLinks = (CheckBox) findViewById(R.id.check_links);
	    checkCheckins = (CheckBox) findViewById(R.id.check_checkins);
	    checkPhotos = (CheckBox) findViewById(R.id.check_photos);
	    checkVideos = (CheckBox) findViewById(R.id.check_videos);
	    
	    renderer = new XYSeriesRenderer();
	    mRenderer = new XYMultipleSeriesRenderer();
	    dataset = new XYMultipleSeriesDataset();

	    checkStatii.setChecked(sharedPrefs.getBoolean("checkStatii", true));
	    checkLinks.setChecked(sharedPrefs.getBoolean("checkLinks", false));
	    checkCheckins.setChecked(sharedPrefs.getBoolean("checkCheckins", false));
	    checkPhotos.setChecked(sharedPrefs.getBoolean("checkPhotos", false));
	    checkVideos.setChecked(sharedPrefs.getBoolean("checkVideos", false));
	    
	    posts = postsDatabaseAdapter.getPosts(checkStatii.isChecked(), checkLinks.isChecked(),
				checkCheckins.isChecked(), checkPhotos.isChecked(), checkVideos.isChecked(), 
				sharedPrefs.getBoolean("exclude_zero_photos", true));
	    CategorySeries series = new CategorySeries("");
		for(int i=0;i<posts.size();i++)
		{
			series.add("", posts.get(i).getLikeCount());
		}
		dataset.addSeries(series.toXYSeries());
		setupRenderers(series.toXYSeries());
        graph = ChartFactory.getBarChartView(getApplicationContext(), dataset, mRenderer, Type.DEFAULT);
        layout.addView(graph);
        
        graph.setOnLongClickListener(new View.OnLongClickListener()
        {
        	@Override
        	public boolean onLongClick(View v)
        	{
        		SeriesSelection seriesSelection = ((GraphicalView) graph).getCurrentSeriesAndPoint();
        		if(seriesSelection != null)
        		{
        			if(posts.get((int) seriesSelection.getXValue()-1) instanceof Status)
        			{
        				displayStatus(((Status) posts.get((int) seriesSelection.getXValue()-1)));
        			}
        			else if(posts.get((int) seriesSelection.getXValue()-1) instanceof Photo)
        			{
        				displayPhoto(((Photo) posts.get((int) seriesSelection.getXValue()-1)));
        			}
        			else if(posts.get((int) seriesSelection.getXValue()-1) instanceof Checkin)
        			{
        				displayCheckin(((Checkin) posts.get((int) seriesSelection.getXValue()-1)));
        			}
        			else if(posts.get((int) seriesSelection.getXValue()-1) instanceof Link)
        			{
        				displayLink(((Link) posts.get((int) seriesSelection.getXValue()-1)));
        			}
        			else if(posts.get((int) seriesSelection.getXValue()-1) instanceof Video)
        			{
        				displayVideo(((Video) posts.get((int) seriesSelection.getXValue()-1)));
        			}
        			
        		}
        		return false;
        	}
        });
        
        View.OnClickListener graphInclusionsListener = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				updateCheckPreferences();
				posts = postsDatabaseAdapter.getPosts(checkStatii.isChecked(), checkLinks.isChecked(),
						checkCheckins.isChecked(), checkPhotos.isChecked(), checkVideos.isChecked(), 
						sharedPrefs.getBoolean("exclude_zero_photos", true));
				Collections.sort(posts, new PostLikesComparator());
				CategorySeries series = new CategorySeries("");
				for(int i=0;i<posts.size();i++)
				{
					series.add("", posts.get(i).getLikeCount());
				}
				dataset.removeSeries(0);
				dataset.addSeries(series.toXYSeries());
				setupRenderers(series.toXYSeries());
				((GraphicalView) graph).repaint();
			}
		};
		checkStatii.setOnClickListener(graphInclusionsListener);
		checkLinks.setOnClickListener(graphInclusionsListener);
		checkCheckins.setOnClickListener(graphInclusionsListener);
		checkPhotos.setOnClickListener(graphInclusionsListener);
		checkVideos.setOnClickListener(graphInclusionsListener);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.graph_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.graph_settings:
			{
				Intent graphPreferences = new Intent(LikeGraphActivity.this, GraphPreferenceActivity.class);
				startActivity(graphPreferences);
				return true;
			}
		}
		return false;
	}
	
	private void setupRenderers(XYSeries series)
	{
		renderer.setColor(getColour(sharedPrefs.getString("graph_bar_colour", "Blue")));
		mRenderer.removeAllRenderers();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setYTitle("Likes");
		mRenderer.setAxisTitleTextSize(25);
		mRenderer.setShowGrid(true);
		mRenderer.setShowGridY(true);
		mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.BLACK);
        if(!sharedPrefs.getBoolean("graph_all_bars", false))
        {
        	mRenderer.setXAxisMax(Integer.parseInt(sharedPrefs.getString("graph_number_bars", "50")));
        }
        mRenderer.setPanEnabled(true, false);
        mRenderer.setPanLimits(new double[]{-1, posts.size(), 0, series.getMaxY()});
        mRenderer.setXAxisMin(-1);
        mRenderer.setYAxisMin(-1);
        mRenderer.setShowLegend(false);
        mRenderer.setXLabels(0);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setBarSpacing(.1);
        mRenderer.setMargins(new int[]{20, 50, 0, 20});
        mRenderer.setClickEnabled(true);
	}
	
	private void updateCheckPreferences()
	{
		sharedPrefs.edit().putBoolean("checkStatii", checkStatii.isChecked()).commit();
		sharedPrefs.edit().putBoolean("checkLinks", checkLinks.isChecked()).commit();
		sharedPrefs.edit().putBoolean("checkCheckins", checkCheckins.isChecked()).commit();
		sharedPrefs.edit().putBoolean("checkPhotos", checkPhotos.isChecked()).commit();
		sharedPrefs.edit().putBoolean("checkVideos", checkVideos.isChecked()).commit();
	}
	
	private int getColour(String colour)
	{
		if(colour.equals("Red"))
		{
			return Color.RED;
		}
		else if(colour.equals("Green"))
		{
			return Color.GREEN;
		}
		else if(colour.equals("Yellow"))
		{
			return Color.YELLOW;
		}
		else if(colour.equals("Orange"))
		{
			return Color.rgb(255, 165, 0);
		}
		else if(colour.equals("Purple"))
		{
			return Color.rgb(160, 32, 240);
		}
		else
		{
			return Color.BLUE;
		}
	}

	private void displayStatus(Status status)
	{
		final Dialog dialog = new Dialog(LikeGraphActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.status_preview);
		
		TextView statusText = (TextView) dialog.findViewById(R.id.status_text);
		statusText.setText(status.getStatus());
		
		Button ok = (Button) dialog.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void displayPhoto(Photo photo)
	{
		final Dialog dialog = new Dialog(LikeGraphActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.photo_preview);
		
		TextView photoText = (TextView) dialog.findViewById(R.id.photo_text);
		photoText.setText(photo.getMessage());
		
		ImageView image = (ImageView) dialog.findViewById(R.id.image);
		image.setScaleType(ScaleType.CENTER_INSIDE);
		new ImageLoader(image, photo.getSource()).execute();
		
		Button ok = (Button) dialog.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	private void displayCheckin(Checkin checkin)
	{
		
	}
	
	private void displayLink(Link link)
	{
		
	}
	
	private void displayVideo(Video video)
	{
		
	}
}
