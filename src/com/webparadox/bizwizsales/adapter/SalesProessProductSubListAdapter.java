package com.webparadox.bizwizsales.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;

public class SalesProessProductSubListAdapter extends BaseAdapter {
	int mIndex;
	public static Context mContext;
	LayoutInflater listinflate, listinflateHead;
	Typeface droidSans, droidSansBold;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	public static GestureDetector gesturedetector = null;
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageOnFail(R.drawable.ic_error)
			.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	public SalesProessProductSubListAdapter(Context context, int index) {
		// TODO Auto-generated constructor stub
		mIndex = index;
		this.mContext = context;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().productsSubCatAndMaterialList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().productsSubCatAndMaterialList
				.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (Singleton.getInstance().productsSubCatAndMaterialList.get(position).mIsHeader) {
			listinflateHead = (LayoutInflater) mContext
					.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			convertView = listinflateHead.inflate(
					R.layout.products_subcategory_list_header, parent, false);

			TextView txt_header_Name = (TextView) convertView
					.findViewById(R.id.textViewHeaderSubCatName);
			txt_header_Name.setTypeface(droidSans);
			txt_header_Name
					.setText(Singleton.getInstance().productsSubCatAndMaterialList
							.get(position).mSubcategoryName);

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
						+ Singleton.getInstance().productsSubCatAndMaterialList
								.get(position).mUnitSellingPrice);
			} else {
				txtProposalAmount.setText("*******");
			}
			txtMaterialName
					.setText(Singleton.getInstance().productsSubCatAndMaterialList
							.get(position).mMaterialName);

			convertView.setTag("isContent");
			imageLoader.displayImage(
					Singleton.getInstance().productsSubCatAndMaterialList
							.get(position).mProductImageURL, imgMaterialView,
					options);

		}

		return convertView;
	}

}
