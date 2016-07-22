package com.webparadox.bizwizsales;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.asynctasks.CustomerAttachmentAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.ListValue;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;

public class CusAttachmentActivity extends Activity {

	Context context;

	ListView cusAttachmentListview;
	TextView cusAttachmentTextview, cusAttachmentPending;
	String dealerId, cusId, cusName;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	Typeface droidSans, droidSansBold;
	ImageView backToPrevious, backToHome;
	DatabaseHandler dbHandler;
	List<ListValue> checkValue;
	AsyncTask<Void, Integer, String> uploadAsyncTask;
	ProgressDialog progressBar;
	String result = "", employeeID;
	CustomerAttachmentAsyncTask cusAsyncTask;
	SearchView cusAttachmentSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Button btnAddAttachments;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// This is to restrict landscape for phone
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.customer_attachment_activity);
		context = this;
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		cusId = userData.getString(Constants.JSON_KEY_CUSTOMER_ID, "");
		cusName = userData.getString(Constants.JSON_KEY_CUSTOMER_NAME, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		dbHandler = new DatabaseHandler(this);
		init();

	}

	public void init() {
		cusAttachmentPending = (TextView) findViewById(R.id.customer_attachment_pending);
		cusAttachmentPending.setTypeface(droidSans);
		checkValue = dbHandler.getFavList(cusId);
		if (checkValue.size() > 0) {
			cusAttachmentPending.setText("Pending Documents" + "("
					+ checkValue.size() + ")");
		} else {
			cusAttachmentPending.setText("Pending Documents");
		}

		cusAttachmentPending.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkValue.size() > 0) {
					if (Constants.isNetworkConnected(getApplicationContext())) {
						uploadAsyncTask = new UploadImages().execute();
					} else {
						messageHandler.sendEmptyMessage(0);
					}
				}

			}
		});

		cusAttachmentSearchView = (SearchView) findViewById(R.id.cus_attachment_searchView);
		cusAttachmentSearchView
				.setOnQueryTextListener(new OnQueryTextListener() {

					@Override
					public boolean onQueryTextSubmit(String query) {
						// TODO Auto-generated method stub

						if (query.trim().length() != 0) {

							searchAsyncTask = new SmartSearchAsyncTask(context,
									dealerId, employeeID, query);
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

		backToHome = (ImageView) findViewById(R.id.cus_attachment_image_back_icon);
		backToHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});
		backToPrevious = (ImageView) findViewById(R.id.cus_attachment_back_icon);

		backToPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		cusAttachmentListview = (ListView) findViewById(R.id.cus_attachment_listview);
		cusAttachmentListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent cusAttachmentIntent = new Intent(context,
						CusAttachmentDetailsActivity.class);
				cusAttachmentIntent.putExtra("Position", position);
				startActivity(cusAttachmentIntent);
			}
		});
		cusAttachmentTextview = (TextView) findViewById(R.id.cus_attachment_header);
		cusAttachmentTextview.setTypeface(droidSansBold);
		cusAttachmentTextview.setText(cusName + " " + Constants.Attachment);
		btnAddAttachments=(Button) findViewById(R.id.button_addattach);
		btnAddAttachments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent galleryIntent = new Intent(
						CusAttachmentActivity.this,
						GalleryMainActivity.class);
				galleryIntent.putExtra("CustomerId", cusId);
				startActivity(galleryIntent);
				
			}
		});
		cusAsyncTask = new CustomerAttachmentAsyncTask(context, dealerId,
				cusId, cusAttachmentListview, cusAttachmentTextview, cusName, 0);
		cusAsyncTask.execute();

	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(context, MainActivity.class);
		backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(backIntent);
		finish();

	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Customer Attachment Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	private Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(getApplicationContext(),
					Constants.NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
		}
	};

	class UploadImages extends AsyncTask<Void, Integer, String> {
		int count, uploadedCount;

		public UploadImages() {
			progressBar = new ProgressDialog(CusAttachmentActivity.this);
			progressBar.setCancelable(false);
			progressBar.setMessage("File Uploading ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			count = checkValue.size();
			progressBar.setProgress(0);
			progressBar.setMax(count);
		}

		@Override
		protected void onPreExecute() {
			progressBar.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d("Values", "val" + values[0]);
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(Void... params) {
			count = checkValue.size();
			for (int i = 0; i < count; i++) {
				try {
					// float value = ((float)i/(float)count)*100;
					// Float obj = new Float(value);
					// int percent = obj.intValue();
					int percent = i + 1;
					publishProgress(percent);
					if (Constants.isNetworkConnected(getApplicationContext())) {
						result = new com.webparadox.bizwizsales.helper.HttpConnection()
								.sendPhoto(Integer.parseInt(checkValue.get(i)
										.getdelearId()), Integer
										.parseInt(checkValue.get(i)
												.getcustomerId()), Integer
										.parseInt(checkValue.get(i)
												.getemployeeId()), "", checkValue
										.get(i).getdescription(), 1, checkValue
										.get(i).getpath());
						if (result != null) {
							if (result.equalsIgnoreCase("1")) {
								//dbHandler.removeFromRow(checkValue.get(i).getId());
								dbHandler.removeCommonRow(Constants.TABLE_TEST, Constants.KEY_ID, checkValue.get(i)
										.getId());
								uploadedCount++;
							}
						}

					} else {
						messageHandler.sendEmptyMessage(0);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progressBar.dismiss();

			if (uploadedCount > 1) {
				Toast.makeText(getApplicationContext(),
						uploadedCount + " images uploaded out of " + count,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),
						uploadedCount + " image uploaded out of " + count,
						Toast.LENGTH_LONG).show();
			}

			cusAsyncTask = new CustomerAttachmentAsyncTask(context, dealerId,
					cusId, cusAttachmentListview, cusAttachmentTextview,
					cusName, 0);
			cusAsyncTask.execute();
			checkValue = dbHandler.getFavList(cusId);
			if (checkValue.size() > 0) {
				cusAttachmentPending.setText("Pending Documents" + "("
						+ checkValue.size() + ")");
			} else {
				cusAttachmentPending.setText("Pending Documents");
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressBar != null) {
			if (progressBar.isShowing()) {
				progressBar.cancel();
			}
			progressBar = null;
		}
		if (cusAsyncTask != null) {
			cusAsyncTask.cancel(true);
			cusAsyncTask = null;
		} 
		if (uploadAsyncTask != null) {
			uploadAsyncTask.cancel(true);
			uploadAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}
