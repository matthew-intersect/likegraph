package com.death.likegraph;

import java.util.ArrayList;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Activity;
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
				GraphViewData[] graphData = new GraphViewData[counts.size()];
				for(int i=0;i<counts.size();i++)
				{
					graphData[i] = new GraphViewData(i+1, counts.get(i));
				}
				GraphViewSeries exampleSeries = new GraphViewSeries(graphData);	
				BarGraphView graphView = new BarGraphView(v.getContext(), "");
				graphView.addSeries(exampleSeries);
				graphView.setViewPort(0, 60);
				graphView.setScrollable(true);
				graphView.setScalable(true);
				graphView.getGraphViewStyle().setNumHorizontalLabels(1);
				graphView.getGraphViewStyle().setVerticalLabelsWidth(80);
				layout.addView(graphView);
			}
		});
	}
}
