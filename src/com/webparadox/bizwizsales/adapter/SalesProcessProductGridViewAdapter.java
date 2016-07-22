package com.webparadox.bizwizsales.adapter;

import java.util.ArrayList;

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
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;

public class SalesProcessProductGridViewAdapter extends BaseAdapter {
	   private Context mContext;
	   Typeface droidSans, droidSansBold;
	   LayoutInflater inflater;
	  
	   protected ImageLoader imageLoader = ImageLoader.getInstance();
		public static GestureDetector gesturedetector = null;
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_error)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		ArrayList<SpProductSubCatAndMaterialModel> mRecentProductsList=new ArrayList<SpProductSubCatAndMaterialModel>();
		boolean isRecentProduct;
	   public SalesProcessProductGridViewAdapter(Context Context, ArrayList<SpProductSubCatAndMaterialModel> RecentProductsList, boolean isLoadResentProducts) {
		// TODO Auto-generated constructor stub
		   mContext=Context;
		   droidSans = Typeface.createFromAsset(mContext.getAssets(),
					"DroidSans.ttf");
			droidSansBold = Typeface.createFromAsset(
					mContext.getAssets(), "DroidSans-Bold.ttf");
			if(imageLoader != null){
				imageLoader.destroy();
				imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
			}else{
				imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
			}
			
			isRecentProduct=isLoadResentProducts;
			mRecentProductsList=RecentProductsList;
	}
	  
	public SalesProcessProductGridViewAdapter(Context Context) {
		// TODO Auto-generated constructor stub
		  mContext=Context;
		   droidSans = Typeface.createFromAsset(mContext.getAssets(),
					"DroidSans.ttf");
			droidSansBold = Typeface.createFromAsset(
					mContext.getAssets(), "DroidSans-Bold.ttf");
//			imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
	}
	public int getCount() {
		if (isRecentProduct) {
			 return mRecentProductsList.size();
		}else {
			return Singleton.getInstance().salesProcessProductsList.size();
		}
	      
	   }

	   public Object getItem(int position) {
	      return null;
	   }

	   public long getItemId(int position) {
	      return 0;
	   }

	   // create a new ImageView for each item referenced by the Adapter
	   public View getView(int position, View convertView, ViewGroup parent) {
		    inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    if (isRecentProduct) {
		    	convertView = inflater.inflate(
						R.layout.sales_process_recent_product_row,
						parent, false);
		    }else {
		    	convertView = inflater.inflate(
						R.layout.sales_process_product_category_grid_row,
						parent, false);
		    }
					TextView txtProductName = (TextView) convertView
							.findViewById(R.id.textViewProductName);
					ImageView imgProduct = (ImageView) convertView
							.findViewById(R.id.imageViewCatProduct);
					
					txtProductName.setTypeface(droidSansBold);
					if (isRecentProduct) {
						txtProductName.setText(mRecentProductsList.get(position).mMaterialName);
						
						imageLoader.displayImage(mRecentProductsList.get(position).mProductImageURL, imgProduct, options);
					}else {
						txtProductName.setText(Singleton.getInstance().salesProcessProductsList.get(position).mCategoryName);
						
						imageLoader.displayImage(Singleton.getInstance().salesProcessProductsList.get(position).mCatgeoryImageURL, imgProduct, options);
					}
					
				
				return convertView;
	   }

	 
	}
