package com.webparadox.bizwizsales.dialogs;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.asynctasks.PhonenumbersAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class AddPhonenumberDialog implements OnClickListener,
		OnItemSelectedListener {

	Dialog dialog;
	Context mContext;
	Typeface droidSans, droidSansBold;
	ArrayAdapter<String> dataAdapter;
	ActivityIndicator pDialog;
	ServiceHelper serviceHelper;
	JSONObject responseJson;
	ArrayList<JSONObject> mRequestJson;
	SharedPreferences userData;
	String empId, dealerID, cusID, phoneNumber;
	EditText addPhonenumberEdit;
	Spinner addPhonenumberSpinner;
	String numberType, status;
	Boolean mFormatting = false;
	
	public PhonenumbersAsyncTask phoneTask;
	public savePhoneNumbertask saveTask;

	public AddPhonenumberDialog(Context mContext2, String cusID) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext2;
		this.cusID = cusID;
		mRequestJson = new ArrayList<JSONObject>();
		responseJson = new JSONObject();
		serviceHelper = new ServiceHelper(this.mContext);
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		userData = mContext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		empId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
	}

	public void AddPhoneNumber() {

		dialog = new Dialog(mContext);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(WMLP);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.add_phonenumber_dialog);

		addPhonenumberEdit = (EditText) dialog
				.findViewById(R.id.add_phonenumber_name);
		addPhonenumberEdit
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(14) });
		addPhonenumberEdit.clearFocus();
		addPhonenumberEdit.setTypeface(droidSans);
		// addPhonenumberEdit.addTextChangedListener(watch);

		addPhonenumberEdit.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!mFormatting) {
					mFormatting = true;
					String editValue = "";
					editValue = addPhonenumberEdit.getText().toString()
							.replaceAll("\\s+", "");
					editValue = editValue.replaceAll("[-.^:,()]", "");
					if (editValue.length() < 4) {
						addPhonenumberEdit.setText(editValue);
					} else if (editValue.length() < 7) {
						addPhonenumberEdit.setText("("
								+ editValue.substring(0, 3) + ") "
								+ editValue.substring(3, editValue.length()));
					} else if (editValue.length() < 11) {
						addPhonenumberEdit.setText("("
								+ editValue.substring(0, 3) + ") "
								+ editValue.substring(3, 6) + "-"
								+ editValue.substring(6, editValue.length()));
					}
					addPhonenumberEdit.setSelection(addPhonenumberEdit
							.getText().toString().length());
					mFormatting = false;
				}
			}
		});
		addPhonenumberEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				addPhonenumberEdit.setCursorVisible(true);
				return false;
			}
		});
		addPhonenumberSpinner = (Spinner) dialog
				.findViewById(R.id.add_phonenumber_spinner);
		addPhonenumberSpinner.setOnItemSelectedListener(this);
		Button addPhonenumberCancelBtn = (Button) dialog
				.findViewById(R.id.add_phonenumber_cancel_btn);
		addPhonenumberCancelBtn.setTypeface(droidSansBold);
		addPhonenumberCancelBtn.setOnClickListener(this);
		Button addPhonenumberSaveBtn = (Button) dialog
				.findViewById(R.id.add_phonenumber_save_btn);
		addPhonenumberSaveBtn.setTypeface(droidSansBold);
		addPhonenumberSaveBtn.setOnClickListener(this);

		ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
				mContext, R.layout.spinner_text,
				Singleton.getInstance().phoneTypeArray) {

			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);

				((TextView) v).setTypeface(droidSansBold);

				return v;
			}

			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				View v = super.getDropDownView(position, convertView, parent);

				((TextView) v).setTypeface(droidSans);

				return v;
			}
		};

		ptspinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		addPhonenumberSpinner.setAdapter(ptspinnerArrayAdapter);

		dialog.show();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.add_phonenumber_cancel_btn:
			dissmissDialog();
			break;
		case R.id.add_phonenumber_save_btn:
			phoneNumber = addPhonenumberEdit.getText().toString();
			if (phoneNumber.length() > 0) {
				dissmissDialog();
				saveTask = new savePhoneNumbertask();
				saveTask.execute();
			}

			break;
		default:
			break;
		}

	}

	public void dissmissDialog() {

		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

	public class savePhoneNumbertask extends AsyncTask<Void, Integer, Void> {

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
			String typeId = Singleton.getInstance().phoneTypeIdArray
					.get(Singleton.getInstance().phoneTypeArray
							.indexOf(numberType));
			Log.d("DEALER ID", dealerID);
			Log.d("KEY_LOGIN_EMPLOYEE_ID", empId);
			Log.d("JSON_KEY_CUSTOMER_ID", cusID);
			Log.d("JSON_KEY_PHONETYPE_ID", typeId);
			Log.d("KEY_ADDPROSPECT_PHONE_NUMBER", cusID);

			final JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, empId);
				reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, cusID);
				reqObj_data.put(Constants.JSON_KEY_PHONETYPE_ID, typeId);
				reqObj_data.put(Constants.KEY_ADDPROSPECT_PHONE_NUMBER,
						phoneNumber);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRequestJson.add(reqObj_data);
			Log.d("REQ", mRequestJson.toString());

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.SAVE_PHONE_NUMBER_URL,
					Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			JSONObject localJsonObject;
			try {
				if (responseJson != null) {

					if (responseJson.has(Constants.JSON_KEY_SAVE_PHONENUMBER)) {
						try {
							localJsonObject = responseJson
									.getJSONObject(Constants.JSON_KEY_SAVE_PHONENUMBER);
							status = localJsonObject
									.getString(Constants.KEY_ADDPROSPECT_STATUS);
							if (status.equals(Constants.VALUE_SUCCESS)) {
								final JSONObject reqObj_data = new JSONObject();
								final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
								reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID,
										dealerID);
								reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID,
										cusID);
								reqData.add(reqObj_data);
								phoneTask = new PhonenumbersAsyncTask(
										mContext, Constants.GET_PHONENUMBER_URL,
										Constants.REQUEST_TYPE_POST, reqData);
								phoneTask.execute();
							} else {

								Toast.makeText(mContext,
										Constants.TOAST_CONNECTION_ERROR,
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) { // TODO Auto-generated catch
													// block
							e.printStackTrace();
						}

					} else {
						Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Toast.makeText(mContext, Constants.TOAST_INTERNET,
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				// TODO: handle exception
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		numberType = parent.getItemAtPosition(position).toString();

		// Showing selected spinner item

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
}
