package com.webparadox.bizwizsales.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.Log;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.NewProposalSummaryActivity;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.SalesProcessProductSubCategoryActivity.Action;
import com.webparadox.bizwizsales.asynctasks.GetProposalAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;
import com.webparadox.bizwizsales.models.SpProductSubCategoryModel;

public class SalesProcessProductSubCategoryFragment extends Fragment{
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;
	Context context = null;
	ListView listViewsubCategory;
	SalesProessProductSubListAdapter mCustomAdapter;
	public ArrayList<SpProductSubCatAndMaterialModel> productList = new ArrayList<SpProductSubCatAndMaterialModel>();
	static String value = "";
	static int index = 0;
	private SwipeDetector swipeDetector = null;
	GetProposalAsyncTask getProposalAsyncTask = null;
	
	public static SalesProcessProductSubCategoryFragment newInstance(int index1,String value1) {
		SalesProcessProductSubCategoryFragment f = new SalesProcessProductSubCategoryFragment();
		index = index1;
		value = value1;
		return f;	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		preferenceSettings =context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);

		settingsEditor = preferenceSettings.edit();
		Singleton.getInstance().isShowPrice = true;
		if (preferenceSettings.contains(Constants.PREF_SWITCH_SHOW_PRICE)) {
			Singleton.getInstance().isShowPrice = preferenceSettings
					.getBoolean(Constants.PREF_SWITCH_SHOW_PRICE, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView1 = inflater.inflate(R.layout.activity_sales_process_product_sub_category, container,false);
		RelativeLayout relativeLayout = (RelativeLayout)rootView1.findViewById(R.id.linearLayout1);
		relativeLayout.setVisibility(View.GONE);
		TextView textViewCategoryName = (TextView)rootView1.findViewById(R.id.textViewCategoryName);
		textViewCategoryName.setText(value);
		getProductSubCategoryAndMaterials(index);
		Button button_generate_proposal = (Button)rootView1.findViewById(R.id.button_generate_proposal);
		button_generate_proposal.setVisibility(View.GONE);
		EditText editTxtUserFiter = (EditText)rootView1.findViewById(R.id.editTextfilter);
		editTxtUserFiter.requestFocus();
		editTxtUserFiter.clearFocus();
		editTxtUserFiter.removeTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mCustomAdapter.getFilter().filter(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
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
		listViewsubCategory = (ListView)rootView1.findViewById(R.id.listViewsubCategory);
		mCustomAdapter = new SalesProessProductSubListAdapter(context);
		listViewsubCategory.setAdapter(mCustomAdapter);
		swipeDetector = new SwipeDetector();
		listViewsubCategory.setOnTouchListener(swipeDetector);
		listViewsubCategory.setOnItemClickListener(new OnItemClickListener() {

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
						UpdateProposalFragment f = new UpdateProposalFragment();
						FragmentTransaction ft = getFragmentManager().beginTransaction();
						UpdateProposalFragment.newInstance(productList.get(position).mSubcategoryName,
						productList.get(position).mSubcategoryId,
						productList.get(position).mMaterialName,
						productList.get(position).mMaterialId,
						productList.get(position).mProductDescription,
						productList.get(position).mProductImageURL,
						productList.get(position).mProductVideoURL,
						productList.get(position).mUnitSellingPrice,
						productList.get(position).mProductType);
						ft.replace(R.id.details, f);
						ft.addToBackStack(null);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						ft.commit();
					}
				}

			}
		});
		
		return rootView1;
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
				}
				Material.mProductType = mListSubCategory.get(i).mListMaterials
						.get(j).mProductType;
				Singleton.getInstance().productsSubCatAndMaterialList
						.add(Material);
			}
		}
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

		public SalesProessProductSubListAdapter(Context context) {
			// TODO Auto-generated constructor stub
		//	mIndex = index;
			this.mContext = context;
			droidSans = Typeface.createFromAsset(mContext.getAssets(),
					"DroidSans.ttf");
			droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
					"DroidSans-Bold.ttf");
			imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
			Log.e(""+Singleton.getInstance().productsSubCatAndMaterialList.size());
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
				txt_header_Name.setText(productList.get(position).mSubcategoryName);

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
					txtProposalAmount.setText("$"+ productList.get(position).mUnitSellingPrice);
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

				if (results.count == 0)
					notifyDataSetInvalidated();
				else {
					productList = (ArrayList<SpProductSubCatAndMaterialModel>) results.values;
					notifyDataSetChanged();
				}

			}
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
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (getProposalAsyncTask != null) {
			getProposalAsyncTask.dismissDialog();
			getProposalAsyncTask.cancel(true);
			getProposalAsyncTask = null;
		}
	}
	
}
