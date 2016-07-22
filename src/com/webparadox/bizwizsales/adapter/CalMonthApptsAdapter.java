package com.webparadox.bizwizsales.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.asynctasks.PhonenumbersAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;

public class CalMonthApptsAdapter extends BaseAdapter {
	// ArrayList<CalendarListPaginationModel> mList;
	public static Context mContext;
	LayoutInflater listinflate;
	String mSelDate, dealerID;
	SharedPreferences userData;
	public PhonenumbersAsyncTask phoneTask;

	public CalMonthApptsAdapter(Context context, String selDate,
			ArrayList<CalendarListPaginationModel> list) {
		// TODO Auto-generated constructor stub
		// this.mList = list;
		
		
		CalMonthApptsAdapter.mContext = context;
		this.mSelDate = selDate;
		userData = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		ArrayList<CalendarListPaginationModel> test = new ArrayList<CalendarListPaginationModel>();
		for (CalendarListPaginationModel calendarListPaginationModel :list) {
			if (!calendarListPaginationModel.mEventType.equals("isHeader")) {
				test.add(calendarListPaginationModel);
			}
		}
		Singleton.getInstance().selectedDayApptsData.clear();
		Singleton.getInstance().selectedDayApptsData = test;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().selectedDayApptsData.size();
		// return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().selectedDayApptsData.get(position);
		// return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// if (mList.get(position).mFormattedApptDate.equals(mSelDate))
		if (Singleton.getInstance().selectedDayApptsData.get(position).mFormattedApptDate
				.equals(mSelDate)) {
			listinflate = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = listinflate.inflate(R.layout.calendar_month_view_row,
					parent, false);
			final TextView eventType = (TextView) convertView
					.findViewById(R.id.textViewApptTitle);
			final TextView eventDate = (TextView) convertView
					.findViewById(R.id.textViewApptTime);
			TextView name = (TextView) convertView
					.findViewById(R.id.textViewName);
			TextView address1 = (TextView) convertView
					.findViewById(R.id.textViewAddress1);
			TextView address2 = (TextView) convertView
					.findViewById(R.id.textViewAddress2);
			final TextView notes = (TextView) convertView
					.findViewById(R.id.textViewNotes);
			ImageView imageIcon = (ImageView) convertView
					.findViewById(R.id.imageViewApptSeprator);
			ImageView imageCall = (ImageView) convertView
					.findViewById(R.id.imageViewCall);
			TextView leadOrVisitType = (TextView) convertView
					.findViewById(R.id.textView1);

			imageCall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					final JSONObject reqObj_data = new JSONObject();
					final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
					try {
						reqObj_data
								.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
						// reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID,
						// (mList.get(position).mCustomerId));
						reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID,
								(Singleton.getInstance().selectedDayApptsData
										.get(position).mCustomerId));

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

			int selCircleIc = R.drawable.shape_circle_green;

			// if (mList.get(position).mEventType.equals("FOLLOW-UP"))
			if (Singleton.getInstance().selectedDayApptsData.get(position).mEventType
					.equals("FOLLOW-UP")) {
				selCircleIc = R.drawable.shape_circle_blue;
			}
			else if (Singleton.getInstance().selectedDayApptsData.get(position).mEventType
					.equals("CALENDAR-NOTES")
					|| Singleton.getInstance().selectedDayApptsData
							.get(position).mEventType.equals("NOTES"))

			{
				selCircleIc = R.drawable.shape_circle_orange;
				name.setVisibility(View.GONE);
				address1.setVisibility(View.GONE);
				address2.setVisibility(View.GONE);
				notes.setVisibility(View.VISIBLE);
				imageCall.setVisibility(View.INVISIBLE);
				leadOrVisitType.setVisibility(View.INVISIBLE);
			}

			imageIcon.setBackgroundResource(selCircleIc);
			eventType.setText(Singleton.getInstance().selectedDayApptsData
					.get(position).mEventType);
			eventDate.setText(Singleton.getInstance().selectedDayApptsData
					.get(position).mApptTime);
			notes.setText(Singleton.getInstance().selectedDayApptsData
					.get(position).mEventNotes);
			name.setText(Singleton.getInstance().selectedDayApptsData
					.get(position).mCustomerName);
			address1.setText(Singleton.getInstance().selectedDayApptsData
					.get(position).mAddress);
			address2.setText(Singleton.getInstance().selectedDayApptsData
					.get(position).mCity
					+ ","
					+ Singleton.getInstance().selectedDayApptsData
							.get(position).mState
					+ ","
					+ Singleton.getInstance().selectedDayApptsData
							.get(position).mZip);

			leadOrVisitType
					.setText(Singleton.getInstance().selectedDayApptsData
							.get(position).mLeadOrVisitType);

		}

		return convertView;
	}

}
