package com.webparadox.bizwizsales;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.libraries.Constants;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCM Tutorial::Service";
	SharedPreferences userData;
	String dealerID = "";
	String employeeID = "";

	// Use your PROJECT ID from Google API into SENDER_ID
	public static final String SENDER_ID = "842449934288";
	private static int FM_NOTIFICATION_ID = 1;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		Log.i(TAG, "onRegistered: registrationId=" + registrationId);
		Singleton.getInstance().setApiKeyValue(registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {

		Log.i(TAG, "onUnregistered: registrationId=" + registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent data) {
		String message;
		// Message from PHP server
		message = data.getStringExtra("message");
		showNotification(context, message);
		// Open a new activity called GCMMessageView
		/*
		 * Intent intent = new Intent(this, GCMMessageView.class); // Pass data
		 * to the new activity intent.putExtra("message", message); // Starts
		 * the activity on notification click PendingIntent pIntent =
		 * PendingIntent.getActivity(this, 0, intent,
		 * PendingIntent.FLAG_UPDATE_CURRENT); // Create the notification with a
		 * notification builder Notification notification = new
		 * Notification.Builder(this) .setSmallIcon(R.drawable.ic_launcher)
		 * .setWhen(System.currentTimeMillis())
		 * .setContentTitle("Android GCM Tutorial")
		 * .setContentText(message).setContentIntent(pIntent)
		 * .getNotification(); // Remove the notification on click
		 * notification.flags |= Notification.FLAG_AUTO_CANCEL;
		 * 
		 * NotificationManager manager = (NotificationManager)
		 * getSystemService(NOTIFICATION_SERVICE);
		 * manager.notify(R.string.app_name, notification);
		 * 
		 * { // Wake Android Device when notification received PowerManager pm =
		 * (PowerManager) context .getSystemService(Context.POWER_SERVICE);
		 * final PowerManager.WakeLock mWakelock = pm.newWakeLock(
		 * PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
		 * "GCM_PUSH"); mWakelock.acquire();
		 * 
		 * // Timer before putting Android Device to sleep mode. Timer timer =
		 * new Timer(); TimerTask task = new TimerTask() { public void run() {
		 * mWakelock.release(); } }; timer.schedule(task, 5000); }
		 */
	}

	@Override
	protected void onError(Context arg0, String errorId) {

		Log.e(TAG, "onError: errorId=" + errorId);
	}

	private void showNotification(Context context, String message) {

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String notificationMessage = "";
		String customerId = "";
		String customerName = "";
		String customerAddress = "";
		String data = message;
		ArrayList<String> customerDetailsArrayList = new ArrayList<String>();
		
		if(data != null){
			String[] items = data.split(";");
		    for (String item : items)
		    {
		        System.out.println("item = " + item);
		        customerDetailsArrayList.add(item);
		    }
		    if(customerDetailsArrayList.size()>0){
		    	notificationMessage = customerDetailsArrayList.get(0);
		    	if(customerDetailsArrayList.size()>1){
		    		customerId = customerDetailsArrayList.get(1).trim();
		    		if(customerDetailsArrayList.size()>2){
		    			customerName = customerDetailsArrayList.get(2).trim();
		    			if(customerDetailsArrayList.size()>3){
		    				customerAddress = customerDetailsArrayList.get(3).trim();
			    		}
		    		}
		    	}
		    }
		}else{
			notificationMessage = "";
		}

		// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		// Uri.parse("android.resource://" + getPackageName() + "/raw/beep")
		
		Intent notificationIntent;
		NotificationCompat.Builder mBuilder;
		
		if(customerId != null){
			if(customerId.length()>0){
				userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
				dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
				employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
				if(dealerID != null && employeeID != null && dealerID.length()>0 && employeeID.length()>0){
					mBuilder = new NotificationCompat.Builder(
							context)
							.setSmallIcon(R.drawable.icon)
							.setContentTitle("BizWiz Notification")
							.setStyle(
									new NotificationCompat.BigTextStyle().bigText(notificationMessage))
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
							.setContentText(notificationMessage).setAutoCancel(true);
					
					notificationIntent = new Intent(context, CustomerDetailsActivity.class);
					Bundle bundle = new Bundle();
					
					if((customerId != null) && (customerId.length()>0)){
						bundle.putString("CustomerId",customerId);
					}else{
						bundle.putString("CustomerId","");
					}
					if((customerName != null) && (customerName.length()>0)){
						bundle.putString("CustomerFullName",customerName);
					}else{
						bundle.putString("CustomerFullName","");
					}
					if((customerAddress != null) && (customerAddress.length()>0)){
						bundle.putString("CustomerAddress",customerAddress);
					}else{
						bundle.putString("CustomerAddress","");
					}
					notificationIntent.putExtras(bundle);
				}else{
					mBuilder = new NotificationCompat.Builder(
							context)
							.setSmallIcon(R.drawable.icon)
							.setContentTitle("BizWiz Notification")
							.setStyle(
									new NotificationCompat.BigTextStyle().bigText(notificationMessage))
							.setSound(
									RingtoneManager
											.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
							.setContentText(notificationMessage).setAutoCancel(true);
					notificationIntent = new Intent(context, LoginActivity.class);
				}
				
				
			}else{
				mBuilder = new NotificationCompat.Builder(
						context)
						.setSmallIcon(R.drawable.icon)
						.setContentTitle("BizWiz Notification")
						.setStyle(
								new NotificationCompat.BigTextStyle().bigText(message))
						.setSound(
								RingtoneManager
										.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
						.setContentText(message).setAutoCancel(true);
				notificationIntent = new Intent(context, LoginActivity.class);
			}
		}else{
			mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.icon)
					.setContentTitle("BizWiz Notification")
					.setStyle(
							new NotificationCompat.BigTextStyle().bigText(message))
					.setSound(
							RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
					.setContentText(message).setAutoCancel(true);
			notificationIntent = new Intent(context, LoginActivity.class);
		}
		
		
		PendingIntent contentIntent1 = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(contentIntent1);
		mNotificationManager.notify(FM_NOTIFICATION_ID, mBuilder.build());
	}
}