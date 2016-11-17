package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.adapter.CusAttachmentAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerAttachmentModel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerAttachmentAsyncTask extends AsyncTask<Void, Integer, Void> {

	Context mContext;
	ActivityIndicator dialog;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;
	String dealerId, customerId, cusName;
	CustomerAttachmentModel cusAttachmentModel;
	ListView cusAttachmentListview;
	TextView cusAttachmentTextview;
	int value;

	public CustomerAttachmentAsyncTask(Context context, String dealerId,
			String cusId, ListView cusAttachmentListview,
			TextView cusAttachmentTextview, String cusName, int value) {
		// TODO Auto-generated constructor stub

		this.mContext = context;
		this.dealerId = dealerId;
		this.customerId = cusId;

		if (value == 0) {
			this.cusName = cusName;
			this.cusAttachmentTextview = cusAttachmentTextview;

		}
		this.cusAttachmentListview = cusAttachmentListview;
		this.value = value;
		serviceHelper = new ServiceHelper(this.mContext);
	}

	@Override
	protected void onPreExecute() {
		responseJson = new JSONObject();
		Singleton.getInstance().cusAttachmentModel.clear();
		mRequestJson = new ArrayList<JSONObject>();
		Log.d("cusId", customerId);
		Log.d("dealerId", dealerId);
		final JSONObject reqObj_data = new JSONObject();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, customerId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRequestJson.add(reqObj_data);
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

	@Override
	protected Void doInBackground(Void... params) {
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), Constants.CUSTOMER_ATTACHMENT_URL,
				Constants.REQUEST_TYPE_POST);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dissmissDialog();

		if (responseJson != null) {
			if (responseJson.has(Constants.JSON_KEY_CUSTOMER_ATTACHMNT)) {
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.JSON_KEY_CUSTOMER_ATTACHMNT);
					for (int i = 0; i < localJsonArray.length(); i++) {

						JSONObject jobj = localJsonArray.getJSONObject(i);

						if(value == 0){
							cusAttachmentModel = new CustomerAttachmentModel();
							cusAttachmentModel.customerName = jobj.get(
									Constants.JSON_KEY_CUSTOMER_NAME).toString();
							cusAttachmentModel.customerId = jobj.get(
									Constants.JSON_KEY_CUSTOMER_ID).toString();

							cusAttachmentModel.attachmentURL = jobj.get(
									Constants.JSON_KEY_ATTACHMENT_URL).toString();

							cusAttachmentModel.attachmentDescription = jobj.get(
									Constants.JSON_KEY_ATTACHEMT_DESCRIPTION)
									.toString();

							cusAttachmentModel.attachmentAtted = jobj.get(
									Constants.JSON_KEY_ATTCHMENT_ADDED).toString();

							cusAttachmentModel.attachemnttype = jobj.get(
									Constants.JSON_KEY_ATTACHMEMT_TYPE).toString();

							Singleton.getInstance().cusAttachmentModel
									.add(cusAttachmentModel);
						}
						else{
							if (jobj.get(Constants.JSON_KEY_ATTACHMEMT_TYPE)
									.toString().equalsIgnoreCase("Picture")) {
								cusAttachmentModel = new CustomerAttachmentModel();
								cusAttachmentModel.customerName = jobj.get(
										Constants.JSON_KEY_CUSTOMER_NAME).toString();
								cusAttachmentModel.customerId = jobj.get(
										Constants.JSON_KEY_CUSTOMER_ID).toString();

								cusAttachmentModel.attachmentURL = jobj.get(
										Constants.JSON_KEY_ATTACHMENT_URL).toString();

								cusAttachmentModel.attachmentDescription = jobj.get(
										Constants.JSON_KEY_ATTACHEMT_DESCRIPTION)
										.toString();

								cusAttachmentModel.attachmentAtted = jobj.get(
										Constants.JSON_KEY_ATTCHMENT_ADDED).toString();

								cusAttachmentModel.attachemnttype = jobj.get(
										Constants.JSON_KEY_ATTACHMEMT_TYPE).toString();

								Singleton.getInstance().cusAttachmentModel
										.add(cusAttachmentModel);
							}
						}
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, Constants.TOAST_INTERNET,
					Toast.LENGTH_SHORT).show();
		}
		if (Singleton.getInstance().cusAttachmentModel.size() > 0) {
			if (value == 0) {
				if (Singleton.getInstance().cusAttachmentModel.size() == 1) {
					cusAttachmentTextview.setText(cusName + " "
							+ Constants.Attachment);
				} else {
					cusAttachmentTextview.setText(cusName + " "
							+ Constants.Attachments);
				}
				CusAttachmentAdapter cusAttachmentAdapter = new CusAttachmentAdapter(
						mContext, value);
				cusAttachmentListview.setAdapter(cusAttachmentAdapter);
			} else {
				for (int i = 0; i < Singleton.getInstance().cusAttachmentModel
						.size(); i++) {
					if (Singleton.getInstance().cusAttachmentModel.get(i).attachemnttype
							.trim().equalsIgnoreCase("Picture")) {
						CusAttachmentAdapter cusAttachmentAdapter = new CusAttachmentAdapter(
								mContext, value);
						cusAttachmentListview.setAdapter(cusAttachmentAdapter);
						break;
					}
				}

			}

		} else {
			if (value == 0) {
				cusAttachmentTextview.setText(cusName + " "
						+ Constants.Attachment);
			}
			Toast.makeText(mContext, Constants.TOAST_NO_DATA,
					Toast.LENGTH_SHORT).show();
		}

	}

	public void dissmissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

	}

}
