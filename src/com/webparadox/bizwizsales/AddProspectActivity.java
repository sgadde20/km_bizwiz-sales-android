package com.webparadox.bizwizsales;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.Window;
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
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.GPSTracker;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;

public class AddProspectActivity extends Activity implements
android.view.View.OnClickListener, OnItemSelectedListener {
	ImageView backicon, image_back_icon, close;
	Intent homeint;
	LinearLayout phoneType, popupLayout;
	TextView professionalContact, firstoptions, secondoptions;
	Button save, saveandbookappt, getAddress;
	int dynamicIncValue = 1000, dynamicPhone = 2000, dynamicPhoneTypeId = 3000;
	EditText companyName, firstName, lastName, email, companyStreetAddress,
	zip, city, phoneNumber;
	Spinner companyType, companySubType, typeText, state, country;
	Typeface droidSans, droidSansBold;
	ArrayList<Integer> phoneValue = new ArrayList<Integer>();
	ArrayList<Integer> phoneValueType = new ArrayList<Integer>();
	ArrayList<String> phoneArrayId = new ArrayList<String>();
	ArrayList<String> phoneArrayNumber = new ArrayList<String>();

	JSONObject jsonRequestText, jsonResultText;
	ServiceHelper serviceHelper;
	public ActivityIndicator pDialog;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String dealerID = "";
	String employeeID = "";
	String CanCreateEvents = "";
	String CanEditEvents = "";
	Context context;

	FrameLayout companySubTypeFrame, companyTypeFrame;
	AsyncTask<String, Void, Void> loadDropDownAsyncTask;
	AsyncTask<String, Void, Void> getCityAndStateFromZipCode;
	AsyncTask<String, Void, Void> addProspectAndAprofessionalContactAsyncTask;

	int defalutposition = 0, type = 1, companySubTypuId = 0,
			statesPosition = 0, focusId = 0;

	GPSTracker gpsTracker;
	double latitude, longitude;
	String addressLine = "", userCity, zipCode;
	Boolean iscompanySubType;
	SearchView addProposalSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	boolean checkStatus;
	String customerId;
	GetCurrentAddress currentadd;
	DatabaseHandler dbHandler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addprospect_activity);

		context = this;
		serviceHelper = new ServiceHelper(context);
		dbHandler = new DatabaseHandler(context);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		CanCreateEvents = userData.getString(
				Constants.KEY_EMPLOYEE_CAN_CREATE_EVENTS, "");
		CanEditEvents = userData.getString(
				Constants.KEY_EMPLOYEE_CAN_EDIT_EVENTS, "");

		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");

		backicon = (ImageView) findViewById(R.id.back_icon);
		backicon.setOnClickListener(this);

		image_back_icon = (ImageView) findViewById(R.id.image_back_icon);
		image_back_icon.setOnClickListener(this);

		phoneType = (LinearLayout) findViewById(R.id.phoneType);
		popupLayout = (LinearLayout) findViewById(R.id.popupLayout);
		popupLayout.setOnClickListener(this);
		professionalContact = (TextView) findViewById(R.id.professionalContact);
		professionalContact.setTypeface(droidSans);
		professionalContact.setOnClickListener(this);

		save = (Button) findViewById(R.id.save);
		save.setTypeface(droidSansBold);
		save.setOnClickListener(this);

		getAddress = (Button) findViewById(R.id.add_address_btn);
		getAddress.setTypeface(droidSansBold);
		getAddress.setOnClickListener(this);

		saveandbookappt = (Button) findViewById(R.id.saveandbookappt);
		saveandbookappt.setTypeface(droidSansBold);
		saveandbookappt.setOnClickListener(this);

		if (CanCreateEvents.equalsIgnoreCase("True")) {
			saveandbookappt.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) save
					.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			save.setLayoutParams(lp);
			RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) saveandbookappt
					.getLayoutParams();
			lp1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			saveandbookappt.setLayoutParams(lp1);
		} else {
			saveandbookappt.setVisibility(View.GONE);
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) save
					.getLayoutParams();
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			save.setLayoutParams(lp);
		}

		close = (ImageView) findViewById(R.id.close);
		close.setOnClickListener(this);

		companyName = (EditText) findViewById(R.id.companyName);
		companyName.setTypeface(droidSans);
		companyName.setText(Constants.EMPTY_STRING);
		companyType = (Spinner) findViewById(R.id.companyTypespinner);
		companyType.setOnItemSelectedListener(this);

		firstName = (EditText) findViewById(R.id.firstName);
		firstName.setTypeface(droidSans);
		firstName.setText(Constants.EMPTY_STRING);

		lastName = (EditText) findViewById(R.id.lastName);
		lastName.setTypeface(droidSans);
		lastName.setText(Constants.EMPTY_STRING);
		email = (EditText) findViewById(R.id.email);
		email.setTypeface(droidSans);
		email.setText(Constants.EMPTY_STRING);
		companyStreetAddress = (EditText) findViewById(R.id.companyStreetAddress);
		companyStreetAddress.setTypeface(droidSans);
		companyStreetAddress.setText(Constants.EMPTY_STRING);
		zip = (EditText) findViewById(R.id.zip);
		zip.setTypeface(droidSans);
		zip.setText(Constants.EMPTY_STRING);
		zip.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_NEXT) {
					Log.d("Action Done", "Action Done");
					getCityAndStateFromZipCode = new GetCityAndStateFromZipCode(
							zip.getText().toString()).execute();
					return true;
				}
				return false;
			}
		});

		addProposalSearchView = (SearchView) findViewById(R.id.add_proposal_searchView);

		addProposalSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if (query.trim().length() != 0) {

					searchAsyncTask = new SmartSearchAsyncTask(context,
							dealerID, employeeID, query);
					searchAsyncTask.execute();
				} else {
					Toast.makeText(context, Constants.TOAST_NO_DATA,
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		city = (EditText) findViewById(R.id.city);
		city.setTypeface(droidSans);
		city.setText(Constants.EMPTY_STRING);
		state = (Spinner) findViewById(R.id.state);
		state.setOnItemSelectedListener(this);
		ArrayAdapter<String> cstspinnerArrayAdapter = new ArrayAdapter<String>(
				AddProspectActivity.this, android.R.layout.simple_spinner_item,
				Constants.getStates());
		cstspinnerArrayAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
		// drop
		// down
		// view
		state.setAdapter(cstspinnerArrayAdapter);
		country = (Spinner) findViewById(R.id.countryspinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.planets_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		country.setAdapter(adapter);
		country.setOnItemSelectedListener(this);

		firstoptions = (TextView) findViewById(R.id.firstoptions);
		firstoptions.setTypeface(droidSans);
		firstoptions.setOnClickListener(this);
		secondoptions = (TextView) findViewById(R.id.secondoptions);
		secondoptions.setTypeface(droidSans);
		secondoptions.setOnClickListener(this);
		companySubType = (Spinner) findViewById(R.id.companysubType);
		companySubType.setOnItemSelectedListener(this);
		companyTypeFrame = (FrameLayout) findViewById(R.id.companyTypeFrame);
		companySubTypeFrame = (FrameLayout) findViewById(R.id.companySubTypeFrame);

		Singleton.getInstance().clearCompanyPhoneTypes();
		if (Utilities.isNetworkConnected(context)) {
			loadDropDownAsyncTask = new LoadDropDownItemsAsyncTask(
					Constants.URL_PROSPECT_CONFIG + dealerID).execute();
		} else {
			JSONObject resultData = new JSONObject();
			resultData = dbHandler.getAddProspectDropdown(Constants.WEBSERVICE_ADD_PROSPECT_DROPDOWN);
			if(resultData != null){
				loadOfflineDropDown(resultData);
			}else{
				try {
				     InputStream is = getAssets().open("dropdown.txt");
				     int size = is.available();
				     byte[] buffer = new byte[size];
				     is.read(buffer);
				     is.close();
				     String json = new String(buffer, "UTF-8");
				     JSONObject obj;
				     obj = new JSONObject(json);
				     loadOfflineDropDown(obj);
				    } catch (IOException ex) {
				     ex.printStackTrace();
				    } catch (JSONException e) {
				     e.printStackTrace();
				    }
			}
		}

		if (savedInstanceState != null) {
			companyName.setText(savedInstanceState.getString("companyname"));
			firstName.setText(savedInstanceState.getString("firstname"));
			lastName.setText(savedInstanceState.getString("lastname"));
			email.setText(savedInstanceState.getString("Email"));
			companyStreetAddress.setText(savedInstanceState
					.getString("companystreetaddress"));
			// phoneNumber.setText(savedInstanceState.getString("phonenumber"));
			city.setText(savedInstanceState.getString("City"));
		}

		companyName.setVisibility(View.GONE);
		companyTypeFrame.setVisibility(View.GONE);
		companySubTypeFrame.setVisibility(View.GONE);
		companyStreetAddress.setHint(R.string.prospect_company_street_Address);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Add Prospect Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("companyname", companyName.getText().toString());
		outState.putString("firstname", firstName.getText().toString());
		outState.putString("lastname", lastName.getText().toString());
		outState.putString("Email", email.getText().toString());
		outState.putString("companystreetaddress", companyStreetAddress
				.getText().toString());
		// outState.putString("phonenumber", phoneNumber.getText().toString());
		outState.putString("City", city.getText().toString());
		super.onSaveInstanceState(outState);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_icon:
			gotoHomeActivity();
			break;

		case R.id.image_back_icon:
			gotoHomeActivity();
			break;

		case R.id.professionalContact:
			v.clearFocus();
			popupLayout.setVisibility(View.VISIBLE);
			break;

		case R.id.close:
			popupLayout.setVisibility(View.GONE);
			break;

		case R.id.save:
			checkStatus = false;
			if (companyValidation()) {
				String requestDataa = null;
				if(Constants.isNetworkConnected(AddProspectActivity.this)){
					if (type == 0) {
						requestDataa = getAddProfessionalContactAndAddProspectJsonRequestData(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE);
						addProspectAndAprofessionalContactAsyncTask = new AddProspectAndAprofessionalContactAsyncTask(
								Constants.URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT,
								requestDataa,
								Constants.TOAST_ADDED_PROFESSIONAL_CONTACT)
						.execute();
					} else {
						requestDataa = getAddProfessionalContactAndAddProspectJsonRequestData(Constants.KEY_ADD_PROSPECT_TYPE_VALUE);
						addProspectAndAprofessionalContactAsyncTask = new AddProspectAndAprofessionalContactAsyncTask(
								Constants.URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT,
								requestDataa, Constants.TOAST_ADDED_PROSPECT)
						.execute();
					}
				}else{
					Log.e("dbHandler.getTestCount()1 ===> ", ""+dbHandler.getTestCount());
					if(type == 0){
						dbHandler.insertValueAddProspect(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE, 
								firstName.getText().toString(), 
								lastName.getText().toString(),
								companyName.getText().toString(),
								Singleton.getInstance().companyTypeIdArray.get(defalutposition), 
								Singleton.getInstance().companySubTypeIdArray.get(defalutposition).get(companySubTypuId), 
								email.getText().toString(),
								companyStreetAddress.getText().toString(), 
								zip.getText().toString(), 
								city.getText().toString(), 
								Constants.getStates().get(statesPosition).substring(0, 2),
								phoneArrayId, 
								phoneArrayNumber);
					}else{
						dbHandler.insertValueAddProspect(Constants.KEY_ADD_PROSPECT_TYPE_VALUE, 
								firstName.getText().toString(), 
								lastName.getText().toString(),
								"",
								"", 
								"", 
								email.getText().toString(),
								companyStreetAddress.getText().toString(), 
								zip.getText().toString(), 
								city.getText().toString(), 
								Constants.getStates().get(statesPosition).substring(0, 2),
								phoneArrayId, 
								phoneArrayNumber);
					}
					
					Log.e("dbHandler.getTestCount() ===> ", ""+dbHandler.getTestCount());
					gotoHomeActivity();
				}
			}
			break;

		case R.id.saveandbookappt:
			checkStatus = true;
			if (CanCreateEvents.equalsIgnoreCase("True")) {
				if (companyValidation()) {
					String requestDataa = null;
					if(Constants.isNetworkConnected(AddProspectActivity.this)){
						if (type == 0) {
							requestDataa = getAddProfessionalContactAndAddProspectJsonRequestData(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE);
							addProspectAndAprofessionalContactAsyncTask = new AddProspectAndAprofessionalContactAsyncTask(
									Constants.URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT,
									requestDataa,
									Constants.TOAST_ADDED_PROFESSIONAL_CONTACT)
							.execute();
						} else {
							requestDataa = getAddProfessionalContactAndAddProspectJsonRequestData(Constants.KEY_ADD_PROSPECT_TYPE_VALUE);
							addProspectAndAprofessionalContactAsyncTask = new AddProspectAndAprofessionalContactAsyncTask(
									Constants.URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT,
									requestDataa, Constants.TOAST_ADDED_PROSPECT)
							.execute();
						}
					}else{
						if(type == 0){
							dbHandler.insertValueAddProspect(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE, 
									firstName.getText().toString(), 
									lastName.getText().toString(),
									companyName.getText().toString(),
									Singleton.getInstance().companyTypeIdArray.get(defalutposition), 
									Singleton.getInstance().companySubTypeIdArray.get(defalutposition).get(companySubTypuId), 
									email.getText().toString(),
									companyStreetAddress.getText().toString(), 
									zip.getText().toString(), 
									city.getText().toString(), 
									Constants.getStates().get(statesPosition).substring(0, 2),
									phoneArrayId, 
									phoneArrayNumber);
						}else{
							dbHandler.insertValueAddProspect(Constants.KEY_ADD_PROSPECT_TYPE_VALUE, 
									firstName.getText().toString(), 
									lastName.getText().toString(),
									"",
									"", 
									"", 
									email.getText().toString(),
									companyStreetAddress.getText().toString(), 
									zip.getText().toString(), 
									city.getText().toString(), 
									Constants.getStates().get(statesPosition).substring(0, 2),
									phoneArrayId, 
									phoneArrayNumber);
						}
						gotoHomeActivity();
					}
					
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"You can't create events.", Toast.LENGTH_SHORT).show();
			}

			break;

		case R.id.firstoptions:
			type = 1;
			professionalContact.setText(Constants.KEY_ADD_PROSPECT_TYPE_VALUE);
			companyName.setVisibility(View.GONE);
			companyTypeFrame.setVisibility(View.GONE);
			companySubTypeFrame.setVisibility(View.GONE);
			companyStreetAddress
			.setHint(R.string.prospect_company_street_Address);
			popupLayout.setVisibility(View.GONE);
			break;

		case R.id.secondoptions:
			type = 0;
			professionalContact
			.setText(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE);
			companyName.setVisibility(View.VISIBLE);
			companyTypeFrame.setVisibility(View.VISIBLE);
			companySubTypeFrame.setVisibility(View.VISIBLE);
			companyStreetAddress.setHint(R.string.company_street_Address);
			popupLayout.setVisibility(View.GONE);
			break;

		default:
			popupLayout.setVisibility(View.VISIBLE);
			companyStreetAddress.setHint(R.string.company_street_Address);
			break;

		case R.id.add_address_btn:
			gpsTracker = new GPSTracker(context);
			// check if GPS enabled
			if (gpsTracker.canGetLocation()) {
				latitude = gpsTracker.getLatitude();
				longitude = gpsTracker.getLongitude();
				if(latitude != 0.0 && longitude != 0.0){
					currentadd = new GetCurrentAddress();
					currentadd.execute();	
				}else{
					Toast.makeText(getApplicationContext(), "Can't get location", Toast.LENGTH_SHORT).show();
				}
				
				// \n is for new line
			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gpsTracker.showSettingsAlert();
			}

			break;
		}
	}

	private void addDynamicLayout() {
		int dynamicValue = 0;
		dynamicIncValue++;
		dynamicPhone++;
		dynamicPhoneTypeId++;
		phoneValue.add(dynamicPhone);
		phoneValueType.add(dynamicPhoneTypeId);
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
		phoneEditParams.setMargins(dynamicValue, dynamicValue, dynamicValue,
				dynamicValue);
		phoneNumber.setLayoutParams(phoneEditParams);
		phoneNumber.setId(dynamicPhone);
		phoneNumber.setHint(R.string.phone);
		phoneNumber.setTypeface(droidSans);
		phoneNumber.setSingleLine();
		phoneNumber.setBackgroundResource(R.drawable.edittext_border);
		phoneNumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		phoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		phoneNumber
		.setFilters(new InputFilter[] { new InputFilter.LengthFilter(14) });
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
		typeText.setId(dynamicPhoneTypeId);
		typeText.setBackgroundResource(R.drawable.custome_spinner_background);
		frameLayout.addView(typeText);
		typeText.setOnItemSelectedListener(this);

		ImageView downImageView = new ImageView(this);
		downImageView.setLayoutParams(new FrameLayout.LayoutParams(
				(int) getResources().getDimensionPixelOffset(R.dimen.fifteen),
				(int) getResources().getDimensionPixelOffset(R.dimen.ten),
				Gravity.CENTER_VERTICAL | Gravity.RIGHT));
		downImageView.setBackgroundResource(R.drawable.ic_spinner_background);
		frameLayout.addView(downImageView);

		ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
				AddProspectActivity.this, android.R.layout.simple_spinner_item,
				Singleton.getInstance().phoneTypeArray);
		ptspinnerArrayAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
		// drop
		// down
		// view
		typeText.setAdapter(ptspinnerArrayAdapter);

		final ImageView addImageView = new ImageView(this);
		LinearLayout.LayoutParams addImageViewParams = new LinearLayout.LayoutParams(
				(int) getResources()
				.getDimensionPixelOffset(R.dimen.twentyfive),
				(int) getResources()
				.getDimensionPixelOffset(R.dimen.twentyfive));
		addImageViewParams.gravity = Gravity.CENTER_VERTICAL;
		addImageViewParams.setMargins((int) getResources()
				.getDimensionPixelOffset(R.dimen.twenty), (int) getResources()
				.getDimensionPixelOffset(R.dimen.five), (int) getResources()
				.getDimensionPixelOffset(R.dimen.twenty), (int) getResources()
				.getDimensionPixelOffset(R.dimen.five));
		addImageView.setLayoutParams(addImageViewParams);
		addImageView.setId(dynamicIncValue);
		// addImageView.setBackgroundResource(R.drawable.ic_add_blue_icon);
		if (dynamicIncValue == 1001) {
			addImageView
			.setBackgroundResource(R.drawable.selector_add_phonenumber);
		} else {
			addImageView
			.setBackgroundResource(R.drawable.selector_close_phonenumber);
		}

		typeLayout.addView(addImageView);
		addImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if(v.getId() == dynamicIncValue){
				// addImageView.setBackgroundResource(R.drawable.ic_close);
				// addDynamicLayout();
				// }else{
				// int phoneId = v.getId() + 1000;
				// int phoneValueId = v.getId() + 2000;
				// phoneValue.remove(new Integer(phoneId));
				// phoneValueType.remove(new Integer(phoneValueId));
				// LinearLayout layout = (LinearLayout)findViewById(v.getId());
				// layout.removeAllViews();
				// }
				if (v.getId() == 1001) {
					addDynamicLayout();
				} else {
					int phoneId = v.getId() + 1000;
					int phoneValueId = v.getId() + 2000;
					phoneValue.remove(Integer.valueOf(phoneId));
					phoneValueType.remove(Integer.valueOf(phoneValueId));
					LinearLayout layout = (LinearLayout) findViewById(v.getId());
					layout.removeAllViews();
				}
			}
		});
	}

	private boolean phoneValidation() {
		phoneArrayId.clear();
		phoneArrayNumber.clear();
		boolean newCheckValue = false;
		for (int i = 0; i < phoneValue.size(); i++) {
			EditText phoneVauleText = (EditText) findViewById(phoneValue.get(i));
			Spinner phoneTypeText = (Spinner) findViewById(phoneValueType
					.get(i));
			if (phoneVauleText.getText().toString().length() > 0) {
				newCheckValue = true;
				phoneArrayNumber.add(phoneVauleText.getText().toString());
				phoneArrayId.add(Singleton.getInstance().phoneTypeIdArray
						.get(Singleton.getInstance().phoneTypeArray
								.indexOf(phoneTypeText.getSelectedItem()
										.toString())));
			} else {
				newCheckValue = false;
			}
		}
		return newCheckValue;
	}

	private boolean companyValidation() {
		boolean checkValue = false;
		if (type == 0) {
			if (companyName.getText().toString().length() > 0) {
				checkValue = validation();
			} else {
				Toast.makeText(getApplicationContext(),
						Constants.TOAST_COMPANY_NAME, Constants.TOASTMSG_TIME)
						.show();
			}
		} else {
			checkValue = validation();
		}
		return checkValue;
	}

	private boolean validation() {
		boolean checkValue = false;

		if (firstName.getText().toString().length() > 0) {
			if (lastName.getText().toString().length() > 0) {
				if (phoneValidation()) {
					if (email.getText().toString().length() > 0) {
						//						if (Constants.isEditTextContainEmail(email.getText().toString()) == true) {
						//							
						//						} else {
						//							Toast.makeText(
						//									context,
						//									getResources().getString(
						//											R.string.valid_email),
						//									Constants.TOASTMSG_TIME).show();
						//						}

						if (companyStreetAddress.getText().toString()
								.length() > 0) {
							if (zip.getText().toString().length() > 0) {
								if (city.getText().toString().length() > 0) {
									checkValue = true;
								} else {
									Toast.makeText(getApplicationContext(),
											Constants.TOAST_CITY,
											Constants.TOASTMSG_TIME).show();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										Constants.TOAST_ZIP,
										Constants.TOASTMSG_TIME).show();
							}
						} else {
							if (type == 1) {
								Toast.makeText(
										getApplicationContext(),
										Constants.PROSPECT_TOAST_COMPANY_STREET_ADDRESS,
										Constants.TOASTMSG_TIME).show();
							} else {
								Toast.makeText(
										getApplicationContext(),
										Constants.TOAST_COMPANY_STREET_ADDRESS,
										Constants.TOASTMSG_TIME).show();
							}
						}

					} else {
						Toast.makeText(getApplicationContext(),
								Constants.TOAST_EMAIL, Constants.TOASTMSG_TIME)
								.show();
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

	class LoadDropDownItemsAsyncTask extends AsyncTask<String, Void, Void> {
		String mRequestUrl;

		public LoadDropDownItemsAsyncTask(String requestUrl) {
			this.mRequestUrl = requestUrl;
		}

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(AddProspectActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				jsonResultText = serviceHelper.jsonSendHTTPRequest(
						Constants.EMPTY_STRING, this.mRequestUrl,
						Constants.REQUEST_TYPE_GET);
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
			loadOfflineDropDown(jsonResultText);

		}
	}

	public void loadOfflineDropDown(JSONObject resultData){
		try {
			if(resultData != null){
				if (resultData.has(Constants.KEY_PHONE_TYPE_RESPONSE)) {
					JSONArray localJsonArray = resultData.getJSONArray(Constants.KEY_PHONE_TYPE_RESPONSE);

					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject localJsonObject = localJsonArray.getJSONObject(i);
						if (localJsonObject.has(Constants.KEY_PHONE_TYPE_ID)) {
							Singleton.getInstance().phoneTypeIdArray.add(localJsonObject.getString(
									Constants.KEY_PHONE_TYPE_ID).toString());
							Singleton.getInstance().phoneTypeArray
							.add(localJsonObject.getString(Constants.KEY_PHONE_TYPE_PHONE_TYPE).toString());
						} else {
							Toast.makeText(context,Constants.TOAST_CONNECTION_ERROR,Constants.TOASTMSG_TIME).show();
						}
					}
					addDynamicLayout();
					ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
							AddProspectActivity.this,
							android.R.layout.simple_spinner_item,
							Singleton.getInstance().phoneTypeArray);
					ptspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
					typeText.setAdapter(ptspinnerArrayAdapter);

				}
				if (resultData.has(Constants.KEY_COMPANY_TYPE_RESPONSE)) {

					JSONArray localJsonArray = resultData.getJSONArray(Constants.KEY_COMPANY_TYPE_RESPONSE);

					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject localJsonObject = localJsonArray.getJSONObject(i);
						ArrayList<String> cSubTypeIdArray = new ArrayList<String>();
						ArrayList<String> cSubTypeArray = new ArrayList<String>();
						if (localJsonObject.has(Constants.KEY_COMPANY_TYPE_ID)) {
							Singleton.getInstance().companyTypeIdArray.add(localJsonObject.getString(Constants.KEY_COMPANY_TYPE_ID).toString());
							Singleton.getInstance().companyTypeArray.add(localJsonObject
									.getString(Constants.KEY_COMPANY_TYPE_COMPANY_TYPE).toString());
							JSONArray localJsonArray1 = localJsonObject.getJSONArray(Constants.KEY_ADDPROSPECT_COMPANY_SUB_TYPE_RESPONSE);
							for (int j = 0; j < localJsonArray1.length(); j++) {
								JSONObject localJsonObject1 = localJsonArray1.getJSONObject(j);
								if (localJsonObject.has(Constants.KEY_COMPANY_SUB_TYPE_ID)) {
									cSubTypeIdArray.add(localJsonObject1.getString(Constants.KEY_COMPANY_TYPE_ID).toString());
									cSubTypeArray.add(localJsonObject1.getString(Constants.KEY_COMPANY_SUB_TYPE_COMPANY_SUB_TYPE).toString());
								} else {
									Toast.makeText(context,Constants.TOAST_CONNECTION_ERROR,Constants.TOASTMSG_TIME).show();
								}
							}
							Singleton.getInstance().companySubTypeIdArray.add(cSubTypeIdArray);
							Singleton.getInstance().companySubTypeArray.add(cSubTypeArray);
						} else {
							Toast.makeText(context,
									Constants.TOAST_CONNECTION_ERROR,
									Constants.TOASTMSG_TIME).show();
						}
					}


					ArrayAdapter<String> ctspinnerArrayAdapter = new ArrayAdapter<String>(
							AddProspectActivity.this,
							android.R.layout.simple_spinner_item,
							Singleton.getInstance().companyTypeArray);
					ctspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
					companyType.setAdapter(ctspinnerArrayAdapter);

					if (Singleton.getInstance().companySubTypeArray.size() > 0) {
						if (Singleton.getInstance().companySubTypeArray.get(0).size() > 0) {
							companySubTypeFrame.setVisibility(View.GONE);
							ArrayAdapter<String> cstspinnerArrayAdapter = new ArrayAdapter<String>(
									AddProspectActivity.this,
									android.R.layout.simple_spinner_item,
									Singleton.getInstance().companySubTypeArray
									.get(0));
							cstspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
							companySubType.setAdapter(cstspinnerArrayAdapter);

							iscompanySubType = true;
						} else {
							iscompanySubType = false;
							companySubTypeFrame.setVisibility(View.VISIBLE);
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

	public String getAddProfessionalContactAndAddProspectJsonRequestData(
			String type) {
		// TODO Auto-generated method stub
		String stringData = "";

		JSONArray phoneNumberArray;
		JSONObject customerJsonData, phoneNumberJsonData;

		customerJsonData = new JSONObject();
		jsonRequestText = new JSONObject();
		phoneNumberArray = new JSONArray();
		for (int i = 0; i < phoneArrayNumber.size(); i++) {
			phoneNumberJsonData = new JSONObject();
			try {
				phoneNumberJsonData.putOpt(
						Constants.KEY_ADDPROSPECT_PHONE_NUMBERTYPE_ID,
						phoneArrayId.get(i));
				phoneNumberJsonData.putOpt(
						Constants.KEY_ADDPROSPECT_PHONE_NUMBER,
						phoneArrayNumber.get(i));
			} catch (JSONException e) {
				Log.e(Constants.KEY_ERROR, e.toString());
			}
			phoneNumberArray.put(phoneNumberJsonData);
		}

		try {
			customerJsonData = new JSONObject();
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_DEALER_ID,
					dealerID);
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_EMPLOYEE_ID,
					employeeID);
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_TYPE, type);
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_FIRST_NAME,
					firstName.getText().toString());
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_LAST_NAME,
					lastName.getText().toString());
			if (type.equalsIgnoreCase(Constants.KEY_ADD_PROFESSIONAL_TYPE_VALUE)) {
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMPANY_NAME,
						companyName.getText().toString());
				customerJsonData.putOpt(
						Constants.KEY_ADDPROSPECT_COMAPNY_TYPE_ID, Singleton
						.getInstance().companyTypeIdArray
						.get(defalutposition));
				if (iscompanySubType) {
					customerJsonData.putOpt(
							Constants.KEY_ADDPROSPECT_COMAPNY_SUB_TYPE_ID,
							Singleton.getInstance().companySubTypeIdArray.get(
									defalutposition).get(companySubTypuId));
				}
			}
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_EMAIL, email
					.getText().toString());
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_STREET,
					companyStreetAddress.getText().toString());
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_ZIP, zip
					.getText().toString());
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_CITY, city
					.getText().toString());
			customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_STATE, Constants
					.getStates().get(statesPosition).substring(0, 2));
			customerJsonData.put(
					Constants.KEY_ADDPROSPECT_CUSTOMER_PHONE_NUMBER,
					phoneNumberArray);

		} catch (JSONException e) {
			Log.e(Constants.KEY_ERROR, e.toString());
		}

		try {
			jsonRequestText.put(Constants.KEY_ADDPROSPECT_REQUEST,
					customerJsonData);
			stringData = jsonRequestText.toString();
			Log.e("stringData ", stringData);
		} catch (JSONException e) {
			Log.e(Constants.KEY_ERROR, e.toString());
		}
		return stringData;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if ((TextView) parent.getChildAt(0) != null) {
			((TextView) parent.getChildAt(0)).setTypeface(droidSans);
			((TextView) parent.getChildAt(0)).setPadding(0, 0, 0, 0);
		}
		switch (parent.getId()) {

		case R.id.companyTypespinner:
			if (defalutposition != position) {
				defalutposition = position;
				if (Singleton.getInstance().companySubTypeArray.size() > position) {
					if (Singleton.getInstance().companySubTypeArray.get(
							position).size() > 0) {
						companySubTypeFrame.setVisibility(View.VISIBLE);
						ArrayAdapter<String> cstspinnerArrayAdapter = new ArrayAdapter<String>(
								AddProspectActivity.this,
								android.R.layout.simple_spinner_item,
								Singleton.getInstance().companySubTypeArray
								.get(position));
						cstspinnerArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
						// drop
						// down
						// view
						companySubType.setAdapter(cstspinnerArrayAdapter);
						iscompanySubType = true;
					} else {
						companySubTypeFrame.setVisibility(View.GONE);
						iscompanySubType = false;
					}
				}
			} else {
			}
			break;

		case R.id.companysubType:
			companySubTypuId = position;
			break;

		case R.id.state:
			statesPosition = position;
			break;

		case R.id.countryspinner:
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	class AddProspectAndAprofessionalContactAsyncTask extends
	AsyncTask<String, Void, Void> {

		String mRequestUrl, mRequestData, mCustType;

		public AddProspectAndAprofessionalContactAsyncTask(String requestUrl,
				String requestData, String custType) {
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
			pDialog = new ActivityIndicator(AddProspectActivity.this);
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

									Toast.makeText(
											context,
											mCustType
											+ localJsonObject
											.getString(
													Constants.KEY_ADDPROSPECT_CUSTOMER_ID)
													.toString(),
													Toast.LENGTH_LONG).show();
									customerId = localJsonObject.getString(
											Constants.KEY_ADDPROSPECT_CUSTOMER_ID)
											.toString();
									if (!checkStatus) {
										gotoHomeActivity();
									} else {
										Intent calenderIntent = new Intent(
												AddProspectActivity.this,
												CalendarActivity.class);
										calenderIntent.putExtra("PageInfo",
												AddProspectActivity.this.getClass()
												.getSimpleName());
										calenderIntent.putExtra("CustomerId",
												customerId);
										calenderIntent.putExtra("CustomerFullName",
												firstName.getText().toString()
												+ ", "
												+ lastName.getText()
												.toString());
										calenderIntent
										.putExtra("CustomerAddress",
												companyStreetAddress.getText().toString()+ ", "
														+ city.getText().toString()+ ", "
														+ Constants.getStates().get(statesPosition)
														.substring(0,2)+ ", "+ zip.getText().toString());
										startActivity(calenderIntent);
										finish();
									}

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

	class GetCityAndStateFromZipCode extends AsyncTask<String, Void, Void> {

		String mRequestURL;

		public GetCityAndStateFromZipCode(String zipcode) {
			this.mRequestURL = Constants.URL_GET_CITY_AND_STATE_FROM_ZIPCODE
					+ zipcode;
		}

		@Override
		protected void onPreExecute() {
			/*if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(AddProspectActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();*/
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
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//pDialog.dismiss();
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
										city.setText(separated[0]);
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
												state.setSelection(position1);
												
												Log.d("State : " + position1 + " ",
														stateArray1.get(position1));
												break;
											}
										}

										int position2 = -1;
										String[] countryArray = new String[2];
										countryArray = getResources()
												.getStringArray(
														R.array.planets_array);
										for (int j = 0; j < countryArray.length; j++) {

											if (countryArray[j]
													.equalsIgnoreCase(separated[2])) {
												position2 = j;
												country.setSelection(position2);
												Log.d("Country : " + position2
														+ " ",
														countryArray[position2]);
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

	public void gotoHomeActivity() {
		// TODO Auto-generated method stub
		homeint = new Intent(this, MainActivity.class);
		startActivity(homeint);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		gotoHomeActivity();
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
		if (loadDropDownAsyncTask != null) {
			loadDropDownAsyncTask.cancel(true);
			loadDropDownAsyncTask = null;
		} 
		if (addProspectAndAprofessionalContactAsyncTask != null) {
			addProspectAndAprofessionalContactAsyncTask.cancel(true);
			addProspectAndAprofessionalContactAsyncTask = null;
		} 
		if (getCityAndStateFromZipCode != null) {
			getCityAndStateFromZipCode.cancel(true);
			getCityAndStateFromZipCode = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (currentadd != null) {
			currentadd.cancel(true);
			currentadd = null;
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
			//			 latitude = 36.53489052024688;
			//			 longitude = -119.00457180454396;
			String address = getAddress(context, latitude, longitude);
			return address;
		}

		@Override
		protected void onPostExecute(String resultString) {
			dissmisspDialog();
			if (resultString != null) {
				if (addressLine != null) {
					companyStreetAddress.setText(addressLine);

				}
				if (zipCode != null) {
					Log.d("ZipCode", zipCode);
					zip.setText(zipCode.replace(" ", ""));
					getCityAndStateFromZipCode = new GetCityAndStateFromZipCode(
							zip.getText().toString()).execute();
				}
			} else {
				Toast.makeText(context, "Can't get address", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void dissmisspDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}
	}

	public String getAddress(Context ctx, double latitude, double longitude) {
		StringBuilder result = new StringBuilder();
		try {
			Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 10);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				zipCode = address.getPostalCode();
				addressLine = address.getAddressLine(0);
				int count =0;
				while (zipCode==null&&count<addresses.size()) {
					zipCode=addresses.get(count).getPostalCode();
					count++;
				}
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
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		companyName.setText(Constants.EMPTY_STRING);
		firstName.setText(Constants.EMPTY_STRING);
		lastName.setText(Constants.EMPTY_STRING);
		email.setText(Constants.EMPTY_STRING);
		companyStreetAddress.setText(Constants.EMPTY_STRING);
		zip.setText(Constants.EMPTY_STRING);
		city.setText(Constants.EMPTY_STRING);
		System.gc();
	}
}
