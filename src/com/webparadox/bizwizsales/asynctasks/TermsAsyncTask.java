package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class TermsAsyncTask extends AsyncTask<Void, Integer, Void> {

	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();
	JSONArray localJsonArray = new JSONArray();
	Context mContext;
	ActivityIndicator dialog;
	String dealId;
	Step2LabelListener step2labelListener;
	
	public interface Step2LabelListener{
		
		public void step2Listener();
	}
	
	public TermsAsyncTask(Context context,String dealId){
		
		this.mContext=context;
		this.dealId=dealId;
		this.step2labelListener=(Step2LabelListener) mContext;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		serviceHelper = new ServiceHelper(mContext);
		responseJson = new JSONObject();
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

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), Constants.DEALER_SETTING_URL + "?"
						+ Constants.SAVE_CALL_DEALERID + "=" + dealId,
				Constants.REQUEST_TYPE_GET);

		return null;
	}

	@Override
	protected void onPostExecute(Void Result) {
		super.onPostExecute(Result);

		if (responseJson != null) {

			if (responseJson.has(Constants.DEALER_SETTING)) {

				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.DEALER_SETTING);
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(0);
						Constants.termString = jobj.getString(Constants.salesAppContractText);
						Constants.setp2labelString = jobj.getString(Constants.step2Label);

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				step2labelListener.step2Listener();
			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(mContext, Constants.TOAST_NO_DATA,
					Toast.LENGTH_SHORT).show();
		}
		dissmissDialog();
	}

	public void dissmissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

	}
}