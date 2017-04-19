package com.webparadox.bizwizsales.asynctasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.webparadox.bizwizsales.LoginActivity;
import com.webparadox.bizwizsales.MainActivity;
import com.webparadox.bizwizsales.R;
import com.webparadox.bizwizsales.adapter.PhonenumberListAdapter;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.dialogs.AddPhonenumberDialog;
import com.webparadox.bizwizsales.dialogs.PhonenumbersDialog.getPhonetypeTask;
import com.webparadox.bizwizsales.helper.BizWizUpdateReciver;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CustomerAttachmentModel;
import com.webparadox.bizwizsales.models.DispoModel;
import com.webparadox.bizwizsales.models.SubDispoModel;
import com.webparadox.bizwizsales.models.phoneModel;

public class CheckUserAccessAsynctask extends AsyncTask<Void, Void, Void> {
	ActivityIndicator dialog;
	Context context;
	String dealerId;
	String employeeId;
	ServiceHelper helper;
	JSONObject responsJson;

	public CheckUserAccessAsynctask(Context context, String dealerId,
			String empId) {
		this.dealerId = dealerId;
		this.context = context;
		this.employeeId = empId;
		helper = new ServiceHelper(context);
	}

	@Override
	protected void onPreExecute() {
		try {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				dialog = null;
			}
			dialog = new ActivityIndicator(context);
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		responsJson = helper.jsonSendHTTPRequest("",
				Constants.CHECK_USER_ACCESS_URL + "?DealerId=" + dealerId
						+ "&EmployeeId=" + employeeId + "&AppId="
						+ Constants.APPID, Constants.REQUEST_TYPE_GET);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		if (dialog != null & dialog.isShowing()) {
			dialog.dismiss();
			JSONArray responseArray;
			JSONObject responseObj;
			if (responsJson != null) {
				try {
					responseArray = responsJson.getJSONArray("CUA");
					responseObj = responseArray.getJSONObject(0);
					String status = responseObj.get("Status").toString(); 
					if (!status.equalsIgnoreCase("success")) {
						Log.i("CUA", status);
						showCUAdialog(status);
						
						
					}
					

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		super.onPostExecute(result);
	}

	public void showCUAdialog(String content) {

		Typeface droidSans = Typeface.createFromAsset(context.getAssets(),
				"DroidSans.ttf");

		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		dialog.getWindow().setAttributes(WMLP);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setContentView(R.layout.check_user_access_dialog);

		TextView txtStatus = (TextView) dialog
				.findViewById(R.id.txt_cua_status);
		txtStatus.setTypeface(droidSans);
		Button btnLogout = (Button) dialog.findViewById(R.id.btn_cua_logout);

		txtStatus.setText(content);
		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/**
				 * Canceling the Alarm manager when logout is clicked
				 */
				AlarmManager alarmreminderManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
				Intent intentReminder = new Intent(context
						.getApplicationContext(), BizWizUpdateReciver.class);
				PendingIntent pendingReminderIntent = PendingIntent
						.getBroadcast(context, 0, intentReminder,
								PendingIntent.FLAG_CANCEL_CURRENT);
				alarmreminderManager.cancel(pendingReminderIntent);
				SharedPreferences userData = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
				SharedPreferences.Editor editor = userData.edit();
				editor.putString(Constants.KEY_LOGIN_STATUS, "False");
				editor.commit();
				Intent loginIntent = new Intent(context,
						LoginActivity.class);
				loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				((Activity) context).startActivity(loginIntent);
				((Activity) context).finish();
				dialog.dismiss();
			}
		});

		dialog.show();

	}

}
