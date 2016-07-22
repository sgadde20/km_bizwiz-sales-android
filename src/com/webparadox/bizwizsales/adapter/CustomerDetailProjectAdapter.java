package com.webparadox.bizwizsales.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.models.CustomerDetailsProjectModel;

public class CustomerDetailProjectAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater layoutInflater;
	Typeface droidSans, droidSansBold;

	public CustomerDetailProjectAdapter(Context applicationContext) {
		this.mContext = applicationContext;
		this.layoutInflater = LayoutInflater.from(applicationContext);
		droidSans = Typeface.createFromAsset(applicationContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(
				applicationContext.getAssets(), "DroidSans-Bold.ttf");
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().projectsArray.size();
	}

	@Override
	public CustomerDetailsProjectModel getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().projectsArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	private static class ViewHolder {
		TextView id, startDate, endDate, apptType, Foreman, CancelReason,
				ProjectAmount, balanceAmount, duo, sold, textView_install_days,
				soldText,salemanText;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = layoutInflater.inflate(R.layout.customer_project_row,
					null);

			holder.id = (TextView) convertView
					.findViewById(R.id.textView_project_id);
			holder.duo = (TextView) convertView.findViewById(R.id.textView_due);
			holder.startDate = (TextView) convertView
					.findViewById(R.id.textView_install_date);
			holder.endDate = (TextView) convertView
					.findViewById(R.id.textView_install_date1);
			holder.textView_install_days = (TextView) convertView
					.findViewById(R.id.textView_install_days);

			holder.apptType = (TextView) convertView
					.findViewById(R.id.textview_project_type);
			holder.Foreman = (TextView) convertView
					.findViewById(R.id.textView_foremnan);
			holder.salemanText = (TextView) convertView
					.findViewById(R.id.textView_salesman);
			holder.ProjectAmount = (TextView) convertView
					.findViewById(R.id.textView_project_amount);
			holder.sold = (TextView) convertView
					.findViewById(R.id.textView_proj_balance);

			holder.balanceAmount = (TextView) convertView
					.findViewById(R.id.textView_due);
			holder.soldText = (TextView) convertView
					.findViewById(R.id.textView_proj_sold);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CustomerDetailsProjectModel model = getItem(position);

		holder.duo.setTypeface(droidSansBold);
		holder.startDate.setTypeface(droidSansBold);
		holder.endDate.setTypeface(droidSansBold);
		holder.textView_install_days.setTypeface(droidSans);
		holder.apptType.setTypeface(droidSansBold);
		holder.id.setTypeface(droidSansBold);
		holder.Foreman.setTypeface(droidSansBold);
		holder.ProjectAmount.setTypeface(droidSans);
		holder.sold.setTypeface(droidSans);
		holder.balanceAmount.setTypeface(droidSansBold);
		holder.soldText.setTypeface(droidSansBold);
		holder.salemanText.setTypeface(droidSansBold);
		holder.textView_install_days.setTypeface(droidSans);
			holder.textView_install_days.setText("           "+model.getInstallDays());
		holder.id.setText(model.getProjectId());

		holder.startDate.setTextColor(mContext.getResources().getColor(R.color.black));
		String startdate = model.getStartDate();
		String first = "<font color='#356185'>Start : </font>";
		holder.startDate.setText(Html.fromHtml(first + startdate));
		holder.endDate.setTextColor(mContext.getResources().getColor(R.color.black));
		String enddate = model.getEndDate();
		String end = "<font color='#356185'>End      : </font>";
		holder.endDate.setText(Html.fromHtml(end + enddate));
		holder.apptType.setText(model.getProjectType());
		holder.Foreman.setTextColor(mContext.getResources().getColor(R.color.black));
		String foreman = model.getForeman();
		String f = "<font color='#356185'>F: </font>";
		holder.Foreman.setText(Html.fromHtml(f + foreman));
		holder.salemanText.setTextColor(mContext.getResources().getColor(R.color.black));
		String saleman = model.getSalesMan();
		String s = "<font color='#356185'>S: </font>";
		holder.salemanText.setText(Html.fromHtml(s + saleman));
		holder.ProjectAmount.setText(" " + model.getProjectAmount());
		holder.sold.setText(" " + model.getBalanceDue());
		return convertView;
	}

}
