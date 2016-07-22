package com.webparadox.bizwizsales;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ePN.AndroidSDK.ePNKeyed;
import com.webparadox.bizwizsales.helper.CCTypesHandler;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;

public class CreditCardManualEntryActivity extends Activity implements OnClickListener, OnItemSelectedListener{
	ImageView imgBack,imgHome,deleteButton;
	TextView txtBalance,txtDialogSwipperStatus,timerCount,txtDialogSwipeMessage;
	EditText editTextAmount,editTextCC,editTextCVV,editTextExp,editTextEmail;
	Button btnProceed,btnOne,btnTwo,btnThree,btnFour,btnFive,btnsix,swipeProceed,
	btnSeven,btnEight,btnNine,btnZero,btnDoubleZero;
	LinearLayout layoutBackSpace;
	StringBuffer sBuffer = new StringBuffer("");
	String balanceDueSting = "";
	String amount="";
	String status = "";
	String addressVerify = "";
	String cvv = "";
	String invoiceID = "";
	String transId = "";
	String userName = "";
	String userAddress = "";
	String userCity = "";
	String userState = "";
	String userZip = "";
	String email;

	SharedPreferences userData;

	ActivityIndicator pDialog;
	ServiceHelper serviceHelper;
	String customerId, employeeID, dealerID;

	String CustomerId;
	String dealerSecurityKey,dealerAccountNumber;

