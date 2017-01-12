package com.webparadox.bizwizsales.dialogs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.adapter.PhonenumberListAdapter;
import com.webparadox.bizwizsales.asynctasks.CheckUserAccessAsynctask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerAttachmentModel;
import com.webparadox.bizwizsales.models.phoneModel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhonenumbersDialog {

	Dialog dialog;
	TextView textview;
	Context mContext;
	ArrayList<phoneModel> mCustomerPhoneData = new ArrayList<phoneModel>();
	ArrayList<CustomerAttachmentModel> mcustomerData = new ArrayList<CustomerAttachmentModel>();
	String cusID, dealerId;
	ActivityIndicator pDialog;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	SharedPreferences userData;
	JSONObject localJsonObject;
	Typeface droidSansBold;
	String name, companyname;
	String employeeId;

	CustomerAttachmentModel cusAttachmentModel;

//	public static final String TAG = "YOUR-TAG-NAME";
	
	

	public void showPhonenumberDialog(Context context,
			final ArrayList<phoneModel> PhoneData, String cusId,ArrayList<CustomerAttachmentModel> customerData) {
		mContext = context;
		this.cusID = cusId;
		this.mCustomerPhoneData = PhoneData;
		this.mcustomerData=customerData;
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		
		dialog = new Dialog(mContext);
		textview = new TextView(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(WMLP);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.phonenumber_listview);
		
		TextView name = (TextView) dialog.findViewById(R.id.cus_name);
		TextView companyname = (TextView) dialog.findViewById(R.id.company_name);
	
		
		
		if(mcustomerData!=null)
		{
			if(mcustomerData.size()>=1)
			{
			name.setText(""+mcustomerData.get(0).customerName);
			companyname.setText(""+mcustomerData.get(0).companyName);
			}
		}
		
		userData = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		responseJson = new JSONObject();
		mRequestJson = new ArrayList<JSONObject>();
		RelativeLayout phonenumbersLayout = (RelativeLayout) dialog
				.findViewById(R.id.phonenumber_layout);
	    TextView noNumberTextview = (TextView) dialog
				.findViewById(R.id.no_number_textview);
		noNumberTextview.setTypeface(droidSansBold);
		
		//Check user access
		CheckUserAccessAsynctask cuaTask = new CheckUserAccessAsynctask(context, dealerId, employeeId);
		cuaTask.execute();
	
		int size3 = (int) mContext.getResources().getDimension(R.dimen.eighty);
		int five = (int) mContext.getResources().getDimension(R.dimen.five);
		int ten = (int) mContext.getResources().getDimension(R.dimen.ten);
		int forty = (int) mContext.getResources().getDimension(R.dimen.forty);
		int sixty = (int) mContext.getResources().getDimension(R.dimen.seventy);
	
		Button phoneNumberAddBtn = (Button) dialog
				.findViewById(R.id.phonenumber_add_btn);
		ListView listitem = (ListView) dialog
				.findViewById(R.id.phonenumber_listview);
		
		
		// phoneNumberAddBtn.setTypeface(droidSansBold);
		noNumberTextview.setVisibility(View.GONE);
		
		
		listitem.setVisibility(View.VISIBLE);
		if (mCustomerPhoneData.size() < 4) {
		
			if (mCustomerPhoneData.size() == 0) {
				//Log.d(TAG,"Error");
				noNumberTextview.setVisibility(View.VISIBLE);
				
				listitem.setVisibility(View.GONE);
				
				RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT, forty
								+ (sixty + (ten + ten)));
				layout_description.setMargins(five, ten, five, five);
				phonenumbersLayout.setLayoutParams(layout_description);
			
			} else {
				
			    RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.MATCH_PARENT,
						(mCustomerPhoneData.size() * size3)
								+ (sixty + (ten + ten)));
				layout_description.setMargins(five, ten, five, five);
				
				phonenumbersLayout.setLayoutParams(layout_description);
				
			}

		} else {
		
			RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, (3 * size3)
							+ (sixty + (ten + ten + ten)));
			layout_description.setMargins(five, ten, five, five);
			phonenumbersLayout.setLayoutParams(layout_description);
			
		}
		Button cancel = (Button) dialog
				.findViewById(R.id.button_phonenumber_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissAlert();
			}
		});
		phoneNumberAddBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismissAlert();
				if (Singleton.getInstance().phoneTypeArray.size() > 0) {
					AddPhonenumberDialog addDialog = new AddPhonenumberDialog(
							mContext, cusID);
					addDialog.AddPhoneNumber();
				} else {
					getPhonetypeTask getTask = new getPhonetypeTask();
					getTask.execute();
				}

			}
		});

		listitem.setAdapter(new PhonenumberListAdapter(mContext,
				mCustomerPhoneData, dialog, cusID));
		
		listitem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				dismissAlert();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm aa");
				final String startTime = sdf.format(new Date());
				Singleton.getInstance().setStartTime(startTime);
				Singleton.getInstance().setCustomerId(cusID);
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:"
						+ mCustomerPhoneData.get(position).mPhone));
				((Activity) mContext).startActivityForResult(callIntent,
						Constants.REQUEST_CALLED);

			}
		});
		
		
		dialog.show();
		
	}

	
	public void dismissAlert() {

		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

	}

	public class getPhonetypeTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				pDialog = null;
			}

			pDialog = new ActivityIndicator(mContext);
			pDialog.show();
			serviceHelper = new ServiceHelper(mContext);

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.GET_PHONETYPE_URL
							+ dealerId, Constants.REQUEST_TYPE_GET);

			Log.d("UEL", Constants.GET_DEALER_EMPLOYEDD_URL + dealerId);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			try {
				Log.d("Orientation = ", "Orientation ...");
				if (responseJson != null) {
					if (responseJson.has(Constants.KEY_PHONE_TYPE_RESPONSE)) {
						JSONArray localJsonArray;
						try {
							localJsonArray = responseJson
									.getJSONArray(Constants.KEY_PHONE_TYPE_RESPONSE);

							for (int i = 0; i < localJsonArray.length(); i++) {

								try {
									localJsonObject = localJsonArray
											.getJSONObject(i);
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (localJsonObject
										.has(Constants.KEY_PHONE_TYPE_ID)) {
									Singleton.getInstance().phoneTypeIdArray
											.add(localJsonObject
													.getString(
															Constants.KEY_PHONE_TYPE_ID)
													.toString());
									Singleton.getInstance().phoneTypeArray
											.add(localJsonObject
													.getString(
															Constants.KEY_PHONE_TYPE_PHONE_TYPE)
													.toString());
								} else {
									Toast.makeText(mContext,
											Constants.TOAST_CONNECTION_ERROR,
											Constants.TOASTMSG_TIME).show();
								}
							}
						} //end try 
						catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						AddPhonenumberDialog addDialog = new AddPhonenumberDialog(
								mContext, cusID);
						addDialog.AddPhoneNumber();

					} //end first if
					
					
				} else {
					Toast.makeText(mContext, Constants.TOAST_INTERNET,
							Constants.TOASTMSG_TIME).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
			try {
				if (pDialog != null) {
					if (pDialog.isShowing()) {
						pDialog.dismiss();
					}
					pDialog = null;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		Log.d("Orientation = ", "Orientation Changed");
	}
}
