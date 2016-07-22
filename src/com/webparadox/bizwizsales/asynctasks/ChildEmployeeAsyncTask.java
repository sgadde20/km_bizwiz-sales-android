package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.webparadox.bizwizsales.CalendarActivity;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class ChildEmployeeAsyncTask extends AsyncTask<Void, Void, Void> {
	Context mContext;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	String requestUrl, dealerId, employeeId, employeeName;
	JSONArray localJsonArray;
	ActivityIndicator dialog;

	public ChildEmployeeAsyncTask(Context context, String dealerId2,String employeeId2, String employeeName2) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.dealerId = dealerId2;
		this.employeeId = employeeId2;
		this.employeeName = employeeName2;
		serviceHelper = new ServiceHelper(this.mContext);

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub

		localJsonArray = new JSONArray();
		responseJson = new JSONObject();
		mRequestJson = new ArrayList<JSONObject>();
		requestUrl = Constants.URL_GET_CHILD_EMPLOYEE+ dealerId+"&EmployeeId="+employeeId;

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
		Singleton.getInstance().mChildEmployeeID.clear();
		Singleton.getInstance().mChildEmployeeName.clear();
		if (responseJson != null) {
			dismissDialog();
			if (responseJson.has(Constants.JSON_KEY_CE)) {
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.JSON_KEY_CE);
					Singleton.getInstance().mChildEmployeeID.add(employeeId);
					Singleton.getInstance().mChildEmployeeName.add(employeeName);
					for (int i = 0; i < localJsonArray.length(); i++) {
						
						JSONObject Jsonobj = localJsonArray.getJSONObject(i);
						Singleton.getInstance().mChildEmployeeID.add(Jsonobj.getString(Constants.JSON_KEY_CHILD_EMPLOYEE_ID));
						Singleton.getInstance().mChildEmployeeName.add(Jsonobj.getString(Constants.JSON_KEY_CHILD_EMPLOYEE_NAME));
					}
					if(Singleton.getInstance().mChildEmployeeName.size() > 1){
						CalendarActivity.spinnerChildEmployeeName.setVisibility(View.VISIBLE);
						CalendarActivity.loadChildEmployeeSpinner();
					}else{
						CalendarActivity.spinnerChildEmployeeName.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					CalendarActivity.loadChildEmployeeSpinner();
				}
				Log.d("CAL INTER", "CAL INTE");
			} else {
				CalendarActivity.loadChildEmployeeSpinner();
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}

		} else {
			dismissDialog();
			CalendarActivity.loadChildEmployeeSpinner();
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
