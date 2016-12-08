package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.dialogs.PhonenumbersDialog;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerAttachmentModel;
import com.webparadox.bizwizsales.models.phoneModel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class PhonenumbersAsyncTask extends AsyncTask<Void, Integer, Void> {
	ActivityIndicator pDialog;
	ArrayList<phoneModel> mCustomerPhoneData;
	ArrayList<CustomerAttachmentModel> mcustomerData;
	Context mContext;
	String mRequestUrl, mMethodType;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray = null;
	JSONArray jPhoneArray = null;
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
		mcustomerData=new ArrayList<CustomerAttachmentModel>();
		try {
			cusID = requestJson.get(0)
					.getString(Constants.JSON_KEY_CUSTOMER_ID);
		} 
		catch (JSONException e) {
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
	/*	try {
			if(responseJson != null){

				if (responseJson.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
					localJsonArray = responseJson
							.getJSONArray(Constants.EDIT_PROSPECT_CONFIGUATION_KEY);
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
		}*/
		Log.e(Constants.KEY_LOGIN_RESPONSE_MESSAGE, responseJson.toString());
		return null;
	}

	@Override
	
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		Log.d("Orientation = ", "Orientation ...");
		if (responseJson != null) {
			if (responseJson.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
				
				try {
					if(responseJson != null){

						if (responseJson.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
							localJsonArray = responseJson
									.getJSONArray(Constants.EDIT_PROSPECT_CONFIGUATION_KEY);
							
						/*	for(int k =0; k<=1;k++)
							{
								JSONObject n = localJsonArray.getJSONObject(k);	
								CustomerAttachmentModel customerattachmentmodel = new CustomerAttachmentModel();
								customerattachmentmodel.customerName = n.get(
                                   		Constants.JSON_KEY_CUSTOMER_NAME).toString();
                                customerattachmentmodel.CompanyName = n.get(
                                		Constants.JSON_KEY_COMPANY_NAME).toString();*/
							 for (int i = 0; i < localJsonArray.length(); i++) {
								 
                            	JSONObject c = localJsonArray.getJSONObject(i);	
                            	
                            	JSONArray jPhoneArray = c.getJSONArray("PhoneNumbers");
                            
                                for (int j = 0; j < jPhoneArray.length(); j++) {
                                
                            	 phoneModel phonemodel = new phoneModel();
                            	
                                JSONObject jobj = jPhoneArray.getJSONObject(j);
                             
                                phonemodel.mCustomerId = jobj.get(
                                        Constants.JSON_KEY_CUSTOMER_ID).toString();
                                phonemodel.mPhone = jobj.get(Constants.JSON_KEY_PHONE)
                                        .toString();
                                phonemodel.mTypeName = jobj
                                        .get(Constants.JSON_KEY_TYPENAME).toString();
                                phonemodel.mPhoneNumberId = jobj.get(
                                        Constants.JSON_KEY_PHONENUMBER_ID).toString();
                                phonemodel.mnotes = jobj.get(
                                		Constants.JSON_KEY_NOTES).toString();
                                mCustomerPhoneData.add(phonemodel);    
                             }
                              
                            }
							}
						
						if (responseJson.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
							JSONArray localJsonArray1;
							localJsonArray1 = responseJson
									.getJSONArray(Constants.EDIT_PROSPECT_CONFIGUATION_KEY);
							 for (int k = 0; k < localJsonArray1.length(); k++) {
								 CustomerAttachmentModel cusmodel = new CustomerAttachmentModel();
							 
								 JSONObject n = localJsonArray1.getJSONObject(k);	
	                         	
								 cusmodel.customerName = n.get(
	                                		Constants.JSON_KEY_CUSTOMER_NAME).toString();
								 cusmodel.companyName = n.get(
	                             		Constants.JSON_KEY_COMPANY_NAME).toString();
								 
					
								 mcustomerData.add(cusmodel);
							     System.out.println("Customer name in phn nos:"+n.getString("CustomerName"));
								
							 }
						
						}
						
						
							else {
								Toast.makeText(mContext,
										Constants.TOAST_CONNECTION_ERROR,
										Constants.TOASTMSG_TIME).show();
							}
						
                        
                            }
							 
							 
						}

					
					
				 catch (Exception e) {
					Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
							Constants.TOASTMSG_TIME).show();
				}
				
				if (localJsonArray != null && localJsonArray.length() > 0) {
					phonenumberDialog = new PhonenumbersDialog();
					phonenumberDialog.showPhonenumberDialog(mContext,
							mCustomerPhoneData,  cusID,mcustomerData);
				} else {
					try {
						
						
					} catch (Exception e) {
						// TODO: handle exception
					}
					phonenumberDialog = new PhonenumbersDialog();
					phonenumberDialog.showPhonenumberDialog(mContext,
							mCustomerPhoneData,  cusID,mcustomerData);
				}
			} else {
				/*Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();*/
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

	/*protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		Log.d("Orientation = ", "Orientation ...");
		if (responseJson != null) {
			if (responseJson.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
				
				try {
					if(responseJson != null){

						if (responseJson.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
							localJsonArray = responseJson
									.getJSONArray(Constants.EDIT_PROSPECT_CONFIGUATION_KEY);
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
				
				if (localJsonArray != null && localJsonArray.length() > 0) {
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
	}*/

