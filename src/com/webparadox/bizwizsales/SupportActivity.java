package com.webparadox.bizwizsales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;

public class SupportActivity extends Activity {
	ImageView imageViewBack, logoBtn;
	Button buttonCallBizwiz, buttonSupport;
	TextView text_support, text_call;
	Typeface droidSans, droidSansBold;
	String myString = "Call one of our support \n agents at ";
	String myString1 = "&lt;u>877-358-3100&lt;/u>.";
	String dealerId,empId;
	SearchView supportSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	SharedPreferences userData;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.support_activity);
		context=this;
		imageViewBack = (ImageView) findViewById(R.id.back_icon);

		text_support = (TextView) findViewById(R.id.textView_supporturl);
		text_call = (TextView) findViewById(R.id.textView_call);
		
		
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		empId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		droidSans = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "DroidSans-Bold.ttf");
		// text_call.setMovementMethod(LinkMovementMethod.getInstance());

		text_support.setText(Html.fromHtml(getString(R.string.support_string)));
		// text_call.setText(myString+Html.fromHtml(getString(R.string.support_call_number),
		// BufferType.SPANNABLE);
		text_call.setText(Html
				.fromHtml(getString(R.string.support_call_number)));

		// Spannable mySpannable = (Spannable) text_call.getText();

		// ClickableSpan myClickableSpan = new ClickableSpan() {
		// @Override
		// public void onClick(View widget) { /* do something */
		// Intent callIntent = new Intent(Intent.ACTION_CALL);
		// callIntent
		// .setData(Uri
		// .parse(com.webparadox.bizwizsales.libraries.Constants.CALL_BIZWIZ));
		// startActivity(callIntent);
		// }
		// };

		// mySpannable.setSpan(myClickableSpan, i1, i2 + 1,
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


		supportSearchView=(SearchView)findViewById(R.id.support_searchView);

		supportSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if(query.trim().length() !=0){

					searchAsyncTask=new SmartSearchAsyncTask(context,dealerId,empId,query);
					searchAsyncTask.execute();
				}else{
					Toast.makeText(context, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});


		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});

		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});

		text_support.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent supportIntent = new Intent(Intent.ACTION_VIEW);
				supportIntent.setData(Uri
						.parse(com.webparadox.bizwizsales.libraries.Constants.SUPPORT_URL));
				startActivity(supportIntent);

			}
		});
		text_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri
						.parse(com.webparadox.bizwizsales.libraries.Constants.CALL_BIZWIZ));
				startActivity(callIntent);
			}
		});

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Support Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}
	public void onDestroy(){
		super.onDestroy();
		if(searchAsyncTask !=null){
			searchAsyncTask.cancel(true);
			searchAsyncTask=null;
		}
	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(SupportActivity.this, MainActivity.class);
		startActivity(backIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		gotoHomeActivity();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}
