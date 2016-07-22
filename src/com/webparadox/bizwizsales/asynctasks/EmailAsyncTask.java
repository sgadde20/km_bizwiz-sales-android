package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.dialogs.PhonenumbersDialog;
import com.webparadox.bizwizsales.dialogs.SendEmailDialog;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class EmailAsyncTask extends AsyncTask<Void, Integer, Void> {
	ActivityIndicator dialog;
	Context mContext;
	String delearId, employeeId, mRequestUrl;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;
	PhonenumbersDialog phonenumberDialog;
	String cusID;
	String email;
	JSONArray emailArray;

	public EmailAsyncTask(Context context, String delearId, String employeeId) {
		this.mContext = context;
		this.delearId = delearId;
		this.employeeId = employeeId;
		serviceHelper = new ServiceHelper(this.mContext);
	}

	@Override
	protected void onPreExecute() {
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
		Singleton.getInstance().leadQuestionaryModel.clear();
		responseJson = new JSONObject();
		localJsonArray = new JSONArray();
		mRequestUrl = Constants.SEND_EMAIL_URL + "?" + "DealerId=" + delearId
				+ "&" + "CustomerId=" + employeeId;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest("",
				mRequestUrl.trim(), Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dismissDialog();
		if (responseJson != null) {
			if (responseJson.has(Constants.SAVE_EMAIL_CUSTOMER_EMAIL)) {
				try {
					emailArray = responseJson.getJSONArray(Constants.SAVE_EMAIL_CUSTOMER_EMAIL);
					JSONObject c = emailArray.getJSONObject(0);
					email = c.getString(Constants.SAVE_EMAIL_EMAIL);
					//if(!email.isEmpty()){
						SendEmailDialog sendEmailAsyncTask = new SendEmailDialog(mContext,email);
					//}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Constants.TOASTMSG_TIME).show();
		}
	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
}
