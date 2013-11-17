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

	public ImageLoader(ImageView image, String source)
	{
        this.image = image;
        this.source = source;
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