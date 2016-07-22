package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.dialogs.PhonenumbersDialog;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.phoneModel;

public class PhonenumbersAsyncTask extends AsyncTask<Void, Integer, Void> {
	ActivityIndicator pDialog;
	ArrayList<phoneModel> mCustomerPhoneData;
	Context mContext;
	String mRequestUrl, mMethodType;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;
	PhonenumbersDialog phonenumberDialog;
	String cusID;

	public PhonenumbersAsyncTask(Context context, String requestUrl,
			String methodType, ArrayList<JSONObject> requestJson) {
		this.mContext = context;
		this.mRequestUrl = requestUrl;
		this.mRequestJson = requestJson;
		this.mMethodType = methodType;
		serviceHelper = new ServiceHelper(this.mContext);
		mCustomerPhoneData = new ArrayList<phoneModel>();
		try {
			cusID = requestJson.get(0)
					.getString(Constants.JSON_KEY_CUSTOMER_ID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPreExecute() {
		if (pDialog != null) {
			if (pDialog.isShowing()) {
				pDialog.dismiss();
			}
			pDialog = null;
		}
		pDialog = new ActivityIndicator(mContext);
		pDialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), mRequestUrl, mMethodType);
		try {
			if(responseJson != null){

				if (responseJson.has(Constants.JSON_KEY_CUSTOMER_PHONE)) {
					localJsonArray = responseJson
							.getJSONArray(Constants.JSON_KEY_CUSTOMER_PHONE);
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(i);
						phoneModel phonemodel = new phoneModel();
						phonemodel.mCustomerId = jobj.get(
								Constants.JSON_KEY_CUSTOMER_ID).toString();
						phonemodel.mPhone = jobj.get(Constants.JSON_KEY_PHONE)
								.toString();
						phonemodel.mTypeName = jobj
								.get(Constants.JSON_KEY_TYPENAME).toString();
						phonemodel.mPhoneNumberId = jobj.get(
								Constants.JSON_KEY_PHONENUMBER_ID).toString();
						mCustomerPhoneData.add(phonemodel);
					}
				}

			
			}
		} catch (Exception e) {
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Constants.TOASTMSG_TIME).show();
		}
		Log.e(Constants.KEY_LOGIN_RESPONSE_MESSAGE, responseJson.toString());
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		Log.d("Orientation = ", "Orientation ...");
		if (responseJson != null) {
			if (responseJson.has(Constants.JSON_KEY_CUSTOMER_PHONE)) {
				if (localJsonArray.length() > 0) {
					phonenumberDialog = new PhonenumbersDialog();
					phonenumberDialog.showPhonenumberDialog(mContext,
							mCustomerPhoneData, cusID);
				} else {
					try {
						
					} catch (Exception e) {
						// TODO: handle exception
					}
					phonenumberDialog = new PhonenumbersDialog();
					phonenumberDialog.showPhonenumberDialog(mContext,
							mCustomerPhoneData, cusID);
				}
			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}

		} else {
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Constants.TOASTMSG_TIME).show();
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
	}
}
