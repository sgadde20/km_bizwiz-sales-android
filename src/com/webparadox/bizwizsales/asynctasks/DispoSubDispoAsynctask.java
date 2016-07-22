package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.DispoModel;
import com.webparadox.bizwizsales.models.SubDispoModel;

public class DispoSubDispoAsynctask extends AsyncTask<Void, Void, Void> {
	ActivityIndicator dialog;
	Context context;
	String dealerId;
	ServiceHelper helper;
	JSONObject responsJson;
	DispoModel dispoModel;
	
	public DispoSubDispoAsynctask(Context context, String dealerId) {
		this.dealerId = dealerId;
		this.context = context;
		helper = new ServiceHelper(context);
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
			dialog = new ActivityIndicator(context);
			dialog.show(); 
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		responsJson = helper.jsonSendHTTPRequest("",
				Constants.URL_LOAD_DISPO_SUB_DISPO + "DealerId=" + dealerId,
				Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		if (dialog != null & dialog.isShowing()) {
			dialog.dismiss();
			JSONArray responseArray;
			JSONArray responseServiceArray;
			if (responsJson != null) {
				try {
					responseArray = responsJson
							.getJSONArray(Constants.DISPO_KEY_SALES);
					responseServiceArray = responsJson
							.getJSONArray(Constants.DISPO_KEY_SERVICE);
					Singleton.getInstance().SubDispoArray.clear();
					Singleton.getInstance().salesDispoArray.clear();
					Singleton.getInstance().serviceDispoArray.clear();
					dispoModel = new DispoModel();
					dispoModel.setDispoId("0");
					dispoModel.setDispoName("Choose Appointment Result");
					Singleton.getInstance().salesDispoArray.add(dispoModel);
					Singleton.getInstance().serviceDispoArray.add(dispoModel);
					for (int i = 0; i < responseArray.length(); i++) {
						JSONObject objDispo = responseArray.getJSONObject(i);
						dispoModel = new DispoModel();
						dispoModel.setDispoId(objDispo.getString(Constants.DISPO_KEY_ID));
						dispoModel.setDispoName(objDispo.getString(Constants.DISPO_KEY_DISPO_NAME));
						if (objDispo.has(Constants.DISPO_KEY_SUB_DISPO_OPTIONS)) {
							JSONArray subDispoOptions = objDispo.getJSONArray(Constants.DISPO_KEY_SUB_DISPO_OPTIONS);
							ArrayList<SubDispoModel> SubDispoArray111 = new ArrayList<SubDispoModel>();
//							Singleton.getInstance().SubDispoArray.clear();
							for (int j = 0; j < subDispoOptions.length(); j++) {
								JSONObject subDispoObj = subDispoOptions.getJSONObject(j);
								SubDispoModel subDispo = new SubDispoModel();
								subDispo.setSubDispoId(subDispoObj.getString(Constants.DISPO_KEY_ID));
								
								subDispo.setSubDispoName(subDispoObj.getString(Constants.DISPO_KEY_SUB_DISPO_NAME));
								SubDispoArray111.add(subDispo);
							}
							dispoModel.setSubDispo(SubDispoArray111);
						}
						//Log.e("test ===> "+Singleton.getInstance().SubDispoArray);
 						Singleton.getInstance().salesDispoArray.add(dispoModel);
						Log.d(" mmmmmmmmmmmmmmmmmm ===> ", +i+ " m " + Singleton.getInstance().salesDispoArray.get(i).getSubDispo().toString());	
						for (int c = 0; c < Singleton.getInstance().salesDispoArray.size(); c++) {
							Log.d(" ttttttttttttttttttt ===> ", +c+ " n " + Singleton.getInstance().salesDispoArray.get(c).getDispoName());	
							Log.d(" nnnnnnnnnnnnnnnnnnn ===> ", +c+ " n " + Singleton.getInstance().salesDispoArray.get(c).getSubDispo().toString());	
						}
					}
					
					for (int i = 0; i < Singleton.getInstance().salesDispoArray.size(); i++) {
						Log.d(" oooooooooooooooooooooooo ===> ", +i+ " o " + Singleton.getInstance().salesDispoArray.get(i).getSubDispo().toString());	
					}
					
					for (int i = 0; i < responseArray.length(); i++) {
						Log.d(" kkkkkkkkkkkkkkkkkkkkk ===> ", +i+ " i " + Singleton.getInstance().salesDispoArray.get(i).getSubDispo().toString());	
					}
					

					for (int k = 0; k < responseServiceArray.length(); k++) {
						JSONObject objServiDispo = responseServiceArray
								.getJSONObject(k);
						dispoModel = new DispoModel();
						dispoModel.setDispoId(objServiDispo
								.getString(Constants.DISPO_KEY_ID));
						dispoModel.setDispoName(objServiDispo
								.getString(Constants.DISPO_KEY_DISPO_NAME));
						if (objServiDispo
								.has(Constants.DISPO_KEY_SUB_DISPO_OPTIONS)) {
							JSONArray subDispoServiceOptions = objServiDispo
									.getJSONArray(Constants.DISPO_KEY_SUB_DISPO_OPTIONS);
							if (subDispoServiceOptions != null
									&& subDispoServiceOptions.length() > 0) {
								Singleton.getInstance().SubDispoArray.clear();
								for (int l = 0; l < subDispoServiceOptions
										.length(); l++) {
									JSONObject subDispoServiceObj = subDispoServiceOptions
											.getJSONObject(l);
									SubDispoModel subDispoService = new SubDispoModel();
									subDispoService
											.setSubDispoId(subDispoServiceObj
													.getString(Constants.DISPO_KEY_ID));
									subDispoService
											.setSubDispoName(subDispoServiceObj
													.getString(Constants.DISPO_KEY_SUB_DISPO_NAME));
									Singleton.getInstance().SubDispoArray.add(subDispoService);
								}
								dispoModel.setSubDispo(Singleton.getInstance().SubDispoArray);
							}
						}
						Singleton.getInstance().serviceDispoArray.add(dispoModel);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		super.onPostExecute(result);
	}

}
