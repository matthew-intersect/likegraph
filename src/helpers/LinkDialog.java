package helpers;

import models.Link;
import android.app.Dialog;
import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.death.likegraph.R;

public class LinkDialog extends Dialog
{

	public LinkDialog(Context context, Link link)
	{
		super(context);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.link_preview);
		
		TextView linkText = (TextView) this.findViewById(R.id.link_text);
		linkText.setText(link.getLink());
		Linkify.addLinks(linkText, Linkify.ALL);
		TextView linkMessageText = (TextView) this.findViewById(R.id.link_message_text);
		linkMessageText.setText(link.getMessage());
		
		Button ok = (Button) this.findViewById(R.id.ok);
		
		ok.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				LinkDialog.this.dismiss();
			}
		});
	}
}
