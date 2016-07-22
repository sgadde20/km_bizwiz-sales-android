package com.webparadox.bizwizsales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.SimpleGestureFilter;
import com.webparadox.bizwizsales.helper.SimpleGestureFilter.SimpleGestureListener;
import com.webparadox.bizwizsales.helper.TouchImageView;
import com.webparadox.bizwizsales.helper.TouchImageView.OnTouchImageViewListener;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CusAttachmentDetailsActivity extends Activity implements
		SimpleGestureListener {

	Context context;
	ImageView backToPrevious, backToHome;
	TouchImageView cusImage;
	TextView cusDec;
	WebView cusDoc;
	int position;
	String GoogleDocs = "http://docs.google.com/gview?embedded=true&url=",
			dealerID, employeeID;
	ActivityIndicator dialog;
	LinearLayout cusAttachmentLayout;
	private SimpleGestureFilter filter;
	Typeface droidSans, droidSansBold;
	ImageLoader imageLoader;
	DisplayImageOptions options;
	SearchView cusAttachmentDetailsSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	SharedPreferences userData;

	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// This is to restrict landscape for phone
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.customer_attachment_details);
		context = this;

		init();

	}

	public void init() {

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");

		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.ic_error)
				.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		this.filter = new SimpleGestureFilter(this, this);
		this.filter.setMode(SimpleGestureFilter.MODE_TRANSPARENT);
		cusDec = (TextView) findViewById(R.id.customer_attachment_dec);
		cusDec.setTypeface(droidSans);
		cusDec.setMovementMethod(new ScrollingMovementMethod());
		cusDec.setText(Singleton.getInstance().cusAttachmentModel.get(position).attachmentDescription);

		backToHome = (ImageView) findViewById(R.id.cus_attachment_details_image_back_icon);
		backToHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});
		backToPrevious = (ImageView) findViewById(R.id.cus_attachment_details_back_icon);

		backToPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		cusAttachmentDetailsSearchView = (SearchView) findViewById(R.id.cus_attachment_details_searchView);

		cusAttachmentDetailsSearchView
				.setOnQueryTextListener(new OnQueryTextListener() {

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

		cusAttachmentLayout = (LinearLayout) findViewById(R.id.cus_attachment_image_layout);
		cusImage = (TouchImageView) findViewById(R.id.img);
		cusImage.setOnTouchImageViewListener(new OnTouchImageViewListener() {

			@Override
			public void onMove() {
				PointF point = cusImage.getScrollPosition();
				Log.d("point X", String.valueOf(point.x));
				Log.d("point y", String.valueOf(point.y));
				RectF rect = cusImage.getZoomedRect();
				boolean isZoomed = cusImage.isZoomed();
				if (isZoomed) {

					Constants.swipe_Min_Distance = 800;
					Constants.swipe_Max_Distance = 800;
					Constants.swipe_Min_Velocity = 800;
					Log.d("ZOOM", "ZOOM");

				} else {
					Constants.swipe_Min_Distance = 150;
					Constants.swipe_Max_Distance = 800;
					Constants.swipe_Min_Velocity = 150;
					Log.d("OUT", "OUT");
				}
			}
		});
		cusDoc = (WebView) findViewById(R.id.cus_attachment_details_webView);
		Bundle extras = getIntent().getExtras();
		position = extras.getInt("Position");
		Log.d("POS", position + "");
		Log.d("STR",
				Singleton.getInstance().cusAttachmentModel.get(position).attachmentURL);

		LoadData(position);

	}

	public void LoadData(int pos) {
		if (Singleton.getInstance().cusAttachmentModel.get(pos).attachmentURL
				.endsWith(".JPG")) {

			loadImage(Singleton.getInstance().cusAttachmentModel.get(pos).attachmentURL);
			cusDec.setText(Singleton.getInstance().cusAttachmentModel.get(pos).attachmentDescription);

		} else if (Singleton.getInstance().cusAttachmentModel.get(pos).attachmentURL
				.endsWith(".pdf")) {

			loadPdf(Singleton.getInstance().cusAttachmentModel.get(pos).attachmentURL);
		} else {
			loadImage(Singleton.getInstance().cusAttachmentModel.get(pos).attachmentURL);
			cusDec.setText(Singleton.getInstance().cusAttachmentModel.get(pos).attachmentDescription);
		}
	}

	public void loadImage(String URL) {
		Constants.swipe_Min_Distance = 150;
		Constants.swipe_Max_Distance = 800;
		Constants.swipe_Min_Velocity = 150;
		cusAttachmentLayout.setVisibility(View.VISIBLE);
		cusDoc.setVisibility(View.GONE);

		imageLoader.displayImage(URL, cusImage, options,
				new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						dialog = new ActivityIndicator(context);
						dialog.show();
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						dismissDialog();
						cusImage.setZoom(1);
						cusImage.setImageDrawable(getResources().getDrawable(
								R.drawable.no_image));
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						dismissDialog();
						cusImage.setZoom(1);
						cusImage.setImageBitmap(loadedImage);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						dismissDialog();
					}
				}, new ImageLoadingProgressListener() {
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {
					}
				});

		/*
		 * ImageLoader imgLoader = new ImageLoader(context);
		 * imgLoader.DisplayImage(URL, R.drawable.no_image, cusImage);
		 */
	}

	public void loadPdf(String URL) {

		dialog = new ActivityIndicator(context);
		dialog.setLoadingText("Loading....");
		dialog.show();
		cusDoc.setVisibility(View.VISIBLE);
		cusAttachmentLayout.setVisibility(View.GONE);
		cusDoc.getSettings().setJavaScriptEnabled(true);
		cusDoc.getSettings().setLoadWithOverviewMode(true);
		cusDoc.getSettings().setUseWideViewPort(true);

		// following lines are to show the loader untile downloading the pdf
		// file for view.
		cusDoc.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// progDailog.show();

				view.loadUrl(url);

				return true;
			}

			@Override
			public void onPageFinished(WebView view, final String url) {
				dismissDialog();
			}
		});

		cusDoc.loadUrl(GoogleDocs + URL);
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent me) {
		this.filter.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}

	@Override
	public void onSwipe(int direction) {
		// TODO Auto-generated method stub
		switch (direction) {

		case SimpleGestureFilter.SWIPE_RIGHT:

			position = position - 1;

			Log.d("POS RIG", position + "");
			if (position < 0) {
				position = (Singleton.getInstance().cusAttachmentModel.size() - 1);
				LoadData(position);
			} else {
				LoadData(position);

			}

			break;
		case SimpleGestureFilter.SWIPE_LEFT:
			position = position + 1;
			Log.d("POS LEF", position + "");
			if (position > (Singleton.getInstance().cusAttachmentModel.size() - 1)) {
				position = 0;
				LoadData(position);
			} else {
				LoadData(position);
			}
			break;
		case SimpleGestureFilter.SWIPE_DOWN:
		case SimpleGestureFilter.SWIPE_UP:

		}
	}

	@Override
	public void onDoubleTap() {
		// TODO Auto-generated method stub

	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public void onDestroy() {
		super.onDestroy();

		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
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
