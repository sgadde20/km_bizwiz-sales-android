package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class SavePhoneNumberAsynctask extends AsyncTask<Void, Integer, Void>{

	Context mContext;
	ActivityIndicator dialog;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;
	String delearId,empId,customerId,callStartTime,callEndTime;
	ListView cusAttachmentListview;
	TextView cusAttachmentTextview;
	public SavePhoneNumberAsynctask(Context context, String dealerId,String empId, String cusId, String callStartTime, String callEndTime) {
		this.mContext=context;
		this.delearId=dealerId;
		this.empId=empId;
		this.customerId=cusId;
		this.callStartTime=callStartTime;
		this.callEndTime=callEndTime;
		serviceHelper = new ServiceHelper(this.mContext);
	}

	@Override
	protected void onPreExecute() {
		responseJson=new JSONObject();
		mRequestJson = new ArrayList<JSONObject>();
		final JSONObject reqObj_data = new JSONObject();
		try {
			reqObj_data.put(Constants.SAVE_CALL_DEALERID, delearId);
			reqObj_data.put(Constants.SAVE_CALL_EMPLOYEEID, empId);
			reqObj_data.put(Constants.SAVE_CALL_CUSTOMERID, customerId);
			reqObj_data.put(Constants.SAVE_CALL_STARTDATETIME, callStartTime);
			reqObj_data.put(Constants.SAVE_CALL_ENDDATETIME, callEndTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRequestJson.add(reqObj_data);
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			} else {
				dialog = null;
			}
		}
		dialog = new ActivityIndicator(mContext);
		dialog.setLoadingText("Loading....");
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), Constants.SAVE_PHONE_STATE,
				Constants.REQUEST_TYPE_POST);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dissmissDialog();
		Log.e("Save Call responseJson ===> ", ""+responseJson);
		if(responseJson != null){
			if (responseJson.has(Constants.SAVE_CALL_STATUS)) {
				
			}else{
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(mContext, Constants.TOAST_INTERNET, Toast.LENGTH_SHORT).show();
		}
	}

	public void dissmissDialog(){
		if(dialog !=null){
			dialog.dismiss();
			dialog = null;
		}
	}
}
