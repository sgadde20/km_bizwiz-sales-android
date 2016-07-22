package com.webparadox.bizwizsales;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;

public class GalleryUploadActivity extends BaseActivity implements
		OnClickListener {
	ImageView gallerImageUpload, imageViewBack, logoBtn;
	ArrayList<String> selectUrl = new ArrayList<String>();
	ArrayList<String> discriptionForImage = new ArrayList<String>();
	ArrayList<String> statusIdArray = new ArrayList<String>();
	DisplayImageOptions options;
	String result = "";
	String dealerId = "", employeeId = "", customerId = "";
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	DatabaseHandler dbHandler;
	AsyncTask<Void, Integer, String> uploadAsyncTask;
	ProgressDialog progressBar;
	LinearLayout mainLayout;
	EditText editText;
	int editTextTag = 1000;
	int pos;
	ArrayList<Bitmap> allBit = new ArrayList<Bitmap>();
	SearchView galleryUploadSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Context context;
	String from = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.ic_error)
				.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		dbHandler = new DatabaseHandler(this);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");

		Bundle extras = getIntent().getExtras();
		customerId = extras.getString("CustomerId");

		if (getIntent().hasExtra("From")) {
			from = extras.getString("From");
		} else {
			from = "";
		}

		setContentView(R.layout.galleryupload);
		context = this;
		selectUrl = getIntent().getStringArrayListExtra("UPLOADPHOTOURL");
		for (int i = 0; i < selectUrl.size(); i++) {
			discriptionForImage.add("");
			allBit.add(resizeBitmap(selectUrl.get(i), (int) getResources()
					.getDimension(R.dimen.hundred), (int) getResources()
					.getDimension(R.dimen.hundred)));
		}

		mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		// listView = (ListView)findViewById(R.id.listView);
		// listAdapder = new ListViewAdapder();
		// listView.setAdapter(listAdapder);
		galleryUploadSearchView = (SearchView) findViewById(R.id.gallery_upload_searchView);

		galleryUploadSearchView
				.setOnQueryTextListener(new OnQueryTextListener() {

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

		gallerImageUpload = (ImageView) findViewById(R.id.gallerImageUpload);
		gallerImageUpload.setOnClickListener(this);

		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		imageViewBack.setOnClickListener(this);

		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(this);

		addDynamicView();
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Gallery Upload Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}
	
	private void addDynamicView() {
		for (int i = 0; i < allBit.size(); i++) {

			LinearLayout horiLayout = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams1.setMargins(
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five));
			horiLayout.setLayoutParams(layoutParams1);
			horiLayout.setOrientation(LinearLayout.HORIZONTAL);
			horiLayout.setBackgroundResource(R.drawable.gallary_border);
			mainLayout.addView(horiLayout);

			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					(int) getResources().getDimension(R.dimen.sixty),
					(int) getResources().getDimension(R.dimen.sixty));
			layoutParams.setMargins(
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five));
			// imageLoader.displayImage("file://" +selectUrl.get(i), imageView,
			// options);
			imageView.setImageBitmap(allBit.get(i));
			imageView.setLayoutParams(layoutParams);
			horiLayout.addView(imageView);

			editText = new EditText(this);
			if (selectUrl.size() - 1 == i) {
				editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			}

			editText.setSingleLine(true);
			editText.setId(editTextTag + i);
			LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
					0, LayoutParams.MATCH_PARENT, 1);
			editParams.gravity = Gravity.CENTER_VERTICAL;
			editParams.setMargins(
					(int) getResources().getDimension(R.dimen.fifteen),
					(int) getResources().getDimension(R.dimen.fifteen),
					(int) getResources().getDimension(R.dimen.fifteen),
					(int) getResources().getDimension(R.dimen.fifteen));
			editText.setLayoutParams(editParams);
			editText.setPadding(50, 50, 50, 50);
			editText.setBackgroundResource(R.drawable.galleryuploadedit);
			editText.setHint("description");
			horiLayout.addView(editText);
		}
	}

	public Bitmap resizeBitmap(String url, int targetW, int targetH) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		return BitmapFactory.decodeFile(url, bmOptions);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gallerImageUpload:
			if (Constants.isNetworkConnected(getApplicationContext())) {
				for (int i = 0; i < allBit.size(); i++) {
					EditText edit = (EditText) findViewById(editTextTag + i);
					if (!edit.getText().toString().trim().equalsIgnoreCase("")
							&& !edit.getText().toString()
									.equalsIgnoreCase(null)) {
						discriptionForImage.set(i, edit.getText().toString());
						String status = dbHandler.adddata(customerId,
								employeeId, dealerId, selectUrl.get(i), edit
										.getText().toString());
						statusIdArray.add(status);
					} else {
						SimpleDateFormat sdf = new SimpleDateFormat(
								"dd:MMMM:yyyy HH:mm:ss ");
						final String currentDateandTime = sdf
								.format(new Date());
						discriptionForImage.set(i, currentDateandTime);
						String status = dbHandler.adddata(customerId,
								employeeId, dealerId, selectUrl.get(i),
								currentDateandTime);
						statusIdArray.add(status);
					}
				}
				Log.e("discriptionForImage ===>", "" + discriptionForImage);
				Log.e("selectUrl ===>", "" + selectUrl);
				Log.e("statusIdArray ===>", "" + statusIdArray);

				uploadAsyncTask = new UploadImages().execute();
			} else {
				messageHandler.sendEmptyMessage(0);
			}
			break;

		case R.id.back_icon:
			finish();
			break;

		case R.id.image_back_icon:
			Intent backIntent = new Intent(GalleryUploadActivity.this,
					MainActivity.class);
			backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(backIntent);
			break;

		default:
			break;
		}
	}

	private void sendGalleryImage() {
		if (Constants.isNetworkConnected(getApplicationContext())) {
			uploadAsyncTask = new UploadImages().execute();
		} else {
			messageHandler.sendEmptyMessage(0);
		}
	}

	class UploadImages extends AsyncTask<Void, Integer, String> {
		int count, uploadedCount;

		public UploadImages() {
			progressBar = new ProgressDialog(GalleryUploadActivity.this);
			progressBar.setCancelable(false);
			progressBar.setMessage("File Uploading ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			count = selectUrl.size();
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
			count = selectUrl.size();
			for (int i = 0; i < count; i++) {
				try {
					// float value = ((float)i/(float)count)*100;
					// Float obj = new Float(value);
					// int percent = obj.intValue();
					int percent = i + 1;
					publishProgress(percent);
					if (Constants.isNetworkConnected(getApplicationContext())) {
						result = new com.webparadox.bizwizsales.helper.HttpConnection()
								.sendPhoto(Integer.parseInt(dealerId),
										Integer.parseInt(customerId),
										Integer.parseInt(employeeId),
										"",
										discriptionForImage.get(i), 1,
										selectUrl.get(i));
						if (result != null) {
							if (result.equalsIgnoreCase("1")) {
								//dbHandler.removeRow(statusIdArray.get(i));
								dbHandler.removeCommonRow(Constants.TABLE_TEST, Constants.KEY_ID, statusIdArray.get(i));
								uploadedCount++;
							}
						}

					} else {
						// messageHandler.sendEmptyMessage(0);
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

		// GalleryUploadActivity.java
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// statusIdArray.clear();
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

			Intent redirectIntent;
			if (from.equalsIgnoreCase("SalesProcess")) {
				finish();
				GalleryMainActivity.closeActivity();
			} else {
				redirectIntent = new Intent(GalleryUploadActivity.this,
						CusAttachmentActivity.class);
				startActivity(redirectIntent);
				finish();
				GalleryMainActivity.closeActivity();
			}
		}
	}

	private Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(getApplicationContext(),
					Constants.NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
			Log.e("dbHandler.getCount() ====> ", "" + dbHandler.getCount());
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (progressBar != null) {
			if (progressBar.isShowing()) {
				progressBar.cancel();
			}
			progressBar = null;
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
