package adapters;

import java.util.ArrayList;

import com.death.likegraph.R;

import models.Post;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PostAdapter extends BaseAdapter
{
	private LayoutInflater inflater;
	
    public ArrayList<Post> posts;
    
    public PostAdapter(Context context, ArrayList<Post> posts)
    {
        this.posts = posts;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

	@Override
	public int getCount()
	{
		return posts.size();
	}

	@Override
	public Object getItem(int position)
	{
		return posts.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		convertView = inflater.inflate(R.layout.post_item, null);
    	TextView post = (TextView) convertView.findViewById(R.id.post_content);
    	post.setText(posts.get(position).getListDisplay());
    	
    	return convertView;
	}
	
}
