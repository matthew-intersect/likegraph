package com.death.likegraph;

import helpers.PostLikesComparator;

import java.util.ArrayList;
import java.util.Collections;

import models.Post;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LikeGraphActivity extends Activity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	private SharedPreferences sharedPrefs;
	
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
	    
	    ArrayList<Post> counts = postsDatabaseAdapter.getStatiiCounts();
	    CategorySeries series = new CategorySeries("");
		for(int i=0;i<counts.size();i++)
		{
			series.add("", counts.get(i).getLikeCount());
		}
		dataset.addSeries(series.toXYSeries());
		setupRenderers(counts, series.toXYSeries());
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
        			Toast.makeText(getApplicationContext(), "Chart element in series index " + seriesSelection.getSeriesIndex()
        					+ " data point index " + seriesSelection.getPointIndex() + " was clicked"
        					+ " closest point value X=" + seriesSelection.getXValue() + ", Y=" + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
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
				ArrayList<Post> posts = postsDatabaseAdapter.getPosts(checkStatii.isChecked(), checkLinks.isChecked(),
						checkCheckins.isChecked(), checkPhotos.isChecked(), checkVideos.isChecked());
				Collections.sort(posts, new PostLikesComparator());
				CategorySeries series = new CategorySeries("");
				for(int i=0;i<posts.size();i++)
				{
					series.add("", posts.get(i).getLikeCount());
				}
				dataset.removeSeries(0);
				dataset.addSeries(series.toXYSeries());
				setupRenderers(posts, series.toXYSeries());
				((GraphicalView) graph).repaint();
			}
		};
		checkStatii.setChecked(sharedPrefs.getBoolean("checkStatii", true));
		checkStatii.setOnClickListener(graphInclusionsListener);
		checkLinks.setChecked(sharedPrefs.getBoolean("checkLinks", false));
		checkLinks.setOnClickListener(graphInclusionsListener);
		checkCheckins.setChecked(sharedPrefs.getBoolean("checkCheckins", false));
		checkCheckins.setOnClickListener(graphInclusionsListener);
		checkPhotos.setChecked(sharedPrefs.getBoolean("checkPhotos", false));
		checkPhotos.setOnClickListener(graphInclusionsListener);
		checkVideos.setChecked(sharedPrefs.getBoolean("checkVideos", false));
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
	
	private void setupRenderers(ArrayList<Post> posts, XYSeries series)
	{
		renderer.setColor(getColour(sharedPrefs.getString("graph_bar_colour", "blue")));
		mRenderer.removeAllRenderers();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setYTitle("Likes");
		mRenderer.setAxisTitleTextSize(25);
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
}
