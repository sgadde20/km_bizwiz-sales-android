package com.webparadox.bizwizsales.adapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.EditProposalActivity;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;

public class SmartSummaryAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mLayout;
	Typeface droidSans, droidSansBold;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageOnFail(R.drawable.ic_error)
			.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
			.cacheOnDisk(true).considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();
	double amount = 0;

	public SmartSummaryAdapter(Context context) {
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
		return Singleton.getInstance().quotedProductModel.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().quotedProductModel.get(position).BoundproductId;
	}

	@Override
	public int getViewTypeCount() {

		if (getCount() != 0)
			return getCount();

		return 1;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int initialposition, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		convertView = null;
		if (convertView == null) {
			holder = new ViewHolder();
			mLayout = (LayoutInflater) mContext
					.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			convertView = mLayout.inflate(R.layout.proposal_summary_adapter,
					parent, false);

			holder.summaryName = (TextView) convertView
					.findViewById(R.id.summary_name);
			holder.summaryNote = (TextView) convertView
					.findViewById(R.id.summary_note);
			
			
			holder.summaryAmount = (EditText) convertView
					.findViewById(R.id.summary_amount);
			holder.summaryAmount.setFocusable(false);
			holder.summaryImage = (ImageView) convertView
					.findViewById(R.id.summary_image);
			holder.summaryQtyEd = (EditText) convertView
					.findViewById(R.id.summary_qty_count);
			holder.summaryQtyEd.setFocusable(false);
			holder.summaryQty = (TextView) convertView
					.findViewById(R.id.summary_qty);
			holder.summaryAmount.setTag(initialposition);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.summaryQty.setTypeface(droidSans);
		holder.summaryQtyEd.setTypeface(droidSans);
		holder.summaryNote.setTypeface(droidSans);
		final int position = (Integer) holder.summaryAmount.getTag();
		Log.e("quotedProductModel ===> ", ""+Singleton.getInstance().quotedProductModel.size());
		if (Singleton.getInstance().quotedProductModel.get(position).BoundProductNotes
				.length() > 0) {
			holder.summaryNote.setVisibility(View.VISIBLE);
			holder.summaryNote
					.setText(Singleton.getInstance().quotedProductModel
							.get(position).BoundProductNotes);
		} else {
			holder.summaryNote.setVisibility(View.GONE);
		}
		holder.summaryName.setText(Singleton.getInstance().quotedProductModel
				.get(position).ProductQuoted);
		holder.summaryName.setTypeface(droidSansBold);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

		String amountValue = addDollerSymbol(Singleton.getInstance().quotedProductModel
								.get(position).QuotedAmount.replace("$", ""));
		holder.summaryAmount
		.setText(amountValue);
		
		holder.summaryAmount.setId(position);
		holder.summaryAmount.setTypeface(droidSansBold);
		imageLoader
				.displayImage(Singleton.getInstance().quotedProductModel
						.get(position).ProductImageURL, holder.summaryImage,
						options);
		holder.summaryQtyEd.setId(position);
		
		holder.summaryQtyEd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				Intent editProposalActivity=new Intent(mContext,EditProposalActivity.class);
				editProposalActivity.putExtra("Position",position);
				editProposalActivity.putExtra("From", "cusAppo");
				mContext.startActivity(editProposalActivity);
				
			}
		});
holder.summaryAmount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				Intent editProposalActivity=new Intent(mContext,EditProposalActivity.class);
				editProposalActivity.putExtra("Position",position);
				editProposalActivity.putExtra("From", "cusAppo");
				mContext.startActivity(editProposalActivity);
				
			}
		});
		
		
		holder.summaryQtyEd.setText(Singleton.getInstance().quotedProductModel
				.get(position).Quantity);
		return convertView;
	}

	public String addDollerSymbol(String value) {
		if (Double.parseDouble(value.replace(",", "").replace("-", "")) < 1) {
			NumberFormat formatter = new DecimalFormat("#0.00");
			value = formatter.format(Double.parseDouble(value));
			value = "$" + value;
		}else{
			if (value.contains("-")) {
				
				double amount = Double.parseDouble(value.substring(1, value.length()).replace("," , "").replace("$", ""));
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				String formatted = formatter.format(amount);
				
				value = value.subSequence(0, 1) + "$"
						+ formatted;
			} else {
				double amount = Double.parseDouble(value.replace("," , "").replace("$", ""));
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				String formatted = formatter.format(amount);
				
				value = "$" + formatted;
			}
		}
		
		return value;
	}
	
	class ViewHolder {
		public TextView summaryName;
		public EditText summaryAmount;
		public ImageView summaryImage;
		public EditText summaryQtyEd;
		public TextView summaryQty;
		public TextView summaryNote;
	}

}
