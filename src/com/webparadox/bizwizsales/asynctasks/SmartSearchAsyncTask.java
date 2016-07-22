package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.webparadox.bizwizsales.CustomerDetailsActivity;
import com.webparadox.bizwizsales.SmartSearchActivity;
import com.webparadox.bizwizsales.adapter.SmartSearchAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SmartSearchModel;

public class SmartSearchAsyncTask extends AsyncTask<Void, integer, Void>{

	Context mContext;
	ActivityIndicator dialog;
	JSONObject responseJson;
	JSONArray localJsonArray;
	ArrayList<JSONObject> mRequestJson;
	String mRequestUrl,dealerID,employeeID,searchText;
	ServiceHelper serviceHelper;
	ListView smartSearchListview;
	boolean isSamerSeart=false;
	
	public SmartSearchAsyncTask(Context context,String dealerId, String empId, String searchText){
		
		this.mContext=context;
		this.dealerID=dealerId;
		this.employeeID=empId;
		this.searchText=searchText;
		this.isSamerSeart=false;
		serviceHelper = new ServiceHelper(this.mContext);
	}
	
	
	public SmartSearchAsyncTask(Context applicationContext, String dealerId2,
			String empId, String query, boolean b, ListView smartSearchListview2) {
		// TODO Auto-generated constructor stub
		this.mContext=applicationContext;
		this.dealerID=dealerId2;
		this.employeeID=empId;
		this.searchText=query;
		this.isSamerSeart=b;
		this.smartSearchListview=smartSearchListview2;
		serviceHelper = new ServiceHelper(this.mContext);
	}


	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dismissDialog();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			} else {
				dialog = null;
			}
		}
		dialog = new ActivityIndicator(mContext);
		dialog.show();
		responseJson=new JSONObject();
		localJsonArray=new JSONArray();
		mRequestJson=new ArrayList<JSONObject>();
		Singleton.getInstance().smartSearchModel.clear();
		mRequestUrl=Constants.SMART_SEARCH_URL+"?"+"DealerId="+dealerID+"&"+"EmployeeId="+employeeID+"&"+"SearchText="+searchText.replaceAll(" ", "%20");
		super.onPreExecute();
	}
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), mRequestUrl, Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if(responseJson != null){
			
			if(responseJson.has(Constants.SS)){
				
				if (responseJson.length() != 0) {
					try {
						localJsonArray = responseJson
								.getJSONArray(Constants.SS);
						if(localJsonArray.length() != 0){
							for (int i = 0; i < localJsonArray.length(); i++) {
								SmartSearchModel smartSearchModel = new SmartSearchModel();
								JSONObject jobj = localJsonArray
										.getJSONObject(i);
								smartSearchModel.CustomerId=jobj.getString(Constants.JSON_KEY_CUSTOMER_ID);
								smartSearchModel.CustomerFullName=jobj.getString(Constants.KEY_MYQUOTES_CUSTOMER_NAME);
								smartSearchModel.Address=jobj.getString(Constants.JSON_KEY_ADDRESS);
								smartSearchModel.City=jobj.getString(Constants.JSON_KEY_CITY);
								smartSearchModel.State=jobj.getString(Constants.JSON_KEY_STATE);
								smartSearchModel.Zip=jobj.getString(Constants.JSON_KEY_ZIP);
								Singleton.getInstance().smartSearchModel.add(smartSearchModel);
								
							}
						}else{
							Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
						}
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(Singleton.getInstance().smartSearchModel.size() !=0){
						if(Singleton.getInstance().smartSearchModel.size()==1){
							
							dismissDialog();
							Bundle bundle = new Bundle();
							bundle.putString("CustomerFullName",
									Singleton.getInstance().smartSearchModel.get(0).CustomerFullName);
							bundle.putString(
									"CustomerAddress",
									Singleton.getInstance().smartSearchModel.get(0).Address
											+ ", "
											+ Singleton.getInstance().smartSearchModel.get(0).City
											+ ", "
											+ Singleton.getInstance().smartSearchModel.get(0).State
											+ ", "
											+ Singleton.getInstance().smartSearchModel.get(0).Zip);
							bundle.putString("CustomerId",
									Singleton.getInstance().smartSearchModel.get(0).CustomerId);
							Intent customerDetailsIntent = new Intent(mContext, CustomerDetailsActivity.class);
							customerDetailsIntent.putExtras(bundle);
							if(isSamerSeart){
								mContext.startActivity(customerDetailsIntent);
								((Activity) mContext).finish();
							}else{
								mContext.startActivity(customerDetailsIntent);
							}
							
							
						}else{
							if (isSamerSeart) {
								SmartSearchAdapter searchAdapter=new SmartSearchAdapter(mContext);
								smartSearchListview.setAdapter(searchAdapter);
							}else{
								Intent smartSearchIntent=new Intent(mContext,SmartSearchActivity.class);
								mContext.startActivity(smartSearchIntent);
							}
							
							
						}
					}else{
						Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
					}
					
					
					
					
				}else{
					Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
				}
				
			}else{
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
			}
			
		}else{
			Toast.makeText(mContext, Constants.TOAST_INTERNET, Toast.LENGTH_SHORT).show();
		}
		dismissDialog();
	}
	
	
	public void dismissDialog(){
		Log.d("Entered dismiss","Entered dismiss");
		if(dialog !=null){
			dialog.dismiss();
			dialog=null;
			Log.d("Dialog dismiss","Dialog dismiss");
		}
	}
}
