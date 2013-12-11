package helpers;

import com.death.likegraph.R;

import models.Photo;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class PhotoDialog extends Dialog
{
	public PhotoDialog(Context context, Photo photo)
	{
		super(context);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.photo_preview);
		
		TextView photoText = (TextView) this.findViewById(R.id.photo_text);
		photoText.setText(photo.getMessage());
		
		ImageView image = (ImageView) this.findViewById(R.id.image);
		image.setScaleType(ScaleType.CENTER_INSIDE);
		new ImageLoader(image, photo.getSource(), 0).execute();
		
		Button ok = (Button) this.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				PhotoDialog.this.dismiss();
			}
		});
	}
}
