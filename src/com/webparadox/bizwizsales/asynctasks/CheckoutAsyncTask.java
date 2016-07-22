package com.webparadox.bizwizsales.asynctasks;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.CustomerAppointmentsActivity;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CheckoutAsyncTask extends AsyncTask<Void, Integer, Void> {


	ActivityIndicator dialog;
	String dealerId,customerId,employeeId,apptResultId,result = "";
	Context context = null;

	public CheckoutAsyncTask(Context context,String dealId,String customerId,
			String employeeId, String apptResultId) {

		this.dealerId = dealId;
		this.customerId = customerId;
		this.employeeId = employeeId;
		this.apptResultId = apptResultId;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				dialog = null;
			} else {
				dialog = null;
			}
		}
		dialog = new ActivityIndicator(context);
		dialog.setLoadingText("Loading....");
		dialog.show();

	}

	@Override
	protected Void doInBackground(Void... params) {
		// TODO Auto-generated method stub

		if (Constants.isNetworkConnected(context)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(
						"dd:MMMM:yyyy HH:mm:ss ");
				final String currentDateandTime = sdf.format(new Date());

				String dec = "Proposal Signature Signoff_"
						+ currentDateandTime;
				Log.d("DEC", dec);


				result = new com.webparadox.bizwizsales.helper.HttpConnection()
				.sendPhoto(
						Integer.parseInt(dealerId),
						Integer.parseInt(customerId),
						Integer.parseInt(employeeId),
						apptResultId,
						dec,
						1,
						Constants.signPath.toString().replace(
								"file://", ""));

			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			// messageHandler.sendEmptyMessage(0);
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void Result) {
		super.onPostExecute(Result);

		Log.d("result", result.toString());
		if (result != null) {
			if (result.equalsIgnoreCase("1")) {
				// editor.putString("Amount",amount);
				// editor.commit();
				Constants.isCheckOut = true;
				Constants.isProposalList = false;
				Constants.isSelectProduct = false;
				Intent cusAppo = new Intent(context,
						CustomerAppointmentsActivity.class);
				cusAppo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(cusAppo);
				((Activity)context).finish();
			}
		} else {
			Toast.makeText(context, Constants.TOAST_CONNECTION_ERROR,
					Toast.LENGTH_SHORT).show();
		}
		dissmissDialog();
	}

	public void dissmissDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}

	}
}