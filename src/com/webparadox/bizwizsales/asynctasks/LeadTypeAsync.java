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
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.AppointmentTypeModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;

public class LeadTypeAsync extends AsyncTask<Void, Void, Void> {
	Context mContext;
	JSONObject responseJson, localJsonArray;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	String requestUrl, dealerId;
	JSONArray localInnerJsonArray1, localInnerJsonArray2;
	LeadTypeModel leadTypeModel;
	AppointmentTypeModel appointmentTypeModel;
	ActivityIndicator dialog;

	public LeadTypeAsync(Context context, String dealerId2) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.dealerId = dealerId2;
		serviceHelper = new ServiceHelper(this.mContext);

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		localInnerJsonArray1 = new JSONArray();
		localInnerJsonArray2 = new JSONArray();
		localJsonArray = new JSONObject();
		responseJson = new JSONObject();
		mRequestJson = new ArrayList<JSONObject>();
		requestUrl = Constants.QUESTIONNAIRE_CONFIG + "?" + "DealerId="
				+ dealerId;
		Singleton.getInstance().appointmentModel.clear();
		Singleton.getInstance().leadTypeModel.clear();
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
		super.onPreExecute();
	}

	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper
				.jsonSendHTTPRequest(mRequestJson.toString(), requestUrl,
						Constants.REQUEST_TYPE_GET);
		return null;
	}

	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (responseJson != null) {
			if (responseJson.has(Constants.QC)) {
				try {
					localJsonArray = responseJson.getJSONObject(Constants.QC);

					localInnerJsonArray1 = localJsonArray
							.getJSONArray(Constants.APPOINTMENT_TYPE);

					if (localInnerJsonArray1.length() > 0) {
						for (int i = 0; i < localInnerJsonArray1.length(); i++) {
							appointmentTypeModel = new AppointmentTypeModel();
							JSONObject obj1 = localInnerJsonArray1
									.getJSONObject(i);
							appointmentTypeModel.setId(obj1
									.getString(Constants.ID));
							appointmentTypeModel.setTypeName(obj1
									.getString(Constants.TYPENAME));
							Singleton.getInstance().appointmentModel
									.add(appointmentTypeModel);
						}

					}

					localInnerJsonArray2 = localJsonArray
							.getJSONArray(Constants.LEAD_TYPE);

					if (localInnerJsonArray2.length() > 0) {
						for (int j = 0; j < localInnerJsonArray2.length(); j++) {
							leadTypeModel = new LeadTypeModel();
							JSONObject obj2 = localInnerJsonArray2
									.getJSONObject(j);
							leadTypeModel.setId(obj2.getString(Constants.ID));
							leadTypeModel.setTypeName(obj2
									.getString(Constants.TYPENAME));
							Singleton.getInstance().leadTypeModel
									.add(leadTypeModel);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				dismissDialog();
				Log.d("CAL INTER", "CAL INTE");
			} else {
				dismissDialog();
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}

		} else {
			dismissDialog();
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Toast.LENGTH_SHORT).show();
		}

	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
