package com.webparadox.bizwizsales;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.asynctasks.SaveSummaryAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ObjectSerializer;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.ProposalCartModel;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;

@SuppressWarnings({ "static-access", "unchecked", "unused", "deprecation" })
public class UpdateProposalActivity extends Activity implements
		OnClickListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5530315907286342047L;
	TextView txtMaterialName, txtProductDescription, txtProposalAmount,
			txtAmount;
	static Typeface droidSans;
	static Typeface droidSansBold;
	String dealerID = "";
	String employeeID = "";
	String employeeName = "";
	String appointmentResultId = "";
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	AsyncTask<Void, Integer, Void> updateProposalAsyncTask;
	ImageView imageViewBack, logoBtn;
	ImageView imgPlayButton, imgThumbnail;
	Button btnUpdateProposal, btnAddNewProduct;
	EditText editTxtQuantity, editTxtAmount, editTxtNotes;

	SpProductSubCatAndMaterialModel mMaterial = new SpProductSubCatAndMaterialModel();
	SearchView uploadProposalSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Context context;
	SaveSummaryAsyncTask saveTask;
	Boolean isOther = false;
	Boolean mFormatting = false;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public static GestureDetector gesturedetector = null;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageOnFail(R.drawable.ic_error)
			.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;
	boolean mShowPrice = true, isAddnew = false;
	ScrollView gestureLayout;
	ProposalCartModel proposalCart = new ProposalCartModel();
	ArrayList<SpProductSubCatAndMaterialModel> mRecentProductsList = new ArrayList<SpProductSubCatAndMaterialModel>();
	ObjectSerializer mObjetserilizer = new ObjectSerializer();
	ArrayList<ProposalCartModel> mproposalCartList = new ArrayList<ProposalCartModel>();
	int curPos;

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
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_proposal);
		context = this;
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(getApplicationContext()));
		Bundle extras = getIntent().getExtras();

		mMaterial.mSubcategoryName = extras.getString("SubCatName");
		mMaterial.mSubcategoryId = extras.getString("SubCatId");
		mMaterial.mMaterialName = extras.getString("MaterialName");
		mMaterial.mMaterialId = extras.getString("MaterialId");
		mMaterial.mProductDescription = extras.getString("ProductDescription");
		mMaterial.mProductImageURL = extras.getString("ProductImageURL");
		mMaterial.mProductVideoURL = extras.getString("ProductVideoURL");
		mMaterial.mUnitSellingPrice = extras.getString("UnitSellingPrice");
		mMaterial.mProductType = extras.getString("ProductType");

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		employeeName = userData.getString(Constants.KEY_EMPLOYEE_NAME, "");
		appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,
				"0");
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");

		preferenceSettings = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		settingsEditor = preferenceSettings.edit();
		if (preferenceSettings.contains(Constants.PREF_SWITCH_SHOW_PRICE)) {
			mShowPrice = preferenceSettings.getBoolean(
					Constants.PREF_SWITCH_SHOW_PRICE, false);
		}

		if (preferenceSettings.contains(Constants.PREF_RECENT_PRODUCT_KEY)) {
			mRecentProductsList.clear();
			try {

				mRecentProductsList = (ArrayList<SpProductSubCatAndMaterialModel>) mObjetserilizer
						.deserialize(preferenceSettings
								.getString(
										Constants.PREF_RECENT_PRODUCT_KEY,
										mObjetserilizer
												.serialize(new ArrayList<SpProductSubCatAndMaterialModel>())));
				Iterator<SpProductSubCatAndMaterialModel> iter = mRecentProductsList
						.iterator();
				boolean isPresent = false;
				while (iter.hasNext()) {
					SpProductSubCatAndMaterialModel str = iter.next();

					if (str.mMaterialId.equalsIgnoreCase(mMaterial.mMaterialId)) {
						isPresent = true;
					}
				}
				if (!isPresent) {
					if (mRecentProductsList.size() < 5) {
						mRecentProductsList.add(mMaterial);
					} else {
						mRecentProductsList.remove(0);
						mRecentProductsList.add(mMaterial);
					}
					settingsEditor.putString(Constants.PREF_RECENT_PRODUCT_KEY,
							ObjectSerializer.serialize(mRecentProductsList));
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (!preferenceSettings
				.contains(Constants.PREF_RECENT_PRODUCT_KEY)
				&& mRecentProductsList.size() == 0) {
			try {
				mRecentProductsList.add(mMaterial);
				settingsEditor.putString(Constants.PREF_RECENT_PRODUCT_KEY,
						mObjetserilizer.serialize(mRecentProductsList));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		settingsEditor.commit();
		gestureLayout = (ScrollView) findViewById(R.id.scrollView1);
		gesturedetector = new GestureDetector(new MyGestureListener());
		gestureLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				if (gesturedetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		});

		txtMaterialName = (TextView) findViewById(R.id.textViewMaterialName);
		txtProductDescription = (TextView) findViewById(R.id.textViewproductDescription);
		txtProposalAmount = (TextView) findViewById(R.id.textViewproposalamount);
		txtAmount = (TextView) findViewById(R.id.textViewAmount);
		txtMaterialName.setTypeface(droidSans);
		txtProductDescription.setTypeface(droidSans);
		txtProposalAmount.setTypeface(droidSansBold);
		txtAmount.setTypeface(droidSansBold);
		txtMaterialName.setText(mMaterial.mMaterialName);
		if (mMaterial.mProductDescription.equals("")
				|| mMaterial.mProductDescription.length() < 1) {
			txtProductDescription.setText("N/A");
		} else {
			txtProductDescription.setText(mMaterial.mProductDescription);
		}
		if (mShowPrice) {
			txtAmount.setText("$" + mMaterial.mUnitSellingPrice);
		} else {
			txtAmount.setText("*******");
		}

		imgPlayButton = (ImageView) findViewById(R.id.imageViewVideoPlay);
		imgThumbnail = (ImageView) findViewById(R.id.imageViewThumnail);
		btnUpdateProposal = (Button) findViewById(R.id.buttonupdateProposal);
		btnAddNewProduct = (Button) findViewById(R.id.buttonaddNew);
		btnUpdateProposal.setOnClickListener(this);
		btnAddNewProduct.setOnClickListener(this);
		imgPlayButton.setOnClickListener(this);
		setImageThumbnill();
		editTxtQuantity = (EditText) findViewById(R.id.editTextQuantity);
		editTxtAmount = (EditText) findViewById(R.id.editTextAmount);
		editTxtNotes = (EditText) findViewById(R.id.editTextNotes);
		editTxtQuantity.requestFocus();
		editTxtQuantity.clearFocus();
		imageViewBack = (ImageView) findViewById(R.id.back_icon);

		// For filling up amount based on quantity entered
		editTxtQuantity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				float qty = (float) 0.00;
				if(Utilities.isNumeric(s.toString())){
					if (s.length() > 0) {
						qty = Float.parseFloat(s.toString());
					}
				}

				float unitPrice = Float.parseFloat(mMaterial.mUnitSellingPrice
						.replaceAll(",", ""));
				if (unitPrice > 0) {
					BigDecimal bd = new BigDecimal(("" + (qty * unitPrice)));
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					String valueAmount = String.valueOf(bd);
					editTxtAmount.setText(valueAmount);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				isOther = true;
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				isOther = false;
			}
		});

		editTxtAmount.addTextChangedListener(new TextWatcher() {
			private boolean mFormatting; // this is a flag which prevents the

			// stack overflow.

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!mFormatting) {
					mFormatting = true;
					String editValue = editTxtAmount.getText().toString();
					editValue = editValue.replace(".", "");
					editValue = editValue.replace("$", "");
					editValue = editValue.replaceAll("\\s+", "");
					editValue = editValue.replaceAll("[-.^:,()]", "");
					editValue = editValue.replaceAll("[^\\d.]", "");
					if (editValue.length() == 1) {
						editTxtAmount.setText("0.0" + editValue);
						editTxtAmount.setSelection(editTxtAmount.getText()
								.toString().length());
					} else if (editValue.length() == 2) {
						editTxtAmount.setText("0." + editValue);
						editTxtAmount.setSelection(editTxtAmount.getText()
								.toString().length());
					} else if (editValue.length() > 2) {
						int len = editValue.length();
						double amount = Double.parseDouble(editValue.substring(
								0, len - 2));
						NumberFormat formatter = new DecimalFormat(
								"###,###,###.##");
						System.out.println("The Decimal Value is: "
								+ formatter.format(amount));
						Log.d("The Decimal Value is: ",
								"$"
										+ formatter.format(amount)
										+ "."
										+ editValue.substring(len - 2,
												editValue.length()));

						editTxtAmount.setText(formatter.format(amount)
								+ "."
								+ editValue.substring(len - 2,
										editValue.length()));
						editTxtAmount.setSelection(editTxtAmount.getText()
								.toString().length());
					}
					mFormatting = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		// for video icon display
		if (mMaterial.mProductVideoURL.equals("")) {
			imgPlayButton.setVisibility(View.GONE);
		}

		uploadProposalSearchView = (SearchView) findViewById(R.id.upload_proposal_searchView);

		uploadProposalSearchView
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

		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// addToCart();
				gotoHomeActivity();
			}
		});

		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intentHome = new Intent(UpdateProposalActivity.this,
						MainActivity.class);
				startActivity(intentHome);
				finish();
			}
		});
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

		if (updateProposalAsyncTask != null) {
			updateProposalAsyncTask.cancel(true);
			updateProposalAsyncTask = null;
		}
		if (saveTask != null) {
			saveTask.cancel(true);
			saveTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewVideoPlay:
			openSequalVideoFile();
			break;
		case R.id.buttonupdateProposal:
			isAddnew = false;
			addToCart();
			break;
		case R.id.buttonaddNew:
			isAddnew = true;
			addToCart();
			break;
		}

	}

	public void setImageThumbnill() {

		imageLoader.displayImage(mMaterial.mProductImageURL, imgThumbnail,
				options);
	}

	private void openSequalVideoFile() {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.parse(mMaterial.mProductVideoURL);
		intent.setDataAndType(uri, "video/*");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(
					getApplicationContext(),
					getApplicationContext().getResources().getString(
							R.string.no_application), Constants.TOASTMSG_TIME)
					.show();
			e.printStackTrace();
		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return gesturedetector.onTouchEvent(ev);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

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
					showPrice();
				} else {
					// Left Swipe
					showPrice();
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
			}
			return true;
		}
	}

	public void showPrice() {
		if (mShowPrice) {
			mShowPrice = false;
			txtAmount.setText("*******");

		} else {
			mShowPrice = true;
			txtAmount.setText("$" + mMaterial.mUnitSellingPrice);
		}
	}

	public void addToCart() {

		proposalCart = new ProposalCartModel();
		Singleton.getInstance().proposalCartList.clear();
		proposalCart.mDealerId = dealerID;
		proposalCart.mEmployeeId = employeeID;
		proposalCart.mAppointmentResultId = appointmentResultId;
		proposalCart.mMaterialId = mMaterial.mMaterialId;
		proposalCart.mMaterialName = mMaterial.mMaterialName;
		proposalCart.mMaterialThumb = mMaterial.mProductImageURL;
		proposalCart.mQuotedAmount = mMaterial.mUnitSellingPrice;

		if (mMaterial.mProductType != null) {
			Log.d("mMaterial.mProductType ", "" + mMaterial.mProductType);
		}
		proposalCart.mProductType = mMaterial.mProductType;
		proposalCart.mBoundProductId = "0";
		proposalCart.mNotes = editTxtNotes.getText().toString();
		if (!editTxtAmount.getText().toString().equals("0")
				&& !editTxtAmount.getText().toString().isEmpty()) {
			proposalCart.mQuotedAmount = editTxtAmount.getText().toString();
		}
		if (!editTxtQuantity.getText().toString().equals("0")
				&& !editTxtQuantity.getText().toString().isEmpty()) {
			proposalCart.mQuotedQuantity = editTxtQuantity.getText().toString();
			// Singleton.getInstance().proposalCartList.add(proposalCart);
			Log.d("ANONMOFS", proposalCart.mQuotedAmount);
			Singleton.getInstance().proposalCartList.add(proposalCart);
			saveTask = new SaveSummaryAsyncTask(context, saveRequest(),
					isAddnew);
			saveTask.execute();

		} else {
			Toast.makeText(context, "Quantity should not be empty.",
					Toast.LENGTH_LONG).show();
		}
	}

	public JSONObject saveRequest() {

		Log.d("size", Singleton.getInstance().proposalCartList.size() + "");
		JSONArray mRequestJson = new JSONArray();

		JSONObject ProductsData = new JSONObject();
		JSONObject product = new JSONObject();

		for (int l = 0; l < Singleton.getInstance().proposalCartList.size(); l++) {

			JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
				reqObj_data.put(Constants.KEY_APPT_RESULT_ID,
						appointmentResultId);
				reqObj_data
						.put(Constants.JSON_KEY_SALES_PROCESS_MATERIAL_ID,
								Singleton.getInstance().proposalCartList.get(l).mMaterialId);
				reqObj_data
						.put(Constants.QUOTED_QUANTITY,
								Singleton.getInstance().proposalCartList.get(l).mQuotedQuantity);
				reqObj_data
						.put(Constants.QUOTED_AMOUNT,
								Singleton.getInstance().proposalCartList.get(l).mQuotedAmount
										.replace("$", "").replace(",", ""));
				reqObj_data
						.put(Constants.BOUND_PRODUCT_ID,
								Singleton.getInstance().proposalCartList.get(l).mBoundProductId);
				reqObj_data.put(Constants.JSON_KEY_FOLLOWUP_NOTES,
						Singleton.getInstance().proposalCartList.get(l).mNotes);
				reqObj_data
						.put(Constants.JSON_KEY_SALES_PROCESS_PRODUCT_TYPE,
								Singleton.getInstance().proposalCartList.get(l).mProductType);
				if (Singleton.getInstance().proposalCartList.get(l).mProductType != null) {
					Log.d("Value = ",
							""
									+ Singleton.getInstance().proposalCartList
											.get(l).mProductType);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRequestJson.put(reqObj_data);
		}
		try {
			product.put("PRODUCTS", mRequestJson);
			JSONObject reqObj_data = new JSONObject();
			if(Singleton.getInstance().proposalList.size()>0){
				reqObj_data.put(Constants.DESCRIPTION, Singleton.getInstance().proposalList.get(0).getWorkOrderNotes());
			}else{
				reqObj_data.put(Constants.DESCRIPTION, "");
			}
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
			reqObj_data.put(Constants.KEY_APPT_RESULT_ID, appointmentResultId);
			JSONArray mRequestJson2 = new JSONArray();
			mRequestJson2.put(reqObj_data);
			product.put("WORKDESCRIPTION", mRequestJson2);

			ProductsData.put("ProductsData", product);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("res", ProductsData.toString());
		return ProductsData;

	}

	public void setCartListToSharedPrefference(ProposalCartModel proposalCart) {
		settingsEditor = preferenceSettings.edit();

		if (preferenceSettings.contains(appointmentResultId)) {
			mproposalCartList.clear();
			try {

				mproposalCartList = (ArrayList<ProposalCartModel>) mObjetserilizer
						.deserialize(preferenceSettings.getString(
								appointmentResultId,
								mObjetserilizer
										.serialize(new ArrayList<ProposalCartModel>())));

				mproposalCartList.add(proposalCart);

				settingsEditor.putString(appointmentResultId,
						ObjectSerializer.serialize(mproposalCartList));
			}

			catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else if (!preferenceSettings.contains(appointmentResultId)) {
			try {
				mproposalCartList.add(proposalCart);
				settingsEditor.putString(appointmentResultId,
						mObjetserilizer.serialize(mproposalCartList));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		settingsEditor.commit();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Update Proposal Activity");

	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method
	}

	@Override
	protected void onResume() {
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
