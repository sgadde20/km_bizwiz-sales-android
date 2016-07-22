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

public class SmartSearchAdapter extends BaseAdapter{

	LayoutInflater inflater;
	Context mContext;
	Typeface droidSans, droidSansBold;
	public SmartSearchAdapter(Context context){
		
		this.mContext = context;
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	
		droidSans = Typeface.createFromAsset(mContext.getAssets(),"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(), "DroidSans-Bold.ttf");
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Singleton.getInstance().smartSearchModel.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Singleton.getInstance().smartSearchModel.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public static class ViewHolder {
		public TextView Name;
		public TextView Address;
		public TextView city;
		public TextView state;
		public TextView zip;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.smart_search_adapter, null);
			holder.Name = (TextView) convertView
					.findViewById(R.id.smart_search_name);
			holder.Address = (TextView) convertView
					.findViewById(R.id.smart_search_address);
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.Name.setTypeface(droidSans);
		holder.Address.setTypeface(droidSans);
		holder.Name.setText(Singleton.getInstance().smartSearchModel.get(position).CustomerFullName);
		holder.Address.setText(Singleton.getInstance().smartSearchModel.get(position).City + ", " + Singleton.getInstance().smartSearchModel.get(position).State
				+ ", " + Singleton.getInstance().smartSearchModel.get(position).Zip);
		return convertView;
	}
	

}
