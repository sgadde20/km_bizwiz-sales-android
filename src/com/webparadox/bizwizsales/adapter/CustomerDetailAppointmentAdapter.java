package com.webparadox.bizwizsales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.models.CustomerDetailsAppointmentsModel;

public class CustomerDetailAppointmentAdapter extends BaseAdapter {

	LayoutInflater layoutInflater;
	Context mContext;
	Typeface droidSans, droidSansBold;

	public CustomerDetailAppointmentAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.layoutInflater = LayoutInflater.from(context);
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().appointmentArray.size();
	}

	@Override
	public CustomerDetailsAppointmentsModel getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().appointmentArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private static class ViewHolder {
		TextView date, time, apptType, leadType, salesRep, appntResult,
				appntSubResult, quoteAmount, textView_quoted;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.customer_appointment_row, null);
			holder.date = (TextView) convertView
					.findViewById(R.id.textView_appt_date);
			holder.time = (TextView) convertView
					.findViewById(R.id.textView_appt_time);
			holder.apptType = (TextView) convertView
					.findViewById(R.id.textview_appt_type);
			holder.leadType = (TextView) convertView
					.findViewById(R.id.textView_lead_type);
			holder.salesRep = (TextView) convertView
					.findViewById(R.id.textView_salesrep);
			holder.appntResult = (TextView) convertView
					.findViewById(R.id.textView_result);
			holder.appntSubResult = (TextView) convertView
					.findViewById(R.id.textView_sub_result);
			holder.quoteAmount = (TextView) convertView
					.findViewById(R.id.textView_quote_amount);
			holder.textView_quoted = (TextView) convertView
					.findViewById(R.id.textView_quoted);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CustomerDetailsAppointmentsModel model = getItem(position);
		holder.textView_quoted.setTypeface(droidSansBold);
		holder.date.setTypeface(droidSansBold);
		holder.time.setTypeface(droidSans);
		holder.apptType.setTypeface(droidSans);
		holder.apptType.setTypeface(droidSans);
		holder.leadType.setTypeface(droidSans);
		holder.salesRep.setTypeface(droidSans);
		holder.appntResult.setTypeface(droidSans);
		holder.appntSubResult.setTypeface(droidSans);
		holder.quoteAmount.setTypeface(droidSans);

		holder.textView_quoted.setText(mContext.getString(R.string.quoted));
		holder.date.setText(model.getFormattedApptDate());
		holder.time.setText(model.getApptTime());
		holder.apptType.setText(model.getAppointmentType());
		holder.leadType.setText(model.getLeadType());
		holder.salesRep.setText(model.getSalesRep());

		if (!mContext.getResources().getBoolean(R.bool.is_tablet)) {
			if (model.getResult().length() > 15) {
				holder.appntResult.setTextSize(8);
				holder.appntResult.setText(model.getResult());
			} else {
				holder.appntResult.setText(model.getResult());
				holder.appntSubResult.setText(model.getSubResult());
			}
			if (model.getSubResult().length() > 15) {
				holder.appntSubResult.setTextSize(8);
				holder.appntSubResult.setText(model.getSubResult());
			} else {
				holder.appntResult.setText(model.getResult());
				holder.appntSubResult.setText(model.getSubResult());
			}
		} else {
			holder.appntResult.setText(model.getResult());
			holder.appntSubResult.setText(model.getSubResult());
		}

		holder.quoteAmount.setText(model.getAmount());

		return convertView;
	}

}
