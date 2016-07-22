package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class SaveDispoQstnAsyncTask extends AsyncTask<Void, Void, Void> {

	Context cntx;
	JSONObject responseJson;
	ServiceHelper helper;
	ArrayList<JSONObject> reqObj;
	ActivityIndicator pDioalog;
	loadSalesResult loadResult;
	public SaveDispoQstnAsyncTask(Context context,
			ArrayList<JSONObject> arrayList) {
		this.cntx = context;
		this.reqObj = arrayList;
		helper = new ServiceHelper(cntx);
		loadResult = (loadSalesResult)cntx;
	}

	@Override
	protected void onPreExecute() {
		if (pDioalog != null) {
			if (pDioalog.isShowing()) {
				pDioalog.dismiss();
				pDioalog = null;
			} else {
				pDioalog = null;
			}
		}
		pDioalog = new ActivityIndicator(cntx);
		pDioalog.show();
	}

	public interface loadSalesResult
	{
		void loadPostSalesResults();
	}
	@Override
	protected Void doInBackground(Void... params) {
		responseJson = helper.jsonSendHTTPRequest(reqObj.toString(),
				Constants.URL_SAVE_DISPO_QUESTIONNAIRE,
				Constants.REQUEST_TYPE_POST);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (pDioalog != null && pDioalog.isShowing()) {
			pDioalog.dismiss();
			try {
				if(responseJson != null){

					if (responseJson.has(Constants.KEY_SAVE_DISPO)
							& responseJson != null) {
						JSONObject obj = responseJson
								.getJSONObject(Constants.KEY_SAVE_DISPO);
						if (obj.getString(Constants.SAVE_NOTES_STATUS).equals(
								Constants.SAVE_NOTES_SUCCESS)) {
							Toast.makeText(cntx, "Dispo Questionnaire Saved",
									Toast.LENGTH_SHORT).show();
							loadResult.loadPostSalesResults();
						}
					} else {
						Toast.makeText(cntx, "Dispo Questionnaire Not Saved",
								Toast.LENGTH_SHORT).show();
					}
				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		super.onPostExecute(result);
	}

}
