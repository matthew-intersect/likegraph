package helpers;

import com.death.likegraph.R;

import models.Status;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class StatusDialog extends Dialog
{

	public StatusDialog(Context context, Status status)
	{
		super(context);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.status_preview);
		
		TextView statusText = (TextView) this.findViewById(R.id.status_text);
		statusText.setText(status.getStatus());
		
		Button ok = (Button) this.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				StatusDialog.this.dismiss();
			}
		});
	}
}
