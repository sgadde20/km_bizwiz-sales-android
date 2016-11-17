package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

import android.content.Context;
import android.os.AsyncTask;

public class AppointmentResultIdAsyncTask extends AsyncTask<Void, Void, Void> {
	Context cntx;
	String dealerId, appntId, eventId, employeeId, appointmentType, dispoId;
	ActivityIndicator pDialog;
	ServiceHelper helper;
	JSONObject responseJson;
	DispoQuestionnaireAsyncTask dispoAsyncTask;
	String appointmentResultId = "";
	SaveDispoAsyncTask saveDispoAsyncTask;
	JSONObject reqObjList;
	ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();

	public AppointmentResultIdAsyncTask(Context context, JSONObject obj,
			String dealerId, String appointmentId, String eventId,
			String employeeId, String dispoId2, String appointmentType) {
		this.cntx = context;
		this.dealerId = dealerId;
		this.appntId = appointmentId;
		this.employeeId = employeeId;
		this.eventId = eventId;
		this.dispoId = dispoId2;
		this.appointmentType = appointmentType;
		this.reqObjList = obj;
		helper = new ServiceHelper(cntx);
	}

	public AppointmentResultIdAsyncTask(Context context, String dealerID2,
			String appointmentId, String eventId2, String employeeID2) {
		this.cntx = context;
		this.dealerId = dealerID2;
		this.appntId =appointmentId;
		this.eventId = eventId2;
		this.employeeId=employeeID2;
		helper = new ServiceHelper(cntx);
	}

	@Override
	protected void onPreExecute() {
		try {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(cntx);
			pDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		responseJson = helper.jsonSendHTTPRequest("",
				Constants.URL_GET_APPOINTMENT_RESULT_ID + dealerId
						+ "&AppointmentId=" + appntId + "&EventId=" + eventId
						+ "&EmployeeId=" + employeeId,
				Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (pDialog != null && pDialog.isShowing()) {
			pDialog.dismiss();
			try {
				if(responseJson != null){
					if (responseJson.has(Constants.KEY_APPNT_RESULT)
							& responseJson != null) {
						JSONObject obj = responseJson
								.getJSONObject(Constants.KEY_APPNT_RESULT);
						appointmentResultId = obj.getString(Constants.ID);
						if (appointmentResultId != null) {
							reqObjList.put(Constants.KEY_APPT_RESULT_ID,
									appointmentResultId);
							reqArrayList.add(reqObjList);
							saveDispoAsyncTask = new SaveDispoAsyncTask(cntx,
									reqArrayList, dealerId, appointmentType, appointmentResultId,
									dispoId);
							saveDispoAsyncTask.execute();
						}
					}
				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
