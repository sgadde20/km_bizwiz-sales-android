package com.webparadox.bizwizsales.datacontroller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.OfflineCalendarEntryModel;

public class UpSyncServiceController {
	public Context mcontext;
	public SharedPreferences userData;
	public String mDealerId;
	public String mEmployeeId;
	public DatabaseHandler dbHandler;
	public ServiceHelper serviceHelper;
	public JSONObject jsonResultText, jsonDataResultText;
	UpSyncServiceInterface mUpSyncServiceInterface;
	AsyncTask<JSONObject, Void, Void> calendarAsyncTask, SaveAppntAsyncTask;
	AsyncTask<String, Void, Void> addProspectAndAprofessionalContactAsyncTask;
	int flagOfflineSize;
	ArrayList<OfflineCalendarEntryModel> offlineEvents = new ArrayList<OfflineCalendarEntryModel>();

	public UpSyncServiceController(Context ctx) {
		this.mcontext = ctx;
		dbHandler = new DatabaseHandler(mcontext);
		userData = mcontext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		mUpSyncServiceInterface = (UpSyncServiceInterface) mcontext;
		mDealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		mEmployeeId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		/**
		 * This method used to upload latest off line data's to server
		 */
		uploadDataToServer();
	}

	private void uploadDataToServer() {
		// TODO Auto-generated method stub
		offlineEvents.clear();
		offlineEvents = dbHandler.getAllOfflineCalendarEvents();
		flagOfflineSize = 0;
		Log.i("**Start**", "-Up Sync-");
		if (offlineEvents.size() > 0) {
			if (Utilities.isNetworkConnected(mcontext)) {
				startUpSync(flagOfflineSize);
			}
		} else {
			callUpSyncAddProspect();
		}

	}

	private void startUpSync(int position) {
		// TODO Auto-generated method stub
		ArrayList<JSONObject> reqArrayList = forJsonRequest(offlineEvents
				.get(position));
		SaveAppntAsyncTask = new SaveAppntAsyncTask(mcontext, reqArrayList)
				.execute();

	}

	public interface UpSyncServiceInterface {
		void upSyncCompletedInterface();
	}

	public class SaveAppntAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context context;
		ArrayList<JSONObject> reqObj;
		ServiceHelper helper;
		JSONObject responseJson;

		public SaveAppntAsyncTask(Context ctx, ArrayList<JSONObject> reqArrPro) {
			this.context = ctx;
			this.reqObj = reqArrPro;
			helper = new ServiceHelper(ctx);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			responseJson = helper.jsonSendHTTPRequest(reqObj.toString(),
					Constants.CREATE_EVENT_URL, Constants.REQUEST_TYPE_POST);
			Log.d("responseJson = ", "" + responseJson);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (flagOfflineSize < offlineEvents.size()) {
				if (Utilities.isNetworkConnected(mcontext)) {
					startUpSync(flagOfflineSize);
				}
			} else {
				Log.i("**End**", "-Up Sync-");
				Log.i("**flagOfflineSize**", "" + flagOfflineSize);
				dbHandler.clearOffLineCalendarEntryTable();
				callUpSyncAddProspect();
			}

		}

	}

	public ArrayList<JSONObject> forJsonRequest(
			OfflineCalendarEntryModel offlineModel) {
		Log.i("**flagOfflineSize**", "" + flagOfflineSize);
		flagOfflineSize++;
		ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();
		JSONObject reqobjPro = new JSONObject();
		try {

			reqobjPro
					.put(Constants.KEY_LOGIN_DEALER_ID, offlineModel.mDealerId);
			reqobjPro.put(Constants.KEY_LOGIN_EMPLOYEE_ID,
					offlineModel.mEmployeeId);
			reqobjPro
					.put(Constants.EVENT_EMPLOYEE_ID, offlineModel.mEmployeeId);
			reqobjPro.put(Constants.EVENT_NOTES, offlineModel.mNotes);
			reqobjPro.put(Constants.EVENT_START_TIME, offlineModel.mStartTime);
			reqobjPro.put(Constants.EVENT_END_TIME, offlineModel.mEndTime);
			reqobjPro.put(Constants.EVENT_ID, offlineModel.mEventId);

			reqobjPro.put(Constants.EVENT_TYPE, offlineModel.mEventType);
			reqobjPro.put(Constants.KEY_CUSTOMER_ID, offlineModel.mCustomerId);
			reqobjPro.put(Constants.EC_APPNT_TPYE_ID, offlineModel.mTypeId);
			reqobjPro.put(Constants.EVENT_DATE, offlineModel.mEventDate);
			reqobjPro.put(Constants.LEAD_TYPE_ID, offlineModel.mLeadTypeId);

			reqArrayList.add(reqobjPro);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reqArrayList;
	}

	public void callUpSyncAddProspect() {
		JSONObject responseJson = new JSONObject();
		responseJson = dbHandler.getJsonValue(mDealerId, mEmployeeId);
		if (responseJson != null) {
			if (Utilities.isNetworkConnected(mcontext)) {
				addProspectAndAprofessionalContactAsyncTask = new AddProspectAndAprofessionalContactAsyncTask(
						mcontext,
						Constants.URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT,
						responseJson);
				addProspectAndAprofessionalContactAsyncTask.execute();
			} else {
				Toast.makeText(mcontext, Constants.NETWORK_NOT_AVAILABLE,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			mUpSyncServiceInterface.upSyncCompletedInterface();
		}
	}

	class AddProspectAndAprofessionalContactAsyncTask extends
			AsyncTask<String, Void, Void> {

		String mRequestUrl, mRequestData;
		Context context;

		public AddProspectAndAprofessionalContactAsyncTask(Context context,
				String requestUrl, JSONObject responseJson) {
			this.context = context;
			this.mRequestUrl = requestUrl;
			this.mRequestData = responseJson.toString();
			serviceHelper = new ServiceHelper(context);
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				jsonResultText = serviceHelper.jsonSendHTTPRequest(
						this.mRequestData, this.mRequestUrl,
						Constants.REQUEST_TYPE_POST);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// pDialog.dismiss();
			if(jsonResultText != null){
				try {
					if (jsonResultText.has(Constants.KEY_ADDPROSPECT_RESPONSE)) {
						JSONArray localJsonArray = jsonResultText
								.getJSONArray(Constants.KEY_ADDPROSPECT_RESPONSE);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject localJsonObject = localJsonArray
									.getJSONObject(i);
							if (localJsonObject
									.has(Constants.KEY_ADDPROSPECT_STATUS)) {
								if (localJsonObject
										.getString(Constants.KEY_ADDPROSPECT_STATUS)
										.toString()
										.equalsIgnoreCase(Constants.VALUE_SUCCESS)) {
									dbHandler.removeRowAddprospectContact();

									mUpSyncServiceInterface
											.upSyncCompletedInterface();
								} else {
									Toast.makeText(
											context,
											localJsonObject
													.getString(
															Constants.KEY_ADDPROSPECT_STATUS)
													.toString(),
											Constants.TOASTMSG_TIME).show();
								}
							} else {
								Toast.makeText(context,
										Constants.TOAST_CONNECTION_ERROR,
										Constants.TOASTMSG_TIME).show();
							}
						}
					} else {
						Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
								Constants.TOASTMSG_TIME).show();
					}
				} catch (Exception e) {
					Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
							Constants.TOASTMSG_TIME).show();
				}
			}
		}
	}
}
