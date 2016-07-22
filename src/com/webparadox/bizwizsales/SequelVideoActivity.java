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
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.adapter.SequalEmailVideosAdapter;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SequalEmailVideosModel;

public class SequelVideoActivity extends Activity{
	TextView textCustomerName,textCustomerAddress;
	String customerAddress, customerName;
	static Typeface droidSans;
	static Typeface droidSansBold;
	public static String dealerID = "";
	public static String employeeID = "";
	public static String CustomerId;
	String employeeName = "";
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	AsyncTask<Void, Integer, Void> sequalVideosAsyncTask;
	ListView sequalVideosListView;
	ImageView imageViewBack, logoBtn;
	SearchView sequelVideoSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_sequal_videos);
		context=this;
		Bundle extras = getIntent().getExtras();
		customerName = extras.getString("CustomerFullName");
		customerAddress = extras.getString("CustomerAddress");
		CustomerId = extras.getString("CustomerId");
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		employeeName = userData.getString(Constants.KEY_EMPLOYEE_NAME, "");
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		 textCustomerName = (TextView) findViewById(R.id.textView_title);
		textCustomerAddress = (TextView) findViewById(R.id.textview_address);
		textCustomerName.setTypeface(droidSans);
		textCustomerAddress.setTypeface(droidSans);
		textCustomerName.setText(customerName);
		textCustomerAddress.setText(customerAddress);
		sequalVideosListView=(ListView) findViewById(R.id.listViewsequalVideo);
		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});

		sequelVideoSearchView=(SearchView)findViewById(R.id.sequel_video_searchView);
		sequelVideoSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
			    
			    if(query.trim().length() !=0){
			     
			      searchAsyncTask=new SmartSearchAsyncTask(context,dealerID,employeeID,query);
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
		
		
		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentHome=new Intent(SequelVideoActivity.this,MainActivity.class);
				startActivity(intentHome);
				finish();
			}
		});
		loadCustomerFollowUp();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Sequeal Video Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}
	
	public void loadCustomerFollowUp() {

		
		final JSONObject reqObj_data = new JSONObject();
		final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, CustomerId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqData.add(reqObj_data);
		sequalVideosAsyncTask = new SequelVideosAsyncTask(SequelVideoActivity.this, reqData,
				sequalVideosListView);
		sequalVideosAsyncTask.execute();

	}
	
	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		gotoHomeActivity();
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
		
		if (sequalVideosAsyncTask != null) {
			sequalVideosAsyncTask.cancel(true);
			sequalVideosAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
	
	}
	/*public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return gesturedetector.onTouchEvent(ev);
	}

	public static class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 150;

		private static final int SWIPE_MAX_OFF_PATH = 100;

		private static final int SWIPE_THRESHOLD_VELOCITY = 100;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

		float velocityY) {

			float dX = e2.getX() - e1.getX();
			float dY = e1.getY() - e2.getY();
			if (Math.abs(dY) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(dX) >= SWIPE_MIN_DISTANCE) {
				
					if (dX > 0) {
						// Right Swipe
						SequalEmailVideosAdapter.setPreviousMonth();
					} else {
						// Left Swipe
						SequalEmailVideosAdapter.setNextMonth();
					}
				
				return true;

			} else if (Math.abs(dX) >= SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(dY) >= SWIPE_MIN_DISTANCE) {
				
					if (dY > 0) {
						// Up Swipe

					} else {
						// Down Swipe
					}
				
				return true;
			}
			return false;
		}
	}*/
	
	public class SequelVideosAsyncTask  extends AsyncTask<Void, Integer, Void> {
		ActivityIndicator pDialog;
		Context mContext;
		JSONObject responseJson;
		ServiceHelper serviceHelper;
		ArrayList<JSONObject> mRequestJson;
		JSONArray localJsonArray;
		ArrayList<SequalEmailVideosModel> sequalEmailVideosList;
		ListView sequalViewosListview;
		SequalEmailVideosModel sequalEmailVideoModel;

		public SequelVideosAsyncTask(Context context,
				ArrayList<JSONObject> reqData, ListView sequalVideosList) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.mRequestJson = reqData;
			this.sequalViewosListview = sequalVideosList;
			serviceHelper = new ServiceHelper(this.mContext);
			sequalEmailVideosList = new ArrayList<SequalEmailVideosModel>();
		}

		@Override
		protected void onPreExecute() {
			Singleton.getInstance().sequalEmailVideosList.clear();
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(mContext);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.SEQUEL_EMAIL_VIDEOS,
					Constants.REQUEST_TYPE_POST);
			if(responseJson != null){
				
				if (responseJson.has(Constants.KEY_PROJECT_RESPONSE)) {
					try {
						localJsonArray = responseJson
								.getJSONArray(Constants.KEY_PROJECT_RESPONSE);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject jobj = localJsonArray.getJSONObject(i);
							sequalEmailVideoModel = new SequalEmailVideosModel();
							sequalEmailVideoModel.mEmailId = jobj.get(
									Constants.JSON_KEY_EMAIL_ID).toString();
							sequalEmailVideoModel.mSequelEmail = jobj.get(
									Constants.JSON_KEY_SEQUAL_EMAIL).toString();
							sequalEmailVideoModel.mVideoURL = jobj.get(
									Constants.JSON_KEY_VIDEO_URL).toString();
							sequalEmailVideoModel.mStatus = jobj.get(
									Constants.JSON_KEY_STATUS).toString();
							sequalEmailVideoModel.mDateSent = jobj.get(
									Constants.JSON_KEY_DATE_SENT).toString();
							sequalEmailVideoModel.mImageURL = jobj.get(
									Constants.JSON_KEY_IMAGE_URL).toString();
						
							Singleton.getInstance().sequalEmailVideosList
									.add(sequalEmailVideoModel);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dismissDialog();
			if (responseJson != null) {
				if (localJsonArray != null) {
					if (localJsonArray.length() > 0) {
						SequalEmailVideosAdapter sequalVideosListAdapter = new SequalEmailVideosAdapter(
								mContext);
						sequalViewosListview.setAdapter(sequalVideosListAdapter);
					} else {
						SequalEmailVideosAdapter sequalVideosListAdapter = new SequalEmailVideosAdapter(
								mContext);
						sequalViewosListview.setAdapter(sequalVideosListAdapter);
					}

				}

			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}
		}

		public void dismissDialog() {
			if (pDialog != null) {
				pDialog.dismiss();
				pDialog = null;
			}
		}
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}
