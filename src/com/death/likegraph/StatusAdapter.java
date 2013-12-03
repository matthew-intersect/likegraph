package com.death.likegraph;

import java.util.ArrayList;

import models.Status;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	
    public ArrayList<Status> statuses;
    
    public StatusAdapter(Context context, ArrayList<Status> statuses)
    {
        this.statuses = statuses;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	@Override
	public int getCount()
	{
		return statuses.size();
	}

	@Override
	public Object getItem(int position)
	{
		return statuses.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = inflater.inflate(R.layout.status_item, null);
    	TextView status = (TextView) convertView.findViewById(R.id.status_content);
    	status.setText(statuses.get(position).getStatus());
    	
    	return convertView;
	}
	
}
