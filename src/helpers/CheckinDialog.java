package helpers;

import com.death.likegraph.R;

import models.Checkin;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class CheckinDialog extends Dialog
{
	public CheckinDialog(Context context, Checkin checkin)
	{
		super(context);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.checkin_preview);
		
		TextView checkinPlace = (TextView) this.findViewById(R.id.checkin_place);
		checkinPlace.setText(checkin.getLocation());
		TextView checkinMessage = (TextView) this.findViewById(R.id.checkin_message);
		checkinMessage.setText(checkin.getMessage());
		
		Button ok = (Button) this.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				CheckinDialog.this.dismiss();
			}
		});
	}
}
