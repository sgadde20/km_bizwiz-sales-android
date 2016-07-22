package com.webparadox.bizwizsales.dialogs;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CreateFollowUpDialog extends Dialog implements
		android.view.View.OnClickListener, OnItemSelectedListener {

	Typeface droidSans, droidSansBold;
	Context mContext;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	ArrayList<String> empNames = new ArrayList<String>();

	TextView newFollowupScheduleTitle, newFollowupAsignTitle,
			newFollowupDateTitle, newFollowupDate, newFollowupTimeTitle,
			newFollowupTime, newFollowupNoteTitle, newFollowupNoteEdit;
	Button newFollowupSaveBtn, followUpCloseBtn;
	Spinner newFollowupAsignSpinner;
	ImageView newFollowupDateImage, newFollowupTimeImage;
	String cusName, AsignName, dealerId, cusId, empId, followupNote, date,
			time, employeeID;
	String timeSet = "";
	SharedPreferences userData;

	RefreshFollowup refreshFollowup;
	
	public CreateFollowupAsynctask followupTask;

	public interface RefreshFollowup {

		void refreshFollowUp();
	}

	public CreateFollowUpDialog(Context context, String cusName,
			String dealerId, String cusId, String empId) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		droidSans = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans-Bold.ttf");
		this.cusName = cusName;
		this.dealerId = dealerId;
		this.empId = empId;
		this.cusId = cusId;
		this.refreshFollowup = (RefreshFollowup) mContext;

		Constants.IsSaving = true;
		showCreateDialog();
	}

	@SuppressLint("NewApi")
	public void showCreateDialog() {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		WMLP.dimAmount = 0.7f;
		getWindow().setAttributes(WMLP);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.create_followup_dialog);
		
		userData = this.mContext.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		
		newFollowupScheduleTitle = (TextView) findViewById(R.id.schedule_new_followup_title);
		newFollowupScheduleTitle.setTypeface(droidSansBold);
		newFollowupAsignTitle = (TextView) findViewById(R.id.new_followup_asignto_title);
		newFollowupAsignTitle.setTypeface(droidSansBold);
		newFollowupAsignSpinner = (Spinner) findViewById(R.id.new_followup_signto_spinner);
		newFollowupAsignSpinner.setOnItemSelectedListener(this);
		newFollowupDateImage = (ImageView) findViewById(R.id.new_followup_date_img);
		newFollowupDateImage.setOnClickListener(this);
		newFollowupTimeImage = (ImageView) findViewById(R.id.new_followup_time_img);
		newFollowupTimeImage.setOnClickListener(this);
		newFollowupDateTitle = (TextView) findViewById(R.id.new_followup_date_title);
		newFollowupDateTitle.setTypeface(droidSansBold);
		newFollowupDate = (TextView) findViewById(R.id.new_followup_date);
		newFollowupDate.setTypeface(droidSans);
		newFollowupDate.setOnClickListener(this);
		newFollowupTimeTitle = (TextView) findViewById(R.id.new_followup_time_title);
		newFollowupTimeTitle.setTypeface(droidSansBold);
		newFollowupTime = (TextView) findViewById(R.id.new_followup_time);
		newFollowupTime.setTypeface(droidSans);
		newFollowupTime.setOnClickListener(this);
		newFollowupNoteTitle = (TextView) findViewById(R.id.new_followup_notes_title);
		newFollowupNoteTitle.setTypeface(droidSansBold);
		newFollowupNoteEdit = (EditText) findViewById(R.id.new_followup_notes_edittext);
		newFollowupNoteEdit.setTypeface(droidSans);
		newFollowupSaveBtn = (Button) findViewById(R.id.new_followup_save);
		newFollowupSaveBtn.setTypeface(droidSansBold);
		newFollowupSaveBtn.setOnClickListener(this);
		followUpCloseBtn = (Button) findViewById(R.id.new_followup_close_btn);
		followUpCloseBtn.setOnClickListener(this);
		newFollowupSaveBtn.setClickable(false);
		newFollowupSaveBtn.setBackground(mContext.getResources().getDrawable(
				R.drawable.resolve_disable_background));

		newFollowupNoteEdit.clearFocus();
		// followUpNote.setMovementMethod(new ScrollingMovementMethod());
		newFollowupNoteEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				newFollowupNoteEdit.setCursorVisible(true);
				return false;
			}
		});
		newFollowupNoteEdit.addTextChangedListener(new TextWatcher() {

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@SuppressLint("NewApi")
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.toString().trim().length() > 0) {
					newFollowupSaveBtn.setClickable(true);
					newFollowupSaveBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.selector_prospect_save_button));
				} else {
					newFollowupSaveBtn.setClickable(false);
					newFollowupSaveBtn
							.setBackground(mContext.getResources().getDrawable(
									R.drawable.resolve_disable_background));
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		setSpinnerAdapter();

	}

	public void setSpinnerAdapter() {

		for (int i = 0; i < Singleton.getInstance().mEmployeeNames.size(); i++) {

			empNames.add(Singleton.getInstance().mEmployeeNames.get(i).mEmployeeNames);
		}

		/*
		 * ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
		 * R.layout.spinner_text, empNames);
		 * 
		 * // Drop down layout style - list view with radio button
		 * dataAdapter.setDropDownViewResource(R.layout.spinner_text);
		 */

		ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
				mContext, R.layout.spinner_text, empNames) {

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
		newFollowupAsignSpinner.setAdapter(ptspinnerArrayAdapter);

		/*
		 * ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
		 * R.layout.spinner_text,empNames ) {
		 * 
		 * public View getView(int position, View convertView, ViewGroup parent)
		 * { View v = super.getView(position, convertView, parent);
		 * 
		 * ((TextView) v).setTypeface(droidSansBold);
		 * 
		 * return v; }
		 * 
		 * 
		 * public View getDropDownView(int position, View convertView, ViewGroup
		 * parent) { View v =super.getDropDownView(position, convertView,
		 * parent);
		 * 
		 * ((TextView) v).setTypeface(droidSans);
		 * 
		 * return v; } };
		 */

		// attaching data adapter to spinner
		// newFollowupAsignSpinner.setAdapter(dataAdapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.new_followup_close_btn:
			dismiss();
			break;
		case R.id.new_followup_date:
			Time currDate = new Time(Time.getCurrentTimezone());
			currDate.setToNow();
			DatePickerDialog dateDialog = new DatePickerDialog(mContext,
					pickerListener, currDate.year, currDate.month,
					currDate.monthDay);
			dateDialog.show();
			break;
		case R.id.new_followup_time:
			Calendar calNew = Calendar.getInstance();
			hour = calNew.get(Calendar.HOUR_OF_DAY);
			minute = calNew.get(Calendar.MINUTE);
			TimePickerDialog timeDialogNew = new TimePickerDialog(mContext,
					timePickerListener, hour, minute, false);
			timeDialogNew.show();
			break;
		case R.id.new_followup_date_img:
			Time currDateNew = new Time(Time.getCurrentTimezone());
			currDateNew.setToNow();
			DatePickerDialog dateDialogNew = new DatePickerDialog(mContext,
					pickerListener, currDateNew.year, currDateNew.month,
					currDateNew.monthDay);
			dateDialogNew.show();

			break;
		case R.id.new_followup_time_img:
			Calendar cal = Calendar.getInstance();
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
			TimePickerDialog timeDialog = new TimePickerDialog(mContext,
					timePickerListener, hour, minute, false);
			timeDialog.show();

			break;

		case R.id.new_followup_save:
			if (Constants.IsSaving) {
				followupNote = newFollowupNoteEdit.getText().toString();

				if (AsignName.length() > 0) {

					if (newFollowupDate.getText().toString().length() > 0) {

						if (newFollowupTime.getText().toString().length() > 0) {
							if (followupNote.trim().length() > 0) {
								date = month + 1 + "/" + day + "/" + year;
								time = hour + ":" + minute + " " + timeSet;
								String curEmpId = Singleton.getInstance().mEmployeeNames
										.get(empNames.indexOf(AsignName)).mIds;

								final JSONObject reqObj_data = new JSONObject();
								final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
								try {
									reqObj_data.put(
											Constants.KEY_LOGIN_DEALER_ID,
											dealerId);
									reqObj_data.put(
											Constants.JSON_KEY_CUSTOMER_ID,
											cusId);
									reqObj_data.put(
											Constants.KEY_LOGIN_EMPLOYEE_ID,
											curEmpId);
									reqObj_data.put(
											Constants.KEY_LOGIN_CREATED_BY_EMPLOYEE_ID,
											employeeID);
									reqObj_data.put(
											Constants.JSON_KEY_SECTION_ID,
											Constants.SECTION_ID);
									reqObj_data.put(
											Constants.JSON_KEY_FOLLOWUP_NOTES,
											followupNote);
									reqObj_data.put(
											Constants.JSON_KEY_FOLLOWUP_DATE,
											date);
									reqObj_data.put(
											Constants.JSON_KEY_FOLLOWUP_TIME,
											time);

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								reqData.add(reqObj_data);

								Log.d("REQ", reqData.toString());
								Constants.IsSaving = false;
								followupTask = new CreateFollowupAsynctask(
										mContext, reqData, (Activity) mContext);
								followupTask.execute();

							} else {
								Toast.makeText(mContext,
										Constants.TOAST_NO_FOLLOWUP_NOTES,
										Toast.LENGTH_SHORT).show();

							}
						} else {
							Toast.makeText(mContext,
									Constants.TOAST_NO_FOLLOWUP_TIME,
									Toast.LENGTH_SHORT).show();
						}

					} else {

						Toast.makeText(mContext,
								Constants.TOAST_NO_FOLLOWUP_DATE,
								Toast.LENGTH_SHORT).show();
					}
				} else {

					Toast.makeText(mContext, Constants.TOAST_NO_FOLLOWUP_EMP,
							Toast.LENGTH_SHORT).show();
				}

			}

			break;
		default:
			break;
		}

	}

	public DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			String monthName = new DateFormatSymbols().getMonths()[month];
			newFollowupDate.setText(day + " " + monthName.substring(0, 3) + " "
					+ year);

		}
	};

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			// TODO Auto-generated method stub
			hour = hourOfDay;
			minute = minutes;

			if (hour > 12) {
				hour -= 12;
				timeSet = "PM";
			} else if (hour == 0) {
				hour += 12;
				timeSet = "AM";
			} else if (hour == 12)
				timeSet = "PM";
			else
				timeSet = "AM";
			String min = String.valueOf(minutes);
			if (min.length() == 1) {
				newFollowupTime.setText(hour + ":" + "0" + minutes + timeSet);
			} else {
				newFollowupTime.setText(hour + ":" + minutes + timeSet);
			}

		}

	};

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		AsignName = parent.getItemAtPosition(position).toString();

		// Showing selected spinner item

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	public class CreateFollowupAsynctask extends AsyncTask<Void, Integer, Void> {

		Context mContext;
		Activity mContext2;
		ActivityIndicator pDialog;
		ServiceHelper serviceHelper;
		JSONObject responseJson;
		ArrayList<JSONObject> mRequestJson;
		JSONObject localJsonObject;
		String status;

		public CreateFollowupAsynctask(Context context,
				ArrayList<JSONObject> reqData, Context context2) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.mContext2 = (Activity) context2;
			serviceHelper = new ServiceHelper(this.mContext);
			this.mRequestJson = reqData;
		}

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
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.CREATE_FOLLOWUPS_URL,
					Constants.REQUEST_TYPE_POST);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (responseJson != null) {
				if (responseJson.has(Constants.NEW_FOLLOWUP)) {
					try {
						localJsonObject = responseJson
								.getJSONObject(Constants.NEW_FOLLOWUP);
						status = localJsonObject
								.getString(Constants.KEY_ADDPROSPECT_STATUS);
					} catch (JSONException e) { // TODO Auto-generated catch
												// block
						e.printStackTrace();
					}
					if (status.equals(Constants.VALUE_SUCCESS)) {

						refreshFollowup.refreshFollowUp();

					} else {
						Constants.IsSaving = true;
						Toast.makeText(mContext,
								Constants.TOAST_CONNECTION_ERROR,
								Toast.LENGTH_SHORT).show();
					}

				} else {
					Constants.IsSaving = true;
					Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Constants.IsSaving = true;
				Toast.makeText(mContext, Constants.TOAST_INTERNET,
						Toast.LENGTH_SHORT).show();

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
			dismiss();
		}
	}

}
