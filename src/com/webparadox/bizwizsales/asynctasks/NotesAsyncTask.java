package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.webparadox.bizwizsales.adapter.CustomerNotesAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.NotesModel;

public class NotesAsyncTask extends AsyncTask<Void, Integer, Void> {

	ActivityIndicator pDialog;
	Context mContext;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONObject localJsonObject;
	String currentDynamicKey, cusId, dealerId;
	ListView noteListview;

	public NotesAsyncTask(Context context, ListView listView_appointments,
			String customerId, String dealerID) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.noteListview = listView_appointments;
		this.cusId = customerId;
		this.dealerId = dealerID;
		serviceHelper = new ServiceHelper(this.mContext);
	}

	@Override
	protected void onPreExecute() {
		Singleton.getInstance().noteModel.clear();
		mRequestJson = new ArrayList<JSONObject>();
		Log.d("cusId", cusId);
		Log.d("dealerId", dealerId);
		final JSONObject reqObj_data = new JSONObject();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, cusId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRequestJson.add(reqObj_data);
		if (pDialog != null) {
			if (pDialog.isShowing()) {
				pDialog.dismiss();
				pDialog = null;
			} else {
				pDialog = null;
			}
		}
		pDialog = new ActivityIndicator(mContext);
		pDialog.setLoadingText("Loading....");
		pDialog.show();
		super.onPreExecute();

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), Constants.VIEW_CUSTOMER_NOTE_URL,
				Constants.REQUEST_TYPE_POST);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog =null;
		}
		ParseNotesJson(responseJson);

	}

	public void ParseNotesJson(JSONObject responseJson2) {
		JSONArray jsonCNArray;
		try {
			if(responseJson2 != null){
				jsonCNArray = responseJson2.getJSONArray(Constants.KEY_NEW_VIEW_NOTE);
					for (int j = 0; j < jsonCNArray.length(); j++) {
						NotesModel noteModel = new NotesModel();
						JSONObject calJsonObject = jsonCNArray
								.getJSONObject(j);
						noteModel.note = calJsonObject.getString(Constants.JSON_KEY_NOTE).toString();
						noteModel.AdjustedDateCreated = calJsonObject.getString(
								Constants.jSON_KEY_ADJUSTED_CREATED_DATE).toString();
						noteModel.EmployeeName = calJsonObject.getString(
								Constants.KEY_EMPLOYEE_NAME).toString();
						noteModel.NoteType = calJsonObject.getString(Constants.NOTES_TYPE)
								.toString();

						Singleton.getInstance().noteModel.add(noteModel);
					}
				}
				Singleton.getInstance().mNotesList.put(currentDynamicKey,
						Singleton.getInstance().noteModel);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (Singleton.getInstance().noteModel.size() > 0) {

			CustomerNotesAdapter noteAdapter = new CustomerNotesAdapter(
					mContext);
			noteListview.setAdapter(noteAdapter);

		}else{
			CustomerNotesAdapter noteAdapter = new CustomerNotesAdapter(
					mContext);
			noteListview.setAdapter(noteAdapter);
		}

	}

}
