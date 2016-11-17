package com.webparadox.bizwizsales.asynctasks;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.DispoChoiceOptions;
import com.webparadox.bizwizsales.models.DispoQuestionnaireModel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DispoQuestionnaireAsyncTask extends AsyncTask<Void, Void, Void> {
	Context context;
	JSONObject responseJson;
	ServiceHelper helper;
	ActivityIndicator pDialog;
	String dealerId, dispoId;
	String appointmentResultId;
	String appointmentType;
	DispoQuestionnaireModel dispoQuestionnaireModel;
	DispoChoiceOptions optionsModel;
	postQuestionnaire postQuestionnaire;

	public DispoQuestionnaireAsyncTask(Context context2, String dealerId,
			String appointmentResultId, String dispoId, String appointmentType) {
		this.context = context2;
		this.dealerId = dealerId;
		this.appointmentResultId = appointmentResultId;
		this.appointmentType = appointmentType;
		this.dispoId = dispoId;
		helper = new ServiceHelper(context);
		postQuestionnaire = (postQuestionnaire) context;

	}

	public interface postQuestionnaire {
		void postQuestionnaire();
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		Singleton.getInstance().dispoHashMap.clear();
		try {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(context);
			pDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected Void doInBackground(Void... params) {

		responseJson = helper.jsonSendHTTPRequest("",
				Constants.URL_GET_DISPO_QUESTIONNAIRE + dealerId
						+ "&AppointmentResultId=" + appointmentResultId
						+ "&DispoId=" + dispoId, Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if(responseJson != null){
			try {
				if (responseJson.has(Constants.KEY_DISPO_QUESTIONNAIRE)
						&& responseJson != null) {
					JSONArray obj = responseJson
							.getJSONArray(Constants.KEY_DISPO_QUESTIONNAIRE);
					Singleton.getInstance().DispoQuestionsArray.clear();
					Singleton.getInstance().dispoChoiceArray.clear();
					for (int i = 0; i < obj.length(); i++) {
						JSONObject objQuesions = obj.getJSONObject(i);
						dispoQuestionnaireModel = new DispoQuestionnaireModel();
						dispoQuestionnaireModel.setQuestionId(objQuesions
								.getInt(Constants.KEY_DISPO_QUESTION_ID));
						dispoQuestionnaireModel.setQuestion(objQuesions
								.getString(Constants.KEY_DISPO_QUESTION));
						dispoQuestionnaireModel.setQuestionType(objQuesions
								.getString(Constants.kEY_DISPO_QUESTION_TYPE));

						dispoQuestionnaireModel.setResponseId(objQuesions
								.getString(Constants.KEY_DISPO_RESPONSE_ID).equals(
										"") ? "0" : objQuesions
								.getString(Constants.KEY_DISPO_RESPONSE_ID));

						dispoQuestionnaireModel.setResponseText(objQuesions
								.getString(Constants.KEY_DISPO_RESPONSE_TEXT));
						dispoQuestionnaireModel.setResponseChoiceId(objQuesions
								.getString(Constants.KEY_DISPO_RESPONSE_CHOICE_ID));
						JSONArray choiceArray = objQuesions
								.getJSONArray(Constants.KEY_DISPO_CHOICE_OPTIONS);
						
						ArrayList<DispoChoiceOptions> optionsArr = new ArrayList<DispoChoiceOptions>();
						
						for (int j = 0; j < choiceArray.length(); j++) {
							JSONObject objChoice = choiceArray.getJSONObject(j);
							optionsModel = new DispoChoiceOptions();
							optionsModel.ChoiceText = objChoice
									.getString(Constants.KEY_DISPO_CHOICE_TEXT);
							optionsModel.Id = objChoice
									.getString(Constants.KEY_DISPO_CHOICE_ID);
							optionsModel.Ordinal = objChoice
									.getString(Constants.KEY_DISPO_ORDINAL);
							Singleton.getInstance().dispoChoiceArray
									.add(optionsModel);
							optionsArr.add(optionsModel);
						}
					
						Singleton.getInstance().dispoHashMap.put(objQuesions
								.getString(Constants.KEY_DISPO_QUESTION_ID),
								optionsArr);
						Log.d("QUESTION ID", objQuesions
								.getString(Constants.KEY_DISPO_QUESTION_ID));
						
						Log.d("QUESTION RES", optionsArr.toString());
						Singleton.getInstance().DispoQuestionsArray
								.add(dispoQuestionnaireModel);
					}

					postQuestionnaire.postQuestionnaire();
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		super.onPostExecute(result);
	}

}
