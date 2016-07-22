package com.webparadox.bizwizsales;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.BizWizUpdateReciver;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
public class LoginActivity extends Activity {

	Button submit_Btn;
	Context context;
	EditText username, password;
	String usernameStr, passwordStr;
	JSONObject jsonRequestText, jsonResultText;
	ServiceHelper serviceHelper;	
	private ActivityIndicator pDialog;

	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String dealerID = "";
	String employeeID = "", apiKey = "";

	Typeface droidSans, droidSansBold;
	AsyncTask<String, Void, Void> loginAsyncTask;
	AsyncTask<URL, Void, Bitmap> getBitMapImageAsyncTask;
	AsyncTask<String, Void, Void> sendPushApiKeyAsyncTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// This is to restrict landscape for phone

		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		if (Build.VERSION.SDK_INT < 11) {
			getActionBar().hide();
		}
		Constants.initBugSense(LoginActivity.this);

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		GCMRegistrar.register(LoginActivity.this, GCMIntentService.SENDER_ID);

		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");

		setContentView(R.layout.login_activity);

		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		context = this;
		serviceHelper = new ServiceHelper(context);

		submit_Btn = (Button) findViewById(R.id.submit);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		username.setTypeface(droidSans);
		password.setTypeface(droidSans);
		Utilities.isConnectingToInternet(LoginActivity.this);
		submit_Btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginSubmitButtonOnclickAction();
			}
		});

		if (userData.getString(Constants.KEY_LOGIN_STATUS, "").equals("True")) {
			Intent HomePage = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(HomePage);
			finish();
		}
	}

	void loginSubmitButtonOnclickAction(){
		usernameStr = username.getText().toString();
		passwordStr = password.getText().toString();
		if (usernameStr.length() == 0) {
			Toast.makeText(context,
					getResources().getString(R.string.email),
					Constants.TOASTMSG_TIME).show();
			username.setText(Constants.EMPTY_STRING);
			username.setFocusable(true);
		} else if (passwordStr.equals(Constants.EMPTY_STRING)) {
			Toast.makeText(context,
					getResources().getString(R.string.password),
					Constants.TOASTMSG_TIME).show();
			password.requestFocus();
		} else {
			if (Utilities.isConnectingToInternet(LoginActivity.this) == false) {

				if (userData.contains(Constants.KEY_LOGIN_EMAIL)&&userData.contains(Constants.KEY_PASSWORD)) {
					if(!userData.getString(Constants.KEY_LOGIN_EMAIL, "").equalsIgnoreCase("") && !userData.getString(Constants.KEY_PASSWORD,"").equalsIgnoreCase("")){
					if (usernameStr.equals(userData.getString(Constants.KEY_LOGIN_EMAIL, ""))&&passwordStr.equals(userData.getString(Constants.KEY_PASSWORD,""))) {
						editor.putString(Constants.KEY_LOGIN_STATUS,"True");

						startAutoSync();

						Intent HomePage = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(HomePage);
						finish();
					}else {
						Toast.makeText(context,Constants.VALUE_INVALID_EMAIL_AND_PASSWORD,
								Constants.TOASTMSG_TIME).show();
					}
					}else{
						Toast.makeText(context,
								getResources().getString(R.string.internet),
								Constants.TOASTMSG_TIME).show();
					}
				} else {
					Toast.makeText(context,
							getResources().getString(R.string.internet),
							Constants.TOASTMSG_TIME).show();
				}
			}else {
				loginAsyncTask = new LoginAsyncTask().execute();
			}
		}
	}

	class LoginAsyncTask extends AsyncTask<String, Void, Void> {

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
			pDialog = new ActivityIndicator(LoginActivity.this);
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
						jsonRequestText.putOpt(Constants.KEY_LOGIN_EMAIL,
								usernameStr);
						jsonRequestText.putOpt(Constants.KEY_PASSWORD,
								passwordStr);
					} catch (JSONException e) {
						Log.e(Constants.KEY_ERROR, e.toString());
					}
					nameValuePairs.add(jsonRequestText);
				}

				String stringData = nameValuePairs.toString();
				jsonResultText = serviceHelper.jsonSendHTTPRequest(stringData,
						Constants.URL_LOGIN, Constants.REQUEST_TYPE_POST);
				Log.e(Constants.KEY_LOGIN_RESPONSE_MESSAGE,
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

					if (jsonResultText.has(Constants.KEY_LOGIN_RESPONSE)) {

						JSONObject localJsonObject = jsonResultText
								.getJSONObject(Constants.KEY_LOGIN_RESPONSE);

						if (localJsonObject.has(Constants.KEY_LOGIN_RESPONSE_MESSAGE)) {
							if (localJsonObject.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE).toString()
									.equalsIgnoreCase(Constants.VALUE_SUCCESS) | localJsonObject
									.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE)
									.toString()
									.equalsIgnoreCase(
											Constants.KEY_UPDATE_DISPOS_TO_PROCEED)) {

								UpdateApptNotification(localJsonObject.getString(
										Constants.KEY_APPT_NOTIFICATION_DAYS)
										.toString(), localJsonObject.getString(
												Constants.KEY_APPT_NOTIFICATION_HOURS)
												.toString(), localJsonObject.getString(
														Constants.KEY_APPT_NOTIFICATION_MINUTES)
														.toString());
								UpdateFollowUpNotification(localJsonObject.getString(
										Constants.KEY_FOLLOW_UP_NOTIFICATION_DAYS)
										.toString(), localJsonObject.getString(
												Constants.KEY_FOLLOW_UP_NOTIFICATION_HOURS)
												.toString(), localJsonObject.getString(
														Constants.KEY_FOLLOW_UP_NOTIFICATION_MINUTES)
														.toString());
								checkForUpdates(localJsonObject.getString(
										Constants.KEY_LOGIN_DEALER_ID)
										.toString(),localJsonObject.getString(
												Constants.KEY_LOGIN_EMPLOYEE_ID)
												.toString());
								editor.putString(
										Constants.KEY_LOGIN_DEALER_ID,
										localJsonObject.getString(
												Constants.KEY_LOGIN_DEALER_ID)
												.toString());
								editor.putString(
										Constants.KEY_LOGIN_EMPLOYEE_ID,
										localJsonObject.getString(
												Constants.KEY_LOGIN_EMPLOYEE_ID)
												.toString());
								editor.putString(
										Constants.KEY_EMPLOYEE_NAME,
										localJsonObject.getString(
												Constants.KEY_EMPLOYEE_NAME)
												.toString());

								editor.putString(
										Constants.KEY_EMPLOYEE_PHOTO_URL,
										localJsonObject.getString(
												Constants.KEY_EMPLOYEE_PHOTO_URL)
												.toString());

								editor.putString(
										Constants.KEY_EMPLOYEE_CAN_CREATE_EVENTS,
										localJsonObject
										.getString(
												Constants.KEY_EMPLOYEE_CAN_CREATE_EVENTS)
												.toString());

								editor.putString(
										Constants.KEY_EMPLOYEE_CAN_EDIT_EVENTS,
										localJsonObject
										.getString(
												Constants.KEY_EMPLOYEE_CAN_EDIT_EVENTS)
												.toString());

								editor.putString(Constants.KEY_LOGIN_EMAIL,usernameStr.toString());
								editor.putString(Constants.KEY_PASSWORD,passwordStr.toString());
								editor.putString(Constants.KEY_LOGIN_STATUS,"True");
								if(localJsonObject
										.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE)
										.toString()
										.equalsIgnoreCase(
												Constants.KEY_UPDATE_DISPOS_TO_PROCEED)){
									editor.putString(Constants.KEY_UPDATE_DISPOS_TO_PROCEED,Constants.KEY_UPDATE_DISPOS_TO_PROCEED);
								}else{
									editor.putString(Constants.KEY_UPDATE_DISPOS_TO_PROCEED,"");
								}
								editor.commit();

								getBitMapImageAsyncTask = new GetBitMapImageAsyncTask(
										localJsonObject.getString(
												Constants.KEY_EMPLOYEE_PHOTO_URL)
												.toString()).execute();
							} else if (localJsonObject
									.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE)
									.toString()
									.equalsIgnoreCase(
											Constants.VALUE_INVALID_API_CREDENTIAL)) {
								Toast.makeText(
										context,
										localJsonObject
										.getString(
												Constants.KEY_LOGIN_RESPONSE_MESSAGE)
												.toString(),
												Constants.TOASTMSG_TIME).show();
							} else if (localJsonObject
									.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE)
									.toString()
									.equalsIgnoreCase(
											Constants.VALUE_INVALID_EMAIL_AND_PASSWORD)) {
								Toast.makeText(
										context,
										localJsonObject
										.getString(
												Constants.KEY_LOGIN_RESPONSE_MESSAGE)
												.toString(),
												Constants.TOASTMSG_TIME).show();
							}else {
								Toast.makeText(
										context,
										localJsonObject
										.getString(
												Constants.KEY_LOGIN_RESPONSE_MESSAGE)
												.toString(),
												Constants.TOASTMSG_TIME).show();
							}
						} else {
							Toast.makeText(context,
									Constants.TOAST_CONNECTION_ERROR,
									Constants.TOASTMSG_TIME).show();
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

	class SendNotificationAsyncTask extends AsyncTask<String, Void, Void> {

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
			pDialog = new ActivityIndicator(LoginActivity.this);
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
						jsonRequestText.put(
								Constants.SEND_PUSH_NOTIFICATION_DELEARID,
								userData.getString(
										Constants.KEY_LOGIN_DEALER_ID, ""));
						jsonRequestText.put(
								Constants.SEND_PUSH_NOTIFICATION_EMPLOYEEID,
								userData.getString(
										Constants.KEY_LOGIN_EMPLOYEE_ID, ""));
						jsonRequestText.put(
								Constants.SEND_PUSH_NOTIFICATION_DEVICEID,
								Singleton.getInstance().getApiKeyValue());
						jsonRequestText.put(
								Constants.SEND_PUSH_NOTIFICATION_DEVICETYPE,
								"ANDROID");
					} catch (JSONException e) {
						Log.e(Constants.KEY_ERROR, e.toString());
					}
					nameValuePairs.add(jsonRequestText);
				}

				String stringData = nameValuePairs.toString();
				jsonResultText = serviceHelper.jsonSendHTTPRequest(stringData,
						Constants.SEND_PUSH_NOTIFICATION_URL,
						Constants.REQUEST_TYPE_POST);
				Log.e(Constants.KEY_LOGIN_RESPONSE_MESSAGE,
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

					if (jsonResultText
							.has(Constants.KEY_PUSH_NOTIFICATION_RESPONSE)) {
						startAutoSync();
						Intent HomePage = new Intent(LoginActivity.this,
								MainActivity.class);
						startActivity(HomePage);
						finish();
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

	private class GetBitMapImageAsyncTask extends AsyncTask<URL, Void, Bitmap> {
		String mUrl;
		Bitmap myBitmap;

		public GetBitMapImageAsyncTask(String url) {
			mUrl = url;
		}

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
			pDialog = new ActivityIndicator(LoginActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(URL... urls) {
			try {
				HttpGet httpRequest = new HttpGet(URI.create(mUrl));
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) httpclient
						.execute(httpRequest);
				HttpEntity entity = response.getEntity();
				BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(
						entity);

				// decode image size
				BitmapFactory.Options o = new BitmapFactory.Options();
				o.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(bufHttpEntity.getContent(), null, o);

				// Find the correct scale value. It should be the power of 2.
				final int REQUIRED_SIZE = 70;
				int width_tmp = o.outWidth, height_tmp = o.outHeight;
				int scale = 1;
				while (true) {
					if (width_tmp / 2 < REQUIRED_SIZE
							|| height_tmp / 2 < REQUIRED_SIZE)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale *= 2;
				}

				// decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				myBitmap = BitmapFactory.decodeStream(
						bufHttpEntity.getContent(), null, o2);

				// myBitmap = BitmapFactory.decodeStream(bufHttpEntity
				// .getContent());
				httpRequest.abort();

			} catch (Exception e) {
				// TODO: handle exception
			}
			return myBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				Singleton.getInstance().setOwnerPhoto(result);
				// encodeTobase64(result);
				editor.putString("Iamge_Employee", encodeTobase64(result));
				editor.commit();
			}
			apiKey = Singleton.getInstance().getApiKeyValue();
			if (!apiKey.isEmpty()) {
				sendPushApiKeyAsyncTask = new SendNotificationAsyncTask()
				.execute();
			} else {
				startAutoSync();
				Intent HomePage = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(HomePage);
				finish();
			}

		}
	}

	public static String encodeTobase64(Bitmap image) {
		// Bitmap immage = image;
		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// immage.compress(Bitmap.CompressFormat.PNG, 50, baos);
		// byte[] b = baos.toByteArray();
		// String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		// return imageEncoded;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = null;
		try {
			System.gc();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			b = baos.toByteArray();
			temp = Base64.encodeToString(b, Base64.DEFAULT);
			Log.e("EWN", "Out of memory error catched");
		}
		return temp;

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//username.setText(Constants.EMPTY_STRING);
		//password.setText(Constants.EMPTY_STRING);
		System.gc();
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
		if (loginAsyncTask != null) {
			loginAsyncTask.cancel(true);
			loginAsyncTask = null;
		}
		if (getBitMapImageAsyncTask != null) {
			getBitMapImageAsyncTask.cancel(true);
			getBitMapImageAsyncTask = null;
		}
		if (sendPushApiKeyAsyncTask != null) {
			sendPushApiKeyAsyncTask.cancel(true);
			sendPushApiKeyAsyncTask = null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		LoginActivity.this.finish();
	}
	public void UpdateApptNotification(String strDays,String strHours,String strMinutes){


		int numDays = Integer.parseInt(strDays);
		int numHours = Integer.parseInt(strHours);
		int numMin = Integer.parseInt(strMinutes);
		String minValue;
		if(numMin == 0){
			minValue =" ";
		}else if(numMin == 1){
			minValue = ""+numMin+" Minute ";
		}else{
			minValue = ""+numMin+" Minutes ";
		}
		if (numDays != 0 && numHours != 0) {
			if (numHours == 24 && numDays != 31) {
				numDays += 1;

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						numDays + " Days " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();

			} else if (numDays == 1 && numHours == 1) {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						numDays + " Day " + numHours
						+ " Hour " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();

			} else if (numDays == 1 && numHours != 1) {


				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ numDays + " Day " + numHours
						+ " Hours " + minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();

			} else if (numDays != 1 && numHours == 1) {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ numDays + " Days " + numHours
						+ " Hour " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();

			} else if (numDays == 31 && numHours == 24) {
				numDays = 1;

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ numDays + " Month " + numDays
						+ " Day " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();

			} else {
				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ numDays + " Days " + numHours
						+ " Hours " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();
			}
		} else if (numDays == 0 && numHours != 0) {

			if (numHours == 1) {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ numHours + " Hour " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();
			} else if (numHours == 24) {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ (numDays + 1) + " Day " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();
			}
			else {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						+ numHours + " Hours" +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();
			}

		} else if (numDays != 0 && numHours == 0) {

			if (numDays == 1) {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						numDays + " Day " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();
			} else {

				editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
						numDays + " Days " +minValue);
				editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
				editor.commit();
			}

		}else if(numDays == 0 && numHours == 0 && numMin != 0){

			editor.putString(Constants.PREF_TEXT_APPNT_NOTIFY,
					minValue);
			editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, true);
			editor.commit();

		}
		else if(numDays == 0 && numHours == 0 && numMin == 0){

			editor.putBoolean(Constants.PREF_SWITCH_APPNT_CHECKED, false);
			editor.commit();

		}
	}
	public void UpdateFollowUpNotification(String strDays,String strHours,String strMinutes){


		int numDays = Integer.parseInt(strDays);
		int numHours = Integer.parseInt(strHours);
		int numMin = Integer.parseInt(strMinutes);
		String minValue;
		if(numMin == 0){
			minValue =" ";
		}else if(numMin == 1){
			minValue = ""+numMin+" Minute ";
		}else{
			minValue = ""+numMin+" Minutes ";
		}

		if (numDays != 0 && numHours != 0) {
			if (numHours == 24 && numDays != 31) {
				numDays += 1;

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						+ numDays
						+ " Days " +minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();
			} else if (numDays == 1 && numHours == 1) {


				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						+ numDays
						+ " Day "
						+ numHours
						+ " Hour "
						+minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();

			} else if (numDays == 1 && numHours != 1) {


				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						+ numDays
						+ " Day "
						+ numHours
						+ " Hours "
						+minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();

			} else if (numDays != 1 && numHours == 1) {


				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						numDays
						+ " Days "
						+ numHours
						+ " Hour "
						+minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();

			} else if (numDays == 31 && numHours == 24) {
				numDays = 1;


				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						+ numDays
						+ " Month "
						+ numDays
						+ " Day " +minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();

			} else {

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						+ numDays
						+ " Days "
						+ numHours
						+ " Hours "
						+minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();

			}
		} else if (numDays == 0 && numHours != 0) {

			if (numHours == 1) {

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						+ numHours
						+ " Hour"
						+minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();
			} else if (numHours == 24) {

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						(numDays + 1)
						+ " Day " +minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();
			} else {

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						numHours
						+ " Hours"
						+ minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();
			}


		} else if (numDays != 0 && numHours == 0) {

			if (numDays == 1) {

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						numDays
						+ " Day " +minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();
			} else {

				editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
						numDays
						+ " Days " + minValue);
				editor.putBoolean(
						Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
				editor.commit();
			}

		}else if(numDays == 0 && numHours == 0 && numMin != 0){


			editor.putString(Constants.PREF_TEXT_FOLLOW_NOTIFY,
					minValue);
			editor.putBoolean(
					Constants.PREF_SWITCH_FOLLOW_CHECKED, true);
			editor.commit();

		}else if(numDays == 0 && numHours == 0 && numMin == 0){
			editor.putBoolean(
					Constants.PREF_SWITCH_FOLLOW_CHECKED, false);
			editor.commit();
		}


	}
	private void checkForUpdates(String delearId, String employeeId) {
		// TODO Auto-generated method stub
		if (delearId.equals(userData.getString(Constants.KEY_LOGIN_DEALER_ID, "")) && 
				employeeId.equals(userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, ""))) {
			startAutoSync();
		}else {
			// Default value is 1 hour
			editor.putInt(Constants.PREF_AUTO_SYNC, 2);
			editor.commit();
		}

	}

	private void startAutoSync() {
		// TODO Auto-generated method stub
		long time = SystemClock.elapsedRealtime();
		int UPDATE_PERIOD;
		if (userData.contains(Constants.PREF_AUTO_SYNC)) {
			UPDATE_PERIOD=Constants.getSycTimesInMilliseconds(userData.getInt(Constants.PREF_AUTO_SYNC, 0));
		} else {
			UPDATE_PERIOD = Constants.getSycTimesInMilliseconds(2);
		}
		//int UPDATE_PERIOD =30000; for test time 
		if (UPDATE_PERIOD>0) {
			AlarmManager updatemgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent update_intent = new Intent(context, BizWizUpdateReciver.class);
			PendingIntent udpatepi = PendingIntent.getBroadcast(context, 0,
					update_intent, 0);
			updatemgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, time,
					UPDATE_PERIOD, udpatepi);
		}

	}


}
