package com.webparadox.bizwizsales.asynctasks;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.webparadox.bizwizsales.NewProposalSummaryActivity;
import com.webparadox.bizwizsales.NewProposalSummaryActivity.TitlesFragment.FragmentCallback;
import com.webparadox.bizwizsales.adapter.SmartSummaryAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.DiscountsModel;
import com.webparadox.bizwizsales.models.ProposalListModel;
import com.webparadox.bizwizsales.models.QuotedProuctsModel;

public class GetProposalAsyncTask extends AsyncTask<Void, Void, Void> {

	String mRequestUrl, dealerId, appointmentResultId;
	ActivityIndicator mDialog = null;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray, innerLocalJsonArray, inner2LocalJsonArray;
	ProposalListModel proposalModel;
	QuotedProuctsModel quotedModel;
	DiscountsModel discountModel;
	Context mContext;
	String activityName;
	onProposalListener onproposalListener;
	ListView proposalListview;
	TextView productTotal;
	double amount;
	String total;
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;

	public interface onProposalListener {

		void onProposalExecute(ActivityIndicator mDialog);
	}

	private FragmentCallback mFragmentCallback;

	public GetProposalAsyncTask(Context context, String dealId,
			String appointmentResultId, String string) {
		this.activityName = string;
		this.mContext = context;
		this.dealerId = dealId;
		this.appointmentResultId = appointmentResultId;
		if(string.equalsIgnoreCase("SalesProcessProductSubCategoryFragment")){
			
		}else{
			this.onproposalListener = (onProposalListener) mContext;
		}
		

	}

	public GetProposalAsyncTask(FragmentCallback fragmentCallback,String dealId,
			String appointmentResultId,String string,Context context) {

		mFragmentCallback = fragmentCallback;
		this.dealerId = dealId;
		this.appointmentResultId = appointmentResultId;
		this.activityName = string;
		this.mContext = context;
	}

