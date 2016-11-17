package com.webparadox.bizwizsales.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.phoneModel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class PhonenumberListAdapter extends BaseAdapter{
	public Context mContext;
	LayoutInflater listinflate;
	public ArrayList<phoneModel>mPhoneData=new ArrayList<phoneModel>();

	Dialog mDialog;
	String cusID;
//	public static final String TAG = "YOUR-TAG-NAME";
	public PhonenumberListAdapter(Context context,ArrayList<phoneModel> arrData, Dialog dialog, String cusID) {
		super();
		// TODO Auto-generated constructor stub
		this.mContext=context;
		this.mPhoneData=arrData;
		this.mDialog=dialog;
		this.cusID=cusID;
	}

	@Override
	public int getCount() {
	//	Log.d(TAG,"Error");
		// TODO Auto-generated method stub
		return mPhoneData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	} 

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		listinflate = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		convertView = listinflate
				.inflate(R.layout.phonenumber_listview_adapter, parent, false);
		TextView type = (TextView) convertView
				.findViewById(R.id.type);
		TextView phone = (TextView) convertView
				.findViewById(R.id.phone);
		TextView notes = (TextView) convertView
				.findViewById(R.id.notes);
		Button button_call=(Button)convertView.findViewById(R.id.button_phone_call);
		button_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissAlert();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm aa");
				final String startTime = sdf.format(new Date());
				Singleton.getInstance().setStartTime(startTime);
				Singleton.getInstance().setCustomerId(cusID);
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"+mPhoneData.get(position).mPhone));
				((Activity) mContext).startActivityForResult(callIntent, Constants.REQUEST_CALLED);	
			}
		});
		Log.d("array model",""+mPhoneData.get(position).mPhone);

		type.setText(mPhoneData.get(position).mTypeName.toUpperCase());
		phone.setText(mPhoneData.get(position).mPhone);
		notes.setText(mPhoneData.get(position).mnotes);
		return convertView;
	}
public void dismissAlert(){
		
		if(mDialog !=null){
			mDialog.dismiss();
			mDialog=null;
		}
		
	}
}
