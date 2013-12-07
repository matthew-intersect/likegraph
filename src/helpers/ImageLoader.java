package helpers;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageLoader extends AsyncTask<Void, Void, Bitmap>
{
	private ImageView image;
	private String source;
	private int loading;

	public ImageLoader(ImageView image, String source, int loading)
	{
        this.image = image;
        this.source = source;
        this.loading = loading;
	}
	
	@Override
    protected void onPreExecute()
	{
		if(loading != 0)
		{
			image.setImageResource(loading);
		}
    }
	
	@Override
	protected Bitmap doInBackground(Void... args)
	{
		Bitmap bmp = null;
		try
		{
			InputStream in = new URL(source).openStream();
			bmp = BitmapFactory.decodeStream(in);
		}
		catch (Exception e)
		{}
		return bmp;
	}
	
	@Override
    protected void onPostExecute(Bitmap result)
	{
		image.setImageBitmap(result);
    }
}