package com.webparadox.bizwizsales.asynctasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.webparadox.bizwizsales.adapter.SalesProcessProductGridViewAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.SpProductCategoryModel;
import com.webparadox.bizwizsales.models.SpProductMaterialModel;
import com.webparadox.bizwizsales.models.SpProductSubCategoryModel;

public class SpProductsAsyncTask extends AsyncTask<Void, Integer, Void> {
	ActivityIndicator mDialog;
	Context mContext;
	JSONObject responseJson;
	ServiceHelper serviceHelper;
	JSONArray localJsonArray;
	GridView productCategoryGridView;
	SpProductCategoryModel productCategoryModel;
	SpProductSubCategoryModel productSubCategoryModel;
	SpProductMaterialModel productMaterialModel;
	String strDelarId;


	public SpProductsAsyncTask(Context context,
			GridView categoryGridView, String dealerID) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.productCategoryGridView = categoryGridView;
		serviceHelper = new ServiceHelper(this.mContext);
		strDelarId=dealerID;
	}

	@Override
	protected void onPreExecute() {
		Singleton.getInstance().salesProcessProductsList.clear();
		try {
			Log.e("mDialog 1 ====> ", "mDialog 1");
			if (mDialog != null) {
				if (mDialog.isShowing()) {
					mDialog.dismiss();
					mDialog = null;
				} else {
					mDialog = null;
				}
			}else{
				mDialog = new ActivityIndicator(mContext);
				mDialog.show();	
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub
		responseJson = serviceHelper.jsonSendHTTPRequest(
				"", Constants.SALES_PROCESS_PRODUCTS+strDelarId,
				Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dismissDialog();
		if (responseJson != null) {
		if (responseJson.has(Constants.JSON_KEY_SALES_PROCESS_CATEGORY)) {
			try {
				localJsonArray = responseJson
						.getJSONArray(Constants.JSON_KEY_SALES_PROCESS_CATEGORY);
				for (int i = 0; i < localJsonArray.length(); i++) {
					JSONObject jobj = localJsonArray.getJSONObject(i);
					productCategoryModel = new SpProductCategoryModel();
					productCategoryModel.mCategoryName = jobj.get(
							Constants.JSON_KEY_SALES_PROCESS_CATEGORY_NAME).toString();
					productCategoryModel.mCategoryId = jobj.get(
							Constants.JSON_KEY_SALES_PROCESS_CATEGORY_ID).toString();
					productCategoryModel.mCatgeoryImageURL = jobj.get(
							Constants.JSON_KEY_SALES_PROCESS_CATEGORY_IMAGE_URL).toString();


					if (jobj.has(Constants.JSON_KEY_SALES_PROCESS_SUB_CATEGORY)) {
						JSONArray subCatJsonArray = jobj
								.getJSONArray(Constants.JSON_KEY_SALES_PROCESS_SUB_CATEGORY);
						for (int j = 0; j < subCatJsonArray.length(); j++) {
							JSONObject subjobj = subCatJsonArray.getJSONObject(j);
							productSubCategoryModel = new SpProductSubCategoryModel();
							productSubCategoryModel.mSubcategoryName= subjobj.get(
									Constants.JSON_KEY_SALES_PROCESS_SUB_CATEGORY_NAME).toString();
							productSubCategoryModel.mSubcategoryId= subjobj.get(
									Constants.JSON_KEY_SALES_PROCESS_SUB_CATEGORY_ID).toString();


							if (subjobj.has(Constants.JSON_KEY_SALES_PROCESS_MATERIAL)) {
								JSONArray materialJsonArray = subjobj
										.getJSONArray(Constants.JSON_KEY_SALES_PROCESS_MATERIAL);
								for (int k = 0; k < materialJsonArray.length(); k++) {
									JSONObject materialobj = materialJsonArray.getJSONObject(k);
									productMaterialModel = new SpProductMaterialModel();
									productMaterialModel.mMaterialName= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_MATERIAL_NAME).toString();
									productMaterialModel.mMaterialId= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_MATERIAL_ID).toString();
									productMaterialModel.mProductDescription= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_PRODUCT_DESCRIPTION).toString();
									productMaterialModel.mProductImageURL= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_PRODUCT_IMAGE_URL).toString();
									productMaterialModel.mProductVideoURL= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_PRODUCT_VIDEO_URL).toString();
									productMaterialModel.mUnitSellingPrice= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_UNIT_SELLING_PRICE).toString();
									productMaterialModel.mProductType= materialobj.get(
											Constants.JSON_KEY_SALES_PROCESS_PRODUCT_TYPE).toString();
									if (productMaterialModel.mProductType != null){
										Log.d("Value = ", ""+ productMaterialModel.mProductType);
										Log.d("Value = ", ""+ materialobj.get(
												Constants.JSON_KEY_SALES_PROCESS_PRODUCT_TYPE).toString());
									}	
									
									productSubCategoryModel.mListMaterials.add(productMaterialModel);
								}

							}
							productCategoryModel.mListSubCategory.add(productSubCategoryModel);
						}
					}
					Singleton.getInstance().salesProcessProductsList.add(productCategoryModel);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
		if (responseJson != null) {
			if (localJsonArray != null) {
				if (localJsonArray.length() > 0) {
					SalesProcessProductGridViewAdapter salesProcessProductGridAdapter = new SalesProcessProductGridViewAdapter(
							mContext);
					productCategoryGridView.setAdapter(salesProcessProductGridAdapter);
				} else {
					SalesProcessProductGridViewAdapter salesProcessProductGridAdapter = new SalesProcessProductGridViewAdapter(
							mContext);
					productCategoryGridView.setAdapter(salesProcessProductGridAdapter);
				}
			}

		} else {
			Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
					Constants.TOASTMSG_TIME).show();
		}
	}

	public void dismissDialog() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

}

