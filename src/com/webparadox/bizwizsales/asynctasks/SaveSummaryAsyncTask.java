package com.webparadox.bizwizsales.asynctasks;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.widget.Toast;

import com.webparadox.bizwizsales.CheckoutActivity;
import com.webparadox.bizwizsales.CustomerAppointmentsActivity;
import com.webparadox.bizwizsales.MainActivity;
import com.webparadox.bizwizsales.NewProposalSummaryActivity;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.NewProposalSummaryActivity.DetailsFragment;
import com.webparadox.bizwizsales.fragment.CheckoutFragment;
import com.webparadox.bizwizsales.fragment.EditProposalFragment;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class SaveSummaryAsyncTask extends AsyncTask<Void, Integer, Void>{

	ActivityIndicator dialog;
	Context mContext;
	JSONObject jsonRequest;
	JSONObject responseJson,localJsonObject;
	ServiceHelper serviceHelper;
	String status,amount="";
	Boolean isAddnew=false;
	String isBack="";
	SharedPreferences userData;
	
	public SaveSummaryAsyncTask(Context context,JSONObject jsonRequst,String amount){

		this.mContext=context;
		this.jsonRequest=jsonRequst;
		this.amount=amount;
		this.isAddnew=false;
	}
	public SaveSummaryAsyncTask(Context context,JSONObject jsonRequst, String string, String string2){

		this.isBack=string2;
		this.mContext=context;
		this.jsonRequest=jsonRequst;
		this.amount=string;
		this.isAddnew=false;
	}

	public SaveSummaryAsyncTask(Context context, JSONObject saveRequest,
			boolean isAddnew) {
		// TODO Auto-generated constructor stub
		this.mContext=context;
		this.jsonRequest=saveRequest;
		this.isAddnew=isAddnew;
		
		
	}
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
	
		dialog = new ActivityIndicator(mContext);
		dialog.show();
		responseJson=new JSONObject();
		serviceHelper = new ServiceHelper(this.mContext);	
		userData = mContext.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		super.onPreExecute();
	}


	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(jsonRequest.toString(), Constants.SAVE_SUMMARY_URL, Constants.REQUEST_TYPE_POST);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if(responseJson !=null){
			if(responseJson.has(Constants.SAVE_PROPOSAL)){ 
				try { 
					localJsonObject =responseJson.getJSONObject(Constants.SAVE_PROPOSAL);
					status=localJsonObject.getString(Constants.KEY_ADDPROSPECT_STATUS); 

					if(status.equals(Constants.VALUE_SUCCESS)){
						if(!isAddnew){
							if(amount.length()>0){
								if(isBack.equals("isBack")){
									
									if(amount.equals("summary")){
										dissmissDialog();
										Intent cusAppIntent=new Intent(mContext,NewProposalSummaryActivity.class);
										mContext.startActivity(cusAppIntent);
										((Activity) mContext).finish();
									}else{
										dissmissDialog();
										Constants.isProposalList = true;
										Constants.isSelectProduct = false;
										Intent cusAppIntent=new Intent(mContext,CustomerAppointmentsActivity.class);
										mContext.startActivity(cusAppIntent);
										((Activity) mContext).finish();
									}
								}
								else if(isBack.equals("isHome")){
									dissmissDialog();
									Intent backIntent = new Intent(mContext,MainActivity.class);
									backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									mContext.startActivity(backIntent);
									((Activity) mContext).finish();
									
								}
								
								else{
									dissmissDialog();
									if (((Activity) mContext).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
										CheckoutFragment f = new CheckoutFragment();
										FragmentTransaction ft =  ((Activity)mContext).getFragmentManager()
												.beginTransaction();
										CheckoutFragment.newInstance(amount);
										ft.replace(R.id.details, f);
										ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
										ft.commit();
									}else{
										Intent checkoutActivity=new Intent(mContext,CheckoutActivity.class);
										checkoutActivity.putExtra("Amount", amount);
										mContext.startActivity(checkoutActivity);
									}
								}
							
							}else{
								if (((Activity) mContext).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
									dissmissDialog();
									Intent proposalsummaryIntent=new Intent(mContext,NewProposalSummaryActivity.class);
									proposalsummaryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									mContext.startActivity(proposalsummaryIntent);
								}else{
									dissmissDialog();
									Constants.isProposalList = false;
									Constants.isSelectProduct = true;
									Intent cusAppointIntent = new Intent(mContext,
											CustomerAppointmentsActivity.class);
									cusAppointIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									mContext.startActivity(cusAppointIntent);
									((Activity) mContext).finish();
								}
							}
						}else{
							dissmissDialog();
							if (((Activity) mContext).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
								Intent proposalsummaryIntent=new Intent(mContext,NewProposalSummaryActivity.class);
								proposalsummaryIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								mContext.startActivity(proposalsummaryIntent);
							}else{
								dissmissDialog();
								Constants.isProposalList = false;
								Constants.isSelectProduct = true;
								Intent cusAppointIntent = new Intent(mContext,
										CustomerAppointmentsActivity.class);
								cusAppointIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								mContext.startActivity(cusAppointIntent);
								((Activity) mContext).finish();
							}
						}
						

					}else{

						Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR, Toast.LENGTH_SHORT).show(); 
						dissmissDialog();
					}
				}
				catch (JSONException e) { // TODO Auto-generated catch block
					e.printStackTrace(); 
				} 


			}else{ 
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,Toast.LENGTH_SHORT).show(); 
				dissmissDialog();
			} 
		}else{
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,Toast.LENGTH_SHORT).show();
			dissmissDialog();
		}
		
	}
	public void dissmissDialog(){
		if(dialog !=null){
		if(dialog.isShowing()){
			dialog.dismiss();
			dialog=null;
		}else{
			dialog=null;
		}
	}}
}
