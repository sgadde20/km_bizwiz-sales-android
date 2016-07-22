package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.CustomerAppointmentsActivity;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.FinancingCompanyModel;

public class FinancingCompanyAsyncTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	String requestUrl, dealerId;
	JSONArray localJsonArray;
	FinancingCompanyModel mfinancingCompanyModel;
	ActivityIndicator dialog;

	public FinancingCompanyAsyncTask(Context context, String dealerId2) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.dealerId = dealerId2;
		serviceHelper = new ServiceHelper(this.mContext);

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub

		localJsonArray = new JSONArray();
		responseJson = new JSONObject();
		mRequestJson = new ArrayList<JSONObject>();
		requestUrl = Constants.URL_GET_FINANCING_COMPANY + dealerId;

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
		Singleton.getInstance().mfinancingCompanyId.clear();
		Singleton.getInstance().mfinancingCompanyName.clear();
		if (responseJson != null) {
			dismissDialog();
			if (responseJson.has(Constants.JSON_KEY_FC)) {
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.JSON_KEY_FC);
					Singleton.getInstance().mfinancingCompanyId.add("XXXX");
					Singleton.getInstance().mfinancingCompanyName.add("Choose Financing Company");
					for (int i = 0; i < localJsonArray.length(); i++) {
						mfinancingCompanyModel = new FinancingCompanyModel();
						JSONObject Jsonobj = localJsonArray.getJSONObject(i);
						Singleton.getInstance().mfinancingCompanyId.add(Jsonobj
								.getString(Constants.KEY_COMPANY_TYPE_ID));
						Singleton.getInstance().mfinancingCompanyName.add(Jsonobj
								.getString(Constants.JSON_KEY_FINANCING_COMPANY_NAME));
					}
					if(Singleton.getInstance().mfinancingCompanyId != null && Singleton.getInstance().mfinancingCompanyName.size() > 0){
						CustomerAppointmentsActivity.loadFinancongCompanySpinner();
					}
					

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					CustomerAppointmentsActivity.loadFinancongCompanySpinner();
				}
				Log.d("CAL INTER", "CAL INTE");
			} else {
				CustomerAppointmentsActivity.loadFinancongCompanySpinner();
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}

		} else {
			dismissDialog();
			CustomerAppointmentsActivity.loadFinancongCompanySpinner();
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
