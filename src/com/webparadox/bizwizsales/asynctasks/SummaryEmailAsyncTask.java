package com.webparadox.bizwizsales.asynctasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class SummaryEmailAsyncTask extends AsyncTask<Void, Void, Void>{

	Context mContext;
	ActivityIndicator mDialog;
	ServiceHelper serviceHelper;
	JSONArray mreq;
	JSONObject responseJson,mRequestJson;
	String mRequestUrl,dealerId,appoResultId;
	public SummaryEmailAsyncTask(Context context,String dealerId,String resultId){
		this.mContext=context;
		this.dealerId=dealerId;
		this.appoResultId=resultId;
		
	}
	
	@Override
	public void onPreExecute(){
		super.onPreExecute();
		mDialog=new ActivityIndicator(mContext);
		mDialog.show();
		responseJson=new JSONObject();
		mRequestJson=new JSONObject();
		serviceHelper=new ServiceHelper(mContext);
		mreq=new JSONArray();
		try {
			mRequestJson.put("DealerId",dealerId );
			mRequestJson.put("AppointmentResultId",appoResultId );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mreq.put(mRequestJson);

	}
	
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mreq.toString(), Constants.EmailProposalUrl, Constants.REQUEST_TYPE_POST);
		return null;
	}
	
	@Override
	public void onPostExecute(Void result){
		super.onPostExecute(result);
		
		if(responseJson != null){
			
			if(responseJson.has(Constants.EmailProposal)){
				JSONObject obj;
				try {
					obj = responseJson
							.getJSONObject(Constants.EmailProposal);
					if(obj.has(Constants.SAVE_NOTES_STATUS)){
						if (obj.getString(Constants.SAVE_NOTES_STATUS).equals(
								Constants.SAVE_NOTES_SUCCESS)) {
							Toast.makeText(mContext, obj.getString(Constants.SAVE_NOTES_STATUS), Toast.LENGTH_SHORT).show();
							dissmissDismiss();
						}else{
							Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
							dissmissDismiss();
						}
						}else{
							Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
							dissmissDismiss();
						}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
				dissmissDismiss();
			}
		}else{
			Toast.makeText(mContext, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
			dissmissDismiss();
		}
	}
	
	public void dissmissDismiss(){
		if(mDialog !=null){
			if(mDialog.isShowing()){
				mDialog.dismiss();
			}
			mDialog=null;
			}
	}
}
