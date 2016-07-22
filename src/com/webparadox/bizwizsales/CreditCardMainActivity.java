package com.webparadox.bizwizsales;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ePN.AndroidSDK.ePNAudioDevice;
import com.ePN.AndroidSDK.ePNBTDevice;
import com.ePN.AndroidSDK.ePNBTiMixPay;
import com.ePN.AndroidSDK.ePNHttpPost;
import com.ePN.AndroidSDK.ePNiMagPro;
import com.webparadox.bizwizsales.helper.CCTypesHandler;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CreditCardMainActivity extends Activity implements OnClickListener {
	ImageView imgBack, imgHome;
	TextView txtBalance;
	EditText editTextAmount, editTextName, editTextAddress, editTextCity,
	editTextState, editTextZipCode, editTextEmail;
	Button btnSwipe, btnOne, btnTwo, btnThree, btnFour, btnFive, btnsix,
	swipeProceed, btnSeven, btnEight, btnNine, btnZero, btnDoubleZero;
	LinearLayout layoutBackSpace;
	StringBuffer sBuffer = new StringBuffer("");
	String balanceDueSting = "";
	String amount = "";
	boolean isAmountFocused;
	SharedPreferences userData;
	AsyncTask<Void, Void, Void> editProspectAsynTask;
	ActivityIndicator pDialog;
	ServiceHelper serviceHelper;
	String customerId, appointmentResultId, employeeID, dealerID;
	String contact1, contact2, lastName, companyName, companyContact, email,
	physicalAddress, physicalZip, physicalCity, physicalState, typeId,
	subTypeId;
	String customerAddress, customerName;
	String dealerSecurityKey, dealerAccountNumber;
	ScrollView scrollview;
	Dialog mSwiperDialog;
	TextView timerCount, txtDialogSwiperStatus, txtDialogSwipeMessage;
	ePNBTiMixPay swipeController = null;
	ePNAudioDevice swipeController1 = null;
	boolean isPostTransCompleted = false;
	String status = "";
	String addressVerify = "";
	String cvv = "";
	String invoiceID = "";
	String transId = "";
	CountDownTimer swipeTimer;
	CCTypesHandler ccTypesHandler;
	int ccTypeID;
	AsyncTask<String, Void, String> paymentService;
	ImageView imgMasterCard,imgAmexcard,imgVisa,imgDiscover;
	String ccTypee = "", ccLastFour = "", ccExpireMonth = "", ccExpireYear = "";
	AddCustomerToCDMTask addCustomerToCDMTask;
	String strPhone = "";
	boolean isBTDeviceConnected = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.credit_card_main_activity);
		Intent getinIntent = getIntent();
		amount = getinIntent.getStringExtra("BalanceAmount");
		balanceDueSting = getinIntent.getStringExtra("Amount");
		dealerAccountNumber = getinIntent.getStringExtra("DelarAccountNumber");
		dealerSecurityKey = getinIntent.getStringExtra("DealerSecurityKey");

		// if (swipeController != null){
		// swipeController.onStop();
		// }
		swipeController1 = new ePNiMagPro(this, mHandler, dealerAccountNumber,
				dealerSecurityKey);
		swipeController = new ePNBTiMixPay(this, mHandler, dealerAccountNumber,
				dealerSecurityKey);
		swipeController.startBluetoothDiscovery();
		serviceHelper = new ServiceHelper(CreditCardMainActivity.this);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		customerId = userData.getString(Constants.JSON_KEY_CUSTOMER_ID, "");
		appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,
				"0");
		customerName = userData.getString("Name", Constants.EMPTY_STRING);
		customerAddress = userData.getString("Address", Constants.EMPTY_STRING);

		imgBack = (ImageView) findViewById(R.id.back_icon);
		imgHome = (ImageView) findViewById(R.id.image_back_icon);
		txtBalance = (TextView) findViewById(R.id.textViewBalance);
		txtBalance.setText("Balance : " + amount);
		editTextAmount = (EditText) findViewById(R.id.editTextAmount);
		editTextName = (EditText) findViewById(R.id.editTextName);
		editTextAddress = (EditText) findViewById(R.id.editTextAddress);
		editTextCity = (EditText) findViewById(R.id.editTextCity);
		editTextState = (EditText) findViewById(R.id.editTextState);
		editTextZipCode = (EditText) findViewById(R.id.editTextZipecode);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		btnOne = (Button) findViewById(R.id.button_one);
		btnTwo = (Button) findViewById(R.id.button_two);
		btnThree = (Button) findViewById(R.id.button_three);
		btnFour = (Button) findViewById(R.id.button_four);
		btnFive = (Button) findViewById(R.id.button_five);
		btnsix = (Button) findViewById(R.id.button_six);
		btnSeven = (Button) findViewById(R.id.button_seven);
		btnEight = (Button) findViewById(R.id.button_eight);
		btnNine = (Button) findViewById(R.id.button_nine);
		btnZero = (Button) findViewById(R.id.button_zero);
		btnDoubleZero = (Button) findViewById(R.id.button_double_zero);
		btnSwipe = (Button) findViewById(R.id.buttonSwipe);
		layoutBackSpace = (LinearLayout) findViewById(R.id.back_space_layout);
		scrollview = (ScrollView) findViewById(R.id.scrollView1);
		imgMasterCard = (ImageView) findViewById(R.id.imageViewMastercard);
		imgAmexcard= (ImageView) findViewById(R.id.imageViewAmex);
		imgVisa = (ImageView) findViewById(R.id.imageViewVisa);
		imgDiscover=(ImageView) findViewById(R.id.imageViewDiscover);
		
		imgBack.setOnClickListener(this);
		imgHome.setOnClickListener(this);
		btnOne.setOnClickListener(this);
		btnTwo.setOnClickListener(this);
		btnThree.setOnClickListener(this);
		btnFour.setOnClickListener(this);
		btnFive.setOnClickListener(this);
		btnsix.setOnClickListener(this);
		btnSeven.setOnClickListener(this);
		btnEight.setOnClickListener(this);
		btnNine.setOnClickListener(this);
		btnZero.setOnClickListener(this);
		btnDoubleZero.setOnClickListener(this);
		layoutBackSpace.setOnClickListener(this);
		btnSwipe.setOnClickListener(this);
		imgMasterCard.setOnClickListener(this);
		imgAmexcard.setOnClickListener(this);
		imgVisa.setOnClickListener(this);
		imgDiscover.setOnClickListener(this);

		editTextAmount.setHint("$0.00");
		editTextAmount.setText(balanceDueSting);
		editTextAmount.setTextIsSelectable(true);
		sBuffer.append(balanceDueSting);
		editTextAmount.setSelection(editTextAmount.getText().toString()
				.length());
		editTextAmount.setOnClickListener(this);

		editTextAmount
		.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				isAmountFocused = hasFocus;
				if (hasFocus) {
					if (getCurrentFocus() != null
							&& getCurrentFocus() instanceof EditText) {
						editTextAmount.setTextIsSelectable(true);
						editTextAmount.setSelection(editTextAmount
								.getText().toString().length());
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								editTextAmount.getWindowToken(), 0);

					}
				}
			}
		});

		editTextAmount.addTextChangedListener(new TextWatcher() {
			private boolean mFormatting; // this is a flag which prevents the

			// stack overflow.

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!mFormatting) {
					mFormatting = true;
					String editValue = editTextAmount.getText().toString();
					editValue = editValue.replace(".", "");
					editValue = editValue.replace("$", "");
					editValue = editValue.replaceAll("\\s+", "");
					editValue = editValue.replaceAll("[-.^:,()]", "");
					editValue = editValue.replaceAll("[^\\d.]", "");
					if (editValue.length() == 1) {
						editTextAmount.setText("$0.0" + editValue);
					} else if (editValue.length() == 2) {
						editTextAmount.setText("$0." + editValue);
					} else if (editValue.length() > 2) {
						int len = editValue.length();
						double amount = Double.parseDouble(editValue.substring(
								0, len - 2));
						NumberFormat formatter = new DecimalFormat(
								"###,###,###.##");
						System.out.println("The Decimal Value is: "
								+ formatter.format(amount));
						Log.d("The Decimal Value is: ",
								"$"
										+ formatter.format(amount)
										+ "."
										+ editValue.substring(len - 2,
												editValue.length()));

						editTextAmount.setText("$"
								+ formatter.format(amount)
								+ "."
								+ editValue.substring(len - 2,
										editValue.length()));
					}
					mFormatting = false;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		editProspectAsynTask = new EditProspectAsyncTask(
				CreditCardMainActivity.this).execute();

		mSwiperDialog = new Dialog(this);
		mSwiperDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSwiperDialog.setContentView(R.layout.swipperpopup);
		mSwiperDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		WindowManager.LayoutParams WMLP = mSwiperDialog.getWindow()
				.getAttributes();
		WMLP.gravity = Gravity.CENTER;
		mSwiperDialog.getWindow().setAttributes(WMLP);
		mSwiperDialog.setCancelable(false);
		mSwiperDialog.setCanceledOnTouchOutside(false);

		timerCount = (TextView) mSwiperDialog.findViewById(R.id.timer_count);
		txtDialogSwiperStatus = (TextView) mSwiperDialog
				.findViewById(R.id.swipper_status);
		txtDialogSwipeMessage = (TextView) mSwiperDialog
				.findViewById(R.id.swipper_msg);
		swipeProceed = (Button) mSwiperDialog.findViewById(R.id.swipe_proceed);
		swipeProceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSwiperDialog != null) {
					if (mSwiperDialog.isShowing()) {
						mSwiperDialog.dismiss();
						timerCount.setText("");
						swipeTimer.cancel();
						swipeTimer = null;
						swipeOrientation(1);
						if (swipeProceed.getText().toString().equalsIgnoreCase("next")) {
							transId = transId.replace(",", "");
							transId = transId.replace(" ", "");
							Intent intent=new Intent(CreditCardMainActivity.this,CreditCardPaymentSignature.class);
							intent.putExtra("ReceiptEmail",editTextEmail.getText().toString() );
							intent.putExtra("Payment",editTextAmount.getText().toString());
							intent.putExtra("CreditTypeId",""+ccTypeID);
							intent.putExtra("CreditBatchNumber",""+transId);
							startActivity(intent);
							finish(); 
						}
					}
				}
			}
		});

		ImageView deleteButton = (ImageView) mSwiperDialog
				.findViewById(R.id.close);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSwiperDialog != null) {
					if (mSwiperDialog.isShowing()) {
						mSwiperDialog.dismiss();
						swipeOrientation(1);
					}
				}
				timerCount.setText("");
				swipeTimer.cancel();
				swipeTimer = null;
			}
		});
	}

	enum PaymentServices {
		CCTypes
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.back_icon:
			gotoHomeActivity();
			break;
		case R.id.image_back_icon:
			Intent intentHome = new Intent(CreditCardMainActivity.this,
					MainActivity.class);
			startActivity(intentHome);
			finish();
			break;
		case R.id.imageViewMastercard:
			Intent intentManualEntry = new Intent(CreditCardMainActivity.this,
					CreditCardManualEntryActivity.class);
			intentManualEntry.putExtra("BalanceAmount", amount);
			intentManualEntry.putExtra("Amount", editTextAmount.getText()
					.toString());
			intentManualEntry.putExtra("DelarAccountNumber",
					dealerAccountNumber);
			intentManualEntry.putExtra("DealerSecurityKey", dealerSecurityKey);
			intentManualEntry.putExtra("ReceiptEmail", editTextEmail.getText()
					.toString());
			intentManualEntry.putExtra("ReceiptName", editTextName.getText()
					.toString());
			intentManualEntry.putExtra("ReceiptAddress", editTextAddress.getText()
					.toString());
			intentManualEntry.putExtra("ReceiptCity", editTextCity.getText()
					.toString());
			intentManualEntry.putExtra("ReceiptState", editTextState.getText()
					.toString());
			intentManualEntry.putExtra("ReceiptZip", editTextZipCode.getText()
					.toString());
			intentManualEntry.putExtra("CreditCardType", "MasterCard");
			startActivity(intentManualEntry);
			break;
		case R.id.imageViewAmex:
			Intent intentManualAmexEntry = new Intent(CreditCardMainActivity.this,
					CreditCardManualEntryActivity.class);
			intentManualAmexEntry.putExtra("BalanceAmount", amount);
			intentManualAmexEntry.putExtra("Amount", editTextAmount.getText()
					.toString());
			intentManualAmexEntry.putExtra("DelarAccountNumber",
					dealerAccountNumber);
			intentManualAmexEntry.putExtra("DealerSecurityKey", dealerSecurityKey);
			intentManualAmexEntry.putExtra("ReceiptEmail", editTextEmail.getText()
					.toString());
			intentManualAmexEntry.putExtra("ReceiptName", editTextName.getText()
					.toString());
			intentManualAmexEntry.putExtra("ReceiptAddress", editTextAddress.getText()
					.toString());
			intentManualAmexEntry.putExtra("ReceiptCity", editTextCity.getText()
					.toString());
			intentManualAmexEntry.putExtra("ReceiptState", editTextState.getText()
					.toString());
			intentManualAmexEntry.putExtra("ReceiptZip", editTextZipCode.getText()
					.toString());
			intentManualAmexEntry.putExtra("CreditCardType", "Amex");
			startActivity(intentManualAmexEntry);
			break;
		case R.id.imageViewVisa:
			Intent intentManualVisaEntry = new Intent(CreditCardMainActivity.this,
					CreditCardManualEntryActivity.class);

			intentManualVisaEntry.putExtra("BalanceAmount", amount);
			intentManualVisaEntry.putExtra("Amount", editTextAmount.getText()
					.toString());
			intentManualVisaEntry.putExtra("DelarAccountNumber",
					dealerAccountNumber);
			intentManualVisaEntry.putExtra("DealerSecurityKey", dealerSecurityKey);
			intentManualVisaEntry.putExtra("ReceiptEmail", editTextEmail.getText()
					.toString());
			intentManualVisaEntry.putExtra("ReceiptName", editTextName.getText()
					.toString());
			intentManualVisaEntry.putExtra("ReceiptAddress", editTextAddress.getText()
					.toString());
			intentManualVisaEntry.putExtra("ReceiptCity", editTextCity.getText()
					.toString());
			intentManualVisaEntry.putExtra("ReceiptState", editTextState.getText()
					.toString());
			intentManualVisaEntry.putExtra("ReceiptZip", editTextZipCode.getText()
					.toString());
			intentManualVisaEntry.putExtra("CreditCardType", "Visa");
			startActivity(intentManualVisaEntry);
			break;
		case R.id.imageViewDiscover:
			Intent intentManualDiscoverEntry = new Intent(CreditCardMainActivity.this,
					CreditCardManualEntryActivity.class);

			intentManualDiscoverEntry.putExtra("BalanceAmount", amount);
			intentManualDiscoverEntry.putExtra("Amount", editTextAmount.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("DelarAccountNumber",
					dealerAccountNumber);
			intentManualDiscoverEntry.putExtra("DealerSecurityKey", dealerSecurityKey);
			intentManualDiscoverEntry.putExtra("ReceiptEmail", editTextEmail.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("ReceiptName", editTextName.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("ReceiptAddress", editTextAddress.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("ReceiptCity", editTextCity.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("ReceiptState", editTextState.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("ReceiptZip", editTextZipCode.getText()
					.toString());
			intentManualDiscoverEntry.putExtra("CreditCardType", "Discover");
			startActivity(intentManualDiscoverEntry);
			break;
		case R.id.button_one:
			setValue("1");
			break;
		case R.id.button_two:
			setValue("2");
			break;
		case R.id.button_three:
			setValue("3");
			break;
		case R.id.button_four:
			setValue("4");
			break;
		case R.id.button_five:
			setValue("5");
			break;
		case R.id.button_six:
			setValue("6");
			break;
		case R.id.button_seven:
			setValue("7");
			break;
		case R.id.button_eight:
			setValue("8");
			break;
		case R.id.button_nine:
			setValue("9");
			break;
		case R.id.button_zero:
			setValue("0");
			break;
		case R.id.button_double_zero:
			setValue("00");
			break;
		case R.id.back_space_layout:
			Log.d("Is Back Pressed = ", "Yes");
			scrollview.fullScroll(ScrollView.FOCUS_UP);
			if (!isAmountFocused) {
				editTextAmount.requestFocus();
			}
			String editValue = editTextAmount.getText().toString();
			editValue = editValue.replace(".", "");
			editValue = editValue.replace("$", "");
			editValue = editValue.replaceAll("\\s+", "");
			editValue = editValue.replaceAll("[-.^:,()]", "");
			editValue = editValue.replaceAll("[^\\d.]", "");

			if (editValue.length() > 0) {
				int len = editValue.length();
				sBuffer = new StringBuffer(editValue.substring(0, len - 1));
				editTextAmount.setText(editValue.substring(0, len - 1));
				editTextAmount.setSelection(editTextAmount.getText().toString()
						.length());
			}
			break;
		case R.id.editTextAmount:
			editTextAmount.setSelection(editTextAmount.getText().toString()
					.length());
			editTextAmount.setSelection(editTextAmount.getText().toString()
					.length());
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTextAmount.getWindowToken(), 0);
			break;
		case R.id.buttonSwipe:

			String stramount = editTextAmount.getText().toString()
			.replace("$", "");
			stramount = stramount.replace(",", "");
			if (stramount.isEmpty() || stramount.equalsIgnoreCase("0.00")) {
				Toast.makeText(getApplicationContext(),
						"Please enter the amount.", Toast.LENGTH_LONG).show();
			} else if (timerCount.getText().toString().equalsIgnoreCase("")) {
				if (editTextEmail.getText().toString().length() > 0) {
					swipeController.setAmount(stramount);
					//swipeController.setProjectId("" + appointmentResultId);
					//We need to send Customer Id instead of appointment result id for Invoice Id
					swipeController.setInv("" + customerId);
					swipeController
					.setEmail(editTextEmail.getText().toString());
					swipeController.setFirstName(editTextName.getText()
							.toString());
					swipeController.setAddress(editTextAddress.getText()
							.toString());
					swipeController.setCity(editTextCity.getText().toString());
					swipeController
					.setState(editTextState.getText().toString());
					swipeController
					.setZip(editTextZipCode.getText().toString());
					
					//This is for Audio device
					swipeController1.setAmount(stramount);
					swipeController1.setInv("" + customerId);
					swipeController1
					.setEmail(editTextEmail.getText().toString());
					swipeController1.setFirstName(editTextName.getText()
							.toString());
					swipeController1.setAddress(editTextAddress.getText()
							.toString());
					swipeController1.setCity(editTextCity.getText().toString());
					swipeController1
					.setState(editTextState.getText().toString());
					swipeController1
					.setZip(editTextZipCode.getText().toString());

					// Singleton.getInstance().swipeController.getImagPro().setAmount(stramount);
					swipeProceed.setVisibility(View.INVISIBLE);
					swipeTimer = null;
					mSwiperDialog.show();
					swipeOrientation(0);
					isPostTransCompleted = false;
					txtDialogSwipeMessage.setText("");
					if(txtDialogSwiperStatus.getText().toString().equalsIgnoreCase("Connected")){
						txtDialogSwipeMessage.setText("ePN device is ready to use");
						txtDialogSwipeMessage.setTextColor(Color.GREEN);
					}else if(txtDialogSwiperStatus.getText().toString().equalsIgnoreCase("Disconnected")){
						txtDialogSwipeMessage.setText("Please connect the ePN device");
						txtDialogSwipeMessage.setTextColor(Color.RED);
					}

					startCountdown();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please enter your email.", Toast.LENGTH_LONG)
							.show();
				}

			}
			break;
		}

	}

	public void swipeOrientation(int orientationLock) {
		if (getResources().getBoolean(R.bool.is_tablet)) {
			if (orientationLock == 0) {
				int orientation = CreditCardMainActivity.this.getRequestedOrientation();

				int rotation = ((WindowManager) CreditCardMainActivity.this
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay().getRotation();
				Log.d("Val == ", ""+ rotation);
				switch (rotation) {
				case Surface.ROTATION_0:
					orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					break;
				case Surface.ROTATION_90:
					orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
					break;
				case Surface.ROTATION_180:
					orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
					break;
				default:
					orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					break;
				}

				CreditCardMainActivity.this.setRequestedOrientation(orientation);
			} else {
				CreditCardMainActivity.this
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		}
	}

	public void startCountdown() {
		swipeTimer = new CountDownTimer(60000, 1000) {

			public void onTick(long millisUntilFinished) {
				if (isPostTransCompleted) {
					timerCount.setText("");
				} else {
					timerCount.setText("" + millisUntilFinished / 1000);
				}
			}

			public void onFinish() {
				timerCount.setText("");
				if (!isPostTransCompleted) {
					if (mSwiperDialog != null) {
						if (mSwiperDialog.isShowing()) {
							mSwiperDialog.dismiss();
							swipeOrientation(1);
						}
					}
				}
			}
		}.start();
	}

	public void setValue(String keyValue) {
		scrollview.fullScroll(ScrollView.FOCUS_UP);
		if (!isAmountFocused) {
			editTextAmount.requestFocus();
		}
		Log.d("sBuffer Value1", sBuffer.toString());
		Log.d("sBuffer Value2", sBuffer.toString() + keyValue);

		String editValue = (sBuffer.toString() + keyValue);

		editValue = editValue.replace(".", "");
		editValue = editValue.replace("$", "");
		editValue = editValue.replaceAll("\\s+", "");
		editValue = editValue.replaceAll("[-.^:,()]", "");
		editValue = editValue.replaceAll("[^\\d.]", "");

		if (editValue.length() == 1) {
			editValue = "0.0" + editValue;
		} else if (editValue.length() == 2) {
			editValue = "0." + editValue;
		} else if (editValue.length() > 2) {
			editValue = editValue.substring(0, editValue.length() - 2)
					+ "."
					+ editValue.substring(editValue.length() - 2,
							editValue.length());
		}
		Log.d("sBuffer Value3", editValue);
		boolean resultt = checkBalanceExceedAmoount(editValue);

		if (resultt) {
			sBuffer.append(keyValue);
			editTextAmount.setText(sBuffer.toString());
			editTextAmount.setSelection(editTextAmount.getText().toString()
					.length());
		} else {
			Toast.makeText(this, "Cannot exceed the balance amount",
					Toast.LENGTH_SHORT).show();
		}

	}

	public boolean checkBalanceExceedAmoount(String value) {
		amount = amount.replace(",", "");
		amount = amount.replace("$", "");
		value = value.replace(",", "");
		value = value.replace("$", "");
		double balanceDoubleValue = 0.00, amountDoubleValue = 0.00;
		if (amount.length() > 0) {
			balanceDoubleValue = Double.parseDouble(amount);
		}
		if (value.length() > 0) {
			amountDoubleValue = Double.parseDouble(value);
		}

		if (balanceDoubleValue >= amountDoubleValue) {
			return true;
		} else {
			return false;
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
			pDialog = new ActivityIndicator(CreditCardMainActivity.this);
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
								if (phoneArr.length()>0) {
									JSONObject phoneObj=phoneArr.getJSONObject(0);
									strPhone=phoneObj.get(Constants.JSON_KEY_PHONE).toString();
								}
							}
							editTextName.setText(contact1 + " " + contact2 + " "
									+ lastName);
							editTextEmail.setText(email);
							editTextAddress.setText(physicalAddress);
							editTextZipCode.setText(physicalZip);
							editTextCity.setText(physicalCity);
							editTextState.setText(physicalState);
						}
					
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				paymentService = new PaymentService(
						PaymentServices.CCTypes.ordinal())
				.execute(Constants.CC_TYPES + dealerID);
			}
		}
	}

	/*
	 * This is the Handler that is passed when we initialized the ePNAudioDevice
	 * object. It will receive all messages that are returned
	 */
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ePNBTDevice.BLUETOOTH_STARTED_DISCOVERY:
				Log.i("Bluetooth", "Bluetooth discovery started");
				break;
			
			case ePNBTDevice.BLUETOOTH_FINISHED_DISCOVERY:
				Log.i("Bluetooth", "Bluetooth discovery Ended");
				break;
			
			case ePNBTDevice.BLUETOOTH_DEVICE_FOUND:
				Log.i("Bluetooth", "Bluetooth device found");
				if(!isBTDeviceConnected) {
					swipeController.connectToReader((BluetoothDevice)msg.obj);
				}
				break;
			
			case ePNAudioDevice.MESSAGE_SWIPER_CONNECTED:
				// Received when a audio swiper is connected
				Log.i("iMagPay", "Received AudioDevice Swiper Connected");
				deviceConnected();
				break;
				
			case ePNBTDevice.BLUETOOTH_SWIPER_CONNECTED: 
				// Received when a bluetooth swiper is connected
				Log.i("iMagPay", "Received BluetoothDevice Swiper Connected");
				isBTDeviceConnected = true;
				deviceConnected();
				break;

			case ePNBTDevice.BLUETOOTH_SWIPER_DISCONNECTED: // Received when a
				// swiper is
				// disconnected
				isBTDeviceConnected = false;
				deviceDisconnected();
				break;
				
			case ePNAudioDevice.MESSAGE_SWIPER_DISCONNECTED:
				// Received when a audio swiper is disconnected
				deviceDisconnected();
				break;

			case ePNBTDevice.BLUETOOTH_DID_RECEIVE_SWIPE: // Received when a
				// swiper has
				// successfully read
				// a card
				didReceiveSwipe("Swipe Successful");
				break;
				
			case ePNAudioDevice.MESSAGE_DID_RECEIVE_SWIPE:
				// Received when audio swiper successfully read a card
				didReceiveSwipe("Swipe Successful");
				break;

			case ePNAudioDevice.MESSAGE_DID_POST_XACT: // Received when a
				// transaction that was
				// posted receives a
				// response
				didPostTransaction((String) msg.obj);
				break;

			case ePNAudioDevice.MESSAGE_DID_RECEIVE_CARD_DATA: // Received when
				// a request for
				// the card data
				// has been made
				didReceiveCardData((String) msg.obj);
			}
		}
	};

	public void deviceConnected() {
		// TODO Auto-generated method stub
		Log.i("DeviceConnected", "deviceConnected");
		txtDialogSwiperStatus.setText("Connected");
		txtDialogSwipeMessage.setText("ePN device is ready to use");
		txtDialogSwipeMessage.setTextColor(Color.GREEN);
	}

	public void deviceDisconnected() {
		// TODO Auto-generated method stub
		Log.i("DeviceConnected", "deviceConnected");
		txtDialogSwiperStatus.setText("Disconnected");
		txtDialogSwipeMessage.setText("Please connect the ePN device");
		txtDialogSwipeMessage.setTextColor(Color.RED);
	}

	public void didReceiveSwipe(final String response) {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				Log.i("didReceiveSwipe", "" + response);
				// Toast.makeText(getApplicationContext(), response,
				// Toast.LENGTH_LONG).show();

				txtDialogSwipeMessage.setTextColor(Color.RED);
				if (response.equalsIgnoreCase("Swipe Successful")
						|| response
						.equalsIgnoreCase("Card readed successfully")) {
					txtDialogSwipeMessage
					.setText("Swipe successful, transaction initiated");
					txtDialogSwipeMessage.setTextColor(Color.GREEN);
					isPostTransCompleted = true;
					if (mSwiperDialog != null) {
						if (mSwiperDialog.isShowing()) {
							// Check which device is connected and call the method
							if(isBTDeviceConnected) {
								swipeController.getCardInfo();
							}
							else {
								swipeController1.getCardInfo();
							}
						}
					}
				}
			}
		});
	}

	@SuppressLint("DefaultLocale")
	public void didReceiveCardData(String response) {
		// TODO Auto-generated method stub
		Log.d("didReceiveCardData", "" + response);

		ArrayList<String> ccTypeArray = ccTypesHandler.getItemsTypeId();
		ccTypee = "";
		ccLastFour = "";
		ccExpireMonth = "";
		ccExpireYear = "";
		ArrayList<String> customerDetailsArrayList = new ArrayList<String>();

		if(response != null){
			String[] items = response.split(";");
			for (String item : items)
			{
				System.out.println("item = " + item);
				customerDetailsArrayList.add(item.trim());
			}
			if(customerDetailsArrayList.size()>0){
				ccTypee = customerDetailsArrayList.get(0);
				if(customerDetailsArrayList.size()>1){
					ccLastFour = customerDetailsArrayList.get(1).trim();
					if(customerDetailsArrayList.size()>2){
						ccExpireMonth = customerDetailsArrayList.get(2).trim();
						if(customerDetailsArrayList.size()>3){
							ccExpireYear = customerDetailsArrayList.get(3).trim();
						}
					}
				}
			}
		}else{
			ccTypee = "visa";
		}

		if(ccTypee != null){
			for (int k = 0; k < ccTypeArray.size(); k++) {
				if (ccTypee.toLowerCase().contains(
						ccTypeArray.get(k).toLowerCase())) {
					ccTypeID = Integer.parseInt(ccTypesHandler.getItemsListId()
							.get(k));
					Log.d("ccTypeID = ", "" + ccTypeID);
				}
			}
		}

		if (mSwiperDialog != null) {
			if (mSwiperDialog.isShowing()) {
				txtDialogSwipeMessage.setText("Transaction in progress");
				txtDialogSwipeMessage.setTextColor(Color.GREEN);
				Log.d("Inv class = ", "" + customerId);
				// Check which device is connected and call the method
				if(isBTDeviceConnected) {
					swipeController.postTransaction();
				}
				else {
					swipeController1.postTransaction();
				}
			}
		}
	}

	public void didPostTransaction(final String response) {
		// TODO Auto-generated method stub
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// Toast.makeText(getApplicationContext(), response,
				// Toast.LENGTH_LONG).show();

				Log.d("response = ", response);

				List<String> list = new ArrayList<String>(Arrays
						.asList(response.split(",")));

				for (int i = 0; i < list.size(); i++) {
					if (i == 0) {
						status = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']",
								"");
					} else if (i == 1) {
						addressVerify = list.get(i).replaceAll(
								"[|?*<\":>+\\[\\]/']", "");
						;
					} else if (i == 2) {
						cvv = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']", "");
						;
					} else if (i == 3) {
						invoiceID = list.get(i).replaceAll(
								"[|?*<\":>+\\[\\]/']", "");
						;
					} else if (i == 4) {
						transId = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']",
								"");
						;
					}
				}

				Log.d("status = ", status);
				Log.d("addressVerify = ", addressVerify);
				Log.d("cvv = ", cvv);
				Log.d("invoiceID = ", invoiceID);
				Log.d("transId = ", transId);

				if (status.substring(0, 1).equalsIgnoreCase("n")) {
					swipeProceed.setText("Close");
					swipeProceed.setVisibility(View.VISIBLE);
					txtDialogSwipeMessage.setText("Transaction declined");
					txtDialogSwipeMessage.setTextColor(Color.RED);
				} else if (status.substring(0, 1).equalsIgnoreCase("y")) {
					swipeProceed.setText("Next");
					swipeProceed.setVisibility(View.VISIBLE);
					txtDialogSwipeMessage.setText("Payment has been posted");
					txtDialogSwipeMessage.setTextColor(Color.GREEN);

					addCustomerToCDMTask = new AddCustomerToCDMTask();
					addCustomerToCDMTask.execute();

				} else {
					swipeProceed.setText("Ok");
					swipeProceed.setVisibility(View.VISIBLE);
					txtDialogSwipeMessage.setText("Processing failed");
					txtDialogSwipeMessage.setTextColor(Color.RED);
				}
			}
		});
	}

	private class AddCustomerToCDMTask extends AsyncTask<String,Void,String>
	{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(CreditCardMainActivity.this);
			pDialog.setLoadingText("");
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			transId = transId.replace(",", "");
			transId = transId.replace(" ", "");

			ePNHttpPost myPost = new ePNHttpPost("https://www.eprocessingnetwork.com/cgi-bin/tdbe/cdm.pl");
			myPost.addParam("ePNAccount", dealerAccountNumber);
			myPost.addParam("RestrictKey", dealerSecurityKey);
			myPost.addParam("Action", "AddCustomer");
			myPost.addParam("Description", editTextName.getText().toString() + " Transaction");
			myPost.addParam("Email", editTextEmail.getText().toString());
			myPost.addParam("Identifier", appointmentResultId);
			//			myPost.addParam("CustomerID", appointmentResultId);
			myPost.addParam("Phone", strPhone);

			myPost.addParam("Name", editTextName.getText().toString());
			myPost.addParam("Address1", editTextAddress.getText().toString());
			myPost.addParam("City", editTextCity.getText().toString());
			myPost.addParam("State", editTextState.getText().toString());
			myPost.addParam("Zip", editTextZipCode.getText().toString());

			//			myPost.addParam("CardType", ccTypee);
			//			myPost.addParam("LastFour", ccLastFour);
			//			myPost.addParam("ExpireMonth", ccExpireMonth);
			//			myPost.addParam("ExpireYear", ccExpireYear);
			//			myPost.addParam("AddressID", "");
			//			myPost.addParam("RecordType", "C");
			myPost.addParam("XactID", transId);
			myPost.addParam("RespondAs", "JSON");

			String result = myPost.post();
			if(result != null){
				Log.i("ePNAccount = ", ""+dealerAccountNumber + "RestrictKey = "+ dealerSecurityKey);
				Log.i("ACDM = ", "Results from Post Add Customer: 1 " + result);
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			if(result != null){
				Log.i("ACDM = ", "Results from Post Add Customer: 2 " + result);
				if (pDialog != null) {
					if (pDialog.isShowing()) {
						pDialog.cancel();
					}
					pDialog = null;
				}
			}
		}
	}

	class PaymentService extends AsyncTask<String, Void, String> {
		PaymentServices paymentservice;

		public PaymentService(int which) {
			// TODO Auto-generated constructor stub
			paymentservice = PaymentServices.values()[which];
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(CreditCardMainActivity.this);
			pDialog.setLoadingText("Loading...");
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String result = "";
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();

				System.out.println(params[0]);
				switch (paymentservice) {
				case CCTypes:
					ccTypesHandler = new CCTypesHandler();
					xr.setContentHandler(ccTypesHandler);
					break;
				}
				if (params[0].contains("https://")
						|| params[0].contains("http://")) {
					URL _url = new URL(params[0]);
					xr.parse(new InputSource(_url.openStream()));
				}
			} catch (ParserConfigurationException pce) {
				Log.e("SAX XML", "sax parse error", pce);
			} catch (SAXException se) {
				Log.e("SAX XML", "sax error", se);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			switch (paymentservice) {
			case CCTypes:
				break;
			}
		}
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
		if (addCustomerToCDMTask != null) {
			addCustomerToCDMTask.cancel(true);
			addCustomerToCDMTask = null;
		}
		if (editProspectAsynTask != null) {
			editProspectAsynTask.cancel(true);
			editProspectAsynTask = null;
		}
		swipeController = null;
		swipeController1 = null;
		if (paymentService != null) {
			paymentService.cancel(true);
			paymentService = null;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (swipeController != null)
			swipeController.onStart();
		
		if (swipeController1 != null)
			swipeController1.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (swipeController != null) {
			swipeController.onResume();
		}
		if (swipeController1 != null) {
			swipeController1.onResume();
		}
	}

	@Override
	public void onPause() {
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
		if (swipeController != null) {
			swipeController.onPause();
		}
		if (swipeController1 != null) {
			swipeController1.onPause();
		}

		super.onPause();
	}

	@Override
	public void onStop() {
		if (swipeController1 != null) {
			swipeController1.onStop();
		}

		super.onStop();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (swipeController != null) {
			swipeController.onPause();
		}
		if (swipeController1 != null) {
			swipeController1.onStop();
		}
		// TODO
	}
}
