package com.webparadox.bizwizsales.adapter;

import java.util.ArrayList;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerFollowUpsModel;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomerFollowupAdapter extends BaseAdapter implements OnClickListener{

	Context mContext;
	LayoutInflater cusFollowupInfulate;
	Typeface droidSans, droidSansBold;
	ArrayList<CustomerFollowUpsModel> cusFollowupModelList=new ArrayList<CustomerFollowUpsModel>();
	public CustomerFollowupAdapter(Context mContext2) {
		// TODO Auto-generated constructor stub
		clearAll();
		this.mContext=mContext2;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(
				mContext.getAssets(), "DroidSans-Bold.ttf");
		cusFollowupModelList=Singleton.getInstance().cusFollowupModelList;
		this.notifyDataSetChanged();
		this.notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cusFollowupModelList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return cusFollowupModelList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		cusFollowupInfulate = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = cusFollowupInfulate.inflate(
				R.layout.customer_followup_adapter,
				parent, false);
		LinearLayout followupLayout=(LinearLayout)convertView.findViewById(R.id.followup_layout);
		LinearLayout resloveLayout=(LinearLayout)convertView.findViewById(R.id.resolve_layout);
		TextView customerFollowupDate=(TextView)convertView.findViewById(R.id.cus_followup_date);
		customerFollowupDate.setTypeface(droidSansBold);
		TextView customerFollowupTime=(TextView)convertView.findViewById(R.id.cus_followup_time);
		customerFollowupTime.setTypeface(droidSans);
		TextView customerFollowupNote=(TextView)convertView.findViewById(R.id.cus_followup_note);
		customerFollowupNote.setTypeface(droidSans);
		
		TextView customerFollowupEmployee = (TextView)convertView.findViewById(R.id.cus_followup_name);
		customerFollowupEmployee.setTypeface(droidSans);
		
		TextView customerFollowupResolve=(TextView)convertView.findViewById(R.id.cus_followup_resolve);
		customerFollowupResolve.setTypeface(droidSansBold);
		TextView customerFollowupResolveDate=(TextView)convertView.findViewById(R.id.cus_followup_resolve_date);
		customerFollowupResolveDate.setTypeface(droidSans);
		followupLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_myhotquotes_row_blue));
		customerFollowupDate.setText(cusFollowupModelList.get(position).FollowUpDate);
		customerFollowupTime.setText(cusFollowupModelList.get(position).FollowupTime);
		customerFollowupEmployee.setText(cusFollowupModelList.get(position).FollowupEmployee);
		customerFollowupNote.setText(cusFollowupModelList.get(position).FollowupNotes);
		customerFollowupResolveDate.setText(cusFollowupModelList.get(position).FollowupCompletedDate);
		resloveLayout.setVisibility(View.INVISIBLE);
		customerFollowupDate.setTextColor(mContext.getResources().getColor(R.color.white));
		customerFollowupTime.setTextColor(mContext.getResources().getColor(R.color.white));
		customerFollowupNote.setTextColor(mContext.getResources().getColor(R.color.white));
		customerFollowupEmployee.setTextColor(mContext.getResources().getColor(R.color.white));

		if(cusFollowupModelList.get(position).FollowupIsCompleted.equals(Constants.COMPLETED)){
			followupLayout.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selector_calendar_list_content));
			customerFollowupDate.setTextColor(mContext.getResources().getColor(R.color.black));
			customerFollowupTime.setTextColor(mContext.getResources().getColor(R.color.black));
			customerFollowupNote.setTextColor(mContext.getResources().getColor(R.color.black));
			resloveLayout.setVisibility(View.VISIBLE);
		}
		
		
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		switch (v.getId()) {
		

		default:
			break;
		}
		
	}
	public void clearAll(){
		this.mContext=null;
		droidSans =null;
		droidSansBold = null;
		cusFollowupModelList.clear();
	}

}
