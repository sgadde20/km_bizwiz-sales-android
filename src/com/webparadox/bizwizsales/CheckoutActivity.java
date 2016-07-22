package com.webparadox.bizwizsales;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.webparadox.bizwizsales.asynctasks.CheckoutAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Signature;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CheckoutActivity extends Activity implements OnClickListener {

	TextView checkoutHeader, checkoutSignature, checkoutAgree;
	EditText checkoutDesc;
	Button checkoutContinue, checkoutClear;
	Context context;
	ImageView imageViewBack, logoBtn, signatureImage;
	Bitmap bitmap;
	LinearLayout mContent;
	RelativeLayout penLayout;
	Signature m_signature;
	Typeface droidSans, droidSansBold;
	CheckBox checkoutCb;
	View mView;
	ActivityIndicator dialog;
	String result = "", dealerId, customerId, employeeId, statement = "",
			amount;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	AgreeTask agreeTask;
	SearchView checkoutSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Boolean hasSign = false;
	CheckoutAsyncTask chtrask;
	String apptResultId = "";

	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.checkout_activity);
		context = this;
		ImageLoader imageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_error)
		.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
		.cacheOnDisk(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).build();

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();

		apptResultId = userData.getString(Constants.KEY_APPT_RESULT_ID, "0");

		dealerId = userData.getString("DealerId", "");
		employeeId = userData.getString("EmployeeId", "");
		customerId = userData.getString("CustomerId", "");
		amount = getIntent().getStringExtra("Amount");
		droidSans = Typeface.createFromAsset(context.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(context.getAssets(),
				"DroidSans-Bold.ttf");
		mContent = (LinearLayout) findViewById(R.id.sig_layout);
		penLayout = (RelativeLayout) findViewById(R.id.pen_layout);
		m_signature = new Signature(this, null);
		mContent.addView(m_signature, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mContent.setVisibility(View.GONE);
		penLayout.setVisibility(View.VISIBLE);
		imageViewBack = (ImageView) findViewById(R.id.checkout_back_icon);
		imageViewBack.setOnClickListener(this);
		checkoutHeader = (TextView) findViewById(R.id.chechout_header);
		checkoutHeader.setTypeface(droidSansBold);
		checkoutSignature = (TextView) findViewById(R.id.checkout_signature);
		checkoutSignature.setTypeface(droidSansBold);
		checkoutDesc = (EditText) findViewById(R.id.checkout_dec);
		checkoutDesc.setTypeface(droidSans);
		checkoutContinue = (Button) findViewById(R.id.checkout_continue);
		checkoutContinue.setTypeface(droidSansBold);
		checkoutContinue.setOnClickListener(this);
		checkoutAgree = (TextView) findViewById(R.id.checkoyut_agree);
		checkoutAgree.setTypeface(droidSans);
		logoBtn = (ImageView) findViewById(R.id.checkout_image_back_icon);
		logoBtn.setOnClickListener(this);
		checkoutCb = (CheckBox) findViewById(R.id.checkout_checkbox);
		checkoutDesc = (EditText) findViewById(R.id.checkout_dec);
		mView = mContent;
		checkoutDesc.clearFocus();

		agreeTask = new AgreeTask();
		agreeTask.execute();

		checkoutDesc.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				checkoutDesc.setCursorVisible(true);
				return false;
			}
		});

		checkoutSearchView = (SearchView) findViewById(R.id.checkout_searchView);
		checkoutSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if (query.trim().length() != 0) {

					searchAsyncTask = new SmartSearchAsyncTask(context,
							dealerId, employeeId, query);
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

		signatureImage = (ImageView) findViewById(R.id.signature_image);
		if (Singleton.getInstance().proposalList.size() > 0) {
			signatureImage.setVisibility(View.GONE);
			penLayout.setBackgroundColor(getResources().getColor(
					R.color.gray_list1_bg));
			if (Singleton.getInstance().proposalList.get(0)
					.getSignatureExists().equals("False")) {
				hasSign = false;
			} else {
				penLayout.setBackgroundColor(getResources().getColor(
						R.color.white));
				hasSign = true;
				signatureImage.setVisibility(View.VISIBLE);
				imageLoader.displayImage(Singleton.getInstance().proposalList
						.get(0).getSignatureURL(), signatureImage, options);
			}
		}

		penLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (!hasSign) {
					penLayout.setVisibility(View.GONE);
					mContent.setVisibility(View.VISIBLE);
				}

				return false;
			}
		});
		checkoutClear = (Button) findViewById(R.id.checkout_clear);
		checkoutClear.setTypeface(droidSans);
		checkoutClear.setOnClickListener(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Checkout Activity");

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

	public void onDestroy() {
		super.onDestroy();
		if (agreeTask != null) {
			agreeTask.cancel(true);
			agreeTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (chtrask != null) {
			chtrask.cancel(true);
			chtrask = null;
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.cancel();
			}
			dialog = null;
		}
	}

	private void gotoHomeActivity() {
		Intent backIntent = new Intent(context, MainActivity.class);
		backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(backIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.checkout_back_icon:
			finish();
			break;
		case R.id.checkout_image_back_icon:
			gotoHomeActivity();
			break;

		case R.id.checkout_clear:
			m_signature.clear();
			break;

		case R.id.checkout_continue:
			if (!m_signature.isNull() || hasSign) {
				if (checkoutCb.isChecked()) {
					Constants.isCheckOut = true;
					mView.setDrawingCacheEnabled(true);
					m_signature.save(mView);
					chtrask = new CheckoutAsyncTask(CheckoutActivity.this,dealerId,customerId,employeeId,apptResultId);
					chtrask.execute();

				} else {
					Toast.makeText(context,
							"Please accept the terms & conditions.",
							Toast.LENGTH_SHORT).show();

				}
			} else {
				Toast.makeText(context, "Please register your signature.",
						Toast.LENGTH_SHORT).show();

			}

		default:
			break;
		}

	}


	public class AgreeTask extends AsyncTask<Void, Integer, Void> {

		JSONObject responseJson;
		ServiceHelper serviceHelper;
		ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();
		JSONArray localJsonArray = new JSONArray();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			serviceHelper = new ServiceHelper(context);
			responseJson = new JSONObject();
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				} else {
					dialog = null;
				}
			}
			dialog = new ActivityIndicator(context);
			dialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.DEALER_SETTING_URL + "?"
							+ Constants.SAVE_CALL_DEALERID + "=" + dealerId,
							Constants.REQUEST_TYPE_GET);

			return null;
		}

		@Override
		protected void onPostExecute(Void Result) {
			super.onPostExecute(Result);

			if (responseJson != null) {

				if (responseJson.has(Constants.DEALER_SETTING)) {

					try {
						localJsonArray = responseJson
								.getJSONArray(Constants.DEALER_SETTING);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject jobj = localJsonArray.getJSONObject(0);
							statement = jobj.getString("SalesAppContractText");

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					checkoutAgree.setText(statement);
				} else {
					Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(context, Constants.TOAST_NO_DATA,
						Toast.LENGTH_SHORT).show();
			}
			dissmissDialog();
		}
	}

	public void dissmissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	finish();
	    } 
	}
}
