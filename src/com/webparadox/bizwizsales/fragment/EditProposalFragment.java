package com.webparadox.bizwizsales.fragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.webparadox.bizwizsales.CustomerAppointmentsActivity;
import com.webparadox.bizwizsales.NewProposalSummaryActivity;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.asynctasks.SaveSummaryAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class EditProposalFragment extends Fragment implements OnClickListener{
	static int position;
	static String summary;
	Context context = null;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showImageOnLoading(R.drawable.ic_stub)
	.showImageOnFail(R.drawable.ic_error)
	.showImageForEmptyUri(R.drawable.ic_empty).cacheInMemory(true)
	.cacheOnDisk(true).considerExifParams(true)
	.bitmapConfig(Bitmap.Config.RGB_565).build();
	String Amount, Quantity, Note, dealerId, empId, appoResId, status;
	static Typeface droidSans, droidSansBold;
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;
	boolean mShowPrice = true;
	EditText edAmount, edQuantity, edNote;
	ImageView editThumnail, editVideoPlay;
	TextView editProductDescription, editMaterialName,txtAmount;
	Button editUpdate, editDelete;
	SaveSummaryAsyncTask saveTask; 
	DeleteTask deleteTask;
	ActivityIndicator dialog;   

	public static EditProposalFragment newInstance(int Position,String Summary) {
		EditProposalFragment f = new EditProposalFragment();
		position = Position;
		summary = Summary;
		return f;	
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
		userData = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		empId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		appoResId = userData.getString(Constants.KEY_APPT_RESULT_ID, "0");
		imageLoader.init(ImageLoaderConfiguration
				.createDefault(context));

		droidSans = Typeface.createFromAsset(context.getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(context.getAssets(),
				"DroidSans-Bold.ttf");
		preferenceSettings = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		settingsEditor = preferenceSettings.edit();
		if (preferenceSettings.contains(Constants.PREF_SWITCH_SHOW_PRICE)) {
			mShowPrice = preferenceSettings.getBoolean(
					Constants.PREF_SWITCH_SHOW_PRICE, false);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		final View rootView1 = inflater.inflate(R.layout.edit_proposal_activity, container,false);
		 if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		    	getActivity().getFragmentManager().popBackStack();
		    } 
		RelativeLayout linearLayout1 = (RelativeLayout)rootView1.findViewById(R.id.linearLayout1);
		linearLayout1.setVisibility(View.GONE);
		edAmount = (EditText)rootView1.findViewById(R.id.editAmount);
		edNote = (EditText)rootView1.findViewById(R.id.editNotes);
		edQuantity = (EditText)rootView1.findViewById(R.id.editQuantity);
		editVideoPlay = (ImageView)rootView1.findViewById(R.id.editVideoPlay);
		editThumnail = (ImageView)rootView1.findViewById(R.id.editThumnail);
		editProductDescription = (TextView)rootView1.findViewById(R.id.editproductDescription);
		editMaterialName = (TextView)rootView1.findViewById(R.id.editMaterialName);
		editUpdate = (Button)rootView1.findViewById(R.id.editUpdate);
		editUpdate.setOnClickListener(this);
		editDelete = (Button)rootView1.findViewById(R.id.editDelete);
		editDelete.setOnClickListener(this);
		if(Singleton.getInstance().quotedProductModel.size()>0){
		edAmount.setText(Singleton.getInstance().quotedProductModel
				.get(position).QuotedAmount);
		edNote.setText(Singleton.getInstance().quotedProductModel
				.get(position).BoundProductNotes);
		edQuantity.setText(Singleton.getInstance().quotedProductModel
				.get(position).Quantity);
		editMaterialName.setText(Singleton.getInstance().quotedProductModel
				.get(position).ProductQuoted);


		txtAmount = (TextView)rootView1.findViewById(R.id.textViewAmount);
		txtAmount.setTypeface(droidSansBold);
		if (mShowPrice) {
			txtAmount.setText("$" + Singleton.getInstance().quotedProductModel
					.get(position).UnitSellingPrice);
		} else {
			txtAmount.setText("*******");
		}

		edQuantity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				float qty = (float) 0.00;
				if (s.length() > 0) {
					qty = Float.parseFloat(s.toString());
				}

				float unitPrice = Float.parseFloat(Singleton.getInstance().quotedProductModel
						.get(position).UnitSellingPrice.replaceAll(",", ""));
				if(unitPrice>0){
					BigDecimal bd = new BigDecimal(("" + (qty * unitPrice)));
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					String valueAmount = String.valueOf(bd);
					edAmount.setText(valueAmount);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		editVideoPlay.setVisibility(View.GONE);
		imageLoader
		.displayImage(Singleton.getInstance().quotedProductModel
				.get(position).ProductImageURL, editThumnail,
				options);

		edAmount.addTextChangedListener(new TextWatcher() {
			private boolean mFormatting; // this is a flag which prevents the

			// stack overflow.

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!mFormatting) {
					mFormatting = true;
					String editValue = edAmount.getText().toString();
					editValue = editValue.replace(".", "");
					editValue = editValue.replace("$", "");
					editValue = editValue.replaceAll("\\s+", "");
					editValue = editValue.replaceAll("[-.^:,()]", "");
					editValue = editValue.replaceAll("[^\\d.]", "");
					if (editValue.length() == 1) {
						edAmount.setText("0.0" + editValue);
						edAmount.setSelection(edAmount.getText().toString().length());
					} else if (editValue.length() == 2) {
						edAmount.setText("0." + editValue);
						edAmount.setSelection(edAmount.getText().toString().length());
					} else if (editValue.length() > 2) {
						int len = editValue.length();
						double amount = Double.parseDouble(editValue.substring(
								0, len - 2));
						NumberFormat formatter = new DecimalFormat(
								"###,###,###.##");
						System.out.println("The Decimal Value is: "
								+ formatter.format(amount));
						Log.d("The Decimal Value is: ",
								"$"
										+ formatter.format(amount)
										+ "."
										+ editValue.substring(len - 2,
												editValue.length()));

						edAmount.setText(formatter.format(amount)
								+ "."
								+ editValue.substring(len - 2,
										editValue.length()));
						edAmount.setSelection(edAmount.getText().toString().length());
					}
					mFormatting = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		if(Constants.isLock.equalsIgnoreCase("true")){

			edAmount.setEnabled(false);
			edNote.setEnabled(false);
			edQuantity.setEnabled(false);
			editUpdate.setEnabled(false);
			editUpdate.setBackground(context.getResources().getDrawable(
					R.drawable.resolve_disable_background));
			editDelete.setEnabled(false);
		}else{
			edAmount.setEnabled(true);
			edNote.setEnabled(true);
			edQuantity.setEnabled(true);
			editUpdate.setEnabled(true);
			editUpdate.setBackground(context.getResources().getDrawable(
					R.drawable.selector_prospect_save_button));
			editDelete.setEnabled(true);

		}
		}
		

		return rootView1;
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editUpdate:
			Amount = edAmount.getText().toString();
			Note = edNote.getText().toString();
			Quantity = edQuantity.getText().toString();
			if (Amount.length() > 0 && Quantity.length() > 0) {
				saveTask = new SaveSummaryAsyncTask(context, saveRequest(),summary, "isBack");
				saveTask.execute();
			} else {
				Toast.makeText(context,
						"Plese enter the valid amount & qantity",
						Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.editDelete:

			deleteTask = new DeleteTask();
			deleteTask.execute();

			break;
		}
	}

	public JSONObject saveRequest() {

		JSONArray mRequestJson = new JSONArray();

		JSONObject ProductsData = new JSONObject();
		JSONObject product = new JSONObject();

		JSONObject reqObj_data = new JSONObject();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
			reqObj_data.put(Constants.KEY_APPT_RESULT_ID, appoResId);
			reqObj_data.put(Constants.JSON_KEY_SALES_PROCESS_MATERIAL_ID,
					Singleton.getInstance().quotedProductModel
					.get(position).MaterialId);
			reqObj_data.put(Constants.QUOTED_QUANTITY, Quantity);
			reqObj_data.put(Constants.QUOTED_AMOUNT, Amount.replace("$", "")
					.replace(",", ""));
			reqObj_data.put(Constants.BOUND_PRODUCT_ID,
					Singleton.getInstance().quotedProductModel
					.get(position).BoundproductId);
			reqObj_data.put(Constants.JSON_KEY_FOLLOWUP_NOTES, Note);
			reqObj_data.put(Constants.JSON_KEY_SALES_PROCESS_PRODUCT_TYPE,
					Singleton.getInstance().quotedProductModel
					.get(position).ProductType);
			if (Singleton.getInstance().quotedProductModel.get(position).ProductType != null) {
				Log.d("Value = ",
						""
								+ Singleton.getInstance().quotedProductModel
								.get(position).ProductType);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRequestJson.put(reqObj_data);
		try {
			product.put("PRODUCTS", mRequestJson);
			JSONObject reqObj_data2 = new JSONObject();
			if(Singleton.getInstance().proposalList.size()>0){
				reqObj_data2.put(Constants.DESCRIPTION, Singleton.getInstance().proposalList.get(0).getWorkOrderNotes());
			}else{
				reqObj_data2.put(Constants.DESCRIPTION, "");
			}
			reqObj_data2.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj_data2.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
			reqObj_data2.put(Constants.KEY_APPT_RESULT_ID, appoResId);
			JSONArray mRequestJson2 = new JSONArray();
			mRequestJson2.put(reqObj_data2);
			product.put("WORKDESCRIPTION", mRequestJson2);

			ProductsData.put("ProductsData", product);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("res", ProductsData.toString());
		return ProductsData;
	}

	public JSONObject deleteReq() {

		JSONArray mRequestJson = new JSONArray();

		JSONObject ProductsData = new JSONObject();
		JSONObject product = new JSONObject();

		JSONObject reqObj_data = new JSONObject();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
			reqObj_data.put(Constants.KEY_APPT_RESULT_ID, appoResId);
			reqObj_data.put(Constants.BOUND_PRODUCT_ID,
					Singleton.getInstance().quotedProductModel
					.get(position).BoundproductId);
			reqObj_data.put(Constants.KEY_ADDPROSPECT_TYPE,
					Singleton.getInstance().quotedProductModel
					.get(position).ProductType);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mRequestJson.put(reqObj_data);

		try {
			product.put("PRODUCTS", mRequestJson);

			ProductsData.put("ProductsData", product);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("res", ProductsData.toString());
		return ProductsData;

	}

	public class DeleteTask extends AsyncTask<Void, Void, Void> {

		ServiceHelper serviceHelper;
		JSONObject responseJson, innerobj;
		ArrayList<JSONObject> mRequestJson;
		JSONArray localJsonArray;

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			dialog = new ActivityIndicator(context);
			dialog.show();
			innerobj = new JSONObject();
			localJsonArray = new JSONArray();
			responseJson = new JSONObject();
			mRequestJson = new ArrayList<JSONObject>();
			serviceHelper = new ServiceHelper(context);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(deleteReq()
					.toString(), Constants.DeleteProductUrl,
					Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		public void onPostExecute(Void Res) {
			super.onPostExecute(Res);
			if (responseJson != null) {
				if (responseJson.has("DP")) {
					try {
						innerobj = responseJson.getJSONObject("DP");
						status = innerobj
								.getString(Constants.KEY_ADDPROSPECT_STATUS);
						if (status.equals(Constants.VALUE_SUCCESS)) {

							if (summary.equals("summary")) {

								Intent proposalIntent = new Intent(context,
										NewProposalSummaryActivity.class);
								startActivity(proposalIntent);
								getActivity().finish();

							} else {
								Constants.isProposalList = true;
								Constants.isSelectProduct = false;
								Intent cusAppIntent = new Intent(context,
										CustomerAppointmentsActivity.class);
								startActivity(cusAppIntent);
								getActivity().finish();
							}

						}
					} catch (JSONException e) { // TODO Auto-generated catch
						// block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}
			dismiss();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (deleteTask != null) {
			deleteTask.cancel(true);
			deleteTask = null;
		}
		if (saveTask != null) {
			saveTask.cancel(true);
			saveTask = null;
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.cancel();
			}
			dialog = null;
		}
	}
	
	
	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
}
