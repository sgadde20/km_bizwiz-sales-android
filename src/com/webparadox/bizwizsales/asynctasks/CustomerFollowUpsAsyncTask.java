package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.webparadox.bizwizsales.adapter.CustomerFollowupAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerFollowUpsModel;

public class CustomerFollowUpsAsyncTask extends AsyncTask<Void, Integer, Void> {
	ActivityIndicator pDialog;
	Context mContext;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;
	ArrayList<CustomerFollowUpsModel> cusFollowupModelList;
	ListView cusFollowupListview;
	CustomerFollowUpsModel cusFollowupModel;
	CustomerFollowupAdapter customerFollowupAdapter;
	public CustomerFollowUpsAsyncTask(Context context,
			ArrayList<JSONObject> reqData, ListView customerFollowupList) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mRequestJson = reqData;
		this.cusFollowupListview = customerFollowupList;
		serviceHelper = new ServiceHelper(this.mContext);
		cusFollowupModelList = new ArrayList<CustomerFollowUpsModel>();
	}

	@Override
	protected void onPreExecute() {
		Singleton.getInstance().cusFollowupModelList.clear();
		try {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(mContext);
			pDialog.show(); 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), Constants.GET_CUSTOMER_FOLLOWUPS_URL,
				Constants.REQUEST_TYPE_POST);
		if (responseJson.has(Constants.JSON_KEY_CUSTOMER_FOLLOWUPS)) {
			try {
				localJsonArray = responseJson
						.getJSONArray(Constants.JSON_KEY_CUSTOMER_FOLLOWUPS);
				for (int i = 0; i < localJsonArray.length(); i++) {
					JSONObject jobj = localJsonArray.getJSONObject(i);
					cusFollowupModel = new CustomerFollowUpsModel();
					cusFollowupModel.FollowUpId = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_ID).toString();
					cusFollowupModel.FollowupEmployee = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_EMPLOYEE).toString();
					cusFollowupModel.FollowUpDate = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_DATE).toString();
					cusFollowupModel.FollowupTime = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_TIME).toString();
					cusFollowupModel.FollowupNotes = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_NOTES).toString();
					cusFollowupModel.FollowupIsCompleted = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_ISCOMPLETED).toString();
					cusFollowupModel.FollowupResolvedNotes = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_RESOLVED_NOTE)
							.toString();
					cusFollowupModel.FollowupCompletedDate = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_COMPLETED_DATE)
							.toString();
					cusFollowupModel.FollowupCompletedTime = jobj.get(
							Constants.JSON_KEY_FOLLOWUP_COMPLETED_TIME)
							.toString();
					Singleton.getInstance().cusFollowupModelList
							.add(cusFollowupModel);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		if (responseJson != null) {
			if (localJsonArray != null) {
				if (localJsonArray.length() > 0) {
					 customerFollowupAdapter = new CustomerFollowupAdapter(
							mContext);
					cusFollowupListview.setAdapter(customerFollowupAdapter);
					
				} else {
					 customerFollowupAdapter = new CustomerFollowupAdapter(
							mContext);
					cusFollowupListview.setAdapter(customerFollowupAdapter);
				}

			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}

		} else {
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Constants.TOASTMSG_TIME).show();
		}
		dismissDialog();
	}

	public void dismissDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

}
