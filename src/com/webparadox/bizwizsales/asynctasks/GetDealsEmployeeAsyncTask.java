package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.dialogs.CreateFollowUpDialog;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.GetEmployeesModel;

public class GetDealsEmployeeAsyncTask extends AsyncTask<Void, Integer, Void>{

	
	Context mContext;
	ActivityIndicator dialog;
	JSONObject responseJson; 
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;
	String DealerId,cusName,cusId,empId;
	CreateFollowUpDialog ceateFollowupDialog;
	public GetDealsEmployeeAsyncTask(Context context, String dealerID, String customerName, String customerId, String employeeID) {
		// TODO Auto-generated constructor stub
		mContext=context;
		serviceHelper = new ServiceHelper(this.mContext);
		this.DealerId=dealerID;
		this.cusName=customerName;
		this.cusId=customerId;
		this.empId=employeeID;
	}
	
	
	
	@Override
	protected void onPreExecute() {
		dismissDialog();
		mRequestJson=new ArrayList<JSONObject>();
		try {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				dialog = null;
			}
			dialog = new ActivityIndicator(mContext);
			dialog.show(); 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), Constants.GET_DEALER_EMPLOYEDD_URL+DealerId, Constants.REQUEST_TYPE_GET);
		return null;
	}
	
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dismissDialog();
		if(responseJson !=null){
			if(responseJson.has(Constants.DEALER_EMPLOYEE)){
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.DEALER_EMPLOYEE);
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(i);
						GetEmployeesModel empsModel=new GetEmployeesModel();
						empsModel.mIds=jobj.getString(Constants.KEY_PHONE_TYPE_ID);
						empsModel.mEmployeeNames=jobj.getString(Constants.KEY_EMPLOYEE_NAME);
						Singleton.getInstance().mEmployeeNames.add(empsModel);
						Log.d("EMP NAME", jobj.getString(Constants.KEY_EMPLOYEE_NAME));
		}
				}catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ceateFollowupDialog=new CreateFollowUpDialog(mContext,cusName,DealerId,cusId,empId);
				ceateFollowupDialog.show();
				
				
			}else{
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(mContext, Constants.TOAST_INTERNET,Toast.LENGTH_SHORT).show();
		}
	
	}
	
	public void dismissDialog(){
		
		if(dialog !=null){
			dialog.dismiss();
			dialog=null;
		}
	}
	
}
