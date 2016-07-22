package com.webparadox.bizwizsales.dialogs;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerFollowUpsModel;

public class SendEmailDialog extends Dialog {

	Typeface droidSans, droidSansBold;
	int position;
	ArrayList<CustomerFollowUpsModel> cusFollowupModelList;
	Context mContext;
	Boolean isResolved=false;
	Dialog dialog;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONObject localJsonObject;
	EditText followUpResolveCompletionNote;
	String email;
	EditText messageText,emailmessage,subjectText;

	public SendEmailDialog(Context context, String email) {
		super(context,android.R.style.Theme_Translucent_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.mContext=context;
		this.email = email;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(), "DroidSans-Bold.ttf");
		showSendEmailDialog();
	}

	@SuppressWarnings("deprecation")
	public void showSendEmailDialog(){
		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(WMLP);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.send_email_dialog);

		messageText = (EditText)dialog.findViewById(R.id.messageId);
		messageText.setText(email);
		subjectText = (EditText)dialog.findViewById(R.id.subjectText);
		emailmessage = (EditText)dialog.findViewById(R.id.emailmessage);

		Button emailCancel = (Button)dialog.findViewById(R.id.emailCancel);
		emailCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		Button emailSend = (Button)dialog.findViewById(R.id.emailSend);
		emailSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Singleton.getInstance().setEmailSubject(subjectText.getText().toString());
				Singleton.getInstance().setEmailBody(emailmessage.getText().toString());
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{messageText.getText().toString()});	
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, subjectText.getText().toString());
				emailIntent.putExtra(Intent.EXTRA_TEXT, emailmessage.getText().toString());
				emailIntent.setType("message/rfc822");
				((Activity) mContext).startActivityForResult(emailIntent, Constants.REQUEST_EMAILED);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}



