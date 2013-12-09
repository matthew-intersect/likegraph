package adapters;

import java.util.ArrayList;

import com.death.likegraph.R;

import models.Friend;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchFriendAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	
    public ArrayList<Friend> friends;
    
    public SearchFriendAdapter(Context context, ArrayList<Friend> friends)
    {
        this.friends = friends;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public void updateFriends(ArrayList<Friend> friends)
    {
    	this.friends = friends;
    }
    
	@Override
	public int getCount()
	{
		return friends.size();
	}

	@Override
	public Object getItem(int position)
	{
		return friends.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = inflater.inflate(R.layout.friend_search_item, null);
    	TextView friendName = (TextView) convertView.findViewById(R.id.friend_name);
    	friendName.setText(friends.get(position).getName());
    	
    	return convertView;
	}
	
}
