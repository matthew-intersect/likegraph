package com.death.likegraph;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class LikeGraphActivity extends Activity
{
	private PostsDatabaseAdapter postsDatabaseAdapter;
	
	private Button generate;
	private LinearLayout layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.graph);

	    postsDatabaseAdapter = new PostsDatabaseAdapter(this);
	    postsDatabaseAdapter = postsDatabaseAdapter.open();
	    
	    generate = (Button) findViewById(R.id.generateGraph);
	    layout = (LinearLayout) findViewById(R.id.sub_layout);
	    
	    
	    generate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ArrayList<Integer> counts = postsDatabaseAdapter.getStatiiCounts();
//				GraphViewData[] graphData = new GraphViewData[counts.size()];
//				for(int i=0;i<counts.size();i++)
//				{
//					graphData[i] = new GraphViewData(i+1, counts.get(i));
//				}
//				GraphViewSeries exampleSeries = new GraphViewSeries(graphData);	
//				BarGraphView graphView = new BarGraphView(v.getContext(), "");
//				graphView.addSeries(exampleSeries);
//				graphView.setViewPort(0, 60);
//				graphView.setScrollable(true);
//				graphView.setScalable(true);
//				graphView.getGraphViewStyle().setNumHorizontalLabels(1);
//				graphView.getGraphViewStyle().setVerticalLabelsWidth(80);
//				layout.addView(graphView);
				
				
				CategorySeries series = new CategorySeries("");
				for(int i=0;i<counts.size();i++)
				{
					series.add("", counts.get(i));
				}
				XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
				dataSet.addSeries(series.toXYSeries());
				XYSeriesRenderer renderer = new XYSeriesRenderer();
				renderer.setColor(Color.BLUE);
				XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
				mRenderer.addSeriesRenderer(renderer);
				mRenderer.setYTitle("Likes");
				mRenderer.setAxisTitleTextSize(25);
				mRenderer.setShowGridY(true);
				mRenderer.setApplyBackgroundColor(true);
		        mRenderer.setBackgroundColor(Color.BLACK);
		        mRenderer.setPanEnabled(true, false);
		        mRenderer.setXAxisMin(-1);
		        mRenderer.setXAxisMax(60);
		        mRenderer.setShowLegend(false);
		        mRenderer.setPanLimits(new double[]{-1,counts.size(),0,series.toXYSeries().getMaxY()});
		        mRenderer.setXLabels(0);
		        mRenderer.setLabelsTextSize(30);
		        mRenderer.setBarSpacing(.1);
		        mRenderer.setMargins(new int[]{20,50,20,20});
		        View view = ChartFactory.getBarChartView(getApplicationContext(), dataSet, mRenderer, Type.DEFAULT);
		        layout.addView(view);
			}
		});
	}
}
