package helpers;

import com.death.likegraph.R;

import models.Video;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class VideoDialog extends Dialog
{
	public VideoDialog(Context context, Video video)
	{
		super(context);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.video_preview);
		
		TextView videoDescription = (TextView) this.findViewById(R.id.video_description);
		videoDescription.setText(video.getDescription());
		
		ImageView videoThumbnail = (ImageView) this.findViewById(R.id.video_thumbnail);
		videoThumbnail.setScaleType(ScaleType.CENTER_INSIDE);
		new ImageLoader(videoThumbnail, video.getPicture(), 0).execute();
		
		Button ok = (Button) this.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				VideoDialog.this.dismiss();
			}
		});
	}
}
