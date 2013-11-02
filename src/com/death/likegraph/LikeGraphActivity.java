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

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LikeGraphActivity extends Activity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	
	private XYSeriesRenderer renderer;
	private XYMultipleSeriesRenderer mRenderer;
	private XYMultipleSeriesDataset dataset;
	
	private LinearLayout layout;
	private View graph;
	private CheckBox checkStatii, checkLinks, checkCheckins, checkPhotos, checkVideos;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.graph);

	    postsDatabaseAdapter = new PostsDatabaseAdapter(this);
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
	    
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
		checkStatii.setOnClickListener(graphInclusionsListener);
		checkLinks.setOnClickListener(graphInclusionsListener);
		checkCheckins.setOnClickListener(graphInclusionsListener);
		checkPhotos.setOnClickListener(graphInclusionsListener);
		checkVideos.setOnClickListener(graphInclusionsListener);
	}
	
	private void setupRenderers(ArrayList<Post> posts, XYSeries series)
	{
		renderer.setColor(Color.BLUE);
		mRenderer.removeAllRenderers();
		mRenderer.addSeriesRenderer(renderer);
		mRenderer.setYTitle("Likes");
		mRenderer.setAxisTitleTextSize(25);
		mRenderer.setShowGridY(true);
		mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.BLACK);
        mRenderer.setPanEnabled(true, false);
        mRenderer.setPanLimits(new double[]{-1, posts.size(), 0, series.getMaxY()});
        mRenderer.setXAxisMin(-1);
        mRenderer.setXAxisMax(50);
        mRenderer.setShowLegend(false);
        mRenderer.setXLabels(0);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setBarSpacing(.1);
        mRenderer.setMargins(new int[]{20, 50, 0, 20});
        mRenderer.setClickEnabled(true);
	}
}
