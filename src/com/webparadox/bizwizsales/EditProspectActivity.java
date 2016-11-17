package com.webparadox.bizwizsales;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.GPSTracker;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.PhoneNumberListModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditProspectActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	String customerId, employeeID, dealerID;
	ImageView imageViewBack, image_back_icon;
	static Context context;
	SharedPreferences userData;
	AsyncTask<Void, Void, Void> editProspectAsynTask;
	ActivityIndicator pDialog;
	String contact1, contact2, lastName, companyName, companyContact, email,
			physicalAddress, physicalZip, physicalCity, physicalState, typeId,
			subTypeId;
	EditText companyNameEdit, firstnameEdit, secondnameEdit, lastnameEdit,
			emailEdit, companyStreetAddressEdit, zipEdit, cityEdit,
			phoneNumber;
	int dynamicIncValue = 1000, dynamicPhone = 2000, dynamicPhoneTypeId = 3000;
	Button update, getAddress;
	Spinner statespinner, typeText;
	AsyncTask<Void, Void, Void> loadDropDownAsyncTask;
	AsyncTask<String, Void, Void> getCityAndStateFromZipCode;
	ArrayList<PhoneNumberListModel> listPhoneNumberListModel = new ArrayList<PhoneNumberListModel>();
	Typeface droidSans, droidSansBold;
	GPSTracker gpsTracker;
	double latitude, longitude;
	String addressLine = "", zipCode;
	JSONObject jsonRequestText, jsonResultText;
	ServiceHelper serviceHelper;
	GetCurrentAddress currentadd;
	LinearLayout phoneType;
	ArrayList<Integer> phoneValue = new ArrayList<Integer>();
	ArrayList<Integer> phoneValueType = new ArrayList<Integer>();
	int focusId = 0, statePosition = 0;
	AsyncTask<String, Void, Void> saveAsyncTask;
	String customerAddress, CustomerId, customerName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_prospect);
		context = this;
		serviceHelper = new ServiceHelper(context);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		customerId = userData.getString(Constants.JSON_KEY_CUSTOMER_ID, "");

		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");

		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		imageViewBack.setOnClickListener(this);
		image_back_icon = (ImageView) findViewById(R.id.image_back_icon);
		image_back_icon.setOnClickListener(this);

		Bundle extras = getIntent().getExtras();
		if (getIntent().hasExtra("CustomerFullName")) {
			customerName = extras.getString("CustomerFullName");
			customerAddress = extras.getString("CustomerAddress");
			CustomerId = extras.getString("CustomerId");
		} else {
			CustomerId = userData.getString("CustomerId",
					Constants.EMPTY_STRING);
			customerName = userData.getString("Name", Constants.EMPTY_STRING);
			customerAddress = userData.getString("Address",
					Constants.EMPTY_STRING);
		}

		ImageView logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});

		companyNameEdit = (EditText) findViewById(R.id.companyName);
		firstnameEdit = (EditText) findViewById(R.id.firstname);
		secondnameEdit = (EditText) findViewById(R.id.secondname);
		lastnameEdit = (EditText) findViewById(R.id.lastname);
		emailEdit = (EditText) findViewById(R.id.email);
		companyStreetAddressEdit = (EditText) findViewById(R.id.companyStreetAddress);
		zipEdit = (EditText) findViewById(R.id.zip);
		cityEdit = (EditText) findViewById(R.id.city);
		phoneType = (LinearLayout) findViewById(R.id.phoneType);
		statespinner = (Spinner) findViewById(R.id.statespinner);
		statespinner.setOnItemSelectedListener(this);
		ArrayAdapter<String> cstspinnerArrayAdapter = new ArrayAdapter<String>(
				EditProspectActivity.this,
				android.R.layout.simple_spinner_item, Constants.getStates());
		cstspinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		statespinner.setAdapter(cstspinnerArrayAdapter);

		update = (Button) findViewById(R.id.update);
		update.setOnClickListener(this);

		getAddress = (Button) findViewById(R.id.add_address_btn);
		getAddress.setTypeface(droidSansBold);
		getAddress.setOnClickListener(this);

		zipEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_NEXT) {
					Log.d("Action Done", "Action Done");
					getCityAndStateFromZipCode = new GetCityAndStateFromZipCode(
							zipEdit.getText().toString()).execute();
					return true;
				}
				return false;
			}
		});

		loadDropDownAsyncTask = new LoadDropDownItemsAsyncTask(context)
				.execute();

	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(EditProspectActivity.this,
				MainActivity.class);
		backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(backIntent);
	}

	public class EditProspectAsyncTask extends AsyncTask<Void, Void, Void> {
		Context ctx;
		ServiceHelper helper;
		JSONObject responseJson;
		JSONArray phoneArr = null;

		public EditProspectAsyncTask(Context mContext) {
			this.ctx = mContext;
			helper = new ServiceHelper(mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(context);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest("",
					Constants.EDIT_PROSPECT_URL + dealerID + "&customerid="
							+ customerId, Constants.REQUEST_TYPE_GET);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				try {
					if(responseJson != null){

						if (responseJson
								.has(Constants.EDIT_PROSPECT_CONFIGUATION_KEY)) {
							JSONArray jsonarray = responseJson
									.getJSONArray(Constants.EDIT_PROSPECT_CONFIGUATION_KEY);
							for (int i = 0; i < jsonarray.length(); i++) {
								JSONObject objs = jsonarray.getJSONObject(i);
								contact1 = objs
										.getString(Constants.EDIT_PROSPECT_CONTACT1);
								contact2 = objs
										.getString(Constants.EDIT_PROSPECT_CONTACT2);
								lastName = objs
										.getString(Constants.EDIT_PROSPECT_LASTNAME);
								companyName = objs
										.getString(Constants.EDIT_PROSPECT_COMPANYNAME);
								companyContact = objs
										.getString(Constants.EDIT_PROSPECT_COMPANYCONTACT);
								email = objs
										.getString(Constants.EDIT_PROSPECT_EMAIL);
								physicalAddress = objs
										.getString(Constants.EDIT_PROSPECT_PHYSICALADDRESS);
								physicalZip = objs
										.getString(Constants.EDIT_PROSPECT_PHYSICALZIP);
								physicalCity = objs
										.getString(Constants.EDIT_PROSPECT_PHYSICALCITY);
								physicalState = objs
										.getString(Constants.EDIT_PROSPECT_PHYSICALSTATE);
								typeId = objs
										.getString(Constants.EDIT_PROSPECT_TYPEID);
								subTypeId = objs
										.getString(Constants.EDIT_PROSPECT_SUPTYPEID);
								phoneArr = objs
										.getJSONArray(Constants.EDIT_PROSPECT_PHONENUMBER);
								if (phoneArr != null) {
									listPhoneNumberListModel.clear();
									for (int j = 0; j < phoneArr.length(); j++) {
										PhoneNumberListModel phoneNumberListModel = new PhoneNumberListModel();
										JSONObject jsonobj = phoneArr
												.getJSONObject(j);
										phoneNumberListModel
												.setCustomerId(jsonobj
														.getString(Constants.EDIT_PROSPECT_CUSTOMERID));
										phoneNumberListModel
												.setTypeName(jsonobj
														.getString(Constants.EDIT_PROSPECT_TYPENAME));
										phoneNumberListModel
												.setPhoneNumberId(jsonobj
														.getString(Constants.EDIT_PROSPECT_PHONENUMBERID));
										phoneNumberListModel
												.setPhone(jsonobj
														.getString(Constants.EDIT_PROSPECT_PHONE));
										phoneNumberListModel
												.setPhoneTypeId(jsonobj
														.getString(Constants.EDIT_PROSPECT_PHONETYPEID));
										listPhoneNumberListModel
												.add(phoneNumberListModel);
									}

									Log.d("listPhoneNumberListModel.size() ===> ",
											"" + listPhoneNumberListModel.size());
								}
							}
							if (companyName.equalsIgnoreCase("")
									&& companyName.equalsIgnoreCase(null)) {
								companyNameEdit.setVisibility(View.VISIBLE);
								companyNameEdit.setText(companyName);
							}
							firstnameEdit.setText(contact1);
							secondnameEdit.setText(contact2);
							lastnameEdit.setText(lastName);
							emailEdit.setText(email);
							companyStreetAddressEdit.setText(physicalAddress);
							zipEdit.setText(physicalZip);
							cityEdit.setText(physicalCity);
							for (int k = 0; k < Constants.getStates().size(); k++) {
								if(physicalState.length()>1){
									if (Constants
											.getStates()
											.get(k)
											.substring(0, 2)
											.equalsIgnoreCase(
													physicalState.trim()
															.substring(0, 2))) {
										statespinner.setSelection(k);
									}
								}
								
							}
							addDynamicLayout();
						}

					
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class LoadDropDownItemsAsyncTask extends AsyncTask<Void, Void, Void> {
		Context ctx;
		ServiceHelper helper;
		JSONObject responseJson;

		public LoadDropDownItemsAsyncTask(Context mContext) {
			this.ctx = mContext;
			helper = new ServiceHelper(mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(context);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest("",
					Constants.URL_PROSPECT_CONFIG + dealerID,
					Constants.REQUEST_TYPE_GET);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				try {
					if(responseJson != null){

						Singleton.getInstance().phoneTypeIdArray.clear();
						Singleton.getInstance().phoneTypeArray.clear();
						Singleton.getInstance().companyTypeIdArray.clear();
						Singleton.getInstance().companyTypeArray.clear();
						Singleton.getInstance().companySubTypeIdArray.clear();
						Singleton.getInstance().companySubTypeArray.clear();

						if (responseJson.has(Constants.KEY_PHONE_TYPE_RESPONSE)) {
							JSONArray localJsonArray = responseJson
									.getJSONArray(Constants.KEY_PHONE_TYPE_RESPONSE);

							for (int i = 0; i < localJsonArray.length(); i++) {
								JSONObject localJsonObject = localJsonArray
										.getJSONObject(i);
								if (localJsonObject
										.has(Constants.KEY_PHONE_TYPE_ID)) {
									Singleton.getInstance().phoneTypeIdArray
											.add(localJsonObject.getString(
													Constants.KEY_PHONE_TYPE_ID)
													.toString());
									Singleton.getInstance().phoneTypeArray
											.add(localJsonObject
													.getString(
															Constants.KEY_PHONE_TYPE_PHONE_TYPE)
													.toString());
								} else {
									Toast.makeText(context,
											Constants.TOAST_CONNECTION_ERROR,
											Constants.TOASTMSG_TIME).show();
								}
							}
						}
						if (responseJson.has(Constants.KEY_COMPANY_TYPE_RESPONSE)) {

							JSONArray localJsonArray = responseJson
									.getJSONArray(Constants.KEY_COMPANY_TYPE_RESPONSE);

							for (int i = 0; i < localJsonArray.length(); i++) {
								JSONObject localJsonObject = localJsonArray
										.getJSONObject(i);
								ArrayList<String> cSubTypeIdArray = new ArrayList<String>();
								ArrayList<String> cSubTypeArray = new ArrayList<String>();
								if (localJsonObject
										.has(Constants.KEY_COMPANY_TYPE_ID)) {
									Singleton.getInstance().companyTypeIdArray
											.add(localJsonObject.getString(
													Constants.KEY_COMPANY_TYPE_ID)
													.toString());
									Singleton.getInstance().companyTypeArray
											.add(localJsonObject
													.getString(
															Constants.KEY_COMPANY_TYPE_COMPANY_TYPE)
													.toString());
									JSONArray localJsonArray1 = localJsonObject
											.getJSONArray(Constants.KEY_ADDPROSPECT_COMPANY_SUB_TYPE_RESPONSE);
									for (int j = 0; j < localJsonArray1.length(); j++) {
										JSONObject localJsonObject1 = localJsonArray1
												.getJSONObject(j);
										if (localJsonObject
												.has(Constants.KEY_COMPANY_SUB_TYPE_ID)) {
											cSubTypeIdArray
													.add(localJsonObject1
															.getString(
																	Constants.KEY_COMPANY_TYPE_ID)
															.toString());
											cSubTypeArray
													.add(localJsonObject1
															.getString(
																	Constants.KEY_COMPANY_SUB_TYPE_COMPANY_SUB_TYPE)
															.toString());
										} else {
											Toast.makeText(
													context,
													Constants.TOAST_CONNECTION_ERROR,
													Constants.TOASTMSG_TIME).show();
										}
									}
									Singleton.getInstance().companySubTypeIdArray
											.add(cSubTypeIdArray);
									Singleton.getInstance().companySubTypeArray
											.add(cSubTypeArray);
								} else {
									Toast.makeText(context,
											Constants.TOAST_CONNECTION_ERROR,
											Constants.TOASTMSG_TIME).show();
								}
							}
						} else {
							Toast.makeText(context,
									Constants.TOAST_CONNECTION_ERROR,
									Constants.TOASTMSG_TIME).show();
						}
					
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				editProspectAsynTask = new EditProspectAsyncTask(context)
						.execute();
			}
		}
	}

	private class GetCurrentAddress extends AsyncTask<Void, Integer, String> {

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(context);
			pDialog.show();
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// latitude = 36.53489052024688;
			// longitude = -119.00457180454396;
			String address = getAddress(context, latitude, longitude);
			return address;
		}

		@Override
		protected void onPostExecute(String resultString) {

			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			if (resultString != null) {
				if (addressLine != null) {
					companyStreetAddressEdit.setText(addressLine);

				}
				if (zipCode != null) {
					zipEdit.setText(zipCode.replace(" ", ""));
					getCityAndStateFromZipCode = new GetCityAndStateFromZipCode(
							zipEdit.getText().toString()).execute();
				}
			} else {

				Toast.makeText(context, "Can't get address", Toast.LENGTH_SHORT)
						.show();

			}

		}
	}

	class GetCityAndStateFromZipCode extends AsyncTask<String, Void, Void> {

		String mRequestURL;

		public GetCityAndStateFromZipCode(String zipcode) {
			this.mRequestURL = Constants.URL_GET_CITY_AND_STATE_FROM_ZIPCODE
					+ zipcode;
		}

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(EditProspectActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				jsonResultText = serviceHelper.jsonSendHTTPRequest(
						Constants.EMPTY_STRING, this.mRequestURL,
						Constants.REQUEST_TYPE_GET);
				Log.e(Constants.KEY_PHONE_TYPE_RESPONSE,
						jsonResultText.toString());
			} catch (Exception e) {
				Log.e("", "" + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			try {
				if(jsonResultText != null){

					if (jsonResultText.has(Constants.KEY_RESULTS)) {
						Log.d("Result",
								jsonResultText.getString(Constants.KEY_RESULTS)
										.toString());
						JSONArray localJsonArray = jsonResultText
								.getJSONArray(Constants.KEY_RESULTS);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject localJsonObject = localJsonArray
									.getJSONObject(i);
							if (localJsonObject
									.has(Constants.KEY_FORMATTED_ADDRESS)) {
								Log.d("Result",
										localJsonObject.getString(
												Constants.KEY_FORMATTED_ADDRESS)
												.toString());
								String[] separated = localJsonObject
										.getString(Constants.KEY_FORMATTED_ADDRESS)
										.toString().split(",");
								if (separated.length > 2) {
									separated[1] = separated[1].replace(" ", "");
									separated[1] = separated[1].substring(0, 2);
									separated[2] = separated[2].replace(" ", "");
									if (separated[2].equalsIgnoreCase("USA")
											|| separated[2]
													.equalsIgnoreCase("Canada")) {
										cityEdit.setText(separated[0]);
										Log.d("City : ", separated[0]);
										Log.d("State : ", separated[1]);
										Log.d("Country : ", separated[2]);

										int position1 = -1;
										ArrayList<String> stateArray1 = new ArrayList<String>();
										stateArray1 = Constants.getStates();
										for (int j = 0; j < stateArray1.size(); j++) {

											if (stateArray1.get(j).substring(0, 2)
													.equalsIgnoreCase(separated[1])) {
												position1 = j;
												statespinner
														.setSelection(position1);
												Log.d("State : " + position1 + " ",
														stateArray1.get(position1));
												break;
											}
										}
									} else {
										Toast.makeText(context,
												Constants.TOAST_VALID_ZIPCODE,
												Constants.TOASTMSG_TIME).show();
									}
								} else {
									Toast.makeText(context,
											Constants.TOAST_VALID_ZIPCODE,
											Constants.TOASTMSG_TIME).show();
								}

							} else {
								Toast.makeText(context,
										Constants.TOAST_VALID_ZIPCODE,
										Constants.TOASTMSG_TIME).show();
							}
						}
					} else if (jsonResultText.has(Constants.KEY_ERROR)) {
						Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
								Constants.TOASTMSG_TIME).show();
					} else {
						Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
								Constants.TOASTMSG_TIME).show();
					}
				
				}
			} catch (Exception e) {
				Log.e("", "" + e.getMessage());
				Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}
		}
	}

	public String getAddress(Context ctx, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				zipCode = address.getPostalCode();
				addressLine = address.getAddressLine(0);
			} else {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),
								"Address Not Found", Toast.LENGTH_SHORT).show();
					}
				});

			}
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		return result.toString();
	}

	private void addDynamicLayout() {
		int dynamicValue = 0;
		for (int a = 0; a < listPhoneNumberListModel.size(); a++) {
			LinearLayout horizontalLayout = new LinearLayout(this);
			LinearLayout.LayoutParams horiParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			horizontalLayout.setId(dynamicIncValue);
			horizontalLayout.setLayoutParams(horiParams);
			phoneType.addView(horizontalLayout);

			LinearLayout phoneLayout = new LinearLayout(this);
			phoneLayout.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1));
			horizontalLayout.addView(phoneLayout);

			phoneNumber = new EditText(this);
			LinearLayout.LayoutParams phoneEditParams = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			dynamicValue = (int) getResources().getDimensionPixelOffset(
					R.dimen.five);
			phoneEditParams.setMargins(dynamicValue, dynamicValue,
					dynamicValue, dynamicValue);
			phoneNumber.setLayoutParams(phoneEditParams);
			phoneNumber.setId(dynamicPhone + a);
			phoneNumber.setHint(R.string.phone);
			phoneNumber.setTypeface(droidSans);
			phoneNumber.setSingleLine();
			phoneNumber.setText(listPhoneNumberListModel.get(a).getPhone());
			phoneNumber.setBackgroundResource(R.drawable.edittext_border);
			phoneNumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
			phoneNumber
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							14) });
			phoneNumber.addTextChangedListener(watch);
			phoneNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					focusId = v.getId();
				}
			});

			phoneLayout.addView(phoneNumber);

			LinearLayout typeLayout = new LinearLayout(this);
			LinearLayout.LayoutParams typeLayoutParams = new LinearLayout.LayoutParams(
					0, LayoutParams.WRAP_CONTENT, 1);
			typeLayoutParams.gravity = Gravity.CENTER_VERTICAL;
			typeLayout.setLayoutParams(typeLayoutParams);
			horizontalLayout.addView(typeLayout);

			FrameLayout frameLayout = new FrameLayout(this);
			LinearLayout.LayoutParams frameLayoutParams = new LinearLayout.LayoutParams(
					0, LayoutParams.WRAP_CONTENT, 1);
			frameLayoutParams.gravity = Gravity.CENTER_VERTICAL;
			frameLayout.setLayoutParams(frameLayoutParams);
			frameLayout.setBackgroundResource(R.drawable.edittext_border);
			typeLayout.addView(frameLayout);

			typeText = new Spinner(this);
			FrameLayout.LayoutParams typeTextParams = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			typeTextParams.gravity = Gravity.CENTER_VERTICAL;
			typeText.setLayoutParams(typeTextParams);
			typeText.setPadding(0, 0, 0, 0);
			typeText.setId(dynamicPhoneTypeId + a);
			typeText.setBackgroundResource(R.drawable.custome_spinner_background);
			frameLayout.addView(typeText);
			typeText.setOnItemSelectedListener(this);

			ImageView downImageView = new ImageView(this);
			downImageView.setLayoutParams(new FrameLayout.LayoutParams(
					(int) getResources().getDimensionPixelOffset(
							R.dimen.fifteen), (int) getResources()
							.getDimensionPixelOffset(R.dimen.ten),
					Gravity.CENTER_VERTICAL | Gravity.RIGHT));
			downImageView
					.setBackgroundResource(R.drawable.ic_spinner_background);
			frameLayout.addView(downImageView);

			ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
					EditProspectActivity.this,
					android.R.layout.simple_spinner_item,
					Singleton.getInstance().phoneTypeArray);
			ptspinnerArrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			typeText.setAdapter(ptspinnerArrayAdapter);
			int phoneTy = Integer.parseInt(listPhoneNumberListModel.get(a)
					.getPhoneTypeId());
			if (phoneTy != 0
					&& listPhoneNumberListModel.get(a).getPhoneTypeId() != null) {
				typeText.setSelection(phoneTy - 1);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.back_icon:
			finish();
			break;
		case R.id.add_address_btn:
			gpsTracker = new GPSTracker(context);
			// check if GPS enabled
			if (gpsTracker.canGetLocation()) {
				latitude = gpsTracker.getLatitude();
				longitude = gpsTracker.getLongitude();
				currentadd = new GetCurrentAddress();
				currentadd.execute();
				// \n is for new line
			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gpsTracker.showSettingsAlert();
			}
			break;

		case R.id.update:
			if (validation()) {
				String requestDataa = null;
				requestDataa = getAddProfessionalContactAndAddProspectJsonRequestData(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE);
				saveAsyncTask = new SaveContactAsyncTask(
						Constants.SAVE_CONTACT, requestDataa,
						Constants.TOAST_ADDED_UPDATE).execute();
			}
			break;

		case R.id.image_back_icon:
			Intent backIntent = new Intent(EditProspectActivity.this,
					MainActivity.class);
			backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(backIntent);

			break;

		default:
			break;
		}
	}

	class SaveContactAsyncTask extends AsyncTask<String, Void, Void> {

		String mRequestUrl, mRequestData, mCustType;

		public SaveContactAsyncTask(String requestUrl, String requestData,
				String custType) {
			this.mRequestUrl = requestUrl;
			this.mRequestData = requestData;
			this.mCustType = custType;
		}

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(EditProspectActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				jsonResultText = serviceHelper.jsonSendHTTPRequest(
						this.mRequestData, this.mRequestUrl,
						Constants.REQUEST_TYPE_POST);
				Log.e(Constants.KEY_PHONE_TYPE_RESPONSE,
						jsonResultText.toString());
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();

			try {
				if(jsonResultText != null){

					if (jsonResultText.has(Constants.KEY_ADDPROSPECT_RESPONSE)) {

						JSONArray localJsonArray = jsonResultText
								.getJSONArray(Constants.KEY_ADDPROSPECT_RESPONSE);

						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject localJsonObject = localJsonArray
									.getJSONObject(i);

							if (localJsonObject
									.has(Constants.KEY_ADDPROSPECT_STATUS)) {
								if (localJsonObject
										.getString(Constants.KEY_ADDPROSPECT_STATUS)
										.toString()
										.equalsIgnoreCase(Constants.VALUE_SUCCESS)) {

									Toast.makeText(context,
											Constants.TOAST_ADDED_UPDATE,
											Toast.LENGTH_LONG).show();

									Bundle bundle = new Bundle();
									String customerFullName = "";
									if (lastnameEdit.getText().toString().length() > 0) {
										customerFullName = customerFullName
												+ lastnameEdit.getText()
														.toString();
										if (firstnameEdit.getText().toString()
												.length() > 0) {
											customerFullName = customerFullName
													+ ", "
													+ firstnameEdit.getText()
															.toString();
											if (secondnameEdit.getText().toString()
													.length() > 0) {
												customerFullName = customerFullName
														+ ", "
														+ secondnameEdit.getText()
																.toString();
											}
										} else if (secondnameEdit.getText()
												.toString().length() > 0) {
											customerFullName = customerFullName
													+ ", "
													+ secondnameEdit.getText()
															.toString();
										}
									} else if (firstnameEdit.getText().toString()
											.length() > 0) {
										customerFullName = customerFullName
												+ firstnameEdit.getText()
														.toString();
										if (secondnameEdit.getText().toString()
												.length() > 0) {
											customerFullName = customerFullName
													+ ", "
													+ secondnameEdit.getText()
															.toString();
										}
									} else {
										if (secondnameEdit.getText().toString()
												.length() > 0) {
											customerFullName = customerFullName
													+ secondnameEdit.getText()
															.toString();
										}
									}
									bundle.putString("CustomerFullName",
											customerFullName);
									bundle.putString(
											"CustomerAddress",
											companyStreetAddressEdit.getText()
													.toString()
													+ ", "
													+ cityEdit.getText().toString()
													+ ", "
													+ Constants.getStates()
															.get(statePosition)
															.substring(0, 2)
													+ ", "
													+ zipEdit.getText().toString());
									bundle.putString("CustomerId", CustomerId);
									Intent customerDetailsIntent = new Intent(
											EditProspectActivity.this,
											CustomerDetailsActivity.class);
									customerDetailsIntent.putExtras(bundle);
									startActivity(customerDetailsIntent);
									finish();

								} else {
									Toast.makeText(
											context,
											localJsonObject
													.getString(
															Constants.KEY_ADDPROSPECT_STATUS)
													.toString(),
											Constants.TOASTMSG_TIME).show();
								}
							} else {
								Toast.makeText(context,
										Constants.TOAST_CONNECTION_ERROR,
										Constants.TOASTMSG_TIME).show();
							}
						}

					} else {
						Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
								Constants.TOASTMSG_TIME).show();
					}
				
				}
			} catch (Exception e) {
				Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}
		}
	}

	TextWatcher watch = new TextWatcher() {
		private boolean mFormatting; // this is a flag which prevents the
		EditText focusEditText;

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			focusEditText = (EditText) findViewById(focusId);
			if (!mFormatting) {
				mFormatting = true;
				String editValue = "";
				editValue = focusEditText.getText().toString()
						.replaceAll("\\s+", "");
				editValue = editValue.replaceAll("[-.^:,()]", "");
				if (editValue.length() < 4) {
					focusEditText.setText(editValue);
				} else if (editValue.length() < 7) {
					focusEditText
							.setText("("
									+ editValue.substring(0, 3)
									+ ") "
									+ editValue.substring(3, editValue.length()));
				} else if (editValue.length() < 11) {
					focusEditText.setText("(" + editValue.substring(0, 3)
							+ ") " + editValue.substring(3, 6) + "-"
							+ editValue.substring(6, editValue.length()));
				}
				focusEditText.setSelection(focusEditText.getText().toString()
						.length());
				mFormatting = false;
			}
		}

		// called before the text is changed...
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	public String getAddProfessionalContactAndAddProspectJsonRequestData(
			String type) {
		// TODO Auto-generated method stub
		String stringData = "";

		JSONArray phoneNumberArray;
		JSONObject customerJsonData, phoneNumberJsonData = new JSONObject();

		customerJsonData = new JSONObject();
		phoneNumberArray = new JSONArray();

		try {
			customerJsonData = new JSONObject();
			customerJsonData.putOpt(Constants.EDIT_PROSPECT_DELEARID, dealerID);
			customerJsonData.putOpt(Constants.EDIT_PROSPECT_EMPLOYEEID,
					employeeID);
			customerJsonData.putOpt(Constants.EDIT_PROSPECT_CUSTOMERID,
					customerId);
			if (companyName.equalsIgnoreCase("")
					&& companyName.equalsIgnoreCase(null)) {
				customerJsonData.putOpt(Constants.EDIT_PROSPECT_TYPE,
						"Prospect");
			} else {
				customerJsonData.putOpt(Constants.EDIT_PROSPECT_TYPE,
						"professional Contact");
			}

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_FIRST,
					firstnameEdit.getText().toString());
			customerJsonData.putOpt(Constants.EDIT_PROSPECT_SECONDARY_CONTACT,
					secondnameEdit.getText().toString());
			customerJsonData.putOpt(Constants.EDIT_PROSPECT_LASTNAME,
					lastnameEdit.getText().toString());

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_COMPANYNAME,
					companyNameEdit.getText().toString());

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_EMAIL, emailEdit
					.getText().toString());

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_STREET,
					companyStreetAddressEdit.getText().toString());

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_ZIP, zipEdit
					.getText().toString());

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_CITY, cityEdit
					.getText().toString());

			customerJsonData.putOpt(Constants.EDIT_PROSPECT_STATE, Constants
					.getStates().get(statePosition).substring(0, 2));

			for (int x = 0; x < listPhoneNumberListModel.size(); x++) {
				try {
					jsonRequestText = new JSONObject();
					jsonRequestText.put(Constants.EDIT_PROSPECT_TYPEID,
							listPhoneNumberListModel.get(x).getPhoneTypeId());
					jsonRequestText.put(Constants.EDIT_PROSPECT_PHONENUMBERS,
							listPhoneNumberListModel.get(x).getPhone());
					jsonRequestText.put(Constants.EDIT_PROSPECT_PHONENUMBERID,
							listPhoneNumberListModel.get(x).getPhoneNumberId());
				} catch (Exception e) {
					e.printStackTrace();
				}
				phoneNumberArray.put(x, jsonRequestText);
			}
			customerJsonData.putOpt(
					Constants.EDIT_PROSPECT_CUSTOMERPHONENUMBERS,
					phoneNumberArray);
		} catch (JSONException e) {
			Log.e(Constants.KEY_ERROR, e.toString());
		}

		try {
			phoneNumberJsonData.put(Constants.EDIT_PROSPECT_CUSTOMERDATA,
					customerJsonData);
			stringData = phoneNumberJsonData.toString();
			Log.e("stringData ", stringData);
		} catch (JSONException e) {
			Log.e(Constants.KEY_ERROR, e.toString());
		}
		return stringData;
	}

	private boolean validation() {
		boolean checkValue = false;
		if (firstnameEdit.getText().toString().length() > 0) {
//			if (secondnameEdit.getText().toString().length() > 0) {
//				
//			} else {
//				Toast.makeText(getApplicationContext(),
//						Constants.TOAST_SECOND_NAME, Constants.TOASTMSG_TIME)
//						.show();
//			}

			if (lastnameEdit.getText().toString().length() > 0) {
				if (phoneValidation()) {
					if (emailEdit.getText().toString().length() > 0) {
//						if (Constants.isEditTextContainEmail(emailEdit
//								.getText().toString()) == true) {
//							
//						} else {
//							Toast.makeText(
//									context,
//									getResources().getString(
//											R.string.valid_email),
//									Constants.TOASTMSG_TIME).show();
//						}

						if (companyStreetAddressEdit.getText()
								.toString().length() > 0) {
							if (zipEdit.getText().toString().length() > 0) {
								if (cityEdit.getText().toString()
										.length() > 0) {
									checkValue = true;
								} else {
									Toast.makeText(
											getApplicationContext(),
											Constants.TOAST_CITY,
											Constants.TOASTMSG_TIME)
											.show();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										Constants.TOAST_ZIP,
										Constants.TOASTMSG_TIME).show();
							}
						} else {
							Toast.makeText(
									getApplicationContext(),
									Constants.TOAST_COMPANY_STREET_ADDRESS,
									Constants.TOASTMSG_TIME).show();
						}
					
					} else {
						Toast.makeText(getApplicationContext(),
								Constants.TOAST_EMAIL,
								Constants.TOASTMSG_TIME).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							Constants.TOAST_PHONE_NUMBER,
							Constants.TOASTMSG_TIME).show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						Constants.TOAST_LAST_NAME, Constants.TOASTMSG_TIME)
						.show();
			}
		

		} else {
			Toast.makeText(getApplicationContext(), Constants.TOAST_FIRST_NAME,
					Constants.TOASTMSG_TIME).show();
		}
		return checkValue;
	}

	private boolean phoneValidation() {
		boolean value = true;
		for (int z = 0; z < listPhoneNumberListModel.size(); z++) {
			EditText focusEditText = (EditText) findViewById(dynamicPhone + z);
			Log.e("", "" + focusEditText.getText().toString());
			listPhoneNumberListModel.get(z).setPhone(
					focusEditText.getText().toString());
			if (!listPhoneNumberListModel.get(z).getPhone()
					.equalsIgnoreCase("")
					&& !listPhoneNumberListModel.get(z).getPhone()
							.equalsIgnoreCase(null)) {

			} else {
				value = false;
			}
		}

		return value;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if ((TextView) parent.getChildAt(0) != null) {
			((TextView) parent.getChildAt(0)).setTypeface(droidSans);
			((TextView) parent.getChildAt(0)).setPadding(0, 0, 0, 0);
		}
		switch (parent.getId()) {
		case R.id.statespinner:
			statePosition = position;
			break;
		default:
			listPhoneNumberListModel.get(parent.getId() - 3000).setPhoneTypeId(
					String.valueOf(position + 1));
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pDialog != null) {
			if (pDialog.isShowing()) {
				pDialog.cancel();
			}
			pDialog = null;
		}
		if (editProspectAsynTask != null) {
			editProspectAsynTask.cancel(true);
			editProspectAsynTask = null;
		}
		if (loadDropDownAsyncTask != null) {
			loadDropDownAsyncTask.cancel(true);
			loadDropDownAsyncTask = null;
		}
		if (getCityAndStateFromZipCode != null) {
			getCityAndStateFromZipCode.cancel(true);
			getCityAndStateFromZipCode = null;
		}
		if (saveAsyncTask != null) {
			saveAsyncTask.cancel(true);
			saveAsyncTask = null;
		}

	}
}
