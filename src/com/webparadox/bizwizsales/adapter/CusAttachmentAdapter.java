package com.webparadox.bizwizsales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.imageloader.ImageLoader;

public class CusAttachmentAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mLayout;
	Typeface droidSans, droidSansBold;
	int value;

	public CusAttachmentAdapter(Context context, int value) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.value = value;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().cusAttachmentModel.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().cusAttachmentModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (value == 0) {
			mLayout = (LayoutInflater) mContext
					.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			convertView = mLayout.inflate(R.layout.customer_attachment_adapter,
					parent, false);

			TextView cusAttachmentDescription = (TextView) convertView
					.findViewById(R.id.cus_attachment_description);
			ImageView cusAttachmentImage = (ImageView) convertView
					.findViewById(R.id.cus_attachment_image);
			cusAttachmentDescription.setTypeface(droidSansBold);
			String attachmentURL = Singleton.getInstance().cusAttachmentModel
					.get(position).attachmentURL;

			if (attachmentURL.endsWith(".JPG") || attachmentURL.endsWith(".jpg")) {
				ImageLoader imgLoader = new ImageLoader(mContext, 2);
				imgLoader.DisplayImage(
						Singleton.getInstance().cusAttachmentModel
								.get(position).attachmentURL,
						R.drawable.loading_indicator, cusAttachmentImage);
			} else if (attachmentURL.endsWith(".pdf") || attachmentURL.endsWith(".PDF")) {
				cusAttachmentImage.setBackgroundResource(R.drawable.pdf);
			} else if (attachmentURL.endsWith(".doc") || attachmentURL.endsWith(".DOC")) {
				cusAttachmentImage.setBackgroundResource(R.drawable.word);
			} else if (attachmentURL.endsWith(".ppt") || attachmentURL.endsWith(".PPT")) {
				cusAttachmentImage
						.setBackgroundResource(R.drawable.power_point);
			} else {
				cusAttachmentImage.setBackgroundResource(R.drawable.no_image);
			}
			cusAttachmentDescription
					.setText(Singleton.getInstance().cusAttachmentModel
							.get(position).attachmentDescription);
		} else {
			if (Singleton.getInstance().cusAttachmentModel.get(position).attachemnttype
					.trim().equalsIgnoreCase("Picture")) {
				mLayout = (LayoutInflater) mContext
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
				convertView = mLayout.inflate(
						R.layout.customer_attachment_adapter, parent, false);

				TextView cusAttachmentDescription = (TextView) convertView
						.findViewById(R.id.cus_attachment_description);
				ImageView cusAttachmentImage = (ImageView) convertView
						.findViewById(R.id.cus_attachment_image);
				cusAttachmentDescription.setTypeface(droidSansBold);
				String attachmentURL = Singleton.getInstance().cusAttachmentModel
						.get(position).attachmentURL;

				if (attachmentURL.endsWith(".JPG") || attachmentURL.endsWith(".jpg")) {
					ImageLoader imgLoader = new ImageLoader(mContext, 2);
					imgLoader.DisplayImage(
							Singleton.getInstance().cusAttachmentModel
									.get(position).attachmentURL,
							R.drawable.loading_indicator, cusAttachmentImage);
				} else if (attachmentURL.endsWith(".pdf") || attachmentURL.endsWith(".PDF")) {
					cusAttachmentImage.setBackgroundResource(R.drawable.pdf);
				} else if (attachmentURL.endsWith(".doc") || attachmentURL.endsWith(".DOC")) {
					cusAttachmentImage.setBackgroundResource(R.drawable.word);
				} else if (attachmentURL.endsWith(".ppt") || attachmentURL.endsWith(".PPT")) {
					cusAttachmentImage
							.setBackgroundResource(R.drawable.power_point);
				} else {
					cusAttachmentImage
							.setBackgroundResource(R.drawable.no_image);
				}
				cusAttachmentDescription
						.setText(Singleton.getInstance().cusAttachmentModel
								.get(position).attachmentDescription);
			}else{
				mLayout = (LayoutInflater) mContext
						.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
				convertView = mLayout.inflate(R.layout.customer_attachment_adapter,
						parent, false);
			}
		}
		return convertView;
	}

}
