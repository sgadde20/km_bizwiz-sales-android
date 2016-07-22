package com.webparadox.bizwizsales;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Signature;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CreditCardPaymentSignature extends Activity implements OnClickListener{
	Bitmap bitmap;
	LinearLayout mContent;
	RelativeLayout penLayout;
	Signature m_signature;
	ImageView imageViewBack, logoBtn, signatureImage;
	Typeface droidSans, droidSansBold;
	CheckBox CheckBoxTermsCts;
	View mView;
	Button btnclear,btnProceed;
	TextView txtBalance,txtEmail;
	SharedPreferences userData;
	ActivityIndicator pDialog;
	ServiceHelper serviceHelper;
	String customerId, employeeID, dealerID;
	String customerAddress, CustomerId, customerName;
	String dealerSecurityKey,dealerAccountNumber;
	String email,result = "";
	ProceedAsyncTask mProceedAsyTask;
	SavePaymentAsyncTask savePaymentAsyncTask;
	String strApptResultId="",strApptointmentId="",strPaymentMethodId="",pamount="",strCreditTypeId="",strCreditBatchNumber="";
	JSONObject jsonRequestText, jsonResultText;
	ArrayList<JSONObject> nameValuePairs;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.credit_card_signature);
		Intent getinIntent=getIntent();
		
		email=getinIntent.getStringExtra("ReceiptEmail");
		pamount=getinIntent.getStringExtra("Payment");
		strCreditTypeId=getinIntent.getStringExtra("CreditTypeId");
		strCreditBatchNumber=getinIntent.getStringExtra("CreditBatchNumber");
		
		
		serviceHelper = new ServiceHelper(CreditCardPaymentSignature.this);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		customerId = userData.getString(Constants.JSON_KEY_CUSTOMER_ID, "");
		CustomerId = userData.getString("CustomerId",
				Constants.EMPTY_STRING);
		customerName = userData.getString("Name", Constants.EMPTY_STRING);
		customerAddress = userData.getString("Address",
				Constants.EMPTY_STRING);
		
		
		mContent = (LinearLayout) findViewById(R.id.sig_layout);
		penLayout = (RelativeLayout) findViewById(R.id.pen_layout);
		m_signature = new Signature(this, null);
		mContent.addView(m_signature, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mContent.setVisibility(View.GONE);
		penLayout.setVisibility(View.VISIBLE);
		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		imageViewBack.setOnClickListener(this);
		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(this);
		mView = mContent;
		signatureImage = (ImageView) findViewById(R.id.signature_image);
		penLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				penLayout.setVisibility(View.GONE);
				mContent.setVisibility(View.VISIBLE);


				return false;
			}
		});
		btnclear=(Button) findViewById(R.id.buttonClear);
		btnProceed=(Button) findViewById(R.id.buttonProceed);
		CheckBoxTermsCts=(CheckBox) findViewById(R.id.terms_checkbox);
		btnProceed.setOnClickListener(this);
		btnclear.setOnClickListener(this);
		
		txtBalance=(TextView) findViewById(R.id.textViewAmount);
		txtEmail=(TextView) findViewById(R.id.textViewEmail);
		txtEmail.setText("Receipt Email : "+email);
		txtBalance.setText("Amount : "+pamount);
		strApptResultId=Singleton.getInstance().strApptResultId;
		strApptointmentId=Singleton.getInstance().strAppointmentId;
		strPaymentMethodId = Singleton.getInstance().strPaymentMethodId;
	
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_icon:
			gotoHomeActivity();
			break;
		case R.id.image_back_icon:
			Intent intentHome=new Intent(CreditCardPaymentSignature.this,MainActivity.class);
			startActivity(intentHome);
			finish();
			break;
		case R.id.buttonClear:
			m_signature.clear();
			penLayout.setVisibility(View.VISIBLE);
			mContent.setVisibility(View.GONE);

			break;
		case R.id.buttonProceed:
			
			if (!m_signature.isNull()) {
				if (CheckBoxTermsCts.isChecked()) {
					mView.setDrawingCacheEnabled(true);
					m_signature.save(mView);
					mProceedAsyTask = new ProceedAsyncTask();
					mProceedAsyTask.execute();
					

				} else {
					Toast.makeText(getApplicationContext(),
							"Please accept the terms & conditions.",
							Toast.LENGTH_SHORT).show();

				}
			} else {
				Toast.makeText(getApplicationContext(), "Please register your signature.",
						Toast.LENGTH_SHORT).show();

			}
			break;


		}

	}
	public class ProceedAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(CreditCardPaymentSignature.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			if (Constants.isNetworkConnected(getApplicationContext())) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"dd:MMMM:yyyy HH:mm:ss ");
					final String currentDateandTime = sdf.format(new Date());

					String dec = "Proposal Signature Signoff_"
							+ currentDateandTime;
					Log.d("DEC", dec);
					
					
					result = new com.webparadox.bizwizsales.helper.HttpConnection()
							.sendPhoto(
									Integer.parseInt(dealerID),
									Integer.parseInt(customerId),
									Integer.parseInt(employeeID),
									strApptResultId,
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
			dissmissDialog();
			Log.d("result", result.toString());
			if (result != null) {
				if (result.equalsIgnoreCase("1")) {
					savePayments();
				}
			} else {
				Toast.makeText(CreditCardPaymentSignature.this, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}
			
		}

		private void savePayments() {
			// TODO Auto-generated method stub
			nameValuePairs = new ArrayList<JSONObject>();
			jsonResultText = new JSONObject();
			jsonRequestText = new JSONObject();
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(c.getTime());
			for (int i = 0; i < 1; i++) {
			     try {
			      jsonRequestText.put(Constants.DEALERID, dealerID);
			      jsonRequestText.put(Constants.APPOINTMENTID,
			    		  strApptointmentId);
			      jsonRequestText.put(Constants.EMPOLYEEID, employeeID);
			      jsonRequestText.put(Constants.CUSTOMERID, customerId);
			      jsonRequestText.put(Constants.AMOUNT,
			        pamount.replace("$", "").replace(",", ""));
			      jsonRequestText.put(Constants.PAYMENTTYPEID, "2");
			      jsonRequestText.put(Constants.PAYMENTMETHODID,
			    		  strPaymentMethodId);
			      jsonRequestText.put(Constants.CREDITTYPEID, strCreditTypeId);
			      jsonRequestText.put(Constants.CREDITBATCHNUMBER, strCreditBatchNumber);
			      jsonRequestText.put(Constants.CHECKNUMBER, "");
			      jsonRequestText.put(Constants.DATE, date);

			      jsonRequestText.put(Constants.APPOINTMENT_RESULT_ID,
			    		  strApptResultId);
			      jsonRequestText
			      .put(Constants.FINANCING_COMPANY_ID, "0");
			      jsonRequestText.put(
			        Constants.FINANCING_APPROVAL_NUMBER, "");

			     } catch (JSONException e) {
			      Log.e(Constants.KEY_ERROR, e.toString());
			     }
			     nameValuePairs.add(jsonRequestText);
			    }

			    savePaymentAsyncTask = new SavePaymentAsyncTask();
			    savePaymentAsyncTask.execute();
			
		}

	}
	public class SavePaymentAsyncTask extends AsyncTask<String, Void, Void> {

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
			pDialog = new ActivityIndicator(CreditCardPaymentSignature.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			try {
				String stringData = nameValuePairs.toString();
				jsonResultText = serviceHelper.jsonSendHTTPRequest(stringData,
						Constants.SAVE_PAYMENT_INFORMATION,
						Constants.REQUEST_TYPE_POST);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			 dissmissDialog();
			try {
				if(jsonResultText != null){

					JSONArray jsonArray = jsonResultText.getJSONArray("SP");
					JSONObject resultObj = jsonArray.getJSONObject(0);
					String status = resultObj.getString("Status");
					if (status.equalsIgnoreCase("SUCCESS")) {
						Toast.makeText(CreditCardPaymentSignature.this,
								"Transaction has been successfully posted.",
								Toast.LENGTH_SHORT).show();
						Constants.isSavedPayment = true;
						Intent cusAppo = new Intent(CreditCardPaymentSignature.this,
								CustomerAppointmentsActivity.class);
						cusAppo.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(cusAppo);
						finish();
					} else {
						Toast.makeText(CreditCardPaymentSignature.this, Constants.TOAST_CONNECTION_ERROR,
								Constants.TOASTMSG_TIME).show();
					}
				
				}
			} catch (Exception e) {
				Toast.makeText(CreditCardPaymentSignature.this, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}
		}
	}

	public void dissmissDialog() {
		if (pDialog != null) {
			pDialog.dismiss();
			pDialog = null;
		}

	}
	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onDestroy();
		if (pDialog != null) {
			if (pDialog.isShowing()) {
				pDialog.cancel();
			}
			pDialog = null;
		}
		if (mProceedAsyTask != null) {
			mProceedAsyTask.cancel(true);
			mProceedAsyTask = null;
		}
		if (savePaymentAsyncTask != null) {
			savePaymentAsyncTask.cancel(true);
			savePaymentAsyncTask = null;
		}
	}
	@Override
	public void onPause()
	{	
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		super.onPause();
	}
}
