package com.webparadox.bizwizsales.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.asynctasks.PhonenumbersAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.MyHotQuotesModel;

public class MyHotQuotesAdater extends BaseAdapter {

	Context mContext;
	LayoutInflater inflater;
	Typeface droidSans, droidSansBold;
	String mSelDate, dealerID;
	SharedPreferences userData;
	MyHotQuotesModel model;
	public PhonenumbersAsyncTask phoneTask; 
	
	public MyHotQuotesAdater(Context applicationContext) {
		// TODO Auto-generated constructor stub
		this.mContext = applicationContext;
		inflater = (LayoutInflater) applicationContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		droidSans = Typeface.createFromAsset(applicationContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(
				applicationContext.getAssets(), "DroidSans-Bold.ttf");
		userData = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().hotQuotesList.size();
	}

	@Override
	public MyHotQuotesModel getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().hotQuotesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public static class ViewHolder {
		public TextView textName;
		public TextView textAddress;
		public TextView textDate;
		public TextView textAppts;
		public TextView textJobs;
		public TextView textFollowUps;
		public LinearLayout layout_MyHotQuotes;
		public ImageView imageCall;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.myhotquotes_list_row, null);
			holder.textName = (TextView) convertView
					.findViewById(R.id.textView1);
			holder.textAddress = (TextView) convertView
					.findViewById(R.id.textView2);
			holder.textDate = (TextView) convertView
					.findViewById(R.id.textView3);
			holder.textAppts = (TextView) convertView
					.findViewById(R.id.textView4);
			holder.textJobs = (TextView) convertView
					.findViewById(R.id.textView5);
			holder.textFollowUps = (TextView) convertView
					.findViewById(R.id.textView6);
			holder.layout_MyHotQuotes = (LinearLayout) convertView
					.findViewById(R.id.layout_MyHotQuotes);
			holder.imageCall = (ImageView) convertView
					.findViewById(R.id.imageViewCall);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		model = getItem(position);
		String followUpsCount = model.getFollowups();
		if (Integer.valueOf(followUpsCount) != 0) {
			holder.layout_MyHotQuotes.setBackground(mContext.getResources()
					.getDrawable(R.drawable.shape_myhotquotes_row_blue));
			holder.textName.setTextColor(Color.WHITE);
			holder.textAddress.setTextColor(Color.WHITE);
			holder.textDate.setTextColor(Color.WHITE);
			holder.textAppts.setTextColor(Color.WHITE);
			holder.textJobs.setTextColor(Color.WHITE);
			holder.textFollowUps.setTextColor(Color.WHITE);
		} else {
			holder.layout_MyHotQuotes.setBackground(mContext.getResources()
					.getDrawable(R.drawable.shape_myhotquotes_row_grew));
			holder.textName.setTextColor(Color.BLACK);
			holder.textAddress.setTextColor(Color.BLACK);
			holder.textDate.setTextColor(Color.BLACK);
			holder.textAppts.setTextColor(Color.BLACK);
			holder.textJobs.setTextColor(Color.BLACK);
			holder.textFollowUps.setTextColor(Color.BLACK);
		}
		holder.textName.setTypeface(droidSans);
		holder.textAppts.setTypeface(droidSans);
		holder.textJobs.setTypeface(droidSans);
		holder.textFollowUps.setTypeface(droidSans);
		holder.textAddress.setTypeface(droidSans);
		holder.textDate.setTypeface(droidSans);
		holder.textName.setText(model.getCustomerFullName());
		holder.textAddress.setText(model.getCity() + ", " + model.getState()
				+ ", " + model.getZip());
		holder.textDate.setText("Date Last Event: " + model.getDateLastEvent());
		holder.textAppts.setText("(" + model.getAppts() + ")" + "\t" + "Appts");
		holder.textJobs.setText("(" + model.getJobs() + ")" + "\t" + "Jobs");
		holder.textFollowUps.setText("(" + model.getFollowups() + ")" + "\t"
				+ "Followups");

		holder.imageCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				model = getItem(position);
				final JSONObject reqObj_data = new JSONObject();
				final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
				try {
					reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
					reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID,
							model.getCustomerId());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reqData.add(reqObj_data);
				phoneTask = new PhonenumbersAsyncTask(mContext,
						Constants.GET_PHONENUMBER_URL,
						Constants.REQUEST_TYPE_POST, reqData);
				phoneTask.execute();
			}
		});

		return convertView;
	}
}
