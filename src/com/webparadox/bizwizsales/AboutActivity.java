package com.webparadox.bizwizsales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;

public class AboutActivity extends Activity {
	ImageView imageViewBack, logoBtn;
	TextView text_support, text_content;
	Typeface droidSans, droidSansBold;
	SearchView aboutSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	SharedPreferences userData;
	String dealerID, employeeID;
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
		setContentView(R.layout.about_activity);
		context = this;
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");

		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		text_support = (TextView) findViewById(R.id.textview_appt_type);

		text_content = (TextView) findViewById(R.id.textView_content);
		droidSans = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "DroidSans-Bold.ttf");

		text_content.setTypeface(droidSans);

		text_support.setText(Constants.SUPPORT_STRING);
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

		aboutSearchView = (SearchView) findViewById(R.id.about_searchView);

		aboutSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if (query.trim().length() != 0) {

					searchAsyncTask = new SmartSearchAsyncTask(context,
							dealerID, employeeID, query);
					searchAsyncTask.execute();
				} else {
					Toast.makeText(context, Constants.TOAST_NO_DATA,
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "About Activity");

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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(AboutActivity.this, MainActivity.class);
		startActivity(backIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		gotoHomeActivity();
	}
}
