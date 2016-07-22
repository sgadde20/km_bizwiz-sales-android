package com.webparadox.bizwizsales;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.helper.BizWizUpdateReciver;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class SettingsActivity extends Activity  {

	ImageView imageViewBack, logoBtn, imageViewClosePicker, imageViewDone;
	TextView textFollowups, textAppointments;
	Typeface droidSans, droidSansBold;
	RelativeLayout layout_appointment, layout_followups, 
	layoutAddNotify,layoutAddReminder;
	LinearLayout layout_picker;
	boolean isAppntVisible, isFollowVisible;

	Switch switchAppointment, switchFollow, switchPriceInfo;
	CheckBox checkBoxAddReminder, checkBoxAddNotification;
	TextView textView_settings, textview_notify_appnt, textview_notify_follow, textview_price_info;
	NumberPicker numberPickerDays, numperPickerHours,numperPickerMin;

	// Shared preference for store settings
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor editor;
	boolean switchAppntsIsChecked = false;
	boolean switchFollowUpIsChecked = false;
	boolean checkBoxAppntReminder = false;
	boolean checkBoxFollowupReminder = false;
	String prefNotifyAppnts, prefNotifyFollowUps,notificationType ="",notifictionDays ="",noficationHours ="",notificationStatus="";
	int numDays;
	int numHours;
	int numMin;
	private ActivityIndicator pDialog;
	JSONObject jsonRequestText, jsonResultText;
	Context context;
	ServiceHelper serviceHelper;
	AsyncTask<String, Void, Void> enableandDisableNotificationAsyncTask;
	String minValue = "";
	TextView txtAutoSync;
	Spinner autoSyncSpinner;
	int initialAutoSyncValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.setttings_activity);
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");

		preferenceSettings = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		editor = preferenceSettings.edit();
		context = this;
		serviceHelper = new ServiceHelper(context);

		imageViewBack = (ImageView) findViewById(R.id.back_icon);

		layout_appointment = (RelativeLayout) findViewById(R.id.layout_appointment);
		layout_followups = (RelativeLayout) findViewById(R.id.layout_followup);
		layoutAddReminder = (RelativeLayout) findViewById(R.id.layout_add_reminder);
		layoutAddNotify = (RelativeLayout) findViewById(R.id.layout_add_notification);
		textFollowups = (TextView) findViewById(R.id.textView_followups);
		textAppointments = (TextView) findViewById(R.id.textView_appointments);
		textview_price_info = (TextView) findViewById(R.id.textView_price_display);
		txtAutoSync=(TextView) findViewById(R.id.textView_auto_sync_display);
		autoSyncSpinner=(Spinner) findViewById(R.id.autoSyncSpinner);
		autoSyncSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				editor.putInt(Constants.PREF_AUTO_SYNC, position);
				editor.commit();
				if (position==0) {
					AlarmManager alarmreminderManager = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					Intent intentReminder = new Intent(context.getApplicationContext(), BizWizUpdateReciver.class);
					PendingIntent pendingReminderIntent = PendingIntent.getBroadcast(context, 0,
							intentReminder, PendingIntent.FLAG_CANCEL_CURRENT);
					alarmreminderManager.cancel(pendingReminderIntent);
				}
				else if (position>0) {
					long time = SystemClock.elapsedRealtime();
					int UPDATE_PERIOD = Constants.getSycTimesInMilliseconds(position);
					AlarmManager updatemgr = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					Intent update_intent = new Intent(context, BizWizUpdateReciver.class);
					PendingIntent udpatepi = PendingIntent.getBroadcast(context, 0,
							update_intent, 0);
					updatemgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time,
							UPDATE_PERIOD, udpatepi);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		if (preferenceSettings.contains(Constants.PREF_AUTO_SYNC)) {
			initialAutoSyncValue=preferenceSettings.getInt(Constants.PREF_AUTO_SYNC, 0);
		} else {
			initialAutoSyncValue=2;
		}
		ArrayAdapter<String> ctspinnerArrayAdapter = new ArrayAdapter<String>(
				SettingsActivity.this,
				android.R.layout.simple_spinner_item,
				Constants.getSyncTimes());
		ctspinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
		autoSyncSpinner.setAdapter(ctspinnerArrayAdapter);
		autoSyncSpinner.setSelection(initialAutoSyncValue);

		textview_notify_appnt = (TextView) findViewById(R.id.textView_notifyme_appnts);
		textview_notify_follow = (TextView) findViewById(R.id.textView_notifyme_followup);
		textView_settings = (TextView) findViewById(R.id.textView_settings);
		switchAppointment = (Switch) findViewById(R.id.switchAppointments);
		switchAppointment.setSwitchTextAppearance(getApplicationContext(),
				R.style.MySwitch);
		switchFollow = (Switch) findViewById(R.id.switchNotification);
		switchFollow.setSwitchTextAppearance(getApplicationContext(),
				R.style.MySwitch);

		checkBoxAddReminder = (CheckBox) findViewById(R.id.checkBox_addre_reminder);
		checkBoxAddNotification = (CheckBox) findViewById(R.id.checkBox_add_notification);
		layout_picker = (LinearLayout) findViewById(R.id.layout_picker);
		numberPickerDays = (NumberPicker) findViewById(R.id.numberPicker_days);
		numperPickerHours = (NumberPicker) findViewById(R.id.numberPicker_hours);
		numperPickerMin = (NumberPicker) findViewById(R.id.numberPicker_min);
		numberPickerDays.setMinValue(0);
		numberPickerDays.setMaxValue(31);
		numperPickerHours.setMinValue(0);
		numperPickerHours.setMaxValue(24);
		numperPickerMin.setMinValue(0);
		numperPickerMin.setMaxValue(59);
		textview_notify_appnt.setTypeface(droidSansBold);
		textview_notify_follow.setTypeface(droidSansBold);
		textFollowups.setTypeface(droidSansBold);
		textAppointments.setTypeface(droidSansBold);
		textView_settings.setTypeface(droidSansBold);
		textview_price_info.setTypeface(droidSansBold);
		txtAutoSync.setTypeface(droidSansBold);

		switchAppointment.setChecked(preferenceSettings.getBoolean(
				Constants.PREF_SWITCH_APPNT_CHECKED, false));
		switchFollow.setChecked(preferenceSettings.getBoolean(
				Constants.PREF_SWITCH_FOLLOW_CHECKED, false));
		checkBoxAddReminder.setChecked(preferenceSettings.getBoolean(
				Constants.PREF_CHECKBOX_APPNT_CHECKED, false));
		checkBoxAddNotification.setChecked(preferenceSettings.getBoolean(
				Constants.PREF_CHECKBOX_FOLLOW_CHECKED, false));
		if(preferenceSettings.getString(Constants.PREF_TEXT_APPNT_NOTIFY,"").length()>0){
			Spanned textDays = Html.fromHtml("Notify me <font color=#74a7d4>"
					+ preferenceSettings.getString(Constants.PREF_TEXT_APPNT_NOTIFY,"")
					+ "</font> before appointments");
			textview_notify_appnt.setText(textDays);
		}
		if(preferenceSettings.getString(Constants.PREF_TEXT_FOLLOW_NOTIFY,"").length()>0){
			Spanned followDays = Html.fromHtml("Notify me <font color=#74a7d4>"
					+ preferenceSettings.getString(Constants.PREF_TEXT_FOLLOW_NOTIFY,"")
					+ "</font> before followups are due");
			textview_notify_follow.setText(followDays);
		}

		if (preferenceSettings.getBoolean(Constants.PREF_SWITCH_APPNT_CHECKED,
				false)) {
			layoutAddReminder.setVisibility(View.VISIBLE);
		}
		if (preferenceSettings.getBoolean(Constants.PREF_SWITCH_FOLLOW_CHECKED,
				false)) {
			layoutAddNotify.setVisibility(View.VISIBLE);
		}
		switchPriceInfo= (Switch) findViewById(R.id.switch_price_info);
		if (preferenceSettings.contains(Constants.PREF_SWITCH_SHOW_PRICE)) {
			switchPriceInfo.setChecked(preferenceSettings.getBoolean(
					Constants.PREF_SWITCH_SHOW_PRICE, false));
		} else {
			switchPriceInfo.setChecked(true);
			editor.putBoolean(Constants.PREF_SWITCH_SHOW_PRICE,
					true);
			editor.commit();
		}
		switchPriceInfo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					editor.putBoolean(Constants.PREF_SWITCH_SHOW_PRICE,
							false);
					editor.commit();
				}else {
					editor.putBoolean(Constants.PREF_SWITCH_SHOW_PRICE,
							true);
					editor.commit();
				}
			}
		});
		switchAppointment
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					buttonView.setChecked(false);
					checkBoxAddReminder.setChecked(false);
					layoutAddReminder.setVisibility(View.GONE);
					layoutAddReminder.setBackgroundColor(getResources()
							.getColor(R.color.reminder_cell));
					layout_picker.setVisibility(View.GONE);
					editor.putBoolean(
							Constants.PREF_SWITCH_APPNT_CHECKED, false);
					editor.putBoolean(
							Constants.PREF_CHECKBOX_APPNT_CHECKED,
							false);
					editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
							"");
					editor.commit();
					textview_notify_appnt.setText("");

					notificationType = "APPOINTMENT";
					notifictionDays = "0";
					noficationHours = "0";
					notificationStatus ="DISABLE";
					enableandDisableNotificationAsyncTask = new EnableandDisableNotificationAsyncTask().execute();

				} else {
					if (!checkBoxAddNotification.isChecked()) {
						if (switchFollow.isChecked()) {
							switchFollow.setChecked(false);
							editor.putBoolean(
									Constants.PREF_SWITCH_FOLLOW_CHECKED,
									false);
							editor.commit();
						}
					}

					numberPickerDays.setTag(1);
					numperPickerHours.setTag(1);
					numperPickerMin.setTag(1);
					buttonView.isChecked();
					layout_picker.setVisibility(View.VISIBLE);
					layoutAddReminder.setVisibility(View.VISIBLE);
					layoutAddReminder
					.setBackgroundColor(getResources()
							.getColor(
									R.color.settings_reminder_select));
					layoutAddNotify.setBackgroundColor(getResources()
							.getColor(R.color.reminder_cell));
				}
			}
		});

		switchFollow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub

				if (!isChecked) {
					buttonView.setChecked(false);
					checkBoxAddNotification.setChecked(false);
					layoutAddNotify.setVisibility(View.GONE);
					layoutAddNotify.setBackgroundColor(getResources().getColor(
							R.color.reminder_cell));
					layout_picker.setVisibility(View.GONE);
					editor.putBoolean(Constants.PREF_CHECKBOX_FOLLOW_CHECKED,
							false);
					editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY, "");
					editor.commit();
					textview_notify_follow.setText("");
					notificationType = "FOLLOWUP";
					notifictionDays = "0";
					noficationHours = "0";
					notificationStatus ="DISABLE";
					enableandDisableNotificationAsyncTask = new EnableandDisableNotificationAsyncTask().execute();
				} else {
					if (!checkBoxAddReminder.isChecked()) {
						if (switchAppointment.isChecked()) {
							switchAppointment.setChecked(false);
							editor.putBoolean(
									Constants.PREF_SWITCH_APPNT_CHECKED, false);
							editor.commit();
						}
					}

					buttonView.isChecked();
					numberPickerDays.setTag(0);
					numperPickerHours.setTag(0);
					numperPickerMin.setTag(0);
					layout_picker.setVisibility(View.VISIBLE);
					layoutAddNotify.setVisibility(View.VISIBLE);
					layoutAddNotify.setBackgroundColor(getResources().getColor(
							R.color.settings_reminder_select));
					layoutAddReminder.setBackgroundColor(getResources()
							.getColor(R.color.reminder_cell));
				}

			}
		});
		layoutAddReminder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				numberPickerDays.setTag(1);
				numperPickerHours.setTag(1);
				numperPickerMin.setTag(1);
				layout_picker.setVisibility(View.VISIBLE);
				layoutAddReminder.setBackgroundColor(getResources().getColor(
						R.color.settings_reminder_select));
				layoutAddNotify.setBackgroundColor(getResources().getColor(
						R.color.reminder_cell));
			}
		});
		layoutAddNotify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				numberPickerDays.setTag(0);
				numperPickerHours.setTag(0);
				numperPickerMin.setTag(0);
				layout_picker.setVisibility(View.VISIBLE);
				layoutAddReminder.setBackgroundColor(getResources().getColor(
						R.color.reminder_cell));
				layoutAddNotify.setBackgroundColor(getResources().getColor(
						R.color.settings_reminder_select));
			}
		});
		checkBoxAddReminder
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					buttonView.setChecked(false);
					switchAppointment.setChecked(false);
					textview_notify_appnt.setText("");
					layout_picker.setVisibility(View.GONE);
					editor.putBoolean(
							Constants.PREF_SWITCH_APPNT_CHECKED, false);
					editor.commit();
				} else {
					layoutAddReminder
					.setBackgroundColor(getResources()
							.getColor(
									R.color.settings_reminder_select));
					layoutAddNotify.setBackgroundColor(getResources()
							.getColor(R.color.reminder_cell));
					numberPickerDays.setTag(1);
					numperPickerHours.setTag(1);
					numperPickerMin.setTag(1);
					layout_picker.setVisibility(View.VISIBLE);
					buttonView.setChecked(true);
					switchAppointment.setChecked(true);
					editor.putBoolean(
							Constants.PREF_SWITCH_APPNT_CHECKED, true);
					editor.putBoolean(
							Constants.PREF_CHECKBOX_APPNT_CHECKED, true);
					editor.commit();
				}
			}
		});

		checkBoxAddNotification
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (!isChecked) {
					buttonView.setChecked(false);
					switchFollow.setChecked(false);
					textview_notify_follow.setText("");
					layout_picker.setVisibility(View.GONE);
					editor.putBoolean(
							Constants.PREF_SWITCH_FOLLOW_CHECKED, false);
					editor.commit();

				} else {
					layoutAddReminder.setBackgroundColor(getResources()
							.getColor(R.color.reminder_cell));
					layoutAddNotify
					.setBackgroundColor(getResources()
							.getColor(
									R.color.settings_reminder_select));
					numberPickerDays.setTag(0);
					numperPickerHours.setTag(0);
					numperPickerMin.setTag(0);
					layout_picker.setVisibility(View.VISIBLE);
					buttonView.setChecked(true);
					switchFollow.setChecked(true);
					editor.putBoolean(
							Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
					editor.putBoolean(
							Constants.PREF_CHECKBOX_FOLLOW_CHECKED,
							true);
					editor.commit();
				}
			}
		});
		layout_appointment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isAppntVisible) {
					layoutAddReminder.setVisibility(View.VISIBLE);
					isAppntVisible = true;
				} else {
					isAppntVisible = false;
					layoutAddReminder.setVisibility(View.GONE);
					layout_picker.setVisibility(View.GONE);
				}

			}
		});

		layout_followups.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isFollowVisible) {
					layoutAddNotify.setVisibility(View.VISIBLE);
					isFollowVisible = true;
				} else {
					layoutAddNotify.setVisibility(View.GONE);
					isFollowVisible = false;
					layout_picker.setVisibility(View.GONE);
				}

			}
		});
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});
		imageViewDone = (ImageView) findViewById(R.id.textView_done);
		imageViewDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_picker.setVisibility(View.GONE);
				numDays = numberPickerDays.getValue();
				numHours = numperPickerHours.getValue();
				numMin = numperPickerMin.getValue();
				if(numMin == 0){
					minValue =" ";
				}else if(numMin == 1){
					minValue = ""+numMin+" Minute ";
				}else{
					minValue = ""+numMin+" Minutes ";
				}

				if (numberPickerDays.getTag().equals(1)
						&& numberPickerDays.getTag().equals(1)) {
					if (numDays != 0 && numHours != 0) {
						if (numHours == 24 && numDays != 31) {
							numDays += 1;
							Spanned textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Days " +minValue
											+ "</font> before appointments");
							textview_notify_appnt.setText(textDays);
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									numDays + " Days " +minValue);
							editor.commit();
							CloseAppointments();
						} else if (numDays == 1 && numHours == 1) {
							Spanned text = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Day " + numHours
											+ " Hour " +minValue
											+ "</font> before appointments");
							textview_notify_appnt.setText(text);
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									numDays + " Day " + numHours
									+ " Hour " +minValue);
							editor.commit();
							CloseAppointments();
						} else if (numDays == 1 && numHours != 1) {

							Spanned text = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Day " + numHours
											+ " Hours " + minValue
											+ "</font> before appointments");
							textview_notify_appnt.setText(text);
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ numDays + " Day " + numHours
									+ " Hours " + minValue);
							editor.commit();
							CloseAppointments();
						} else if (numDays != 1 && numHours == 1) {
							Spanned text = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Days " + numHours
											+ " Hour " +minValue
											+ "</font> before appointments");
							textview_notify_appnt.setText(text);
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ numDays + " Days " + numHours
									+ " Hour " +minValue);
							editor.commit();
							CloseAppointments();
						} else if (numDays == 31 && numHours == 24) {
							numDays = 1;
							Spanned textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Month " + numDays
											+ " Day " +minValue
											+ "</font> before appointments");
							textview_notify_appnt.setText(textDays);
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ numDays + " Month " + numDays
									+ " Day " +minValue);
							editor.commit();
							CloseAppointments();
						} else {
							Spanned text = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Days " + numHours
											+ " Hours " +minValue
											+ "</font> before appointments");
							textview_notify_appnt.setText(text);
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ numDays + " Days " + numHours
									+ " Hours " +minValue);
							editor.commit();
							CloseAppointments();
						}

					} else if (numDays == 0 && numHours != 0) {
						Spanned textDays;
						if (numHours == 1) {
							textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numHours + " Hour " +minValue
											+ "</font> before appointments");
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ numHours + " Hour " +minValue);
							editor.commit();
						} else if (numHours == 24) {
							textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ (numDays + 1) + " Day " +minValue
											+ "</font> before appointments");
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ (numDays + 1) + " Day " +minValue);
							editor.commit();
						}

						else {
							textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numHours + " Hours " +minValue
											+ "</font> before appointments");
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									+ numHours + " Hours" +minValue);
							editor.commit();
						}
						textview_notify_appnt.setText(textDays);
						CloseAppointments();

					} else if (numDays != 0 && numHours == 0) {
						Spanned textDays;
						if (numDays == 1) {
							textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Day " +minValue
											+ "</font> before appointments");
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									numDays + " Day " +minValue);
							editor.commit();
						} else {
							textDays = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays + " Days " +minValue
											+ "</font> before appointments");
							editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
									numDays + " Days " +minValue);
							editor.commit();
						}
						textview_notify_appnt.setText(textDays);
						CloseAppointments();
						//Added by Ramesh 06/08/2014
					}else if(numDays == 0 && numHours == 0 && numMin != 0){
						Spanned textDays;

						textDays = Html
								.fromHtml("Notify me <font color=#74a7d4>"
										+ minValue
										+ "</font> before appointments");

						textview_notify_appnt.setText(textDays);
						editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
								minValue);
						editor.commit();
						CloseAppointments();
					}

					else {
						layout_picker.setVisibility(View.VISIBLE);
						Toast.makeText(getApplicationContext(),
								Constants.SELECT_DATE_TIME, Toast.LENGTH_SHORT)
								.show();
					}

				} else if (numberPickerDays.getTag().equals(0)
						&& numberPickerDays.getTag().equals(0)) {
					if (numDays != 0 && numHours != 0) {
						if (numHours == 24 && numDays != 31) {
							numDays += 1;
							Spanned textfollowup = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Days " +minValue
											+ "</font> before followups are due");
							textview_notify_follow.setText(textfollowup);
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									+ numDays
									+ " Days " +minValue);
							editor.commit();
						} else if (numDays == 1 && numHours == 1) {
							Spanned texts = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Day "
											+ numHours
											+ " Hour "
											+minValue
											+ "</font> before followups are due");
							textview_notify_follow.setText(texts);
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									+ numDays
									+ " Day "
									+ numHours
									+ " Hour "
									+minValue);
							editor.commit();
							closeFollowUps();
						} else if (numDays == 1 && numHours != 1) {
							Spanned texts = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Day "
											+ numHours
											+ " Hours "
											+minValue
											+ "</font> before followups are due");
							textview_notify_follow.setText(texts);
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									+ numDays
									+ " Day "
									+ numHours
									+ " Hours "
									+minValue);
							editor.commit();
							closeFollowUps();
						} else if (numDays != 1 && numHours == 1) {
							Spanned texts = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Days "
											+ numHours
											+ " Hour "
											+minValue
											+ "</font> before followups are due");
							textview_notify_follow.setText(texts);
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									numDays
									+ " Days "
									+ numHours
									+ " Hour "
									+minValue);
							editor.commit();
							closeFollowUps();
						} else if (numDays == 31 && numHours == 24) {
							numDays = 1;
							Spanned texts = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Month "
											+ numDays
											+ " Day " +minValue
											+ "</font> before followups are due");
							textview_notify_follow.setText(texts);
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									+ numDays
									+ " Month "
									+ numDays
									+ " Day " +minValue);
							editor.commit();
							closeFollowUps();
						} else {
							Spanned texts = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Days "
											+ numHours
											+ " Hours "
											+minValue
											+ "</font> before followups are due");
							textview_notify_follow.setText(texts);
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									+ numDays
									+ " Days "
									+ numHours
									+ " Hours "
									+minValue);
							editor.commit();
							closeFollowUps();
						}
					} else if (numDays == 0 && numHours != 0) {
						Spanned textHour;
						if (numHours == 1) {
							textHour = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numHours
											+ " Hour"
											+minValue
											+ "</font> before followups are due");
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									+ numHours
									+ " Hour"
									+minValue);
							editor.commit();
						} else if (numHours == 24) {
							textHour = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ (numDays + 1)
											+ " Day " +minValue
											+ "</font> before followups are due");
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									(numDays + 1)
									+ " Day " +minValue);
						} else {
							textHour = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numHours
											+ " Hours"
											+ minValue
											+ "</font> before followups are due");
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									numHours
									+ " Hours"
									+ minValue);
						}

						textview_notify_follow.setText(textHour);
						closeFollowUps();
					} else if (numDays != 0 && numHours == 0) {
						Spanned textDay;
						if (numDays == 1) {
							textDay = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Day " +minValue
											+ "</font> before followups are due");
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									numDays
									+ " Day " +minValue);
							editor.commit();
						} else {
							textDay = Html
									.fromHtml("Notify me <font color=#74a7d4>"
											+ numDays
											+ " Days " + minValue
											+ "</font> before followups are due");
							editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
									numDays
									+ " Days " + minValue);
							editor.commit();
						}
						textview_notify_follow.setText(textDay);
						closeFollowUps();
						//add by Ramesh 08/08/2014
					}else if(numDays == 0 && numHours == 0 && numMin != 0){
						Spanned textDays;

						textDays = Html
								.fromHtml("Notify me <font color=#74a7d4>"
										+ minValue
										+ "</font> before followups are due");

						textview_notify_follow.setText(textDays);
						editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
								minValue);
						editor.commit();
						closeFollowUps();
					}
					else {
						layout_picker.setVisibility(View.VISIBLE);
						Toast.makeText(getApplicationContext(),
								Constants.SELECT_DATE_TIME, Toast.LENGTH_SHORT)
								.show();
					}

				}

			}

			private void closeFollowUps() {
				if (!checkBoxAddNotification.isChecked()) {
					checkBoxAddNotification.setChecked(true);
					switchFollow.setChecked(true);
					layout_picker.setVisibility(View.GONE);
					layoutAddNotify.setBackgroundColor(getResources().getColor(
							R.color.reminder_cell));
					editor.putBoolean(Constants.PREF_SWITCH_FOLLOW_CHECKED,
							true);
					editor.putBoolean(Constants.PREF_CHECKBOX_FOLLOW_CHECKED,
							true);
					editor.commit();
				}
				layoutAddNotify.setBackgroundColor(getResources().getColor(
						R.color.reminder_cell));

				notificationType = "FOLLOWUP";
				notifictionDays = String.valueOf(numDays);
				noficationHours = String.valueOf(numHours);
				notificationStatus = "ENABLE";
				enableandDisableNotificationAsyncTask = new EnableandDisableNotificationAsyncTask().execute();

			}

			private void CloseAppointments() {
				if (!checkBoxAddReminder.isChecked()) {
					checkBoxAddReminder.setChecked(true);
					switchAppointment.setChecked(true);
					layoutAddReminder.setBackgroundColor(getResources()
							.getColor(R.color.reminder_cell));
					layout_picker.setVisibility(View.GONE);
					editor.putBoolean(Constants.PREF_CHECKBOX_APPNT_CHECKED,
							true);
					editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
					editor.commit();

					notificationType = "APPOINTMENT";
					notifictionDays = String.valueOf(numDays);
					noficationHours = String.valueOf(numHours);
					notificationStatus = "ENABLE";
					enableandDisableNotificationAsyncTask = new EnableandDisableNotificationAsyncTask().execute();
				}
				layoutAddReminder.setBackgroundColor(getResources().getColor(
						R.color.reminder_cell));

			}

		});

		imageViewClosePicker = (ImageView) findViewById(R.id.imageView_close);
		imageViewClosePicker.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (numberPickerDays.getTag().equals(1)
						&& numberPickerDays.getTag().equals(1)) {
					if (checkBoxAddReminder.isChecked()) {
						layout_picker.setVisibility(View.GONE);
						layoutAddReminder.setBackgroundColor(getResources()
								.getColor(R.color.reminder_cell));
					} else {
						layout_picker.setVisibility(View.GONE);
						layoutAddReminder.setBackgroundColor(getResources()
								.getColor(R.color.reminder_cell));
						switchAppointment.setChecked(false);
						editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED,
								false);
						editor.commit();
					}
				}
				if (numberPickerDays.getTag().equals(0)
						&& numberPickerDays.getTag().equals(0)) {
					if (checkBoxAddNotification.isChecked()) {
						layout_picker.setVisibility(View.GONE);
						layoutAddNotify.setBackgroundColor(getResources()
								.getColor(R.color.reminder_cell));
					} else {
						layout_picker.setVisibility(View.GONE);
						layoutAddNotify.setBackgroundColor(getResources()
								.getColor(R.color.reminder_cell));
						switchFollow.setChecked(false);
						editor.putBoolean(Constants.PREF_SWITCH_FOLLOW_CHECKED,
								false);
						editor.commit();
					}
				}

			}
		});
		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Settings Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	class EnableandDisableNotificationAsyncTask extends AsyncTask<String, Void, Void> {

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
			pDialog = new ActivityIndicator(SettingsActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				ArrayList<JSONObject> nameValuePairs = new ArrayList<JSONObject>();
				jsonResultText = new JSONObject();
				for (int i = 0; i < 1; i++) {
					jsonRequestText = new JSONObject();
					try {
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_DELEARID,preferenceSettings.getString(Constants.KEY_LOGIN_DEALER_ID, "") );
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_EMPLOYEEID, preferenceSettings.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, ""));
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_NOTIFICATION_TYPE, notificationType);
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_NOTIFICATION_DAYS, notifictionDays);
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_NOTIFICATION_HOURS, noficationHours);
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_NOTIFICATION_MINUTE, String.valueOf(numMin));
						jsonRequestText.put(Constants.ENABLE_PUSH_NOTIFICATION_NOTIFICATION_STATUS, notificationStatus);
					} catch (JSONException e) {
						Log.e(Constants.KEY_ERROR, e.toString());
					}
					nameValuePairs.add(jsonRequestText);
				}

				String stringData = nameValuePairs.toString();
				jsonResultText = serviceHelper.jsonSendHTTPRequest(stringData,
						Constants.ENABLE_PUSH_NOTIFICATION_SETTINGS, Constants.REQUEST_TYPE_POST);
				Log.e(Constants.KEY_ENABLE_PUSH_NOTIFICATION_RESPONSE,
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

					if (jsonResultText.has(Constants.KEY_ENABLE_PUSH_NOTIFICATION_RESPONSE)) {

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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(SettingsActivity.this,
				MainActivity.class);
		startActivity(backIntent);
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
		if (enableandDisableNotificationAsyncTask != null) {
			enableandDisableNotificationAsyncTask.cancel(true);
			enableandDisableNotificationAsyncTask = null;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}


}
