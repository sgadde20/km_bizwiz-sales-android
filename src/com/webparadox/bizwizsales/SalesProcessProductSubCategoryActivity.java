package com.webparadox.bizwizsales;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.asynctasks.GetProposalAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;
import com.webparadox.bizwizsales.models.SpProductSubCategoryModel;

public class SalesProcessProductSubCategoryActivity extends Activity implements
		OnClickListener {
	ImageView imageViewBack, logoBtn;
	TextView txtCategoryName;
	ImageView imgFilter;

	ListView listViewSubCategory;
	EditText editTxtUserFiter;
	public SalesProessProductSubListAdapter mCustomAdapter;
	int index;
	String strCatName, dealerID, employeeID, appointmentResultId;
	Typeface droidSans, droidSansBold;
	SearchView salesProcessSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	SharedPreferences userData;
	Context context;
	private SwipeDetector swipeDetector = null;
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;
	Button btnGenerateProposal;
	GetProposalAsyncTask getProposalAsyncTask;
	public ArrayList<SpProductSubCatAndMaterialModel> productList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_process_product_sub_category);
		context = this;
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,
				"");
		preferenceSettings = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);

		settingsEditor = preferenceSettings.edit();
		Singleton.getInstance().isShowPrice = true;
		if (preferenceSettings.contains(Constants.PREF_SWITCH_SHOW_PRICE)) {
			Singleton.getInstance().isShowPrice = preferenceSettings
					.getBoolean(Constants.PREF_SWITCH_SHOW_PRICE, false);
		}
		Bundle extras = getIntent().getExtras();
		index = extras.getInt("CatIndex");
		strCatName = extras.getString("CategoryName");
		getProductSubCategoryAndMaterials(index);
		txtCategoryName = (TextView) findViewById(R.id.textViewCategoryName);
		imgFilter = (ImageView) findViewById(R.id.imageViewFilter);
		txtCategoryName.setTypeface(droidSans);
		txtCategoryName.setText(strCatName);
		editTxtUserFiter = (EditText) findViewById(R.id.editTextfilter);
		editTxtUserFiter.requestFocus();
		editTxtUserFiter.clearFocus();
		listViewSubCategory = (ListView) findViewById(R.id.listViewsubCategory);
		mCustomAdapter = new SalesProessProductSubListAdapter(this, index);
		listViewSubCategory.setAdapter(mCustomAdapter);
		btnGenerateProposal = (Button) findViewById(R.id.button_generate_proposal);
		btnGenerateProposal.setOnClickListener(this);
		salesProcessSearchView = (SearchView) findViewById(R.id.sales_process_searchView);

		salesProcessSearchView
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
		swipeDetector = new SwipeDetector();
		listViewSubCategory.setOnTouchListener(swipeDetector);
		listViewSubCategory.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (swipeDetector.swipeDetected()) {
					if (swipeDetector.getAction() == Action.RL) {
						if (Singleton.getInstance().isShowPrice) {
							Singleton.getInstance().isShowPrice = false;
							mCustomAdapter.notifyDataSetChanged();
						} else {
							Singleton.getInstance().isShowPrice = true;
							mCustomAdapter.notifyDataSetChanged();
						}
					} else if (swipeDetector.getAction() == Action.LR) {
						if (Singleton.getInstance().isShowPrice) {
							Singleton.getInstance().isShowPrice = false;
							mCustomAdapter.notifyDataSetChanged();
						} else {
							Singleton.getInstance().isShowPrice = true;
							mCustomAdapter.notifyDataSetChanged();
						}
					}
				} else {
					if (!productList.get(position).mIsHeader) {
						Intent intentUpdateProposal = new Intent(
								getApplicationContext(),
								UpdateProposalActivity.class);
						intentUpdateProposal.putExtra("SubCatName",
								productList.get(position).mSubcategoryName);
						intentUpdateProposal.putExtra("SubCatId",
								productList.get(position).mSubcategoryId);
						intentUpdateProposal.putExtra("MaterialName",
								productList.get(position).mMaterialName);
						intentUpdateProposal.putExtra("MaterialId",
								productList.get(position).mMaterialId);
						intentUpdateProposal.putExtra("ProductDescription",
								productList.get(position).mProductDescription);
						intentUpdateProposal.putExtra("ProductImageURL",
								productList.get(position).mProductImageURL);
						intentUpdateProposal.putExtra("ProductVideoURL",
								productList.get(position).mProductVideoURL);
						intentUpdateProposal.putExtra("UnitSellingPrice",
								productList.get(position).mUnitSellingPrice);
						intentUpdateProposal.putExtra("ProductType",
								productList.get(position).mProductType);
						startActivity(intentUpdateProposal);
					}
				}

			}
		});
		imgFilter.setOnClickListener(this);

		imageViewBack = (ImageView) findViewById(R.id.back_icon);
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
				Intent intentHome = new Intent(
						SalesProcessProductSubCategoryActivity.this,
						MainActivity.class);
				startActivity(intentHome);
				finish();
			}
		});

		editTxtUserFiter.removeTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mCustomAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		editTxtUserFiter.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				mCustomAdapter.getFilter().filter(s.toString());

			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();

		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this,
				"Sales Process Product Sub Category Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	private void getProductSubCategoryAndMaterials(int index2) {
		// TODO Auto-generated method stub
		ArrayList<SpProductSubCategoryModel> mListSubCategory = Singleton
				.getInstance().salesProcessProductsList.get(index2).mListSubCategory;
		Singleton.getInstance().productsSubCatAndMaterialList.clear();
		for (int i = 0; i < mListSubCategory.size(); i++) {
			SpProductSubCatAndMaterialModel subCat = new SpProductSubCatAndMaterialModel();
			subCat.mIsHeader = true;
			subCat.mSubcategoryId = mListSubCategory.get(i).mSubcategoryId;
			subCat.mSubcategoryName = mListSubCategory.get(i).mSubcategoryName;
			Singleton.getInstance().productsSubCatAndMaterialList.add(subCat);
			for (int j = 0; j < mListSubCategory.get(i).mListMaterials.size(); j++) {
				SpProductSubCatAndMaterialModel Material = new SpProductSubCatAndMaterialModel();
				Material.mIsHeader = false;
				Material.mMaterialId = mListSubCategory.get(i).mListMaterials
						.get(j).mMaterialId;
				Material.mMaterialName = mListSubCategory.get(i).mListMaterials
						.get(j).mMaterialName;
				Material.mProductDescription = mListSubCategory.get(i).mListMaterials
						.get(j).mProductDescription;
				Material.mProductImageURL = mListSubCategory.get(i).mListMaterials
						.get(j).mProductImageURL;
				Material.mProductVideoURL = mListSubCategory.get(i).mListMaterials
						.get(j).mProductVideoURL;
				Material.mUnitSellingPrice = mListSubCategory.get(i).mListMaterials
						.get(j).mUnitSellingPrice;
				if (mListSubCategory.get(i).mListMaterials.get(j).mProductType != null) {
					Log.d("Value = ",
							""
									+ mListSubCategory.get(i).mListMaterials
											.get(j).mProductType);
				}
				Material.mProductType = mListSubCategory.get(i).mListMaterials
						.get(j).mProductType;
				Singleton.getInstance().productsSubCatAndMaterialList
						.add(Material);
			}
		}
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
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (getProposalAsyncTask != null) {
			getProposalAsyncTask.cancel(true);
			getProposalAsyncTask = null;
		}

	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.imageViewFilter:

			break;
		case R.id.button_generate_proposal:
			getProposalAsyncTask = new GetProposalAsyncTask(context, dealerID,
					appointmentResultId, true);
			getProposalAsyncTask.execute();
			break;
		}

	}

	class SwipeDetector implements View.OnTouchListener {

		private static final int MIN_DISTANCE = 100;
		private float downX, downY, upX, upY;
		private Action mSwipeDetected = Action.None;

		public boolean swipeDetected() {
			return mSwipeDetected != Action.None;
		}

		public Action getAction() {
			return mSwipeDetected;
		}

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				downX = event.getX();
				downY = event.getY();
				mSwipeDetected = Action.None;
				return false;
			}
			case MotionEvent.ACTION_MOVE: {
				upX = event.getX();
				upY = event.getY();

				float deltaX = downX - upX;
				float deltaY = downY - upY;

				// horizontal swipe detection
				if (Math.abs(deltaX) > MIN_DISTANCE) {
					// left or right
					if (deltaX < 0) {

						mSwipeDetected = Action.LR;
						return true;
					}
					if (deltaX > 0) {

						mSwipeDetected = Action.RL;
						return true;
					}
				} else

				// vertical swipe detection
				if (Math.abs(deltaY) > MIN_DISTANCE) {
					// top or down
					if (deltaY < 0) {

						mSwipeDetected = Action.TB;
						return false;
					}
					if (deltaY > 0) {

						mSwipeDetected = Action.BT;
						return false;
					}
				}
				return true;
			}
			}
			return false;
		}
	}

	public static enum Action {
		LR, RL, TB, BT, None
	}

	public class SalesProessProductSubListAdapter extends BaseAdapter {
		int mIndex;
		public Context mContext;
		LayoutInflater listinflate, listinflateHead;
		Typeface droidSans, droidSansBold;
		protected ImageLoader imageLoader = ImageLoader.getInstance();
		public GestureDetector gesturedetector = null;
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.ic_error)
				.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		private ProductFilter filter;
		private ArrayList<SpProductSubCatAndMaterialModel> originalList;

		public SalesProessProductSubListAdapter(Context context, int index) {
			// TODO Auto-generated constructor stub
			mIndex = index;
			this.mContext = context;
			droidSans = Typeface.createFromAsset(mContext.getAssets(),
					"DroidSans.ttf");
			droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
					"DroidSans-Bold.ttf");
			imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
			productList = Singleton.getInstance().productsSubCatAndMaterialList;
			originalList = productList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return productList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return productList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public ProductFilter getFilter() {
			if (filter == null) {
				filter = new ProductFilter();
			}
			return filter;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			if (productList.get(position).mIsHeader) {
				listinflateHead = (LayoutInflater) mContext
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
				convertView = listinflateHead.inflate(
						R.layout.products_subcategory_list_header, parent,
						false);

				TextView txt_header_Name = (TextView) convertView
						.findViewById(R.id.textViewHeaderSubCatName);
				txt_header_Name.setTypeface(droidSans);
				txt_header_Name
						.setText(productList.get(position).mSubcategoryName);

				convertView.setTag("isHeader");

			} else {
				listinflate = (LayoutInflater) mContext
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
				convertView = listinflate.inflate(
						R.layout.prouduct_subcategory_list_row, parent, false);

				ImageView imgMaterialView = (ImageView) convertView
						.findViewById(R.id.imageViewMaterial);
				TextView txtMaterialName = (TextView) convertView
						.findViewById(R.id.textViewMaterialName);
				TextView txtProposalAmount = (TextView) convertView
						.findViewById(R.id.textViewProposalAmount);

				txtMaterialName.setTypeface(droidSans);
				txtProposalAmount.setTypeface(droidSans);

				if (Singleton.getInstance().isShowPrice) {
					txtProposalAmount.setText("$"
							+ productList.get(position).mUnitSellingPrice);
				} else {
					txtProposalAmount.setText("*******");
				}
				txtMaterialName
						.setText(productList.get(position).mMaterialName);

				convertView.setTag("isContent");
				imageLoader.displayImage(
						productList.get(position).mProductImageURL,
						imgMaterialView, options);

			}

			return convertView;
		}

		private class ProductFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				// We implement here the filter logic
				if (constraint == null || constraint.length() == 0) {

					results.values = originalList;
					results.count = originalList.size();
				} else {
					// We perform filtering operation
					ArrayList<SpProductSubCatAndMaterialModel> nProductlist = new ArrayList<SpProductSubCatAndMaterialModel>();

					for (SpProductSubCatAndMaterialModel p : productList) {
						
						String[] splitValue = p.mMaterialName.split("\\s+");
						for(int j=0;j<splitValue.length;j++){
							if (splitValue[j].toUpperCase().startsWith(
									constraint.toString().toUpperCase()))
								nProductlist.add(p);
						}
						
					}

					results.values = nProductlist;
					results.count = nProductlist.size();

				}
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {

				// Now we have to inform the adapter about the new list filtered

				if (results.count == 0)
					notifyDataSetInvalidated();
				else {
					productList = (ArrayList<SpProductSubCatAndMaterialModel>) results.values;
					notifyDataSetChanged();
				}

			}
		}
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
	    	Intent intent = new Intent(this,NewProposalSummaryActivity.class);
			startActivity(intent);
			finish();
	    } 
	}
	
}
