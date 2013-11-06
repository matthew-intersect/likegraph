package com.death.likegraph;

import java.util.ArrayList;

import models.Friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendRankAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	
    public ArrayList<Friend> friendRanks;
    private int show;
 
    public FriendRankAdapter(Context context, ArrayList<Friend> friends)
    {
        friendRanks = friends;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        show = 20;
    }
 
    @Override
    public int getCount()
    {
        return show;
    }
 
    @Override
    public Object getItem(int position)
    {
        return friendRanks.get(position);
    }
 
    @Override
    public long getItemId(int position)
    {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	convertView = inflater.inflate(R.layout.friend_rank_item, null);
    	ImageView friendPicture = (ImageView) convertView.findViewById(R.id.friend_picture);
    	friendPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
    	TextView friendName = (TextView) convertView.findViewById(R.id.friend_name);
    	TextView likeCount = (TextView) convertView.findViewById(R.id.friend_like_count);

    	friendName.setText(friendRanks.get(position).getName());
    	likeCount.setText(String.valueOf(friendRanks.get(position).getCount()));
    	
    	return convertView;
    }
    
    public void increaseLoadCount()
    {
    	show += 20;
    }
}
