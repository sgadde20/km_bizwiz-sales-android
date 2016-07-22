package com.webparadox.bizwizsales.helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.webparadox.bizwizsales.libraries.Constants;

@SuppressWarnings("deprecation")
public class SendLocationService extends Service {

	Context context = this;
	private final long mDelay = 0;
	private final long mPeriod = 900000; // send latitude & longitude for every
											// 30 min
	// private final long mPeriod = 1000 * 5 * 1; // send latitude & longitude
	// for every 5 sec
	public static Timer mTimer;
	public static LogTask mLogTask;
	Location location;
	private LocationManager locManager;
	private LocationListener locListener = new MyLocationListener();
	String latitude, longitude, dealerId, employeeId, status;
	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	SharedPreferences userData;
	ServiceHelper serviceHelper;
	JSONObject responseJson = new JSONObject();
	ArrayList<JSONObject> mRequestJson;
	JSONArray localJsonArray;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mTimer = new Timer();
		mLogTask = new LogTask();
		locManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		serviceHelper = new ServiceHelper(this.context);
		try {
			gps_enabled = locManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			network_enabled = locManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {

		}
		loadLocation();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		mTimer.cancel();
		mTimer = new Timer();
		mLogTask = new LogTask();
		mTimer.schedule(mLogTask, mDelay, mPeriod);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mTimer.cancel();
	}

	private class LogTask extends TimerTask {
		public void run() {
			if (isNetworkAvailable()) {
				DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
				f.setValidating(false);
				userData = getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, 0);
				dealerId = userData
						.getString(Constants.KEY_LOGIN_DEALER_ID, "");
				employeeId = userData.getString(
						Constants.KEY_LOGIN_EMPLOYEE_ID, "");

				if (location != null) {
					Log.d("LAT", location.getLatitude() + "");
					Log.d("LONG", location.getLongitude() + "");
					SaveLatLngAsyncTask saveTask = new SaveLatLngAsyncTask();
					saveTask.execute();
				}
			}
		}
	}

	public void loadLocation() {
		if (!gps_enabled || !network_enabled) {

		}
		if (gps_enabled) {
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, locListener);
			location = locManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (network_enabled) {
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					0, 0, locListener);
			location = locManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		if (location != null) {
			DecimalFormat df = new DecimalFormat("#.####");
			latitude = df.format(location.getLatitude());
			longitude = df.format(location.getLongitude());
		} else {
			latitude = "0.0000";
			longitude = "0.0000";
		}

	}

	class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location locations) {
			if (locations != null) {
				locManager.removeUpdates(locListener);
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public class SaveLatLngAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			localJsonArray = new JSONArray();
			mRequestJson = new ArrayList<JSONObject>();
			final JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
				reqObj_data.put(Constants.LATITUDE, location.getLatitude());
				reqObj_data.put(Constants.LONGITUDE, location.getLongitude());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRequestJson.add(reqObj_data);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.SAVE_LOCATION_URL,
					Constants.REQUEST_TYPE_POST);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (responseJson != null) {
				if (responseJson.has(Constants.SAVE_GPS_COORDINATES)) {

					try {
						localJsonArray = responseJson
								.getJSONArray(Constants.SAVE_GPS_COORDINATES);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject jobj = localJsonArray.getJSONObject(i);
							status = jobj
									.getString(Constants.KEY_ADDPROSPECT_STATUS);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (status.equals(Constants.SAVE_NOTES_SUCCESS)) {
						Log.d("STATUS", "SAVE_NOTES_SUCCESS");
					}

				}
			}
		}
	}
}
