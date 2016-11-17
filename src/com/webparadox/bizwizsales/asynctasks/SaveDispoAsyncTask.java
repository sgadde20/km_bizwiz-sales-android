package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SaveDispoAsyncTask extends AsyncTask<Void, Void, Void> {

	Context cntx;
	JSONObject responseJson;
	ServiceHelper helper;
	ArrayList<JSONObject> objList;
	ActivityIndicator pDialog;
	DispoQuestionnaireAsyncTask dispoAsyncTask;
	String dealerId, appntId, eventId, employeeId, dispoId, appointmentType;

	public SaveDispoAsyncTask(Context context, ArrayList<JSONObject> reqArrPro,
			String dealerId, String appntResultId, String apppointmentType, String dispoId) {
		this.cntx = context;
		this.objList = reqArrPro;
		this.dealerId = dealerId;
		this.appntId = appntResultId;
		this.appointmentType = appointmentType;
		this.dispoId = dispoId;
		helper = new ServiceHelper(cntx);
	}

	@Override
	protected void onPreExecute() {
		if (pDialog != null) {
			if (pDialog.isShowing()) {
				pDialog.dismiss();
				pDialog = null;
			} else {
				pDialog = null;
			}
		}
		pDialog = new ActivityIndicator(cntx);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		responseJson = helper.jsonSendHTTPRequest(objList.toString(),
				Constants.URL_SAVE_DISPO, Constants.REQUEST_TYPE_POST);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		try {
			if(responseJson != null){

				if (responseJson.has(Constants.KEY_SAVE_DISPO)
						&& responseJson != null) {
					JSONObject obj = responseJson
							.getJSONObject(Constants.KEY_SAVE_DISPO);
					if (obj.getString(Constants.SAVE_NOTES_STATUS).equals(
							Constants.SAVE_NOTES_SUCCESS)) {
						Toast.makeText(cntx, "Appointment Results Saved",
								Toast.LENGTH_SHORT).show();
						dispoAsyncTask = new DispoQuestionnaireAsyncTask(cntx,
								dealerId, appntId, dispoId, appointmentType);
						dispoAsyncTask.execute();
					} else {
						Toast.makeText(cntx, "Appointment Results Not Saved",
								Toast.LENGTH_SHORT).show();
					}
				}
			
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		super.onPostExecute(result);
	}

}