	public GetProposalAsyncTask(Context context, String dealerID2,
			String appointmentResultId2, boolean b) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.activityName = "salesProcess";
		this.dealerId = dealerID2;
		this.appointmentResultId = appointmentResultId2;

	}

	public GetProposalAsyncTask(Context context, String dealerId2,
			String appointmentResultId2, ListView proposalListview,
			String string, TextView productTotal) {
		// TODO Auto-generated constructor stub
		this.activityName = string;
		this.mContext = context;
		this.dealerId = dealerId2;
		this.appointmentResultId = appointmentResultId2;
		this.onproposalListener = (onProposalListener) mContext;
		this.proposalListview = proposalListview;
		this.productTotal = productTotal;
	}

	@Override
	public void onPreExecute() {
		super.onPreExecute();
		preferenceSettings = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		settingsEditor = preferenceSettings.edit();
		inner2LocalJsonArray = new JSONArray();
		innerLocalJsonArray = new JSONArray();
		localJsonArray = new JSONArray();
		responseJson = new JSONObject();
		mRequestJson = new ArrayList<JSONObject>();
		serviceHelper = new ServiceHelper(mContext);
		
		try {
			if (mDialog != null) {
				if(mDialog.isShowing()){
					//mDialog.dismiss();
				}
				mDialog = null;
			}else{
				mDialog = new ActivityIndicator(mContext);
				mDialog.show();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		mRequestUrl = Constants.GenerateProposalUrl + "?" + "DealerId="
				+ dealerId + "&" + "AppointmentResultId=" + appointmentResultId;

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
	public void onPostExecute(Void Result) {
		super.onPostExecute(Result);

		if (responseJson != null) {
			if (responseJson.has(Constants.PROPOSAL)) {
				dismissDialog();
				try {
					localJsonArray = responseJson
							.getJSONArray(Constants.PROPOSAL);
					Singleton.getInstance().quotedProductModel.clear();
					Singleton.getInstance().discountModel.clear();
					Singleton.getInstance().proposalList.clear();
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(i);
						proposalModel = new ProposalListModel();
						proposalModel.setAppointmentResultId(jobj
								.getString(Constants.KEY_APPT_RESULT_ID));
						proposalModel.setDisposition(jobj
								.getString(Constants.disposition));
						proposalModel.setDispoId(jobj
								.getString(Constants.KEY_DISPO_ID));
						proposalModel.setSubDispo(jobj
								.getString(Constants.subDispo));
						proposalModel.setSubDispoId(jobj
								.getString(Constants.subDispoId));
						proposalModel.setProposalAmount(jobj.getString(
								Constants.ProposalAmount).replace("$", ""));
						proposalModel.setWorkOrderNotes(jobj
								.getString(Constants.WorkOrderNotes));
						proposalModel.setSignatureExists(jobj
								.getString(Constants.SignatureExists));
						proposalModel.setSignatureURL(jobj
								.getString(Constants.SignatureURL));
						innerLocalJsonArray = jobj
								.getJSONArray(Constants.QuotedProducts);
						for (int j = 0; j < innerLocalJsonArray.length(); j++) {
							JSONObject innerJobj = innerLocalJsonArray
									.getJSONObject(j);
							quotedModel = new QuotedProuctsModel();
							quotedModel.BoundproductId = (innerJobj
									.getString(Constants.BoundproductId));
							quotedModel.ProductQuoted = (innerJobj
									.getString(Constants.ProductQuoted));
							quotedModel.Quantity = (innerJobj
									.getString(Constants.Quantity));
							quotedModel.QuotedAmount = (innerJobj
									.getString(Constants.QUOTED_AMOUNT)
									.replace("$", ""));
							quotedModel.BoundProductNotes = (innerJobj
									.getString(Constants.BoundProductNotes));
							quotedModel.ProductImageURL = (innerJobj
									.getString(Constants.ProductImageURL));
							quotedModel.MaterialId = (innerJobj
									.getString(Constants.MaterialId));
							quotedModel.ProductType = (innerJobj
									.getString(Constants.Type));
							quotedModel.UnitSellingPrice = (innerJobj
									.getString(Constants.JSON_KEY_SALES_PROCESS_UNIT_SELLING_PRICE));
							
							Singleton.getInstance().quotedProductModel.add(quotedModel);
						}
						Singleton.getInstance().proposalList.add(proposalModel);
					}

					if(activityName.equals("TitlesFragment")){
						 mFragmentCallback.onTaskDone();
					}
					
					if (activityName.equals("proposal")) {
						onproposalListener.onProposalExecute(mDialog);
					}
					if (activityName.equals("salesProcess")) {
						Intent generateProp = new Intent(mContext,
								NewProposalSummaryActivity.class);
						mContext.startActivity(generateProp);
						//dismissDialog();

					}
					if (activityName.equals("customerAppointment")) {
						Intent generateProp = new Intent(mContext,
								NewProposalSummaryActivity.class);
						mContext.startActivity(generateProp);
					//	dismissDialog();

					}
					if (activityName.equals("check")) {
						if (Constants.isProposal) {
							Log.e("", ""+Singleton.getInstance().quotedProductModel);
							if (Singleton.getInstance().quotedProductModel.size() > 0) {
								setTotal();
							} else {
								productTotal.setText("$0.00");
							}
							SmartSummaryAdapter smartApapter = new SmartSummaryAdapter(
									mContext);
							proposalListview.setAdapter(smartApapter);
							//dismissDialog();
						} else {
							Intent generateProp = new Intent(mContext,
									NewProposalSummaryActivity.class);
							mContext.startActivity(generateProp);
						//	dismissDialog();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
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
		try {
			if (mDialog != null) {
				if(mDialog.isShowing()){
					mDialog.dismiss();
				}
				mDialog = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public double setTotal() {
		amount = 0;
		for (int i = 0; i < Singleton.getInstance().quotedProductModel.size(); i++) {

			Log.d("Previous Amount", amount + "");
			double value1;
			if(Singleton.getInstance().quotedProductModel
					.get(i).QuotedAmount.toString().replace(",", "") != null && Singleton.getInstance().quotedProductModel
							.get(i).QuotedAmount.toString().replace(",", "") != "")
			 value1 = Double.parseDouble(Singleton.getInstance().quotedProductModel.get(i).QuotedAmount.toString().replace(",", ""));
			else
				value1 = 0;
			
			amount = amount + value1;
		}
		String amountValue = addDollerSymbol(String.valueOf(amount).replace(",", "").replace("$", ""));
		productTotal.setText(amountValue);
		// productTotal.setText("$"+total);
		settingsEditor.putString("Amount", amountValue);
		settingsEditor.commit();
		return amount;
	}

	public String addDollerSymbol(String value) {
		if (Double.parseDouble(value.replace(",", "").replace("-", "")) < 1) {
			NumberFormat formatter = new DecimalFormat("#0.00");
			value = formatter.format(Double.parseDouble(value));
			value = "$" + value;
		}else{
			if (value.contains("-")) {

				double amount = Double.parseDouble(value
						.substring(1, value.length()).replace(",", "")
						.replace("$", ""));
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				String formatted = formatter.format(amount);

				value = value.subSequence(0, 1) + "$" + formatted;
			} else {
				double amount = Double.parseDouble(value.replace(",", "").replace(
						"$", ""));
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				String formatted = formatter.format(amount);

				value = "$" + formatted;
			}
		}

		return value;
	}

}
