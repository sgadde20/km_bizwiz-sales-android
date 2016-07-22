package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.AppointmentQuestionnaireModel;
import com.webparadox.bizwizsales.models.AppointmentQuestionnaireQuestionOptionModel;
import com.webparadox.bizwizsales.models.AppointmentRadioButtonModel;

public class AppointmentQuestionnaireAsyncTask extends
		AsyncTask<Void, Integer, Void> {

	Context mContext;
	ActivityIndicator dialog;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray, localInnerJsonArray;
	String mRequestUrl, leadTypeId, dealerId, appointmentId, appointmentTypeId;
	AppointmentQuestionnaireInterface appointerface;

	public AppointmentQuestionnaireAsyncTask(Context context,
			String appointmentId, String dealerId, String leadTypeId,
			String appointmentTypeId) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.appointmentId = appointmentId;
		this.dealerId = dealerId;
		this.appointmentTypeId = appointmentTypeId;
		this.leadTypeId = leadTypeId;
		appointerface = (AppointmentQuestionnaireInterface) mContext;
		serviceHelper = new ServiceHelper(this.mContext);
	}

	public interface AppointmentQuestionnaireInterface {

		void appointmentQuestioninterface();

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
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
		responseJson = new JSONObject();
		localJsonArray = new JSONArray();
		mRequestJson = new ArrayList<JSONObject>();
		mRequestUrl = Constants.APPO_QUESTIONNAIRES_URL + "?" + "DealerId="
				+ dealerId + "&" + "AppointmentId=" + appointmentId + "&"
				+ "LeadTypeId=" + leadTypeId + "&" + "AppointmentTypeId="
				+ appointmentTypeId;
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), mRequestUrl,
				Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (responseJson != null) {

			if (responseJson.has(Constants.LEAD_QUESTIONNAIRES)) {
				Singleton.getInstance().appointmentQuestionnaireModel.clear();
				Singleton.getInstance().appointmentQuestionOptionModel.clear();
				Singleton.getInstance().apponintQuestionArrayModel.clear();
				Singleton.getInstance().radioArrayModel.clear();
				Singleton.getInstance().appointmentQuestionRadioModel.clear();
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.LEAD_QUESTIONNAIRES);

					if (localJsonArray.length() > 0) {
						for (int i = 0; i < localJsonArray.length(); i++) {
							AppointmentQuestionnaireModel appointmentQuesModel = new AppointmentQuestionnaireModel();
							JSONObject obj = localJsonArray.getJSONObject(i);
							appointmentQuesModel.QuestionId = obj
									.getString(Constants.QUESTION_ID);
							appointmentQuesModel.Question = obj
									.getString(Constants.QUESTION);
							appointmentQuesModel.QuestionType = obj
									.getString(Constants.QUESTION_TYPE);

							if (obj.getString(Constants.QUESTION_TYPE).equals(
									"Textbox")) {

							} else if (obj.getString(Constants.QUESTION_TYPE)
									.equals("Dropdown")) {
								localInnerJsonArray = obj
										.getJSONArray(Constants.CHOICE_OPTION);
								Singleton.getInstance().apponintQuestionArrayModel
										.clear();
								ArrayList<AppointmentQuestionnaireQuestionOptionModel> optionsArr = new ArrayList<AppointmentQuestionnaireQuestionOptionModel>();
								for (int j = 0; j < localInnerJsonArray
										.length(); j++) {
									AppointmentQuestionnaireQuestionOptionModel appointmentOptionModel = new AppointmentQuestionnaireQuestionOptionModel();
									JSONObject obj2 = localInnerJsonArray
											.getJSONObject(j);
									appointmentOptionModel.Id = obj2
											.getString(Constants.ID);
									appointmentOptionModel.ChoiceText = obj2
											.getString(Constants.CHOICE_TEXT);
									appointmentOptionModel.Ordinal = obj2
											.getString(Constants.ORDINAL);
									Singleton.getInstance().apponintQuestionArrayModel
											.add(appointmentOptionModel);
									optionsArr.add(appointmentOptionModel);

								}

								Log.d("IDDDDD",
										obj.getString(Constants.QUESTION_ID));

//								Singleton.getInstance().appointmentQuestionOptionModel
//										.put(obj.getString(Constants.QUESTION_ID),
//												Singleton.getInstance().apponintQuestionArrayModel);
								Singleton.getInstance().appointmentQuestionOptionModel
					              .put(appointmentQuesModel.QuestionId,
					                optionsArr);

							} else if (obj.getString(Constants.QUESTION_TYPE)
									.equals("RadioButton")) {
								Singleton.getInstance().radioArrayModel.clear();
								localInnerJsonArray = obj
										.getJSONArray(Constants.CHOICE_OPTION);
								ArrayList<AppointmentRadioButtonModel>radioOptionsArray=new ArrayList<AppointmentRadioButtonModel>();

								for (int j = 0; j < localInnerJsonArray
										.length(); j++) {
									AppointmentRadioButtonModel radioModel = new AppointmentRadioButtonModel();
									JSONObject obj2 = localInnerJsonArray
											.getJSONObject(j);
									radioModel.Id = obj2
											.getString(Constants.ID);
									radioModel.ChoiceText = obj2
											.getString(Constants.CHOICE_TEXT);
									radioModel.Ordinal = obj2
											.getString(Constants.ORDINAL);
									Singleton.getInstance().radioArrayModel
											.add(radioModel);
									radioOptionsArray.add(radioModel);

								}
								Log.d("IDDDfffffffffffDD",
										obj.getString(Constants.QUESTION_ID));
//								Singleton.getInstance().appointmentQuestionRadioModel
//										.put(obj.getString(Constants.QUESTION_ID),
//												Singleton.getInstance().radioArrayModel);
								Singleton.getInstance().appointmentQuestionRadioModel
						         .put(obj.getString(Constants.QUESTION_ID),
						           radioOptionsArray);

							}
							appointmentQuesModel.ResponseId = obj
									.getString(Constants.RESPONSE_ID);
							appointmentQuesModel.ResponseText = obj
									.getString(Constants.RESPONSE_TEXT);
							appointmentQuesModel.ResponseChoiceId = obj
									.getString(Constants.RESPONSE_CHOICE_ID);
							Singleton.getInstance().appointmentQuestionnaireModel
									.add(appointmentQuesModel);
						}

						Log.d("Main Count = ",
								""
										+ Singleton.getInstance().appointmentQuestionnaireModel
												.size());
					} else {
						Toast.makeText(mContext, Constants.TOAST_NO_DATA,
								Toast.LENGTH_SHORT).show();
						dismissDialog();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("CAl", "CALLL ASY");
				dismissDialog();
				appointerface.appointmentQuestioninterface();

			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
				dismissDialog();
			}

		} else {
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Toast.LENGTH_SHORT).show();
			dismissDialog();
		}

	}

	public void dismissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
