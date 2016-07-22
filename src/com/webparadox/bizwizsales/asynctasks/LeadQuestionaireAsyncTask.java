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
import com.webparadox.bizwizsales.models.AppointmentQuestionnaireQuestionOptionModel;
import com.webparadox.bizwizsales.models.AppointmentRadioButtonModel;
import com.webparadox.bizwizsales.models.LeadQuestionaireModel;

public class LeadQuestionaireAsyncTask extends AsyncTask<Void, Integer, Void>{

	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray,localInnerJsonArray;
	ActivityIndicator dialog;
	Context mContext;
	String mRequestUrl,dealerId,appointmentId;
	LeadQuestionaireModel leadModel;
	
	LeadPostExecute leadExecute;
	
	public LeadQuestionaireAsyncTask(Context context, String appointmentId2, String dealerId2) {
		// TODO Auto-generated constructor stub
		this.mContext=context;
		this.appointmentId=appointmentId2;
		this.dealerId=dealerId2;
		serviceHelper = new ServiceHelper(this.mContext);
		leadExecute=(LeadPostExecute) mContext;
	}
	
	public interface LeadPostExecute{
		
		void leadInterface();
		
	}
	
	
	@Override
	protected void onPreExecute() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			} else {
				dialog = null;
			}
		}
		dialog = new ActivityIndicator(mContext);
		dialog.show();
		Singleton.getInstance().apponintQuestionArrayModel.clear();
		Singleton.getInstance().appointmentQuestionRadioModel.clear();
		Singleton.getInstance().leadQuestionaryModel.clear();
		Singleton.getInstance().appointmentQuestionOptionModel.clear();
		Singleton.getInstance().appointmentQuestionRadioModel.clear();
		responseJson=new JSONObject();
		localJsonArray=new JSONArray();
		localInnerJsonArray=new JSONArray();
		mRequestJson=new ArrayList<JSONObject>();
		mRequestUrl=Constants.LEAD_QUESTIONNAIRES_URL+"?"+"DealerId="+dealerId+"&"+"AppointmentId="+appointmentId;
		
	}
	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(
				mRequestJson.toString(), mRequestUrl, Constants.REQUEST_TYPE_GET);
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dismissDialog();
		
		if(responseJson != null){
			
			if(responseJson.has(Constants.LEAD_QUESTIONNAIRES)){
				
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.LEAD_QUESTIONNAIRES);
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(i);
						leadModel = new LeadQuestionaireModel();
						leadModel.questionId = jobj.getString(Constants.QUESTION_ID);
						leadModel.questions=jobj.getString(Constants.QUESTIONS);
						leadModel.questionType=jobj.getString(Constants.QUESTION_TYPE);
						if (jobj.getString(Constants.QUESTION_TYPE)
								.equals("Textbox")) {
							localInnerJsonArray = jobj
									.getJSONArray(Constants.CHOICE_OPTION);
							Singleton.getInstance().apponintQuestionArrayModel
									.clear();
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
							}
						}
						else if (jobj.getString(Constants.QUESTION_TYPE)
								.equals("Dropdown")) {
							localInnerJsonArray = jobj
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
//							Singleton.getInstance().appointmentQuestionOptionModel
//							.put(jobj.getString(Constants.QUESTION_ID),
//									Singleton.getInstance().apponintQuestionArrayModel);
							Singleton.getInstance().appointmentQuestionOptionModel
						       .put(leadModel.questionId,
						         optionsArr);
						}
						else if (jobj.getString(Constants.QUESTION_TYPE)
								.equals("RadioButton")) {
							Singleton.getInstance().radioArrayModel.clear();
							ArrayList<AppointmentRadioButtonModel>radioOptionsArray=new ArrayList<AppointmentRadioButtonModel>();
							localInnerJsonArray = jobj
									.getJSONArray(Constants.CHOICE_OPTION);
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
									jobj.getString(Constants.QUESTION_ID));
//							Singleton.getInstance().appointmentQuestionRadioModel
//									.put(jobj.getString(Constants.QUESTION_ID),
//											Singleton.getInstance().radioArrayModel);
							
							Singleton.getInstance().appointmentQuestionRadioModel
					         .put(jobj.getString(Constants.QUESTION_ID),
					           radioOptionsArray);

						}
						
						leadModel.responseId=jobj.getString(Constants.RESPONSE_ID);
						leadModel.questionsResponse=jobj.getString(Constants.QUESTION_RESPONSE);
						leadModel.responseChoiceId=jobj.getString(Constants.RESPONSE_CHOICE_ID);
						leadModel.type=jobj.getString(Constants.KEY_ADDPROSPECT_TYPE);
						leadModel.ordinalByOrdinal=jobj.getString(Constants.ORDER_BY_ORDINAL);
						Singleton.getInstance().leadQuestionaryModel.add(leadModel);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(Singleton.getInstance().leadQuestionaryModel.size()>0){
					leadExecute.leadInterface();
				}else{
					
				}
				
				
			}else{
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR, Toast.LENGTH_SHORT).show();
			}
			
		}else{
			
			Toast.makeText(mContext, Constants.TOAST_INTERNET, Toast.LENGTH_SHORT).show();
			
		}
		
		
		
		
	}

	
	public void dismissDialog(){
		
		if(dialog !=null){
			dialog.dismiss();
			dialog=null;
		}
	}
}
