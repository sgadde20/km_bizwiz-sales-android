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
import com.webparadox.bizwizsales.models.DiscountsModel;
import com.webparadox.bizwizsales.models.ProposalListModel;
import com.webparadox.bizwizsales.models.QuotedProuctsModel;

public class ProposalAsynTask extends AsyncTask<Void, Void, Void> {
	String mRequestUrl;
	ActivityIndicator mDialog;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray, innerLocalJsonArray, inner2LocalJsonArray;
	ProposalListModel proposalModel;
	QuotedProuctsModel quotedModel;
	DiscountsModel discountModel;
	Context mContext;
	String dealerId, appnResultId;
	IDispoSubDispoQuestions iDispoSubDispo;

	public ProposalAsynTask(Context context, String dealerId,
			String appntResultId) {
		this.mContext = context;
		this.dealerId = dealerId;
		this.appnResultId = appntResultId;
		serviceHelper = new ServiceHelper(mContext);
		iDispoSubDispo = (IDispoSubDispoQuestions) mContext;
	}

	public interface IDispoSubDispoQuestions {
		void DispoSubDispoQstns();
	}

	@Override
	protected void onPreExecute() {
		if (mDialog != null) {
			if (mDialog.isShowing()) {
				mDialog.dismiss();
				mDialog = null;
			} else {
				mDialog = null;
			}
		}
		mDialog = new ActivityIndicator(mContext);
		mDialog.show();
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		responseJson = serviceHelper.jsonSendHTTPRequest("",
				Constants.GenerateProposalUrl + "?" + "DealerId=" + dealerId
						+ "&" + "AppointmentResultId=" + appnResultId,
				Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			if (responseJson != null) {
				if (responseJson.has(Constants.PROPOSAL)) {
					try {
						localJsonArray = responseJson
								.getJSONArray(Constants.PROPOSAL);
						Singleton.getInstance().dispoId="";
						Singleton.getInstance().SubDispoId="";
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject jobj = localJsonArray.getJSONObject(i);
							Singleton.getInstance().dispoId = jobj
									.getString(Constants.KEY_DISPO_ID);
							Singleton.getInstance().SubDispoId = jobj
									.getString(Constants.subDispoId);
							Singleton.getInstance().lastNotes = jobj
									.getString(Constants.WorkOrderNotes);

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Log.d("Dispo & SubDispoId", Singleton.getInstance().dispoId
							+ "  " + Singleton.getInstance().SubDispoId);
					iDispoSubDispo.DispoSubDispoQstns();

				}

			}
		}
	}
}