	Spinner spinnerCreditCardTypes;
	CCTypesHandler ccTypesHandler;
	AsyncTask<String, Void, String> paymentService;
	int cctype = 0, ccTypeID,currentSelection = 0;
	InputMethodManager imm ;
	ePNKeyed epnManualEntry;
	Dialog mSwipperDialog;
	String transResponse = "";
	boolean isPostTransCompleted = false;
	String strCreditCardType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.credit_card_manual_entry);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		Intent getinIntent=getIntent();
		amount=getinIntent.getStringExtra("BalanceAmount");
		balanceDueSting=getinIntent.getStringExtra("Amount");
		dealerAccountNumber=getinIntent.getStringExtra("DelarAccountNumber");
		dealerSecurityKey=getinIntent.getStringExtra("DealerSecurityKey");
		email=getinIntent.getStringExtra("ReceiptEmail");
		strCreditCardType=getinIntent.getStringExtra("CreditCardType");

		userName=getinIntent.getStringExtra("ReceiptName");
		userAddress=getinIntent.getStringExtra("ReceiptAddress");
		userCity=getinIntent.getStringExtra("ReceiptCity");
		userState=getinIntent.getStringExtra("ReceiptState");
		userZip=getinIntent.getStringExtra("ReceiptZip");

		serviceHelper = new ServiceHelper(CreditCardManualEntryActivity.this);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		customerId = userData.getString(Constants.JSON_KEY_CUSTOMER_ID, "");
		CustomerId = userData.getString("CustomerId",
				Constants.EMPTY_STRING);
		CustomerId = userData.getString("ReceiptCity",
				Constants.EMPTY_STRING);

		imgBack=(ImageView) findViewById(R.id.back_icon);
		imgHome=(ImageView) findViewById(R.id.image_back_icon);
		txtBalance=(TextView) findViewById(R.id.textViewBalance);
		txtBalance.setText("Balance : "+amount);
		editTextAmount=(EditText) findViewById(R.id.editTextAmount);
		editTextCC=(EditText) findViewById(R.id.editTextcc);
		editTextCVV=(EditText) findViewById(R.id.editTextCvv);
		editTextExp=(EditText) findViewById(R.id.editTextExp);
		editTextEmail=(EditText) findViewById(R.id.editTextEmail);
		editTextEmail.setText(email);
		btnOne=(Button) findViewById(R.id.button_one);
		btnTwo=(Button) findViewById(R.id.button_two);
		btnThree=(Button) findViewById(R.id.button_three);
		btnFour=(Button) findViewById(R.id.button_four);
		btnFive=(Button) findViewById(R.id.button_five);
		btnsix=(Button) findViewById(R.id.button_six);
		btnSeven=(Button) findViewById(R.id.button_seven);
		btnEight=(Button) findViewById(R.id.button_eight);
		btnNine=(Button) findViewById(R.id.button_nine);
		btnZero=(Button) findViewById(R.id.button_zero);
		btnDoubleZero=(Button) findViewById(R.id.button_double_zero);
		btnProceed=(Button) findViewById(R.id.buttonproceed);
		layoutBackSpace=(LinearLayout) findViewById(R.id.back_space_layout);

		spinnerCreditCardTypes=(Spinner) findViewById(R.id.spinnerCreditCardTypes);
		spinnerCreditCardTypes.setOnItemSelectedListener(this);

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
		btnProceed.setOnClickListener(this);

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

				if (hasFocus) {
					if (getCurrentFocus() != null
							&& getCurrentFocus() instanceof EditText) {
						currentSelection = 0;
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
		editTextExp.setOnClickListener(this);
		editTextExp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
						currentSelection = 2;
						if(!editTextExp.getText().toString().equalsIgnoreCase("MM/YY")){
							editTextExp.setTextIsSelectable(true);
							editTextExp.setSelection(editTextExp.getText().toString().length());
							imm.hideSoftInputFromWindow(editTextExp.getWindowToken(), 0);
						}else{
							editTextExp.setTextIsSelectable(true);
							editTextExp.setSelection(0);
							imm.hideSoftInputFromWindow(editTextExp.getWindowToken(), 0);
						}
					}
				}
			}
		});


		editTextExp.addTextChangedListener(new TextWatcher() {
			String current = "";
			String ddmmyyyy = "MMYY";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				Log.e("expiry.getText().toString() ===>", ""+editTextExp.getText().toString());

				if (!s.toString().equals(current) && !editTextExp.getText().toString().equalsIgnoreCase("MM/YY")) {
					String clean = s.toString().replaceAll("[^\\d.]", "");
					String cleanC = current.replaceAll("[^\\d.]", "");

					int cl = clean.length();
					int sel = cl;
					for (int i = 2; i <= cl && i < 6; i += 2) {
						sel++;
					}
					if (clean.equals(cleanC))
						sel--;

					if (clean.length() < 4) {
						clean = clean + ddmmyyyy.substring(clean.length());

					} else {
						int mon = Integer.parseInt(clean.substring(0, 2));
						int year = Integer.parseInt(clean.substring(2, 4));
						Calendar cal = Calendar.getInstance();
						if (mon > 12)
							mon = 12;
						cal.set(Calendar.MONTH, mon - 1);
						String yr = (cal.get(Calendar.YEAR) + "").substring(2,4);
						int yrInt = Integer.parseInt(yr);
						year = (year < yrInt) ? yrInt : (year > 99) ? 99 : year;
						clean = String.format("%02d%02d", mon, year);
					}

					clean = String.format("%s/%s", clean.substring(0, 2),
							clean.substring(2, 4));
					current = clean;
					editTextExp.setText(current);
					editTextExp.setSelection(sel < current.length() ? sel : current.length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});


		editTextCC.setOnClickListener(this);
		editTextCC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
						currentSelection = 1;
						editTextCC.setTextIsSelectable(true);
						editTextCC.setSelection(editTextCC.getText().toString().length());
						imm.hideSoftInputFromWindow(editTextCC.getWindowToken(), 0);
					}
				}
			}
		});

		editTextCC.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				editTextCC.setSelection(editTextCC.getText().toString().length());
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});


		editTextCVV.setOnClickListener(this);
		editTextCVV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
						currentSelection = 3;
						editTextCVV.setTextIsSelectable(true);
						editTextCVV.setSelection(editTextCVV.getText().toString().length());
						imm.hideSoftInputFromWindow(editTextCVV.getWindowToken(), 0);
					}
				}
			}
		});

		editTextCVV.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				editTextCVV.setSelection(editTextCVV.getText().toString().length());

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		epnManualEntry = new ePNKeyed(dealerAccountNumber, dealerSecurityKey, kHandler);

		mSwipperDialog = new Dialog(this);
		mSwipperDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSwipperDialog.setContentView(R.layout.manul_swipper_popup);
		mSwipperDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		WindowManager.LayoutParams WMLP = mSwipperDialog.getWindow()
				.getAttributes();
		WMLP.gravity = Gravity.CENTER;
		mSwipperDialog.getWindow().setAttributes(WMLP);
		mSwipperDialog.setCancelable(false);
		mSwipperDialog.setCanceledOnTouchOutside(false);

		txtDialogSwipperStatus = (TextView) mSwipperDialog
				.findViewById(R.id.swipper_status);

		timerCount = (TextView) mSwipperDialog.findViewById(R.id.timer_count);
		txtDialogSwipeMessage = (TextView) mSwipperDialog
				.findViewById(R.id.swipper_msg);
		swipeProceed = (Button) mSwipperDialog.findViewById(R.id.swipe_proceed);

		deleteButton = (ImageView) mSwipperDialog.findViewById(R.id.deleteButton);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mSwipperDialog.dismiss();
				swipeOrientation(1);
				swipeProceed.setText("");
				swipeProceed.setVisibility(View.INVISIBLE);

			}
		});

		swipeProceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mSwipperDialog != null) {
					if (mSwipperDialog.isShowing()) {
						mSwipperDialog.dismiss();
						swipeOrientation(1);
						if(swipeProceed.getText().toString().equalsIgnoreCase("next")){
							transId = transId.replace(",", "");
							transId = transId.replace(" ", "");
							Intent intent=new Intent(CreditCardManualEntryActivity.this,CreditCardPaymentSignature.class);
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
		mSwipperDialog.setCanceledOnTouchOutside(false);
		mSwipperDialog.setCancelable(false);

		paymentService = new PaymentService(PaymentServices.CCTypes.ordinal())
		.execute(Constants.CC_TYPES + dealerID);
	}

	//handler for receiving messages from ePNKeyed object
	private final Handler kHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case ePNKeyed.DID_POST_KEYED_TRANSACTION: //Received when transaction has been processed
				//				myLabel.setText((CharSequence) msg.obj);

				String localResponse = (String) msg.obj;

				if(localResponse != null){
					transResponse = localResponse;
					Log.i("transaction response = ", transResponse);
					List<String> list = new ArrayList<String>(Arrays.asList(transResponse.split(",")));

					for (int i = 0; i<list.size();i++){
						if(i==0){
							status = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']", "");
						}else if(i==1){
							addressVerify = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']", "");;
						}else if(i==2){
							cvv = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']", "");;
						}else if(i==3){
							invoiceID = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']", "");;
						}else if(i==4){
							transId = list.get(i).replaceAll("[|?*<\":>+\\[\\]/']", "");;
						}
					}

					Log.d("status = ", status);
					Log.d("addressVerify = ", addressVerify);
					Log.d("cvv = ", cvv);
					Log.d("invoiceID = ", invoiceID);
					Log.d("transId = ", transId);
					swipeProceed.setVisibility(View.VISIBLE);
					if(status.substring(0, 1).equalsIgnoreCase("n")){
						swipeProceed.setText("Cancel");
						txtDialogSwipeMessage.setText("Transaction declined"); 
						txtDialogSwipeMessage.setTextColor(Color.RED); 
					} else if(status.substring(0, 1).equalsIgnoreCase("y")){
						swipeProceed.setText("Next");
						txtDialogSwipeMessage.setText("Payment has been posted"); 
						txtDialogSwipeMessage.setTextColor(Color.GREEN); 
					}else{
						swipeProceed.setText("Ok");
						txtDialogSwipeMessage.setText("Transaction failed"); 
						txtDialogSwipeMessage.setTextColor(Color.RED); 
					}
					isPostTransCompleted = true;
				}
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.back_icon:
			gotoHomeActivity();
			break;
		case R.id.image_back_icon:
			Intent intentHome=new Intent(CreditCardManualEntryActivity.this,MainActivity.class);
			startActivity(intentHome);
			finish();
			break;
		case R.id.imageViewcreditcard:

			break;
		case R.id.button_one:
			sendKeyboardValue("1");
			break;
		case R.id.button_two:
			sendKeyboardValue("2");
			break;
		case R.id.button_three:
			sendKeyboardValue("3");
			break;
		case R.id.button_four:
			sendKeyboardValue("4");
			break;
		case R.id.button_five:
			sendKeyboardValue("5");
			break;
		case R.id.button_six:
			sendKeyboardValue("6");
			break;
		case R.id.button_seven:
			sendKeyboardValue("7");
			break;
		case R.id.button_eight:
			sendKeyboardValue("8");
			break;
		case R.id.button_nine:
			sendKeyboardValue("9");
			break;
		case R.id.button_zero:
			sendKeyboardValue("0");
			break;
		case R.id.button_double_zero:
			sendKeyboardValue("00");
			break;
		case R.id.back_space_layout:
			if(currentSelection == 0){
				editTextAmount.setText(sendKeyboardBackspace(editTextAmount.getText().toString()));
				editTextAmount.setSelection(editTextAmount.getText().toString().length());
			}else if(currentSelection ==1){
				editTextCC.setText(sendKeyboardBackspace(editTextCC.getText().toString()));
				editTextCC.setSelection(editTextCC.getText().toString().length());
			}else if(currentSelection ==2){
				if(editTextExp.getText().toString().equalsIgnoreCase("MM/YY")){
					editTextExp.setSelection(0);	
				}else{
					editTextExp.setText(sendKeyboardBackspace(editTextExp.getText().toString()));
				}
			}else if(currentSelection ==3){
				editTextCVV.setText(sendKeyboardBackspace(editTextCVV.getText().toString()));
				editTextCVV.setSelection(editTextCVV.getText().toString().length());
			}
			break;
		case R.id.editTextAmount:
			editTextAmount.setSelection(editTextAmount.getText().toString()
					.length());
			editTextAmount.setSelection(editTextAmount.getText().toString()
					.length());

			imm.hideSoftInputFromWindow(editTextAmount.getWindowToken(), 0);
			break;
		case R.id.editTextExp:
			editTextExp.setSelection(editTextExp.getText().toString().length());
			imm.hideSoftInputFromWindow(editTextExp.getWindowToken(), 0);
			break;
		case R.id.editTextcc:
			EditText ccEditText = (EditText) findViewById(v.getId());
			editTextCC.setSelection(editTextCC.getText().toString().length());

			imm.hideSoftInputFromWindow(ccEditText.getWindowToken(), 0);
			break;
		case R.id.editTextCcv:
			EditText cvvEditText = (EditText) findViewById(v.getId());
			editTextCVV.setSelection(editTextCVV.getText().toString().length());

			imm.hideSoftInputFromWindow(cvvEditText.getWindowToken(), 0);
			break;
		case R.id.buttonproceed:
			if(editTextAmount.getText().toString().length()>0 && !editTextAmount.getText().toString().equalsIgnoreCase("$0.00")){
				if(editTextCC.getText().toString().length()>0 ){
					if(editTextExp.getText().toString().length()>0 && !editTextExp.getText().toString().equalsIgnoreCase("MM/YY")){
						if(editTextCVV.getText().toString().length()>0 ){
							if(editTextEmail.getText().toString().length()>0 ){
								txtDialogSwipeMessage.setText("Transaction in progress");
								txtDialogSwipeMessage.setTextColor(Color.GREEN); 
								mSwipperDialog.show();
								swipeOrientation(0);
								isPostTransCompleted = false;

								epnManualEntry.setCardNo(editTextCC.getText().toString());
								String[] expiryMonthYear = editTextExp.getText().toString().split("/");
								String expiryMonth = expiryMonthYear[0];
								String expiryYear = expiryMonthYear[1];
								String stramount = editTextAmount.getText().toString()
										.replace("$", "");
								stramount = stramount.replace(",", "");
								epnManualEntry.setTotal(stramount);
								epnManualEntry.setExpMonth(expiryMonth);
								epnManualEntry.setExpYear(expiryYear);
								epnManualEntry.setCVV2(editTextCVV.getText().toString());
								epnManualEntry.setFirstName(userName);
								epnManualEntry.setAddress(userAddress);
								epnManualEntry.setCity(userCity);
								epnManualEntry.setState(userState);
								epnManualEntry.setZip(userZip);
								epnManualEntry.setInv(""+customerId);
								epnManualEntry.setEmail(editTextEmail.getText().toString());
								epnManualEntry.postTransaction();
							}else{
								Toast.makeText(getApplicationContext(), "Please enter your email.", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "Please enter your CVV.", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "Please enter the expiry date.", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Please enter your CC#", Toast.LENGTH_SHORT).show();
				}

			}else{
				Toast.makeText(getApplicationContext(), "Please enter the amount.", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	public void swipeOrientation(int orientationLock) {
		if (getResources().getBoolean(R.bool.is_tablet)) {
			if (orientationLock == 0) {
				int orientation = CreditCardManualEntryActivity.this.getRequestedOrientation();

				int rotation = ((WindowManager) CreditCardManualEntryActivity.this
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

				CreditCardManualEntryActivity.this.setRequestedOrientation(orientation);
			} else {
				CreditCardManualEntryActivity.this
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			}
		}
	}

	public void setValue(String keyValue) {
		Log.d("sBuffer Value1", sBuffer.toString());
		Log.d("sBuffer Value2", sBuffer.toString() + keyValue);

		String editValue = (sBuffer.toString() + keyValue);

		editValue = editValue.replace(".", "");
		editValue = editValue.replace("$", "");
		editValue = editValue.replaceAll("\\s+", "");
		editValue = editValue.replaceAll("[-.^:,()]", "");

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
			Toast.makeText(CreditCardManualEntryActivity.this, "Cannot exceed the balance amount",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void setCCValue(String keyValue) {
		if(editTextCC.getText().toString().length()<16){
			String editValue = (editTextCC.getText().toString() + keyValue);
			editTextCC.setText(editValue);
		}else{
			Toast.makeText(CreditCardManualEntryActivity.this, "Please enter 16 digit cc number",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void setExpValue(String keyValue) {
		String editValue = (editTextExp.getText().toString() + keyValue);
		editTextExp.setText(editValue);
	}

	public void setCvvValue(String keyValue) {

		Log.d("value = ", "" +ccTypesHandler.getItemsTypeId().get(cctype));
		if(ccTypesHandler.getItemsTypeId().get(cctype).equalsIgnoreCase("Amex")){
			if(editTextCVV.getText().toString().length()<4){
				String editValue = (editTextCVV.getText().toString() + keyValue);
				editTextCVV.setText(editValue);	
			}else{
				Toast.makeText(CreditCardManualEntryActivity.this, "Please enter four digit cvv number",
						Toast.LENGTH_SHORT).show();
			}

		}else{
			if(editTextCVV.getText().toString().length()<3){
				String editValue = (editTextCVV.getText().toString() + keyValue);
				editTextCVV.setText(editValue);	
			}else{
				Toast.makeText(CreditCardManualEntryActivity.this, "Please enter three digit cvv number",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	public void sendKeyboardValue(String keyValue) {
		if(currentSelection == 0){
			setValue(keyValue);
		}else if(currentSelection ==1){
			setCCValue(keyValue);
		}else if(currentSelection ==2){
			setExpValue(keyValue);
		}else if(currentSelection ==3){
			setCvvValue(keyValue);
		}
	}

	public String sendKeyboardBackspace(String keyValue) {
		Log.e("keyValue ==> ", ""+keyValue);
		String editValue = keyValue;
		String value = "";
		editValue = editValue.replace(".", "");
		editValue = editValue.replace("$", "");
		editValue = editValue.replaceAll("\\s+", "");
		editValue = editValue.replaceAll("[-.^:,()]", "");
		editValue = editValue.replaceAll("M", "");
		editValue = editValue.replaceAll("Y", "");
		editValue = editValue.replaceAll("/", "");
		Log.e("editValue ==> ", ""+editValue);

		if (editValue.length() > 0) {
			int len = editValue.length();
			sBuffer = new StringBuffer(editValue.substring(0, len - 1));
			value = editValue.substring(0, len - 1);
		}
		return value;
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

	enum PaymentServices {
		CCTypes
	}
	class PaymentService extends AsyncTask<String, Void, String> {
		PaymentServices paymentservice;

		public PaymentService(int which) {
			// TODO Auto-generated constructor stub
			pDialog = new ActivityIndicator(CreditCardManualEntryActivity.this);
			paymentservice = PaymentServices.values()[which];
			pDialog.setLoadingText("Loading...");
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
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
				if (params[0].contains("https://") || params[0].contains("http://")) {
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

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CreditCardManualEntryActivity.this,
						android.R.layout.simple_spinner_item,  ccTypesHandler.getItemsTypeId());
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerCreditCardTypes.setAdapter(dataAdapter);
				for (int i = 0; i < ccTypesHandler.getItemsTypeId().size(); i++) {
					if (strCreditCardType.equals(ccTypesHandler.getItemsTypeId().get(i))) {
						spinnerCreditCardTypes.setSelection(i);
						cctype = i;
						ccTypeID = Integer.parseInt(ccTypesHandler.getItemsListId().get(cctype));
					}
				}

				break;
			}
		}
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		((TextView) parent.getChildAt(0)).setPadding(0, 0, 0, 0);
		//		((TextView) parent.getChildAt(0)).setTextSize(getResources().getDimension(R.dimen.txt16));
		cctype = position;
		ccTypeID = Integer.parseInt(ccTypesHandler.getItemsListId().get(cctype));
		Log.i("ccTypeID", ""+ccTypeID);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

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
		if(paymentService != null){
			paymentService.cancel(true);
			paymentService = null;
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
