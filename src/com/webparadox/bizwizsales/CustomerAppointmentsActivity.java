package com.webparadox.bizwizsales;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.CalendarActivity.Compare;
import com.webparadox.bizwizsales.adapter.SalesProcessProductGridViewAdapter;
import com.webparadox.bizwizsales.asynctasks.AppointmentQuestionnaireAsyncTask;
import com.webparadox.bizwizsales.asynctasks.AppointmentQuestionnaireAsyncTask.AppointmentQuestionnaireInterface;
import com.webparadox.bizwizsales.asynctasks.CustomerAttachmentAsyncTask;
import com.webparadox.bizwizsales.asynctasks.DispoQuestionnaireAsyncTask;
import com.webparadox.bizwizsales.asynctasks.DispoQuestionnaireAsyncTask.postQuestionnaire;
import com.webparadox.bizwizsales.asynctasks.FinancingCompanyAsyncTask;
import com.webparadox.bizwizsales.asynctasks.GetProposalAsyncTask;
import com.webparadox.bizwizsales.asynctasks.GetProposalAsyncTask.onProposalListener;
import com.webparadox.bizwizsales.asynctasks.LeadQuestionaireAsyncTask;
import com.webparadox.bizwizsales.asynctasks.LeadQuestionaireAsyncTask.LeadPostExecute;
import com.webparadox.bizwizsales.asynctasks.ProposalAsynTask;
import com.webparadox.bizwizsales.asynctasks.ProposalAsynTask.IDispoSubDispoQuestions;
import com.webparadox.bizwizsales.asynctasks.SaveDispoAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SaveDispoQstnAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SaveDispoQstnAsyncTask.loadSalesResult;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SpProductsAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SummaryEmailAsyncTask;
import com.webparadox.bizwizsales.asynctasks.TermsAsyncTask;
import com.webparadox.bizwizsales.asynctasks.TermsAsyncTask.Step2LabelListener;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ObjectSerializer;
import com.webparadox.bizwizsales.helper.PaymentStatusHandler;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.AppointmentDateTimeModel;
import com.webparadox.bizwizsales.models.AppointmentTypeModel;
import com.webparadox.bizwizsales.models.DispoModel;
import com.webparadox.bizwizsales.models.DispoQuestionnaireModel;
import com.webparadox.bizwizsales.models.EventConfigurationAppntTypeModel;
import com.webparadox.bizwizsales.models.EventConfigurationVisitTypeModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;
import com.webparadox.bizwizsales.models.ProposalCartModel;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;
import com.webparadox.bizwizsales.models.SubDispoModel;

public class CustomerAppointmentsActivity extends Activity implements
OnClickListener, LeadPostExecute, AppointmentQuestionnaireInterface,
OnItemSelectedListener, OnCheckedChangeListener, postQuestionnaire,
loadSalesResult, Step2LabelListener, IDispoSubDispoQuestions,
onProposalListener {
	JSONArray paymentConfigJsonArray = null;
	GetProposalAsyncTask getProposalAsyncTask;
	PaymentStatusHandler paymentStatusHandler;
	// PaymentConfigAsyncTask paymentConfigAsyncTask;
	AsyncTask<String, Void, Void> paymentConfigAsyncTask;
	AsyncTask<String, Void, Void> savePaymentAsyncTask;
	public static Dialog mCalendarNotesDialog;
	// Shared Preference Variables
	AsyncTask<Void, Void, Void> eventConfigAsynTask;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	public String CanEditEvents = "";
	EditText edittext_notes;
	int year;
	int monthofday;
	int day;
	int hour;
	int minute, checkClickValue;
	long minuteTime;
	String timeSet = "", currentValue = "", finalAmount = "", response = "";
	String ModuleStatus = "", dealerAccountNumber = "", dealerSecurityKey = "";
	int perValue = 0;
	TextView cusAppointmentName, cusAppointmentAddress, cusAppointmentType,
	cusAppointmentDate, text_time, payment_done;
	ImageView cusAppointmentEdit;
	// Button leadButton,noteButton;
	String text_HotQuotes = "";
	static Context context;
	String selectedItem, name, address, type, date, time, eventType,
	appointmentId, dealerId, LeadTypeId, AppointmentTypeId, employeeId,
	customerId, eventId, Edit_date, Edit_time, typeId, timeInterval;
	public static String appntResultId, locksavingDispo;
	Typeface droidSans, droidSansBold;
	ImageView backToPrevious, backToHome;
	LinearLayout bodyLayout, customLayout, layoutPaymentContent;
	LinearLayout spinnerLayout, uploadLayout;
	EditText edAnswer, payment_number_edit, percentage_edit, amount_edit;
	static Spinner financing_company_spinner;
	String currentFinancingCompanyId = "";
	ImageView btn_select_date;
	int position;
	ScrollView cusAppointmentScrollview;
	TermsAsyncTask termsTask;
	ArrayList<String> idArray = new ArrayList<String>();
	ArrayList<String> methodArray = new ArrayList<String>();
	// GridView cusAppointmentGridview;
	HashMap<Integer, String> spAnswerList = new HashMap<Integer, String>();
	HashMap<Integer, String> spAnswerIdList = new HashMap<Integer, String>();
	HashMap<Integer, EditText> edAnswerList = new HashMap<Integer, EditText>();

	HashMap<Integer, String> raAnswerIdList = new HashMap<Integer, String>();
	HashMap<Integer, String> raAnswerList = new HashMap<Integer, String>();
	HashMap<Integer, RadioButton> raAnswers = new HashMap<Integer, RadioButton>();
	HashMap<Integer, EditText> EditList = new HashMap<Integer, EditText>();
	HashMap<Integer, String> SpinList = new HashMap<Integer, String>();
	HashMap<Integer, String> SpinChoiceList = new HashMap<Integer, String>();

	// leadquestionary
	HashMap<Integer, String> leadAnswerList = new HashMap<Integer, String>();
	HashMap<Integer, String> leadAnswerIdList = new HashMap<Integer, String>();
	HashMap<Integer, EditText> leadEdAnswerList = new HashMap<Integer, EditText>();
	HashMap<Integer, RadioButton> leadraAnswers = new HashMap<Integer, RadioButton>();
	HashMap<Integer, String> leadraAnswerIdList = new HashMap<Integer, String>();
	HashMap<Integer, String> leadraAnswerList = new HashMap<Integer, String>();
	int spinner1Id = 100, spinner2Id = 200, checkedSpinner1 = 0,
			financial_company_spinner_id = 300, checkedSpinner2 = 0;
	int countone = 0, counttwo = 0;

	ImageView imgLead, imgNotes, imgCamera, imgProducts, imgCashCard,
	imgGenerateProposal, imgEdit;

	TextView txtOldMenu, txtMenu1, txtMenu2, txtMenu3, txtMenu4, txtMenu5,
	txtMenu6, text_date, tw_percent, fif_percent, hun_percent, others,
	cashchecknumber, amount, p_amount;
	static LinearLayout layoutcontent, layoutProductContent;
	static GridView gridViewProductsCategory, gridViewRecentProducts;
	static View viewProducts;
	AsyncTask<Void, Integer, Void> spProductsAsyncTask;
	LeadQuestionaireAsyncTask leadAsynctask;
	SaveAppointmentQuestionnaireAsyncTask saveAsyncTask;
	SaveLeadQuestionnaireAsyncTask saveLeadAsyncTask;
	AppointmentQuestionnaireAsyncTask appointmentQuestionireTask;
	AsyncTask<Void, Void, Void> EditAppntAsyncTask;
	AsyncTask<Void, Void, Void> appntResultIdTask;
	ProposalAsynTask proposalAsyncTask;
	DispoQuestionnaireAsyncTask dispoAsyncTask;
	SaveDispoAsyncTask saveDispoAsyncTask;
	SaveDispoQstnAsyncTask saveDispoQstnTask;
	ActivityIndicator pDialog;
	EditCustomerAppointmentDialog EditDialog;
	SearchView cusAppointmentSearchview;
	SmartSearchAsyncTask searchAsyncTask;
	boolean isSuccess = false;
	boolean isSuccessIntent = false;
	Compare compare;
	boolean isNoConflict = false;
	public static ArrayList<SpProductSubCatAndMaterialModel> mRecentProductsList = new ArrayList<SpProductSubCatAndMaterialModel>();
	ObjectSerializer mObjetserilizer = new ObjectSerializer();
	SharedPreferences preferenceSettings;
	SharedPreferences.Editor settingsEditor;
	DatabaseHandler dbHandler;
	ProgressDialog progressBar;
	ArrayList<String> uploadPhotoArray = new ArrayList<String>();
	ArrayList<String> discriptionForImage = new ArrayList<String>();
	ArrayList<Bitmap> allBit = new ArrayList<Bitmap>();
	ArrayList<String> statusIdArray = new ArrayList<String>();
	AsyncTask<Void, Integer, String> uploadAsyncTask;
	TextView text_select_date;
	EditText text_qstns;
	Spinner spinner_qstn;
	EditText editText;
	int editTextTag = 1000;
	String result = "";
	ImageView galleryImageUpload;
	String contractAmount = "";
	ArrayAdapter<SubDispoModel> AppntSubResultAdapter;
	ArrayAdapter<DispoModel> AppntResultAdapter;
	Spinner spinner_Appnt_Sub_Result, spinner_Appnt_Result;
	String DispoId = "";
	String SubDispoId = "";
	String appointmentResultId;
	LinearLayout layout_hotQuotes;
	EditText edit_notes;
	ImageView cash, check, crietcard, financing;
	int depoPosition = 0;
	CustomerAttachmentAsyncTask cusAsyncTask;
	ListView cus_attachment_listview;
	ScrollView cus_appointment_scrollview;
	TextView addphoto;
	float rAmount;
	ArrayList<JSONObject> nameValuePairs;
	ArrayList<ProposalCartModel> mproposalCartList = new ArrayList<ProposalCartModel>();
	ServiceHelper serviceHelper;
	String pamount;
	JSONObject jsonRequestText, jsonResultText;
	int appointmentSelected, leadSelected;
	ListView proposalListview;
	Button emailBtn, viewProposalBtn, addProductBtn;
	SummaryEmailAsyncTask summaryEmailTask;
	LinearLayout layoutProposalContent;
	TextView productHeader, productTotal;
	LinearLayout layout_header_view;
	AsyncTask<Void, Void, Void> financingCompanyNameAsyncTask;
	CalendarActivity.AppointmentTimesAsync AppointmentTimesAsync;
	AppointmentTimesAsync AppointmentTimesAsync1;
	TextView text_appnt_sub_result;

	public void onCreate(Bundle b) {
		super.onCreate(b);
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.customer_appointment_activity);
		// Constants.isProposalList = true;

		layoutProposalContent = (LinearLayout) findViewById(R.id.layoutProposalContent);
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		productHeader = (TextView) findViewById(R.id.product_header);
		productTotal = (TextView) findViewById(R.id.prouct_total);
		productHeader.setTypeface(droidSansBold, Typeface.BOLD);
		productTotal.setTypeface(droidSansBold, Typeface.BOLD);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		CanEditEvents = userData.getString(
				Constants.KEY_EMPLOYEE_CAN_EDIT_EVENTS, Constants.EMPTY_STRING);
		customerId = userData.getString("CustomerId", Constants.EMPTY_STRING);
		name = userData.getString("Name", Constants.EMPTY_STRING);
		address = userData.getString("Address", Constants.EMPTY_STRING);
		employeeId = userData.getString("EmployeeId", Constants.EMPTY_STRING);
		position = Integer.parseInt(userData.getString("Position",
				Constants.EMPTY_STRING));
		dealerId = userData.getString("DealerId", Constants.EMPTY_STRING);
		appointmentSelected = userData.getInt("appointmentSelected", 0);
		leadSelected = userData.getInt("leadSelected", 0);
		contractAmount = userData.getString("Amount", Constants.EMPTY_STRING);
		appointmentResultId = userData.getString(Constants.KEY_APPT_RESULT_ID,
				"0");
		context = this;
		dbHandler = new DatabaseHandler(this);
		serviceHelper = new ServiceHelper(context);
		preferenceSettings = getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);
		settingsEditor = preferenceSettings.edit();

		// getCartListFromSharedPrefference(Singleton.getInstance().appointmentArray
		// .get(position).getAppointmentResultId());

		imgLead = (ImageView) findViewById(R.id.imageViewLead);
		imgNotes = (ImageView) findViewById(R.id.imageViewNotes);
		imgCamera = (ImageView) findViewById(R.id.imageViewCamera);
		imgCashCard = (ImageView) findViewById(R.id.imageViewCashCard);
		imgProducts = (ImageView) findViewById(R.id.imageViewProducts);
		imgGenerateProposal = (ImageView) findViewById(R.id.imageViewGenerateProposal);
		proposalListview = (ListView) findViewById(R.id.listView_proposal);
		proposalListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent editProposalActivity = new Intent(context,
						EditProposalActivity.class);
				editProposalActivity.putExtra("Position", position);
				editProposalActivity.putExtra("From", "cusAppo");
				startActivity(editProposalActivity);
			}
		});

		galleryImageUpload = (ImageView) findViewById(R.id.gallerImageUpload);
		galleryImageUpload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Constants.isNetworkConnected(getApplicationContext())) {
					for (int i = 0; i < allBit.size(); i++) {
						EditText edit = (EditText) findViewById(editTextTag + i);
						if (!edit.getText().toString().trim()
								.equalsIgnoreCase("")
								&& !edit.getText().toString()
								.equalsIgnoreCase(null)) {
							discriptionForImage.set(i, edit.getText()
									.toString());
							String status = dbHandler.adddata(customerId,
									employeeId, dealerId, uploadPhotoArray
									.get(i), edit.getText().toString());
							statusIdArray.add(status);
						} else {
							SimpleDateFormat sdf = new SimpleDateFormat(
									"dd:MMMM:yyyy HH:mm:ss ");
							final String currentDateandTime = sdf
									.format(new Date());
							discriptionForImage.set(i, currentDateandTime);
							String status = dbHandler.adddata(customerId,
									employeeId, dealerId,
									uploadPhotoArray.get(i), currentDateandTime);
							statusIdArray.add(status);
						}
					}

					uploadAsyncTask = new UploadImages().execute();
				} else {
					messageHandler.sendEmptyMessage(0);
				}
			}
		});

		emailBtn = (Button) findViewById(R.id.mail_btn);
		emailBtn.setTypeface(droidSansBold);
		emailBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(Singleton.getInstance().quotedProductModel.size() > 0) {
					summaryEmailTask = new SummaryEmailAsyncTask(context, dealerId,
							appointmentResultId);
					summaryEmailTask.execute();
				}else {
					Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
				}


			}
		});
		viewProposalBtn = (Button) findViewById(R.id.view_proposal_btn);
		viewProposalBtn.setTypeface(droidSansBold);
		viewProposalBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent proposalActivity = new Intent(context,
						NewProposalSummaryActivity.class);
				startActivity(proposalActivity);
			}
		});
		addProductBtn = (Button) findViewById(R.id.add_product_btn);
		addProductBtn.setTypeface(droidSansBold);
		addProductBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*layoutProductContent.setVisibility(View.GONE);
				Constants.isProposalList = false;
				initProducts();*/

				if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
					Intent proposalActivity = new Intent(context,
							NewProposalSummaryActivity.class);
					startActivity(proposalActivity);	
				}else{
					layoutProductContent.setVisibility(View.GONE);
					Constants.isProposalList = false;
					initProducts();
				}

			}
		});
		if (Constants.isLock.equalsIgnoreCase("True")) {
			addProductBtn.setEnabled(false);
			addProductBtn.setBackground(context.getResources().getDrawable(
					R.drawable.resolve_disable_background));
		} else {
			addProductBtn.setEnabled(true);
			addProductBtn.setBackground(context.getResources().getDrawable(
					R.drawable.selector_prospect_save_button));
		}

		imgLead.setOnClickListener(this);
		imgNotes.setOnClickListener(this);
		imgCamera.setOnClickListener(this);
		imgProducts.setOnClickListener(this);
		imgCashCard.setOnClickListener(this);
		imgGenerateProposal.setOnClickListener(this);

		txtMenu1 = (TextView) findViewById(R.id.textViewMenu1);
		txtMenu2 = (TextView) findViewById(R.id.textViewMenu2);
		txtMenu3 = (TextView) findViewById(R.id.textViewMenu3);
		txtMenu4 = (TextView) findViewById(R.id.textViewMenu4);
		txtMenu5 = (TextView) findViewById(R.id.textViewMenu5);
		txtMenu6 = (TextView) findViewById(R.id.textViewMenu6);
		setMenuSelected(txtMenu1);

		viewProducts = getLayoutInflater().inflate(
				R.layout.products_picker_layout, null);
		layoutProductContent = (LinearLayout) findViewById(R.id.layoutProductContent);
		layoutcontent = (LinearLayout) findViewById(R.id.layoutContent);

		uploadLayout = (LinearLayout) findViewById(R.id.uploadLayout);

		amount = (TextView) findViewById(R.id.amount);
		layoutPaymentContent = (LinearLayout) findViewById(R.id.layoutPaymentContent);
		tw_percent = (TextView) findViewById(R.id.tw_percent);
		tw_percent.setOnClickListener(this);
		fif_percent = (TextView) findViewById(R.id.fif_percent);
		fif_percent.setOnClickListener(this);
		hun_percent = (TextView) findViewById(R.id.hun_percent);
		hun_percent.setOnClickListener(this);
		others = (TextView) findViewById(R.id.others);
		others.setOnClickListener(this);
		p_amount = (TextView) findViewById(R.id.p_amount);
		String camount = contractAmount.replace("$", "");
		camount = camount.replace(",", "");
		if (camount == null || camount.equals("")) {
			camount = "0.00";
		}

		rAmount = Float.valueOf(camount) * ((float) 100) / (float) 100;

		String amountValue = addDollerSymbol(""
				+ new BigDecimal(rAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
		p_amount.setText(amountValue);

		// p_amount.setText("$"
		// + new BigDecimal(rAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
		hun_percent
		.setBackgroundResource(R.drawable.shape_main_list_row_pressed);

		tw_percent.setBackgroundResource(R.drawable.shape_main_list_row);
		fif_percent.setBackgroundResource(R.drawable.shape_main_list_row);
		others.setBackgroundResource(R.drawable.shape_main_list_row);

		cash = (ImageView) findViewById(R.id.cash);
		cash.setOnClickListener(this);
		check = (ImageView) findViewById(R.id.check);
		check.setOnClickListener(this);
		crietcard = (ImageView) findViewById(R.id.crietcard);
		crietcard.setOnClickListener(this);
		financing = (ImageView) findViewById(R.id.financing);
		financing.setOnClickListener(this);

		cashchecknumber = (TextView) findViewById(R.id.cashchecknumber);
		cashchecknumber.setOnClickListener(this);
		cashchecknumber.setText("Check Number");

		financing_company_spinner = (Spinner) findViewById(R.id.financing_company_spinner);
		financing_company_spinner.setId(financial_company_spinner_id);
		financing_company_spinner.setOnItemSelectedListener(this);

		payment_number_edit = (EditText) findViewById(R.id.payment_number_edit);
		payment_number_edit.setOnClickListener(this);
		payment_number_edit.setInputType(InputType.TYPE_CLASS_NUMBER);

		payment_done = (TextView) findViewById(R.id.payment_done);
		payment_done.setOnClickListener(this);

		addphoto = (TextView) findViewById(R.id.addphoto);
		addphoto.setOnClickListener(this);
		cus_attachment_listview = (ListView) findViewById(R.id.cus_attachment_listview);
		cus_attachment_listview
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent cusAttachmentIntent = new Intent(context,
						CusAttachmentDetailsActivity.class);
				cusAttachmentIntent.putExtra("Position", position);
				startActivity(cusAttachmentIntent);
			}
		});

		cus_appointment_scrollview = (ScrollView) findViewById(R.id.cus_appointment_scrollview);

		init();

		layout_header_view = (LinearLayout) findViewById(R.id.layout_header_view);
		layout_header_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent cusDetails = new Intent(
						CustomerAppointmentsActivity.this,
						CustomerDetailsActivity.class);
				cusDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(cusDetails);
				finish();
			}
		});

		eventConfigAsynTask = new EventConfigAsyncTask(context).execute();

	}

	public String addDollerSymbol(String value) {
		if (Double.parseDouble(value.replace(",", "").replace("-", "")) < 1) {
			NumberFormat formatter = new DecimalFormat("#0.00");
			value = formatter.format(Double.parseDouble(value));
			value = "$" + value;
		} else {
			if (value.contains("-")) {

				double amount = Double.parseDouble(value
						.substring(1, value.length()).replace(",", "")
						.replace("$", ""));
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				String formatted = formatter.format(amount);

				value = value.subSequence(0, 1) + "$" + formatted;
			} else {
				double amount = Double.parseDouble(value.replace(",", "")
						.replace("$", ""));
				DecimalFormat formatter = new DecimalFormat("#,###.00");
				String formatted = formatter.format(amount);

				value = "$" + formatted;
			}
		}
		return value;
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Customer Appointment Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	public void init() {
		if(Singleton.getInstance().appointmentArray.size()>0){
		cusAppointmentName = (TextView) findViewById(R.id.cus_appointment_name);
		cusAppointmentName.setTypeface(droidSans);
		cusAppointmentAddress = (TextView) findViewById(R.id.cus_appointment_address);
		cusAppointmentAddress.setTypeface(droidSans);
		cusAppointmentType = (TextView) findViewById(R.id.cus_appointment_type);
		cusAppointmentType.setTypeface(droidSans);
		cusAppointmentDate = (TextView) findViewById(R.id.cus_appointment_date_time);
		cusAppointmentDate.setTypeface(droidSans);
		cusAppointmentEdit = (ImageView) findViewById(R.id.cus_appointment_edit);
		cusAppointmentEdit.setOnClickListener(this);
		cusAppointmentScrollview = (ScrollView) findViewById(R.id.cus_appointment_scrollview);
		bodyLayout = (LinearLayout) findViewById(R.id.cus_attachment_body_layout);
		customLayout = new LinearLayout(context);
		LinearLayout.LayoutParams lpspinner = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		customLayout.setLayoutParams(lpspinner);
		customLayout.setOrientation(LinearLayout.VERTICAL);
		date = Singleton.getInstance().appointmentArray.get(position)
				.getFormattedApptDate();
		time = Singleton.getInstance().appointmentArray.get(position)
				.getApptTime();
		type = Singleton.getInstance().appointmentArray.get(position)
				.getAppointmentType();
		typeId = Singleton.getInstance().appointmentArray.get(position)
				.getAppointmentTypeId();
		appointmentId = Singleton.getInstance().appointmentArray.get(position)
				.getAppointmentId();
		eventType = Singleton.getInstance().appointmentArray.get(position)
				.getEventType();
		LeadTypeId = Singleton.getInstance().appointmentArray.get(position)
				.getLeadTypeId();
		eventId = Singleton.getInstance().appointmentArray.get(position)
				.getEventId();
		Log.d("EventId", Singleton.getInstance().appointmentArray.get(position)
				.getAppointmentTypeId());

		appntResultId = Singleton.getInstance().appointmentArray.get(position)
				.getAppointmentResultId();
		Singleton.getInstance().strApptResultId=appntResultId;
		Singleton.getInstance().strAppointmentId=appointmentId;

		locksavingDispo = Singleton.getInstance().appointmentArray
				.get(position).getLocksavingDispo();

		AppointmentTypeId = Singleton.getInstance().appointmentArray.get(
				position).getAppointmentTypeId();
		cusAppointmentName.setText(name);
		cusAppointmentAddress.setText(address);
		cusAppointmentDate.setText(date + " " + time);
		// cusAppointmentType.setText(type + "-" + eventType);
		cusAppointmentType.setText(type
				+ "-"
				+ Singleton.getInstance().appointmentArray.get(position)
				.getLeadType());
		backToPrevious = (ImageView) findViewById(R.id.cus_appointment_back_icon);
		backToPrevious.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isSuccessIntent) {
					Intent cusDetails = new Intent(
							CustomerAppointmentsActivity.this,
							CustomerDetailsActivity.class);
					cusDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(cusDetails);
					finish();
				} else {
					Intent cusDetails = new Intent(
							CustomerAppointmentsActivity.this,
							CustomerDetailsActivity.class);
					cusDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(cusDetails);
					finish();
				}

			}
		});
		backToHome = (ImageView) findViewById(R.id.cus_appointment_image_back_icon);
		backToHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});

		cusAppointmentSearchview = (SearchView) findViewById(R.id.cus_appointment_searchView);

		cusAppointmentSearchview
		.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if (query.trim().length() != 0) {

					searchAsyncTask = new SmartSearchAsyncTask(context,
							dealerId, employeeId, query);
					searchAsyncTask.execute();
				} else {
					Toast.makeText(context, Constants.TOAST_NO_DATA,
							Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// if(iscustQuestion){
		// leadAsynctask = new LeadQuestionaireAsyncTask(context, appointmentId,
		// dealerId);
		// leadAsynctask.execute();
		// }

		if (Constants.isCheckOut) {
			Constants.isCheckOut = false;
			layoutcontent.setVisibility(View.GONE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			layoutPaymentContent.setVisibility(View.VISIBLE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu5);
			contractAmount = userData.getString("Amount",
					Constants.EMPTY_STRING);
			amount.setText(addDollerSymbol(contractAmount.replace("$", "")));
			paymentConfigAsyncTask = new PaymentConfigAsyncTask().execute();
		} else if (Constants.isSavedPayment) {
			Constants.isSavedPayment = false;
			layoutProposalContent.setVisibility(View.GONE);
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			layoutcontent.setVisibility(View.VISIBLE);
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			cus_appointment_scrollview.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu6);
			LoadSalesResults();
		} else if (Constants.isSelectProduct) {
			Constants.isProposalList = false;
			initProducts();
		} else if (Constants.isProposalList) {
			Constants.isSelectProduct = false;
			loadProposalNew();
		} else {
			leadAsynctask = new LeadQuestionaireAsyncTask(context,
					appointmentId, dealerId);
			leadAsynctask.execute();
			if(eventType.equalsIgnoreCase("SALES")){
				//Need to enable Step 2
				imgNotes.setClickable(true);
			}else{
				//Need to disable Step 2
				imgNotes.setClickable(false);


			}

		}
	}
	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(context, MainActivity.class);
		startActivity(backIntent);
		finish();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (isSuccessIntent) {
			Intent cusDetails = new Intent(CustomerAppointmentsActivity.this,
					CustomerDetailsActivity.class);
			cusDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(cusDetails);
			finish();
		} else {
			Intent cusDetails = new Intent(CustomerAppointmentsActivity.this,
					CustomerDetailsActivity.class);
			cusDetails.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(cusDetails);
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.cus_appointment_edit:
			if (CanEditEvents.endsWith("True")) {
				try {
					EditDialog = new EditCustomerAppointmentDialog(context);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				EditDialog.show();
			} else {
				Toast.makeText(getApplicationContext(),
						Constants.PERMISSION_DENIED, Toast.LENGTH_SHORT).show();
			}
			break;

		case 1: {
			saveAsyncTask = new SaveAppointmentQuestionnaireAsyncTask(context,
					saveRequest());
			saveAsyncTask.execute();
		}
		break;
		case 2: {
			saveLeadAsyncTask = new SaveLeadQuestionnaireAsyncTask(context,
					leadsaveRequest());
			saveLeadAsyncTask.execute();
			break;
		}

		case R.id.imageViewLead:
			// iscustQuestion = true;
			layoutProposalContent.setVisibility(View.GONE);
			bodyLayout.removeAllViews();

			layoutcontent.setVisibility(View.VISIBLE);
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			cus_appointment_scrollview.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu1);

			leadAsynctask = new LeadQuestionaireAsyncTask(context,
					appointmentId, dealerId);
			leadAsynctask.execute();

			break;
		case R.id.imageViewNotes:
			// iscustQuestion = false;
			layoutProposalContent.setVisibility(View.GONE);
			bodyLayout.removeAllViews();
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			layoutcontent.setVisibility(View.VISIBLE);
			cus_appointment_scrollview.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu2);
			checkedSpinner1 = 0;
			checkedSpinner2 = 0;
			termsTask = new TermsAsyncTask(context, dealerId);
			termsTask.execute();

			break;
		case R.id.imageViewCamera:
			layoutProposalContent.setVisibility(View.GONE);
			// layoutcontent.setVisibility(View.GONE);
			// layoutProductContent.setVisibility(View.GONE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu3);
			bodyLayout.removeAllViews();
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			layoutcontent.setVisibility(View.VISIBLE);
			uploadLayout.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			galleryImageUpload.setVisibility(View.GONE);
			cus_appointment_scrollview.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.VISIBLE);
			addphoto.setVisibility(View.VISIBLE);
			cusAsyncTask = new CustomerAttachmentAsyncTask(context, dealerId,
					customerId, cus_attachment_listview, txtMenu1, "test", 1);
			cusAsyncTask.execute();
			break;
		case R.id.imageViewProducts:
			Constants.isProposal = true;
			layoutcontent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu4);
			layoutProposalContent.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			getProposalAsyncTask = new GetProposalAsyncTask(context, dealerId,
					appointmentResultId, proposalListview, "check",
					productTotal);
			getProposalAsyncTask.execute();
			break;
		case R.id.imageViewCashCard:
			layoutProposalContent.setVisibility(View.GONE);
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			layoutcontent.setVisibility(View.GONE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			layoutPaymentContent.setVisibility(View.VISIBLE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu5);
			contractAmount = userData.getString("Amount",
					Constants.EMPTY_STRING);
			amount.setText(addDollerSymbol(contractAmount.replace("$", "")));

			hun_percent
			.setBackgroundResource(R.drawable.shape_main_list_row_pressed);

			tw_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			fif_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			others.setBackgroundResource(R.drawable.shape_main_list_row);

			String camount = contractAmount.replace("$", "");
			camount = camount.replace(",", "");
			if (camount == null || camount.equals("")) {
				camount = "0.00";
			}

			rAmount = Float.valueOf(camount) * ((float) 100) / (float) 100;

			String amountValue = addDollerSymbol(""
					+ new BigDecimal(rAmount).setScale(2,
							BigDecimal.ROUND_HALF_UP));
			p_amount.setText(amountValue);

			// p_amount.setText("$"
			// + new BigDecimal(rAmount).setScale(2, BigDecimal.ROUND_HALF_UP));

			paymentConfigAsyncTask = new PaymentConfigAsyncTask().execute();
			break;
		case R.id.imageViewGenerateProposal:
			layoutProposalContent.setVisibility(View.GONE);
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			layoutcontent.setVisibility(View.VISIBLE);
			addphoto.setVisibility(View.GONE);
			cus_attachment_listview.setVisibility(View.GONE);
			cus_appointment_scrollview.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			setMenuNormal(txtOldMenu);
			setMenuSelected(txtMenu6);
			LoadSalesResults();
			// DispoSubDispo();
			break;

		case R.id.addphoto:
			Intent galleryIntent = new Intent(
					CustomerAppointmentsActivity.this,
					GalleryMainActivity.class);
			galleryIntent.putExtra("CustomerId", customerId);
			galleryIntent.putExtra("From", "SalesProcess");
			startActivityForResult(galleryIntent, 1000);
			break;
		case R.id.tw_percent:
			tw_percent
			.setBackgroundResource(R.drawable.shape_main_list_row_pressed);
			fif_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			hun_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			others.setBackgroundResource(R.drawable.shape_main_list_row);
			showPercentageDialog(20);
			break;

		case R.id.fif_percent:
			tw_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			fif_percent
			.setBackgroundResource(R.drawable.shape_main_list_row_pressed);
			hun_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			others.setBackgroundResource(R.drawable.shape_main_list_row);
			showPercentageDialog(50);
			break;

		case R.id.hun_percent:
			tw_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			fif_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			hun_percent
			.setBackgroundResource(R.drawable.shape_main_list_row_pressed);
			others.setBackgroundResource(R.drawable.shape_main_list_row);
			showPercentageDialog(100);
			break;

		case R.id.others:
			tw_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			fif_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			hun_percent.setBackgroundResource(R.drawable.shape_main_list_row);
			others.setBackgroundResource(R.drawable.shape_main_list_row_pressed);
			showPercentageDialog(0);
			break;

		case R.id.cash:
			checkClickValue = 0;
			cashchecknumber.setVisibility(View.GONE);
			payment_number_edit.setVisibility(View.GONE);
			financing_company_spinner.setVisibility(View.GONE);
			payment_number_edit.setText("");
			payment_done.setVisibility(View.VISIBLE);
			cash.setBackgroundResource(R.drawable.cash_click);
			check.setBackgroundResource(R.drawable.check);
			crietcard.setBackgroundResource(R.drawable.credit_card);
			financing.setBackgroundResource(R.drawable.financing);
			int cashPosition = methodArray.indexOf("Cash");
			currentValue = idArray.get(cashPosition);
			break;
		case R.id.check:
			int checkPosition = methodArray.indexOf("Check");
			currentValue = idArray.get(checkPosition);
			checkClickValue = 1;
			cashchecknumber.setVisibility(View.VISIBLE);
			payment_number_edit.setVisibility(View.VISIBLE);
			financing_company_spinner.setVisibility(View.GONE);
			payment_number_edit.setText("");
			cashchecknumber.setText("Check Number");
			payment_number_edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
			payment_number_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
			payment_done.setVisibility(View.VISIBLE);
			cash.setBackgroundResource(R.drawable.cash);
			check.setBackgroundResource(R.drawable.check_click);
			crietcard.setBackgroundResource(R.drawable.credit_card);
			financing.setBackgroundResource(R.drawable.financing);
			break;
		case R.id.crietcard:
			checkPosition = methodArray.indexOf("CreditCard");
			currentValue = idArray.get(checkPosition);
			checkClickValue = 2;
			cashchecknumber.setVisibility(View.GONE);
			payment_number_edit.setVisibility(View.GONE);
			financing_company_spinner.setVisibility(View.GONE);
			payment_number_edit.setText("");
			cash.setBackgroundResource(R.drawable.cash);
			check.setBackgroundResource(R.drawable.check);
			crietcard.setBackgroundResource(R.drawable.credit_card_click);
			financing.setBackgroundResource(R.drawable.financing);
			payment_done.setVisibility(View.GONE);

			//			Toast.makeText(getApplicationContext(), "Credit card payment is in progress.", Toast.LENGTH_LONG).show();

			Singleton.getInstance().strPaymentMethodId=currentValue;
			Intent creditIntent = new Intent(CustomerAppointmentsActivity.this,
					CreditCardMainActivity.class);
			creditIntent.putExtra("BalanceAmount", amount.getText().toString());
			creditIntent.putExtra("Amount", p_amount.getText().toString());
			creditIntent.putExtra("DelarAccountNumber", dealerAccountNumber);
			creditIntent.putExtra("DealerSecurityKey", dealerSecurityKey);
			startActivity(creditIntent);
			break;

		case R.id.financing:
			checkPosition = methodArray.indexOf("Financed");
			currentValue = idArray.get(checkPosition);
			checkClickValue = 3;
			cashchecknumber.setVisibility(View.VISIBLE);
			payment_number_edit.setVisibility(View.VISIBLE);
			financing_company_spinner.setVisibility(View.VISIBLE);
			payment_number_edit.setText("");
			cashchecknumber.setText("Authorization Number");
			payment_number_edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
			payment_number_edit.setInputType(InputType.TYPE_CLASS_TEXT);
			payment_done.setVisibility(View.VISIBLE);
			cash.setBackgroundResource(R.drawable.cash);
			check.setBackgroundResource(R.drawable.check);
			crietcard.setBackgroundResource(R.drawable.credit_card);
			financing.setBackgroundResource(R.drawable.financing_click);
			financingCompanyNameAsyncTask = new FinancingCompanyAsyncTask(
					CustomerAppointmentsActivity.this, dealerId).execute();
			break;

		case R.id.payment_done:
			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(c.getTime());
			jsonRequestText = new JSONObject();
			int curPosition = idArray.indexOf(currentValue);
			if (p_amount.getText().toString().trim().equalsIgnoreCase("")
					|| p_amount.getText().toString().trim()
					.equalsIgnoreCase(null)) {
				pamount = "0";
			} else {
				pamount = p_amount.getText().toString();
			}
			nameValuePairs = new ArrayList<JSONObject>();
			jsonResultText = new JSONObject();
			if (methodArray.get(curPosition).equalsIgnoreCase("Cash")) {
				for (int i = 0; i < 1; i++) {
					try {
						jsonRequestText.put(Constants.DEALERID, dealerId);
						jsonRequestText.put(Constants.APPOINTMENTID,
								appointmentId);
						jsonRequestText.put(Constants.EMPOLYEEID, employeeId);
						jsonRequestText.put(Constants.CUSTOMERID, customerId);
						jsonRequestText.put(Constants.AMOUNT,
								pamount.replace("$", "").replace(",", ""));
						jsonRequestText.put(Constants.PAYMENTTYPEID, "2");
						jsonRequestText.put(Constants.PAYMENTMETHODID,
								currentValue);
						jsonRequestText.put(Constants.CREDITTYPEID, "");
						jsonRequestText.put(Constants.CREDITBATCHNUMBER, "");
						jsonRequestText.put(Constants.CHECKNUMBER, "");
						jsonRequestText.put(Constants.DATE, date);

						jsonRequestText.put(Constants.APPOINTMENT_RESULT_ID,
								appntResultId);
						jsonRequestText
						.put(Constants.FINANCING_COMPANY_ID, "0");
						jsonRequestText.put(
								Constants.FINANCING_APPROVAL_NUMBER, "");

					} catch (JSONException e) {
						Log.e(Constants.KEY_ERROR, e.toString());
					}
					nameValuePairs.add(jsonRequestText);
				}

				savePaymentAsyncTask = new SavePaymentAsyncTask().execute();

				String amountValue2 = addDollerSymbol(""
						+ new BigDecimal(rAmount).setScale(2,
								BigDecimal.ROUND_HALF_UP));
				p_amount.setText(amountValue2);

				hun_percent
				.setBackgroundResource(R.drawable.shape_main_list_row_pressed);
				tw_percent
				.setBackgroundResource(R.drawable.shape_main_list_row);
				fif_percent
				.setBackgroundResource(R.drawable.shape_main_list_row);
				others.setBackgroundResource(R.drawable.shape_main_list_row);

				payment_number_edit.setText("");
			}else {
				for (int i = 0; i < 1; i++) {
					jsonRequestText = new JSONObject();
					try {
						jsonRequestText.put(Constants.DEALERID, dealerId);
						jsonRequestText.put(Constants.APPOINTMENTID,
								appointmentId);
						jsonRequestText.put(Constants.EMPOLYEEID, employeeId);
						jsonRequestText.put(Constants.CUSTOMERID, customerId);
						jsonRequestText.put(Constants.AMOUNT,
								pamount.replace("$", "").replace(",", ""));
						jsonRequestText.put(Constants.PAYMENTTYPEID, "2");
						jsonRequestText.put(Constants.PAYMENTMETHODID,
								currentValue);
						jsonRequestText.put(Constants.CREDITTYPEID, "");
						jsonRequestText.put(Constants.CREDITBATCHNUMBER, "");
						jsonRequestText.put(Constants.DATE, date);
						jsonRequestText.put(Constants.APPOINTMENT_RESULT_ID,
								appntResultId);

						if (methodArray.get(curPosition).equalsIgnoreCase(
								"Check")) {
							jsonRequestText.put(Constants.CHECKNUMBER,
									payment_number_edit.getText().toString());
							jsonRequestText.put(Constants.FINANCING_COMPANY_ID,
									"0");
							jsonRequestText.put(
									Constants.FINANCING_APPROVAL_NUMBER, "");
						} else if (methodArray.get(curPosition)
								.equalsIgnoreCase("Financed")) {
							jsonRequestText.put(Constants.CHECKNUMBER, "");

							jsonRequestText.put(Constants.FINANCING_COMPANY_ID,
									currentFinancingCompanyId);
							jsonRequestText.put(
									Constants.FINANCING_APPROVAL_NUMBER,
									payment_number_edit.getText().toString());
						}

					} catch (JSONException e) {
						Log.e(Constants.KEY_ERROR, e.toString());
					}
					nameValuePairs.add(jsonRequestText);
				}
				if (methodArray.get(curPosition).equalsIgnoreCase("Financed")) {
					if (currentFinancingCompanyId.equalsIgnoreCase("XXXX")
							|| currentFinancingCompanyId.equalsIgnoreCase("")
							|| currentFinancingCompanyId == null) {
						Toast.makeText(context,
								"Please Choose the Financing Company",
								Toast.LENGTH_SHORT).show();
					} else {
						savePaymentAsyncTask = new SavePaymentAsyncTask()
						.execute();
						payment_number_edit.setText("");
					}
				} else {
					savePaymentAsyncTask = new SavePaymentAsyncTask().execute();
					payment_number_edit.setText("");
				}

				String amountValue1 = addDollerSymbol(""
						+ new BigDecimal(rAmount).setScale(2,
								BigDecimal.ROUND_HALF_UP));
				p_amount.setText(amountValue1);

				// p_amount.setText("$"
				// + new BigDecimal(rAmount).setScale(2,
				// BigDecimal.ROUND_HALF_UP));
				hun_percent
				.setBackgroundResource(R.drawable.shape_main_list_row_pressed);
				tw_percent
				.setBackgroundResource(R.drawable.shape_main_list_row);
				fif_percent
				.setBackgroundResource(R.drawable.shape_main_list_row);
				others.setBackgroundResource(R.drawable.shape_main_list_row);

			}
			break;
		default:
			break;
		}
	}

	public static void loadFinancongCompanySpinner() {

		ArrayAdapter<String> financingSpinnerArrayAdapter = new ArrayAdapter<String>(
				context, android.R.layout.simple_spinner_item,
				Singleton.getInstance().mfinancingCompanyName);
		financingSpinnerArrayAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
		// drop
		// down
		// view
		financing_company_spinner.setAdapter(financingSpinnerArrayAdapter);
		// financingSpinnerArrayAdapter.notifyDataSetChanged();
	}

	private void showPercentageDialog(int value) {
		if (value == 20) {
			percentageValue(20, 0);
		} else if (value == 50) {
			percentageValue(50, 0);
		} else if (value == 100) {
			percentageValue(100, 0);
		} else {
			mCalendarNotesDialog = new Dialog(CustomerAppointmentsActivity.this);
			mCalendarNotesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mCalendarNotesDialog.setContentView(R.layout.percentage_popup);
			mCalendarNotesDialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			mCalendarNotesDialog.findViewById(R.id.cancel_button)
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCalendarNotesDialog.cancel();

				}
			});

			mCalendarNotesDialog.findViewById(R.id.add_button)
			.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCalendarNotesDialog.cancel();
					if (!amount_edit.getText().toString().equals(null)
							&& !amount_edit.getText().toString()
							.equals("")) {

						String amountValue = addDollerSymbol(""
								+ new BigDecimal(Float
										.valueOf(amount_edit.getText()
												.toString())).setScale(
														2, BigDecimal.ROUND_HALF_UP));
						p_amount.setText(amountValue);

						// p_amount.setText("$"
						// + new BigDecimal(Float
						// .valueOf(amount_edit.getText()
						// .toString())).setScale(
						// 2, BigDecimal.ROUND_HALF_UP));
					}
				}
			});

			percentage_edit = (EditText) mCalendarNotesDialog
					.findViewById(R.id.percentage_edit);
			percentage_edit.addTextChangedListener(perWatcher);
			percentage_edit.setHint("0");

			amount_edit = (EditText) mCalendarNotesDialog
					.findViewById(R.id.amount_edit);
			amount_edit.addTextChangedListener(amountWatcher);
			amount_edit.setHint("0.00");

			mCalendarNotesDialog.setCanceledOnTouchOutside(true);
			mCalendarNotesDialog.show();
		}
	}

	private void percentageValue(int cval, int dVal) {
		String camount = contractAmount.replace("$", "");
		camount = camount.replace(",", "");
		float rAmount = Float.valueOf(camount) * ((float) cval) / (float) 100;
		if (dVal == 0) {

			String amountValue = addDollerSymbol(""
					+ new BigDecimal(rAmount).setScale(2,
							BigDecimal.ROUND_HALF_UP));
			p_amount.setText(amountValue);

			// p_amount.setText("$"
			// + new BigDecimal(rAmount).setScale(2,
			// BigDecimal.ROUND_HALF_UP));
		} else {
			if (Float.valueOf(camount) >= rAmount
					&& Float.valueOf(camount) != 0.0) {

				amount_edit.setText(""
						+ new BigDecimal(Float.valueOf(rAmount)).setScale(2,
								BigDecimal.ROUND_HALF_UP));
				finalAmount = String.valueOf(rAmount);
				amount_edit.setSelection(amount_edit.getText().length());
			} else {
				Toast.makeText(getApplicationContext(),
						"Please enter valid data", Toast.LENGTH_LONG).show();
				percentage_edit.setText("");
			}
		}
	}

	// Text Watcher for Percentage
	TextWatcher perWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence charSequence, int start,
				int before, int count) {
			if (!charSequence.toString().equals(null)
					&& !charSequence.toString().equals("")) {
				String camount = contractAmount.replace("$", "");
				camount = camount.replace(",", "");
				if (Float.valueOf(camount) > 0) {
					if (perValue != Integer.parseInt(charSequence.toString())) {
						int cVal = Integer.valueOf(charSequence.toString());
						float rAmount = Float.valueOf(camount) * ((float) cVal)
								/ (float) 100;
						if (cVal < 101) {
							if (Float.valueOf(camount) >= rAmount
									&& Float.valueOf(camount) != 0.0) {
								amount_edit
								.setText(""
										+ new BigDecimal(Float
												.valueOf(rAmount))
										.setScale(
												2,
												BigDecimal.ROUND_HALF_UP));
								finalAmount = String.valueOf(rAmount);
								amount_edit.setSelection(amount_edit.getText()
										.length());

							} else {
								Toast.makeText(getApplicationContext(),
										"Please enter the valid Amount",
										Toast.LENGTH_LONG).show();
								// percentage_edit.setText(finalAmount);
							}
						} else {
							percentage_edit
							.removeTextChangedListener(perWatcher);
							percentage_edit.setText("" + perValue);
							percentage_edit.setSelection(String.valueOf(
									perValue).length());
							percentage_edit.addTextChangedListener(perWatcher);
						}
					} else {
					}
				} else {
					amount_edit.removeTextChangedListener(amountWatcher);
					percentage_edit.removeTextChangedListener(perWatcher);
					amount_edit.setText(null);
					amount_edit.setHint("0.00");
					percentage_edit.setText(null);
					percentage_edit.setHint("0");
					perValue = 0;
					amount_edit.addTextChangedListener(amountWatcher);
					percentage_edit.addTextChangedListener(perWatcher);
				}
			} else {
				amount_edit.removeTextChangedListener(amountWatcher);
				percentage_edit.removeTextChangedListener(perWatcher);
				amount_edit.setText(null);
				amount_edit.setHint("0.00");
				percentage_edit.setText(null);
				percentage_edit.setHint("0");
				perValue = 0;
				amount_edit.addTextChangedListener(amountWatcher);
				percentage_edit.addTextChangedListener(perWatcher);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	// Text Watcher for Amount
	TextWatcher amountWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence charSequence, int start,
				int before, int count) {
			if (!charSequence.toString().equals(null)
					&& !charSequence.toString().equals("")) {
				if (!finalAmount.equalsIgnoreCase(charSequence.toString())) {
					String camount = contractAmount.replace("$", "");
					camount = camount.replace(",", "");
					if(Utilities.isNumeric(charSequence.toString())){
						if (Float.valueOf(camount) >= Float.valueOf(charSequence
								.toString())) {
							float percentage = (Float.valueOf(charSequence
									.toString()) * 100 / (Float.valueOf(camount)));
							int p = (int) Math.round(percentage);
							perValue = p;
							percentage_edit.setText("" + p);
							percentage_edit
							.setSelection(String.valueOf(p).length());
							finalAmount = String.valueOf(charSequence.toString());
						} else {
							amount_edit.removeTextChangedListener(amountWatcher);
							amount_edit.setText(""
									+ new BigDecimal(Float.valueOf(finalAmount))
									.setScale(2, BigDecimal.ROUND_HALF_UP));

							amount_edit
							.setSelection(amount_edit.getText().length());
							amount_edit.addTextChangedListener(amountWatcher);
							// perValue = 0;
						}
					}

				} else {
					perValue = 0;
				}
			} else {
				percentage_edit.removeTextChangedListener(perWatcher);
				amount_edit.removeTextChangedListener(amountWatcher);
				percentage_edit.setText(null);
				percentage_edit.setHint("0");
				amount_edit.setText(null);
				amount_edit.setHint("0.00");
				percentage_edit.addTextChangedListener(perWatcher);
				amount_edit.addTextChangedListener(amountWatcher);
				perValue = 0;
				finalAmount = "0.0";
			}
		}

		@Override
		public void beforeTextChanged(CharSequence charSequence, int start,
				int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	private void addDynamicView() {
		for (int i = 0; i < allBit.size(); i++) {

			LinearLayout horiLayout = new LinearLayout(this);
			LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutParams1.setMargins(
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five));
			horiLayout.setLayoutParams(layoutParams1);
			horiLayout.setOrientation(LinearLayout.HORIZONTAL);
			horiLayout.setBackgroundResource(R.drawable.gallary_border);
			bodyLayout.addView(horiLayout);

			ImageView imageView = new ImageView(this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					(int) getResources().getDimension(R.dimen.sixty),
					(int) getResources().getDimension(R.dimen.sixty));
			layoutParams.setMargins(
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five),
					(int) getResources().getDimension(R.dimen.five));
			// imageLoader.displayImage("file://" +selectUrl.get(i), imageView,
			// options);
			imageView.setImageBitmap(allBit.get(i));
			imageView.setLayoutParams(layoutParams);
			horiLayout.addView(imageView);

			editText = new EditText(this);
			if (uploadPhotoArray.size() - 1 == i) {
				editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			} else {
				editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			}

			editText.setSingleLine(true);
			editText.setId(editTextTag + i);
			LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
					0, LayoutParams.MATCH_PARENT, 1);
			editParams.gravity = Gravity.CENTER_VERTICAL;
			editParams.setMargins(
					(int) getResources().getDimension(R.dimen.fifteen),
					(int) getResources().getDimension(R.dimen.fifteen),
					(int) getResources().getDimension(R.dimen.fifteen),
					(int) getResources().getDimension(R.dimen.fifteen));
			editText.setLayoutParams(editParams);
			editText.setPadding(50, 50, 50, 50);
			editText.setBackgroundResource(R.drawable.galleryuploadedit);
			editText.setHint("description");
			horiLayout.addView(editText);
		}
	}

	public Bitmap resizeBitmap(String url, int targetW, int targetH) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		return BitmapFactory.decodeFile(url, bmOptions);
	}

	private Handler messageHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Toast.makeText(getApplicationContext(),
					Constants.NETWORK_NOT_AVAILABLE, Toast.LENGTH_LONG).show();
			Log.e("dbHandler.getCount() ====> ", "" + dbHandler.getCount());
		}
	};

	class UploadImages extends AsyncTask<Void, Integer, String> {
		int count, uploadedCount;

		public UploadImages() {
			progressBar = new ProgressDialog(CustomerAppointmentsActivity.this);
			progressBar.setCancelable(false);
			progressBar.setMessage("File Uploading ...");
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

			count = uploadPhotoArray.size();
			progressBar.setProgress(0);
			progressBar.setMax(count);
		}

		@Override
		protected void onPreExecute() {
			progressBar.show();
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.d("Values", "val" + values[0]);
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(Void... params) {
			count = uploadPhotoArray.size();
			for (int i = 0; i < count; i++) {
				try {
					int percent = i + 1;
					publishProgress(percent);
					if (Constants.isNetworkConnected(getApplicationContext())) {
						result = new com.webparadox.bizwizsales.helper.HttpConnection()
						.sendPhoto(Integer.parseInt(dealerId),
								Integer.parseInt(customerId),
								Integer.parseInt(employeeId), "",
								discriptionForImage.get(i), 1,
								uploadPhotoArray.get(i));
						if (result != null) {
							if (result.equalsIgnoreCase("1")) {
								//dbHandler.removeRow(statusIdArray.get(i));
								dbHandler.removeCommonRow(Constants.TABLE_TEST, Constants.KEY_ID, statusIdArray.get(i));
								uploadedCount++;
							}
						}

					} else {
						// messageHandler.sendEmptyMessage(0);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		// GalleryUploadActivity.java
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// statusIdArray.clear();
			progressBar.dismiss();
			if (uploadedCount > 1) {
				Toast.makeText(getApplicationContext(),
						uploadedCount + " images uploaded out of " + count,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),
						uploadedCount + " image uploaded out of " + count,
						Toast.LENGTH_LONG).show();
			}

			bodyLayout.removeAllViews();
			layoutcontent.setVisibility(View.VISIBLE);
			layoutProductContent.setVisibility(View.GONE);
			uploadLayout.setVisibility(View.GONE);
			uploadPhotoArray.clear();
			discriptionForImage.clear();
			allBit.clear();
			statusIdArray.clear();

			// initProducts();
			loadProposalNew();
		}
	}

	private void LoadSalesResults() {
		bodyLayout.removeAllViews();
		// loadDispoSupDispo();
		proposalAsyncTask = new ProposalAsynTask(context, dealerId,
				appointmentResultId);
		proposalAsyncTask.execute();
		// DispoSubDispo();

	}

	// private void loadDispoSupDispo() {
	// dispoSubDispo = new DispoSubDispoAsynctask(context, dealerId);
	// dispoSubDispo.execute();
	// }

	public void loadApptTypeLeadType_spinner() {
		// Add spinner
		bodyLayout.removeAllViews();

		int three = (int) getResources().getDimension(R.dimen.three);
		int five = (int) getResources().getDimension(R.dimen.five);

		spinnerLayout = new LinearLayout(context);
		LinearLayout.LayoutParams lpspinner = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpspinner.setMargins(three, three, three, three);
		spinnerLayout.setLayoutParams(lpspinner);
		spinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
		spinnerLayout.setWeightSum(2);
		// spinner 1
		Spinner Spinner1 = new Spinner(context);
		LinearLayout.LayoutParams lpAnswer1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpAnswer1.setMargins(five, five, five, five);
		lpAnswer1.weight = 1;
		Spinner1.setLayoutParams(lpAnswer1);
		Spinner1.setId(spinner1Id);
		Spinner1.setOnItemSelectedListener(this);
		ArrayList<String> sp1 = new ArrayList<String>();
		for (int ll = 0; ll < Singleton.getInstance().appointmentModel.size(); ll++) {
			sp1.add(Singleton.getInstance().appointmentModel.get(ll).id);
		}
		ArrayAdapter<AppointmentTypeModel> ptspinnerArrayAdapter1 = new ArrayAdapter<AppointmentTypeModel>(
				context, android.R.layout.simple_spinner_item,
				Singleton.getInstance().appointmentModel);
		ptspinnerArrayAdapter1
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner1.setAdapter(ptspinnerArrayAdapter1);
		int curPosition = sp1.indexOf(AppointmentTypeId);
		Spinner1.setSelection(curPosition);
		Log.d("AppointmentTypeId", AppointmentTypeId);
		spinnerLayout.addView(Spinner1);

		// spinner 2
		Spinner Spinner2 = new Spinner(context);
		LinearLayout.LayoutParams lpAnswer2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpAnswer2.setMargins(five, five, five, five);
		lpAnswer2.weight = 1;
		Spinner2.setLayoutParams(lpAnswer2);
		Spinner2.setId(spinner2Id);
		Spinner2.setOnItemSelectedListener(this);

		ArrayList<String> sp2 = new ArrayList<String>();
		for (int ll = 0; ll < Singleton.getInstance().leadTypeModel.size(); ll++) {
			sp2.add(Singleton.getInstance().leadTypeModel.get(ll).id);
		}
		ArrayAdapter<LeadTypeModel> ptspinnerArrayAdapter2 = new ArrayAdapter<LeadTypeModel>(
				context, android.R.layout.simple_spinner_item,
				Singleton.getInstance().leadTypeModel);
		ptspinnerArrayAdapter2
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner2.setAdapter(ptspinnerArrayAdapter2);
		int curPosition2 = sp2.indexOf(LeadTypeId);
		Spinner2.setSelection(curPosition2);
		// Spinner1.setSelection(Singleton.getInstance().leadTypeModel.indexOf(Singleton.getInstance().leadTypeModel.get(Integer.parseInt(LeadTypeId)).id));

		Log.d("LeadTypeId", LeadTypeId);
		spinnerLayout.addView(Spinner2);
		bodyLayout.addView(spinnerLayout);
	}

	public ArrayList<JSONObject> saveRequest() {

		Log.d("size", edAnswerList.size() + "");
		ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();

		for (int l = 0; l < Singleton.getInstance().appointmentQuestionnaireModel
				.size(); l++) {
			JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
				reqObj_data.put(Constants.KEY_APPOINTMENT_ID, appointmentId);
				reqObj_data.put(Constants.QUESTION_ID,
						Singleton.getInstance().appointmentQuestionnaireModel
						.get(l).QuestionId);
				reqObj_data.put(Constants.RESPONSE_ID,
						Singleton.getInstance().appointmentQuestionnaireModel
						.get(l).ResponseId);

				if (Singleton.getInstance().appointmentQuestionnaireModel
						.get(l).QuestionType.equals("Textbox")) {
					reqObj_data
					.put(Constants.RESPONSE_CHOICE_ID,
							Singleton.getInstance().appointmentQuestionnaireModel
							.get(l).ResponseChoiceId);
					reqObj_data.put(Constants.RESPONSE_TEXT, edAnswerList
							.get(l).getText().toString());
				} else if (Singleton.getInstance().appointmentQuestionnaireModel
						.get(l).QuestionType.equals("Dropdown")) {
					reqObj_data.put(Constants.RESPONSE_CHOICE_ID,
							spAnswerIdList.get(l));
					reqObj_data.put(Constants.RESPONSE_TEXT,
							spAnswerList.get(l));
				} else if (Singleton.getInstance().appointmentQuestionnaireModel
						.get(l).QuestionType.equals("RadioButton")) {
					reqObj_data.put(Constants.RESPONSE_CHOICE_ID,
							raAnswerIdList.get(l));
					reqObj_data.put(Constants.RESPONSE_TEXT,
							raAnswerList.get(l));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRequestJson.add(reqObj_data);
		}
		Log.d("res", mRequestJson.toString());
		return mRequestJson;

	}

	public ArrayList<JSONObject> leadsaveRequest() {

		Log.d("size", edAnswerList.size() + "");
		ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();

		for (int l = 0; l < Singleton.getInstance().leadQuestionaryModel.size(); l++) {
			JSONObject reqObj_data = new JSONObject();
			try {
				reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
				reqObj_data.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
				reqObj_data.put(Constants.KEY_APPOINTMENT_ID, appointmentId);
				reqObj_data
				.put(Constants.QUESTION_ID,
						Singleton.getInstance().leadQuestionaryModel
						.get(l).questionId);
				if (Singleton.getInstance().leadQuestionaryModel.get(l).responseId
						.length() > 0) {
					reqObj_data
					.put(Constants.RESPONSE_ID,
							Singleton.getInstance().leadQuestionaryModel
							.get(l).responseId);
				} else {
					reqObj_data.put(Constants.RESPONSE_ID, "0");
				}

				if (Singleton.getInstance().leadQuestionaryModel.get(l).questionType
						.equals("Textbox")) {
					reqObj_data
					.put(Constants.RESPONSE_CHOICE_ID,
							Singleton.getInstance().leadQuestionaryModel
							.get(l).responseChoiceId);
					reqObj_data.put(Constants.RESPONSE_TEXT, leadEdAnswerList
							.get(l).getText().toString());
				} else if (Singleton.getInstance().leadQuestionaryModel.get(l).questionType
						.equals("Dropdown")) {
					reqObj_data.put(Constants.RESPONSE_CHOICE_ID,
							leadAnswerIdList.get(l));
					reqObj_data.put(Constants.RESPONSE_TEXT,
							leadAnswerList.get(l));
				} else if (Singleton.getInstance().leadQuestionaryModel.get(l).questionType
						.equals("RadioButton")) {
					reqObj_data.put(Constants.RESPONSE_CHOICE_ID,
							leadraAnswerIdList.get(l));
					reqObj_data.put(Constants.RESPONSE_TEXT,
							leadraAnswerList.get(l));
				}
				reqObj_data
				.put(Constants.KEY_ADDPROSPECT_TYPE, Singleton
						.getInstance().leadQuestionaryModel.get(l).type);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mRequestJson.add(reqObj_data);
		}
		Log.d("res", mRequestJson.toString());
		return mRequestJson;

	}

	public void appointmentQuestioninterface() {
		// TODO Auto-generated method stub
		bodyLayout.removeAllViews();
		customLayout.removeAllViews();
		bodyLayout.addView(spinnerLayout);
		Log.d("Count = ", "" + bodyLayout.getChildCount());
		if (Singleton.getInstance().appointmentQuestionnaireModel.size() > 0) {
			Log.d("Count = ", "" + bodyLayout.getChildCount());
			int three = (int) getResources().getDimension(R.dimen.three);
			int five = (int) getResources().getDimension(R.dimen.five);
			int twenty = (int) getResources().getDimension(R.dimen.twenty);

			// Header textview
			TextView headerTv = new TextView(context);
			headerTv.setText(Constants.setp2labelString);
			headerTv.setTypeface(droidSansBold, Typeface.BOLD);
			// headerTv.setTextSize(getResources().getDimension(R.dimen.txt8));
			headerTv.setTextColor(getResources().getColor(R.color.black));
			customLayout.addView(headerTv);
			// Add questions and answer
			for (int i = 0; i < Singleton.getInstance().appointmentQuestionnaireModel
					.size(); i++) {
				String mResponseChoiseId = Singleton.getInstance().appointmentQuestionnaireModel.get(i).ResponseChoiceId;
				TextView tvQuestion = new TextView(getApplicationContext());
				LinearLayout.LayoutParams lpQuestions = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lpQuestions.setMargins(three, three, three, three);
				tvQuestion.setLayoutParams(lpQuestions);
				tvQuestion
				.setText(Singleton.getInstance().appointmentQuestionnaireModel
						.get(i).Question);
				tvQuestion.setTypeface(droidSans);
				// tvQuestion.setTextSize(getResources().getDimension(R.dimen.txt8));
				tvQuestion.setTextColor(getResources().getColor(R.color.black));
				customLayout.addView(tvQuestion);
				if (Singleton.getInstance().appointmentQuestionnaireModel
						.get(i).QuestionType.equals("Textbox")) {
					edAnswer = new EditText(context);
					LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lpAnswer.setMargins(five, five, five, five);
					edAnswer.setLayoutParams(lpAnswer);
					edAnswer.setLines(2);
					edAnswer.setId(i);
					edAnswerList.put(i, edAnswer);
					// edAnswer.setTextSize(getResources().getDimension(R.dimen.txt8));
					edAnswer.setText(Singleton.getInstance().appointmentQuestionnaireModel
							.get(i).ResponseText);
					edAnswer.setPadding(five, five, five, five);
					edAnswer.setBackground(getResources().getDrawable(
							R.drawable.resolve_text_background));
					edAnswer.setTypeface(droidSans);
					edAnswer.setGravity(Gravity.TOP);
					edAnswer.setTextColor(getResources()
							.getColor(R.color.black));
					customLayout.addView(edAnswer);

				} else if (Singleton.getInstance().appointmentQuestionnaireModel
						.get(i).QuestionType.equals("Dropdown")) {
					int tempselectioPosition = 0;
					Spinner ansSpinner = new Spinner(context);
					LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lpAnswer.setMargins(five, five, five, five);
					ansSpinner.setLayoutParams(lpAnswer);
					ansSpinner.setId(i);
					ansSpinner.setOnItemSelectedListener(this);
					ArrayList<String> spinnerData = new ArrayList<String>();
					Log.d("Size",
							Singleton.getInstance().appointmentQuestionOptionModel
							.get(Singleton.getInstance().appointmentQuestionnaireModel
									.get(i).QuestionId).size()
									+ "");
					for (int j = 0; j < Singleton.getInstance().appointmentQuestionOptionModel
							.get(Singleton.getInstance().appointmentQuestionnaireModel
									.get(i).QuestionId).size(); j++) {
						spinnerData
						.add(Singleton.getInstance().appointmentQuestionOptionModel
								.get(Singleton.getInstance().appointmentQuestionnaireModel
										.get(i).QuestionId).get(j).ChoiceText);
						if (mResponseChoiseId.equals(Singleton.getInstance().appointmentQuestionOptionModel
								.get(Singleton.getInstance().appointmentQuestionnaireModel
										.get(i).QuestionId).get(j).Id)) {
							tempselectioPosition=j;
						}
					}
					
					if(spinnerData.size()>0){
						ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
								context, android.R.layout.simple_spinner_item,
								spinnerData);
						ptspinnerArrayAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						ansSpinner.setAdapter(ptspinnerArrayAdapter);
						selectedItem = spinnerData.get(tempselectioPosition);
						ansSpinner.setSelection(tempselectioPosition);
						/*selectedItem = String
								.valueOf(spinnerData.indexOf(Singleton
										.getInstance().appointmentQuestionnaireModel
										.get(i).ResponseText));*/

						Log.d("selected ITem", selectedItem + "");
						/*if (selectedItem != null) {
							ansSpinner.setSelection(Integer.parseInt(selectedItem));
						}*/
					}
					customLayout.addView(ansSpinner);
				} else if (Singleton.getInstance().appointmentQuestionnaireModel
						.get(i).QuestionType.equals("RadioButton")) {
					LinearLayout radioLayout = new LinearLayout(context);
					LinearLayout.LayoutParams rglayout = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					radioLayout.setLayoutParams(rglayout);
					RadioGroup rg = new RadioGroup(context);
					// rg.setId(i);
					rg.setId(i - 1);
					LinearLayout.LayoutParams rgAnswer = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					rgAnswer.setMargins(five, five, five, five);
					rg.setLayoutParams(rgAnswer);
					rg.setOrientation(RadioGroup.HORIZONTAL);
					// rg.setOnCheckedChangeListener(this);

					RadioButton[] rb = new RadioButton[Singleton.getInstance().appointmentQuestionRadioModel
					                                   .get(Singleton.getInstance().appointmentQuestionnaireModel
					                                		   .get(i).QuestionId).size()];
					rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {
							// TODO Auto-generated method stub
							raAnswerIdList.put(group.getId() + 1,
									String.valueOf(checkedId));
							raAnswerList.put(group.getId() + 1, String
									.valueOf(((RadioButton) raAnswers
											.get(checkedId)).getText()));

						}
					});

					for (int m = 0; m < Singleton.getInstance().appointmentQuestionRadioModel
							.get(Singleton.getInstance().appointmentQuestionnaireModel
									.get(i).QuestionId).size(); m++) {
						rb[m] = new RadioButton(context);
						rb[m].setId(m);
						raAnswers.put(m, rb[m]);
						LinearLayout.LayoutParams raAnswer = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						raAnswer.weight = 1;
						rb[m].setLayoutParams(raAnswer);
						rb[m].setText(Singleton.getInstance().appointmentQuestionRadioModel
								.get(Singleton.getInstance().appointmentQuestionnaireModel
										.get(i).QuestionId).get(m).ChoiceText);
						if (rb[m]
								.getText()
								.toString()
								.equals(Singleton.getInstance().appointmentQuestionnaireModel
										.get(i).ResponseText)) {
							rb[m].setChecked(true);
						}
						rg.addView(rb[m]);

					}
					radioLayout.addView(rg);
					customLayout.addView(radioLayout);
				}

			}
			// Save button
			Button saveButton = new Button(context);
			LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, (int) getResources()
					.getDimension(R.dimen.forty));
			lpAnswer.setMargins(five, five, five, five);
			lpAnswer.gravity = Gravity.CENTER;
			saveButton.setLayoutParams(lpAnswer);
			saveButton.setId(1);
			saveButton.setOnClickListener(this);
			// saveButton.setTextSize(getResources().getDimension(R.dimen.txt8));
			saveButton.setText(getResources().getString(R.string.save));
			saveButton.setPadding(twenty, five, twenty, five);
			saveButton.setBackground(getResources().getDrawable(
					R.drawable.selector_prospect_save_button));
			saveButton.setTypeface(droidSansBold);
			saveButton.setGravity(Gravity.CENTER);
			saveButton.setTextColor(getResources().getColor(R.color.white));
			customLayout.addView(saveButton);
			bodyLayout.addView(customLayout);
			cusAppointmentScrollview.scrollTo(1, 0);

		}

		// checkedSpinner1 = 0;

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		if (parent.getId() == spinner1Id) {
			checkedSpinner1 += 1;
			if (checkedSpinner1 > 1) {
				AppointmentTypeId = Singleton.getInstance().appointmentModel
						.get(position).id;
				settingsEditor.putInt("appointmentSelected", Integer
						.parseInt(Singleton.getInstance().appointmentModel
								.get(position).id));
				settingsEditor.commit();
				AppointmentQuestionnaireAsyncTask appointmentQuestionireTask = new AppointmentQuestionnaireAsyncTask(
						context, appointmentId, dealerId, LeadTypeId,
						AppointmentTypeId);
				appointmentQuestionireTask.execute();
			}

			// Toast.makeText(context, "Spi1", 4).show();
		} else if (parent.getId() == spinner2Id) {

			checkedSpinner2 += 1;
			if (checkedSpinner2 > 1) {
				LeadTypeId = Singleton.getInstance().leadTypeModel
						.get(position).id;
				settingsEditor.putInt("leadSelected", Integer
						.parseInt(Singleton.getInstance().leadTypeModel
								.get(position).id));
				settingsEditor.commit();
				AppointmentQuestionnaireAsyncTask appointmentQuestionireTask = new AppointmentQuestionnaireAsyncTask(
						context, appointmentId, dealerId, LeadTypeId,
						AppointmentTypeId);
				appointmentQuestionireTask.execute();
			}

		}

		else if (parent.getId() == financial_company_spinner_id) {
			if (Singleton.getInstance().mfinancingCompanyId.size() > position) {
				currentFinancingCompanyId = Singleton.getInstance().mfinancingCompanyId
						.get(position);
			}
		}

		else {
			Log.d("SPINNER ID", parent.getId() + "");
			if ((TextView) parent.getChildAt(0) != null) {
				((TextView) parent.getChildAt(0)).setTypeface(droidSans);
				((TextView) parent.getChildAt(0)).setPadding(0, 0, 0, 0);
			}
			spAnswerList.put(parent.getId(), parent.getItemAtPosition(position)
					.toString());

			if (Singleton.getInstance().appointmentQuestionOptionModel.size() > 0) {
				Log.d("sp Size",
						Singleton.getInstance().appointmentQuestionOptionModel
						.get(Singleton.getInstance().appointmentQuestionnaireModel
								.get(parent.getId()).QuestionId).size()
								+ "");
				for (int j = 0; j < Singleton.getInstance().appointmentQuestionOptionModel
						.get(Singleton.getInstance().appointmentQuestionnaireModel
								.get(parent.getId()).QuestionId).size(); j++) {
					if (Singleton.getInstance().appointmentQuestionOptionModel
							.get(Singleton.getInstance().appointmentQuestionnaireModel
									.get(parent.getId()).QuestionId).get(j).ChoiceText
									.equals(parent.getItemAtPosition(position)
											.toString())) {
						String i = Singleton.getInstance().appointmentQuestionOptionModel
								.get(Singleton.getInstance().appointmentQuestionnaireModel
										.get(parent.getId()).QuestionId).get(j).Id;
						spAnswerIdList.put(parent.getId(), i);

					}

				}
			}

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}

	public void initProductsView() {
		// TODO Auto-generated method stub
		LinearLayout layoutcatHeader;
		layoutcatHeader = (LinearLayout) viewProducts
				.findViewById(R.id.productpickerLayout);
		layoutcatHeader.setVisibility(View.VISIBLE);

		Button generateProposal = (Button) viewProducts
				.findViewById(R.id.generate_proposal_btn);
		generateProposal.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getProposalAsyncTask = new GetProposalAsyncTask(context,
						dealerId, appointmentResultId, "customerAppointment");
				getProposalAsyncTask.execute();
			}
		});

		gridViewProductsCategory
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intentSubcat = new Intent(context,
						SalesProcessProductSubCategoryActivity.class);
				intentSubcat.putExtra("CatIndex", position);
				intentSubcat.putExtra("CategoryName", ((TextView) view
						.findViewById(R.id.textViewProductName))
						.getText().toString());
				startActivity(intentSubcat);

			}

		});

		gridViewRecentProducts
		.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intentUpdateProposal = new Intent(context,
						UpdateProposalActivity.class);
				intentUpdateProposal.putExtra(
						"SubCatName",
						mRecentProductsList.get(position).mSubcategoryName);
				intentUpdateProposal.putExtra(
						"SubCatId",
						mRecentProductsList.get(position).mSubcategoryId);
				intentUpdateProposal.putExtra("MaterialName",
						mRecentProductsList.get(position).mMaterialName);
				intentUpdateProposal.putExtra("MaterialId",
						mRecentProductsList.get(position).mMaterialId);
				intentUpdateProposal
				.putExtra(
						"ProductDescription",
						mRecentProductsList.get(position).mProductDescription);
				intentUpdateProposal
				.putExtra(
						"ProductImageURL",
						mRecentProductsList.get(position).mProductImageURL);
				intentUpdateProposal
				.putExtra(
						"ProductVideoURL",
						mRecentProductsList.get(position).mProductVideoURL);
				intentUpdateProposal
				.putExtra(
						"UnitSellingPrice",
						mRecentProductsList.get(position).mUnitSellingPrice);
				intentUpdateProposal.putExtra("ProductType",
						mRecentProductsList.get(position).mProductType);
				startActivity(intentUpdateProposal);

			}

		});

	}

	private void loadProducts() {
		// TODO Auto-generated method stub
		if (Constants.isProposalList) {
			Intent proposalSummaryIntent = new Intent(context,
					NewProposalSummaryActivity.class);
			startActivity(proposalSummaryIntent);

		} else {
			initProductsView();
			spProductsAsyncTask = new SpProductsAsyncTask(
					CustomerAppointmentsActivity.this,
					gridViewProductsCategory, dealerId);
			spProductsAsyncTask.execute();
			SalesProcessProductGridViewAdapter salesProcessProductGridAdapter = new SalesProcessProductGridViewAdapter(
					this, mRecentProductsList, true);
			gridViewRecentProducts.setAdapter(salesProcessProductGridAdapter);
		}

	}

	public void loadProposalNew() {
		Constants.isProposal = true;
		layoutcontent.setVisibility(View.GONE);
		uploadLayout.setVisibility(View.GONE);
		setMenuNormal(txtOldMenu);
		setMenuSelected(txtMenu4);
		layoutProposalContent.setVisibility(View.VISIBLE);
		layoutProductContent.setVisibility(View.GONE);
		getProposalAsyncTask = new GetProposalAsyncTask(context, dealerId,
				appointmentResultId, proposalListview, "check", productTotal);
		getProposalAsyncTask.execute();
	}

	@SuppressWarnings("unchecked")
	private void initProducts() {
		// TODO Auto-generated method stub
		if (preferenceSettings.contains(Constants.PREF_RECENT_PRODUCT_KEY)) {
			mRecentProductsList.clear();
			try {
				mRecentProductsList = (ArrayList<SpProductSubCatAndMaterialModel>) mObjetserilizer
						.deserialize(preferenceSettings
								.getString(
										Constants.PREF_RECENT_PRODUCT_KEY,
										mObjetserilizer
										.serialize(new ArrayList<SpProductSubCatAndMaterialModel>())));
				Collections.reverse(mRecentProductsList);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		layoutcontent.setVisibility(View.GONE);
		uploadLayout.setVisibility(View.GONE);
		layoutProductContent.setVisibility(View.VISIBLE);
		layoutProductContent.removeAllViews();
		layoutProductContent.addView(viewProducts);
		gridViewProductsCategory = (GridView) viewProducts
				.findViewById(R.id.gridViewProducts);
		gridViewRecentProducts = (GridView) viewProducts
				.findViewById(R.id.gridViewRecentProducts);
		setMenuNormal(txtOldMenu);
		setMenuSelected(txtMenu4);
		Constants.isSelectProduct = false;
		loadProducts();
	}

	public void setMenuSelected(TextView NewMenu) {
		android.view.ViewGroup.LayoutParams newParams = NewMenu
				.getLayoutParams();
		if (getResources().getBoolean(R.bool.is_tablet)) {
			newParams.height = (int) getResources().getDimension(
					R.dimen.sevnteen);
			newParams.width = (int) getResources().getDimension(
					R.dimen.sevnteen);
		} else {
			newParams.height = (int) getResources().getDimension(
					R.dimen.twentytwo);
			newParams.width = (int) getResources().getDimension(
					R.dimen.twentytwo);
		}
		NewMenu.setLayoutParams(newParams);
		NewMenu.setTextColor(Color.WHITE);
		NewMenu.setBackground(getResources().getDrawable(
				R.drawable.shape_circle_sales_process_selected));
		txtOldMenu = NewMenu;
	}

	public void setMenuNormal(TextView oldMenu) {
		android.view.ViewGroup.LayoutParams oldParams = oldMenu
				.getLayoutParams();
		if (getResources().getBoolean(R.bool.is_tablet)) {
			oldParams.height = (int) getResources()
					.getDimension(R.dimen.twelve);
			oldParams.width = (int) getResources().getDimension(R.dimen.twelve);
		} else {
			oldParams.height = (int) getResources().getDimension(
					R.dimen.fifteen);
			oldParams.width = (int) getResources()
					.getDimension(R.dimen.fifteen);
		}
		oldMenu.setLayoutParams(oldParams);
		oldMenu.setTextColor(Color.BLACK);
		oldMenu.setBackground(getResources().getDrawable(
				R.drawable.shape_circle_sales_process_normal));
	}

	public class EditCustomerAppointmentDialog extends Dialog implements
	android.view.View.OnClickListener {

		Spinner TypeSpinner, intervalSpinner, leadTypeSpinner;
		Context ctx;
		String selectedItem, leadType;
		TextView text_title;

		ArrayList<JSONObject> reqArrPro;

		Button btn_save;
		LinearLayout layout_time, layout_leadtype;

		public EditCustomerAppointmentDialog(Context context)
				throws java.text.ParseException {
			super(context, android.R.style.Theme_Translucent_NoTitleBar);
			// TODO Auto-generated constructor stub
			this.ctx = context;
			showDialog();
		}

		public void showDialog() throws java.text.ParseException {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams WMLP = getWindow().getAttributes();
			WMLP.gravity = Gravity.CENTER_VERTICAL;
			WMLP.dimAmount = 0.7f;
			getWindow().setAttributes(WMLP);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			setCancelable(true);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.add_appnts_dialog);
			layout_leadtype = (LinearLayout) findViewById(R.id.layout_leadtype);

			text_date = (TextView) findViewById(R.id.add_appnt_date);
			text_time = (TextView) findViewById(R.id.add_appnt_time);

			text_title = (TextView) findViewById(R.id.text_addcustomernotes);
			layout_time = (LinearLayout) findViewById(R.id.layout_timer);
			text_title.setTypeface(droidSansBold);
			text_title.setText(selectedItem);

			String[] times = time.toString().split("-");

			DateFormat formatter2 = new SimpleDateFormat("MMM dd yyyy hh:mm a");
			Date date1 = formatter2.parse(date.replace(",", "") + " "
					+ times[0]);
			Date date2 = formatter2.parse(date.replace(",", "") + " "
					+ times[1]);
			String s = getTimeDiff(date1, date2);
			Log.d("Date & Time", date + "  " + time);
			text_date.setText(date);
			Log.d("Text_date", text_date.toString());
			// }
			text_time.setText(times[0]);
			// text_time.setHint(time);
			// Log.d("strSelectedDate", dat[0].substring(0, 3) + dat[1] +
			// date[1]);
			ImageView Btn_date = (ImageView) findViewById(R.id.add_appnt_date_img);
			ImageView Btn_time = (ImageView) findViewById(R.id.add_appnt_time_img);
			edittext_notes = (EditText) findViewById(R.id.textView1);
			edittext_notes.clearFocus();
			btn_save = (Button) findViewById(R.id.button_save_appnt);
			Button btn_cancel = (Button) findViewById(R.id.button_cancel_appnt);
			btn_cancel.setOnClickListener(this);
			intervalSpinner = (Spinner) findViewById(R.id.spinner_time_interval);
			TypeSpinner = (Spinner) findViewById(R.id.spinner_appnt_type);
			// leadTypeSpinner = (Spinner) findViewById(R.id.spinner_lead_type);
			// if (selectedItem.equals("Appointment")) {
			// TypeSpinner.setVisibility(View.VISIBLE);
			layout_time.setVisibility(View.VISIBLE);
			intervalSpinner.setVisibility(View.VISIBLE);
			btn_save.setOnClickListener(this);
			getAppointmentTime();
			btn_save.setClickable(false);
			Btn_date.setOnClickListener(this);
			Btn_time.setOnClickListener(this);

			btn_cancel.setOnClickListener(this);
			btn_save.setBackground(ctx.getResources().getDrawable(
					R.drawable.resolve_disable_background));
			edittext_notes.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					edittext_notes.setCursorVisible(true);
					return false;
				}
			});
			edittext_notes.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub
					if (s.toString().trim().length() > 0) {
						btn_save.setClickable(true);
						btn_save.setBackground(ctx.getResources().getDrawable(
								R.drawable.selector_prospect_save_button));

					} else {
						btn_save.setClickable(false);
						btn_save.setBackground(ctx.getResources().getDrawable(
								R.drawable.resolve_disable_background));
					}

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});

			ArrayAdapter<String> intervalAdapter = new ArrayAdapter<String>(
					ctx, R.layout.spinner_text, getResources().getStringArray(
							R.array.time_interval)) {

				public View getView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);

					((TextView) v).setTypeface(droidSans);

					return v;
				}

				public View getDropDownView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getDropDownView(position, convertView,
							parent);
					((TextView) v).setTypeface(droidSans);
					return v;
				}
			};

			intervalAdapter
			.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			intervalSpinner.setAdapter(intervalAdapter);

			if (minuteTime == 30) {
				intervalSpinner.setSelection(0);
			} else if (minuteTime == 60) {
				intervalSpinner.setSelection(1);
			} else if (minuteTime == 90) {
				intervalSpinner.setSelection(2);
			} else if (minuteTime == 120) {
				intervalSpinner.setSelection(3);
			} else if (minuteTime == 150) {
				intervalSpinner.setSelection(4);
			} else if (minuteTime == 180) {
				intervalSpinner.setSelection(5);
			} else if (minuteTime == 210) {
				intervalSpinner.setSelection(6);
			} else if (minuteTime == 240) {
				intervalSpinner.setSelection(7);
			}

			intervalSpinner
			.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO Auto-generated method stub
					if (position == 0) {
						timeInterval = "30";
					} else if (position == 1) {
						timeInterval = "60";
					} else if (position == 2) {
						timeInterval = "90";
					} else if (position == 3) {
						timeInterval = "120";
					} else if (position == 4) {
						timeInterval = "150";
					} else if (position == 5) {
						timeInterval = "180";
					} else if (position == 6) {
						timeInterval = "210";
					} else if (position == 7) {
						timeInterval = "240";
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});

		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch (v.getId()) {
			case R.id.button_cancel_appnt:
				EditDialog.dismiss();
				break;

			case R.id.add_appnt_time:
				getTime();
				break;

			case R.id.add_appnt_date:
				getDate();
				break;

			case R.id.add_appnt_time_img:

				getTime();
				break;

			case R.id.add_appnt_date_img:
				getDate();
				break;
			case R.id.button_save_appnt:
				Date da1 = null;
				Date da2 = null;
				isSuccess = false;
				isNoConflict = false;
				Log.d("AppntModelSize",
						Singleton.getInstance().appntDateTime.size() + "");
				Log.d("EventId", eventId);
				for (int i = 0; i < Singleton.getInstance().appntDateTime
						.size(); i++) {
					if (Singleton.getInstance().appntDateTime.get(i)
							.getAppntId().equals(eventId)) {
						Singleton.getInstance().appntDateTime.get(i)
						.setAppntId("");
						Singleton.getInstance().appntDateTime.get(i)
						.setApptTime("");
						Singleton.getInstance().appntDateTime.get(i)
						.setFormattedApptDate("");
					}
				}
				String newTime = null;
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(
							"MMM dd yyyy hh:mm aa");
					SimpleDateFormat sdf1 = new SimpleDateFormat("MMM dd yyyy");
					SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy");
					Date date = null;
					String txttime = text_date.getText().toString() + " "
							+ text_time.getText().toString();

					if (isValidFormat("dd MMM yyyy", text_date.getText()
							.toString())) {
						String ds2 = sdf1.format(sdf2.parse(text_date.getText()
								.toString().replace(",", "")));
						Log.d("ds2", ds2);
						txttime = ds2 + " " + text_time.getText().toString();
					} else if (isValidFormat("MMM dd yyyy", text_date.getText()
							.toString())) {
						txttime = text_date.getText().toString() + " "
								+ text_time.getText().toString();
					}
					try {
						date = sdf.parse(txttime.replace(",", ""));
						Calendar calendar = Calendar.getInstance();
						if (date != null)
							calendar.setTime(date);
						calendar.add(Calendar.MINUTE,
								Integer.valueOf(timeInterval));
						newTime = sdf.format(calendar.getTime());
						String formDate = text_date.getText().toString()
								.replace(",", "");
						if (Singleton.getInstance().appntDateTime.size() > 0) {
							for (AppointmentDateTimeModel model : Singleton
									.getInstance().appntDateTime) {
								if (!model.getFormattedApptDate().isEmpty()) {
									formDate = model.getFormattedApptDate();
								} else {
									formDate = text_date.getText().toString()
											.replace(",", "");
								}
							}
						}
						if (!formDate.isEmpty()) {
							da1 = new Date(newTime.substring(0, 11));
							da2 = new Date(formDate);
						}
						if (da1.compareTo(da2) == 0) {
							System.out.println("Date1 is equal to Date2");
							for (AppointmentDateTimeModel model : Singleton
									.getInstance().appntDateTime) {
								compare = new CalendarActivity().new Compare(
										context, newTime, model.getApptTime(),
										model.getFormattedApptDate());
								if (compare.compare(context)) {
									Log.d("INTERVAL", "TIME CONFLICT");
									isNoConflict = true;
								} else {
									Log.d("Data", "NUll");
								}
							}
							isSuccess = true;
							postEditAppnt();
						} else if (da1.compareTo(da2) > 0) {
							getAppointment(newTime.substring(0, 11), newTime);
							System.out.println("Date1 is after Date2");
						}

					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				break;

			default:
				break;
			}

		}

		private boolean isValidFormat(String format, String date) {
			Date dates = null;
			try {
				dates = new SimpleDateFormat(format).parse(date
						.replace(",", ""));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO Auto-generated method stub
			return dates != null ? true : false;
		}

		public void getDate() {
			Time currDate = new Time(Time.getCurrentTimezone());
			currDate.setToNow();
			DatePickerDialog dateDialog = new DatePickerDialog(ctx,
					pickerListener, currDate.year, currDate.month,
					currDate.monthDay);
			dateDialog.show();
		}

		public void getTime() {
			final Calendar cal = Calendar.getInstance();
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
			TimePickerDialog timeDialog = new TimePickerDialog(ctx,
					timePickerListener, hour, minute, false);
			timeDialog.show();
		}

		public DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

			// when dialog box is closed, below method will be called.
			@Override
			public void onDateSet(DatePicker view, int selectedYear,
					int selectedMonth, int selectedDay) {
				year = selectedYear;
				monthofday = selectedMonth;
				day = selectedDay;
				String monthName = new DateFormatSymbols().getMonths()[monthofday];
				text_date.setText(day + " " + monthName.substring(0, 3) + " "
						+ year);
				getAppointmentTime();
			}
		};

		private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
				// TODO Auto-generated method stub
				String min, Hour;
				hour = hourOfDay;
				minute = minutes;
				int length = (int) Math.log10(minute) + 1;
				if (length == 1) {
					min = "0" + String.valueOf(minute);
				} else {
					min = String.valueOf(minute);
				}
				if (minutes == 0) {
					min = "00";
				}
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

				int lengthHour = (int) Math.log10(hour) + 1;

				if (lengthHour == 1) {
					Hour = "0" + String.valueOf(hour);
				} else {
					Hour = String.valueOf(hour);
				}
				// String min = String.valueOf(minutes);
				text_time.setText(Hour + ":" + min + " " + timeSet);

			}

		};

	}

	public void getAppointmentTime() {
		ArrayList<JSONObject> objList = new ArrayList<JSONObject>();
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put(Constants.KEY_CALENDAR_CLANDER_VIEW, "Day");
			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
			reqObj.put(Constants.KEY_CALENDAR_EVENTDATE, text_date.getText()
					.toString());
			reqObj.put(Constants.KEY_CALENDAR_CURRENTMONTH, "");
			reqObj.put(Constants.KEY_CALENDAR_CURRENTYEAR, "");
			objList.add(reqObj);
			AppointmentTimesAsync = new CalendarActivity().new AppointmentTimesAsync(
					context, objList);
			AppointmentTimesAsync.execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void postEditAppnt() {
		if (isSuccess) {
			if (!isNoConflict) {
				postEditDatas();
			} else {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						context);
				alertDialog.setTitle("Time Conflict?");
				alertDialog
				.setMessage("Click OK to allow")
				.setCancelable(false)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						postEditDatas();
					}
				})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).show();
			}
		} else {
			Log.d("PROCESS", "PROCESS NOT SUCCESS");
		}

	}

	private void postEditDatas() {
		try {
			JSONObject reqobj = requestObjAppnts();
			ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();
			reqobj.put(Constants.EVENT_TYPE, Constants.APPNT);
			reqobj.put(Constants.KEY_CUSTOMER_ID, customerId);
			reqobj.put(Constants.EC_APPNT_TPYE_ID, typeId);
			reqobj.put(Constants.LEAD_TYPE_ID, LeadTypeId);
			reqArrayList.add(reqobj);
			EditAppntAsyncTask = new EditAppntAsyncTask(context, reqArrayList);
			EditAppntAsyncTask.execute();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("SimpleDateFormat")
	private JSONObject requestObjAppnts() throws ParseException {
		String newTime = null;
		JSONObject reqobjPro = new JSONObject();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
			Date date = null;
			String txttime = text_time.getText().toString();
			try {
				date = sdf.parse(txttime);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MINUTE, Integer.valueOf(timeInterval));
			newTime = sdf.format(calendar.getTime());
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			reqobjPro.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqobjPro.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
			reqobjPro.put(Constants.EVENT_EMPLOYEE_ID, employeeId);
			reqobjPro.put(Constants.EVENT_DATE, text_date.getText().toString());
			reqobjPro.put(Constants.EVENT_START_TIME, text_time.getText()
					.toString());
			reqobjPro.put(Constants.EVENT_END_TIME, newTime);
			reqobjPro.put(Constants.EVENT_NOTES, edittext_notes.getText()
					.toString().trim());
			reqobjPro.put(Constants.EVENT_ID, eventId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reqobjPro;

	}

	public void getAppointment(String substring, String newTime) {
		final ArrayList<JSONObject> objList = new ArrayList<JSONObject>();
		try {
			JSONObject reqObj = new JSONObject();
			reqObj.put(Constants.KEY_CALENDAR_CLANDER_VIEW, "Day");
			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
			reqObj.put(Constants.KEY_CALENDAR_EVENTDATE, substring);
			reqObj.put(Constants.KEY_CALENDAR_CURRENTMONTH, "");
			reqObj.put(Constants.KEY_CALENDAR_CURRENTYEAR, "");
			objList.add(reqObj);
			// TODO Auto-generated method stub

			AppointmentTimesAsync1 = new AppointmentTimesAsync(context,
					objList, newTime);
			AppointmentTimesAsync1.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class AppointmentTimesAsync extends AsyncTask<Void, Void, Void> {

		Context context;
		ArrayList<JSONObject> reqObj;
		ServiceHelper helper;
		JSONObject responseJson;
		String time;

		public AppointmentTimesAsync(Context ctx,
				ArrayList<JSONObject> objList, String newTime) {
			this.context = ctx;
			this.reqObj = objList;
			this.time = newTime;
			helper = new ServiceHelper(ctx);
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
			pDialog = new ActivityIndicator(context);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest(reqObj.toString(),
					Constants.URL_CALENDAR, Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				try {
					if(responseJson != null){

						if (responseJson.has(Constants.JSON_KEY_SC)) {
							JSONArray responseArray = responseJson
									.getJSONArray(Constants.JSON_KEY_SC);
							Singleton.getInstance().appntDateTime.clear();
							for (int i = 0; i < responseArray.length(); i++) {
								JSONObject obj = responseArray.getJSONObject(i);
								AppointmentDateTimeModel model = new AppointmentDateTimeModel();
								model.setApptTime(obj
										.getString(Constants.JSON_KEY_APPT_TIME));
								model.setFormattedApptDate(obj
										.getString(Constants.JSON_KEY_FORMATED_APPT_DATE));
								model.setAppntId(obj.getString(Constants.EVENT_ID));
								Singleton.getInstance().appntDateTime.add(model);
							}
						}

						for (AppointmentDateTimeModel model : Singleton
								.getInstance().appntDateTime) {
							compare = new CalendarActivity().new Compare(context,
									time, model.getApptTime(),
									model.getFormattedApptDate());
							if (compare.compare(context)) {
								Log.d("INTERVAL", "TIME CONFLICT");
								isNoConflict = true;
							} else {
								Log.d("Data", "NUll");

							}
						}
						isSuccess = true;
						postEditAppnt();
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public class EditAppntAsyncTask extends AsyncTask<Void, Void, Void> {

		Context contxt;
		ArrayList<JSONObject> objArrayList;
		ServiceHelper helper;
		JSONObject responseJson;

		public EditAppntAsyncTask(Context ctx,
				ArrayList<JSONObject> reqArrayList) {
			this.contxt = ctx;
			this.objArrayList = reqArrayList;
			helper = new ServiceHelper(ctx);
			// TODO Auto-generated constructor stub
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
			pDialog = new ActivityIndicator(contxt);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest(objArrayList.toString(),
					Constants.CREATE_EVENT_URL, Constants.REQUEST_TYPE_POST);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				try {
					if (responseJson != null) {
						Log.d("responseJson = ", "" + responseJson.toString());
						if (responseJson.has(Constants.CREATE_EVENT_KEY)) {
							JSONObject keyObj = responseJson
									.getJSONObject(Constants.CREATE_EVENT_KEY);
							String status = keyObj
									.getString(Constants.SAVE_NOTES_STATUS);
							if (status.equals(Constants.SAVE_NOTES_SUCCESS)) {
								Toast.makeText(context,
										Constants.SAVE_NOTES_SUCCESS,
										Toast.LENGTH_SHORT).show();

								for (int i = 0; i < objArrayList.size(); i++) {

									Edit_date = objArrayList.get(i).getString(
											"EventDate");
									Edit_time = objArrayList.get(i).getString(
											"StartTime")
											+ " - "
											+ objArrayList.get(i).getString(
													"EndTime");
								}
								isSuccessIntent = true;
								getAppointmentTime();
								cusAppointmentDate.setText(Edit_date + " "
										+ Edit_time);
								EditDialog.dismiss();

							}
						} else {
							Toast.makeText(context,
									Constants.TOAST_CONNECTION_ERROR,
									Toast.LENGTH_SHORT).show();
						}

					} else {
						Toast.makeText(contxt, "Process failed.",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public class EventConfigAsyncTask extends AsyncTask<Void, Void, Void> {
		Context ctx;
		ServiceHelper helper;
		JSONObject responseJson;

		public EventConfigAsyncTask(Context mContext) {
			this.ctx = mContext;
			helper = new ServiceHelper(mContext);
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper
					.jsonSendHTTPRequest("", Constants.EVENT_CONFIGURATION_URL
							+ "?DealerId=" + dealerId,
							Constants.REQUEST_TYPE_GET);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			try {
				if(responseJson != null){


					if (responseJson.has(Constants.EVENT_CONFIGUATION_KEY)) {

						JSONArray jsonarray = responseJson
								.getJSONArray(Constants.EVENT_CONFIGUATION_KEY);

						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject objs = jsonarray.getJSONObject(i);
							String defaultTime = objs
									.getString(Constants.DEFAULT_APPNT_DURATION);
							Log.d("defaultTime", defaultTime);
							JSONArray arrays = objs
									.getJSONArray(Constants.APPNT_TYPE);
							if (arrays != null) {
								Singleton.getInstance().eventConfigAppntType
								.clear();
								for (int j = 0; j < arrays.length(); j++) {
									EventConfigurationAppntTypeModel model = new EventConfigurationAppntTypeModel();
									JSONObject objAppnt = arrays.getJSONObject(j);
									model.setTypeId(objAppnt
											.getString(Constants.EC_APPNT_TPYE_ID));
									model.setTypeName(objAppnt
											.getString(Constants.EC_APPNT_TPYE_NAME));
									Singleton.getInstance().eventConfigAppntType
									.add(model);
								}
							}

							JSONArray arraysVisit = objs
									.getJSONArray(Constants.CUSTOMER_VISIT_TYPE);
							if (arraysVisit != null) {
								Singleton.getInstance().eventConfigVisiType.clear();
								for (int a = 0; a < arraysVisit.length(); a++) {
									JSONObject objVisit = arraysVisit
											.getJSONObject(a);
									EventConfigurationVisitTypeModel model = new EventConfigurationVisitTypeModel();
									model.setTypeId(objVisit
											.getString(Constants.EC_APPNT_TPYE_ID));
									model.setTypeName(objVisit
											.getString(Constants.EC_APPNT_TPYE_NAME));
									Singleton.getInstance().eventConfigVisiType
									.add(model);
								}

							}

						}

					}

				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
		if (spProductsAsyncTask != null) {
			spProductsAsyncTask.cancel(true);
			spProductsAsyncTask = null;
		}
		if (leadAsynctask != null) {
			leadAsynctask.cancel(true);
			leadAsynctask = null;
		}
		if (saveAsyncTask != null) {
			saveAsyncTask.cancel(true);
			saveAsyncTask = null;
		}
		if (appointmentQuestionireTask != null) {
			appointmentQuestionireTask.cancel(true);
			appointmentQuestionireTask = null;
		}
		if (eventConfigAsynTask != null) {
			eventConfigAsynTask.cancel(true);
			eventConfigAsynTask = null;
		}
		if (EditAppntAsyncTask != null) {
			EditAppntAsyncTask.cancel(true);
			EditAppntAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (uploadAsyncTask != null) {
			uploadAsyncTask.cancel(true);
			uploadAsyncTask = null;
		}
		if (dispoAsyncTask != null) {
			dispoAsyncTask.cancel(true);
			dispoAsyncTask = null;
		}
		if (appntResultIdTask != null) {
			appntResultIdTask.cancel(true);
			appntResultIdTask = null;
		}
		if (saveDispoQstnTask != null) {
			saveDispoQstnTask.cancel(true);
			saveDispoQstnTask = null;
		}
		if (paymentConfigAsyncTask != null) {
			paymentConfigAsyncTask.cancel(true);
			paymentConfigAsyncTask = null;
		}
		if (savePaymentAsyncTask != null) {
			savePaymentAsyncTask.cancel(true);
			savePaymentAsyncTask = null;
		}
		if (cusAsyncTask != null) {
			cusAsyncTask.cancel(true);
			cusAsyncTask = null;
		}
		if (termsTask != null) {
			termsTask.cancel(true);
			termsTask = null;
		}
		if (summaryEmailTask != null) {
			summaryEmailTask.cancel(true);
			summaryEmailTask = null;
		}
		if (saveLeadAsyncTask != null) {
			saveLeadAsyncTask.cancel(true);
			saveLeadAsyncTask = null;
		}
		if (financingCompanyNameAsyncTask != null) {
			financingCompanyNameAsyncTask.cancel(true);
			financingCompanyNameAsyncTask = null;
		}
		if (getProposalAsyncTask != null) {
			getProposalAsyncTask.cancel(true);
			getProposalAsyncTask = null;
		}
		if (proposalAsyncTask != null) {
			proposalAsyncTask.cancel(true);
			proposalAsyncTask = null;
		}
		if (AppointmentTimesAsync != null) {
			AppointmentTimesAsync.cancel(true);
			AppointmentTimesAsync = null;
		}
		if (AppointmentTimesAsync1 != null) {
			AppointmentTimesAsync1.cancel(true);
			AppointmentTimesAsync1 = null;
		}
	}

	@Override
	public void leadInterface() {
		// TODO Auto-generated method stub

		int three = (int) getResources().getDimension(R.dimen.three);
		int five = (int) getResources().getDimension(R.dimen.five);
		int twenty = (int) getResources().getDimension(R.dimen.twenty);

		// Header text
		TextView headerTv = new TextView(context);
		headerTv.setText(Constants.CUSTOMER_QUESTIONARE);
		headerTv.setTypeface(droidSansBold, Typeface.BOLD);
		// headerTv.setTextSize(getResources().getDimension(R.dimen.txt8));
		headerTv.setTextColor(getResources().getColor(R.color.black));
		bodyLayout.addView(headerTv);

		// Question and answer
		for (int i = 0; i < Singleton.getInstance().leadQuestionaryModel.size(); i++) {
			String mResponseChoiceId = Singleton.getInstance().leadQuestionaryModel.get(i).responseChoiceId;
			TextView tvQuestion = new TextView(getApplicationContext());
			LinearLayout.LayoutParams lpQuestions = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lpQuestions.setMargins(three, three, three, three);
			tvQuestion.setLayoutParams(lpQuestions);
			tvQuestion.setText(Singleton.getInstance().leadQuestionaryModel
					.get(i).questions);
			tvQuestion.setTypeface(droidSans);
			// tvQuestion.setTextSize(getResources().getDimension(R.dimen.txt8));
			tvQuestion.setTextColor(getResources().getColor(R.color.black));
			bodyLayout.addView(tvQuestion);

			if (Singleton.getInstance().leadQuestionaryModel.get(i).questionType
					.equals("Textbox")) {

				edAnswer = new EditText(context);
				LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lpAnswer.setMargins(five, five, five, five);
				edAnswer.setLayoutParams(lpAnswer);
				edAnswer.setLines(2);
				edAnswer.setId(i);
				leadEdAnswerList.put(i, edAnswer);
				edAnswer.setText(Singleton.getInstance().leadQuestionaryModel
						.get(i).questionsResponse);
				edAnswer.setPadding(five, five, five, five);
				edAnswer.setBackground(getResources().getDrawable(
						R.drawable.resolve_text_background));
				edAnswer.setTypeface(droidSans);
				edAnswer.setGravity(Gravity.TOP);
				edAnswer.setTextColor(getResources().getColor(R.color.black));
				bodyLayout.addView(edAnswer);
			} else if (Singleton.getInstance().leadQuestionaryModel.get(i).questionType
					.equals("Dropdown")) {
				int tempChoicePosition=0;
				Spinner ansSpinner = new Spinner(context);
				LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lpAnswer.setMargins(five, five, five, five);
				ansSpinner.setLayoutParams(lpAnswer);
				ansSpinner.setId(i);
				ArrayList<String> spinnerData = new ArrayList<String>();
				Log.e("leadQuestionaryModel ===>", ""+Singleton.getInstance().leadQuestionaryModel.size());
				Log.e("leadQuestionaryModel ===>", ""+Singleton.getInstance().appointmentQuestionOptionModel.size());
				Log.e("leadQuestionaryModel ===>", ""+Singleton.getInstance().leadQuestionaryModel.get(i).questionId);
				for (int j = 0; j < Singleton.getInstance().appointmentQuestionOptionModel
						.get(Singleton.getInstance().leadQuestionaryModel
								.get(i).questionId).size(); j++) {
					spinnerData
					.add(Singleton.getInstance().appointmentQuestionOptionModel
							.get(Singleton.getInstance().leadQuestionaryModel
									.get(i).questionId).get(j).ChoiceText);
					if (mResponseChoiceId.equals(Singleton.getInstance().appointmentQuestionOptionModel
							.get(Singleton.getInstance().leadQuestionaryModel
									.get(i).questionId).get(j).Id)) {
						tempChoicePosition=j;
					}
				}
				if(spinnerData.size()>0){
					ArrayAdapter<String> ptspinnerArrayAdapter = new ArrayAdapter<String>(
							context, android.R.layout.simple_spinner_item,
							spinnerData);
					ptspinnerArrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					ansSpinner.setAdapter(ptspinnerArrayAdapter);
					selectedItem=spinnerData.get(tempChoicePosition);
					ansSpinner.setSelection(tempChoicePosition);
					/*selectedItem = String
							.valueOf(spinnerData.indexOf(Singleton.getInstance().leadQuestionaryModel
									.get(i).questionsResponse));*/

					Log.d("selected ITem", selectedItem + "");
					/*	if (selectedItem != null) {
						ansSpinner.setSelection(Integer.parseInt(selectedItem));
					}*/
				}
		
				ansSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub

						Log.d("SPINNER ID", parent.getId() + "");
						if ((TextView) parent.getChildAt(0) != null) {
							((TextView) parent.getChildAt(0))
							.setTypeface(droidSans);
							((TextView) parent.getChildAt(0))
							.setPadding(0, 0, 0, 0);
						}
						leadAnswerList.put(parent.getId(), parent
								.getItemAtPosition(position).toString());

						if (Singleton.getInstance().appointmentQuestionOptionModel
								.size() > 0) {
							Log.d("sp Size",
									Singleton.getInstance().appointmentQuestionOptionModel
									.get(Singleton
											.getInstance().leadQuestionaryModel
											.get(parent.getId()).questionId)
											.size()
											+ "");
							for (int j = 0; j < Singleton.getInstance().appointmentQuestionOptionModel
									.get(Singleton.getInstance().leadQuestionaryModel
											.get(parent.getId()).questionId)
											.size(); j++) {
								if (Singleton.getInstance().appointmentQuestionOptionModel
										.get(Singleton.getInstance().leadQuestionaryModel
												.get(parent.getId()).questionId)
												.get(j).ChoiceText
												.equals(parent
														.getItemAtPosition(
																position)
																.toString())) {
									String i = Singleton.getInstance().appointmentQuestionOptionModel
											.get(Singleton
													.getInstance().leadQuestionaryModel
													.get(parent.getId()).questionId)
													.get(j).Id;
									leadAnswerIdList.put(
											parent.getId(), i);

								}

							}
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
				bodyLayout.addView(ansSpinner);

			} else if (Singleton.getInstance().leadQuestionaryModel.get(i).questionType
					.equals("RadioButton")) {
				LinearLayout radioLayout = new LinearLayout(context);
				LinearLayout.LayoutParams rglayout = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				radioLayout.setLayoutParams(rglayout);
				RadioGroup rg = new RadioGroup(context);
				// rg.setId(i);
				rg.setId(i - 1);
				LinearLayout.LayoutParams rgAnswer = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				rgAnswer.setMargins(five, five, five, five);
				rg.setLayoutParams(rgAnswer);
				rg.setOrientation(RadioGroup.HORIZONTAL);
				// rg.setOnCheckedChangeListener(this);

				RadioButton[] rb = new RadioButton[Singleton.getInstance().appointmentQuestionRadioModel
				                                   .get(Singleton.getInstance().leadQuestionaryModel
				                                		   .get(i).questionId).size()];
				rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						leadraAnswerIdList.put(group.getId() + 1,
								String.valueOf(checkedId));
						leadraAnswerList.put(group.getId() + 1, String
								.valueOf(((RadioButton) leadraAnswers
										.get(checkedId)).getText()));

					}
				});

				for (int m = 0; m < Singleton.getInstance().appointmentQuestionRadioModel
						.get(Singleton.getInstance().appointmentQuestionnaireModel
								.get(i).QuestionId).size(); m++) {
					rb[m] = new RadioButton(context);
					rb[m].setId(m);
					leadraAnswers.put(m, rb[m]);
					LinearLayout.LayoutParams raAnswer = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					raAnswer.weight = 1;
					rb[m].setLayoutParams(raAnswer);
					rb[m].setText(Singleton.getInstance().appointmentQuestionRadioModel
							.get(Singleton.getInstance().leadQuestionaryModel
									.get(i).questionId).get(m).ChoiceText);
					if (rb[m]
							.getText()
							.toString()
							.equals(Singleton.getInstance().leadQuestionaryModel
									.get(i).questionsResponse)) {
						rb[m].setChecked(true);
					}
					rg.addView(rb[m]);

				}
				radioLayout.addView(rg);
				bodyLayout.addView(radioLayout);
			}
		}
		// Save button
		Button saveButton = new Button(context);
		LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(
						R.dimen.forty));
		lpAnswer.setMargins(five, five, five, five);
		lpAnswer.gravity = Gravity.CENTER;
		saveButton.setLayoutParams(lpAnswer);
		saveButton.setId(2);
		saveButton.setOnClickListener(this);
		// saveButton.setTextSize(getResources().getDimension(R.dimen.txt8));
		saveButton.setText(getResources().getString(R.string.save));
		saveButton.setPadding(twenty, five, twenty, five);
		saveButton.setBackground(getResources().getDrawable(
				R.drawable.selector_prospect_save_button));
		saveButton.setTypeface(droidSansBold);
		saveButton.setGravity(Gravity.CENTER);
		saveButton.setTextColor(getResources().getColor(R.color.white));
		bodyLayout.addView(saveButton);
		cusAppointmentScrollview.scrollTo(0, 0);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		raAnswerIdList.put(group.getId(), String.valueOf(checkedId));
		raAnswerList.put(group.getId(), String.valueOf(((RadioButton) raAnswers
				.get(checkedId)).getText()));
	}

	public class SaveAppointmentQuestionnaireAsyncTask extends
	AsyncTask<Void, Integer, Void> {

		// ActivityIndicator dialog;
		JSONObject responseJson;
		ServiceHelper serviceHelper;
		ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();
		Context mContext;
		JSONObject localJsonObject;
		String status = "";

		public SaveAppointmentQuestionnaireAsyncTask(Context context,
				ArrayList<JSONObject> saveRequest) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.mRequestJson = saveRequest;
			serviceHelper = new ServiceHelper(this.mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			responseJson = new JSONObject();
			localJsonObject = new JSONObject();
			pDialog = new ActivityIndicator(mContext);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(),
					Constants.SAVE_APPOINTMENT_QUESTIONNAIRE_URL,
					Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (responseJson != null) {
				if (responseJson.has(Constants.SR)) {
					try {
						localJsonObject = responseJson
								.getJSONObject(Constants.SR);
						status = localJsonObject
								.getString(Constants.KEY_ADDPROSPECT_STATUS);
					} catch (JSONException e) { // TODO Auto-generated catch
						// block
						e.printStackTrace();
					}
					if (status.equals(Constants.VALUE_SUCCESS)) {
						dissmisspDialog();
						bodyLayout.removeAllViews();
						layoutcontent.setVisibility(View.VISIBLE);
						layoutProductContent.setVisibility(View.GONE);
						setMenuNormal(txtOldMenu);
						setMenuSelected(txtMenu2);
						appointmentQuestionireTask = new AppointmentQuestionnaireAsyncTask(
								context, appointmentId, dealerId, LeadTypeId,
								AppointmentTypeId);
						appointmentQuestionireTask.execute();
					} else {
						dissmisspDialog();
						Toast.makeText(mContext,
								Constants.TOAST_CONNECTION_ERROR,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					dissmisspDialog();
					Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				dissmisspDialog();
				Toast.makeText(mContext, Constants.TOAST_INTERNET,
						Toast.LENGTH_SHORT).show();
			}

		}

		public void dissmisspDialog() {

			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
		}
	}

	public class SaveLeadQuestionnaireAsyncTask extends
	AsyncTask<Void, Integer, Void> {

		// ActivityIndicator dialog;
		JSONObject responseJson;
		ServiceHelper serviceHelper;
		ArrayList<JSONObject> mRequestJson = new ArrayList<JSONObject>();
		Context mContext;
		JSONObject localJsonObject;
		String status = "";

		public SaveLeadQuestionnaireAsyncTask(Context context,
				ArrayList<JSONObject> saveRequest) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.mRequestJson = saveRequest;
			serviceHelper = new ServiceHelper(this.mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			responseJson = new JSONObject();
			localJsonObject = new JSONObject();
			pDialog = new ActivityIndicator(mContext);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(),
					Constants.SAVE_LEAD_QUESTIONARY_URL,
					Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (responseJson != null) {
				if (responseJson.has(Constants.LeadQuestionaryResult)) {
					try {
						localJsonObject = responseJson
								.getJSONObject(Constants.LeadQuestionaryResult);
						status = localJsonObject
								.getString(Constants.KEY_ADDPROSPECT_STATUS);
					} catch (JSONException e) { // TODO Auto-generated catch
						// block
						e.printStackTrace();
					}
					if (status.equals(Constants.VALUE_SUCCESS)) {
						dissmisspDialog();
						layoutProposalContent.setVisibility(View.GONE);
						bodyLayout.removeAllViews();

						layoutcontent.setVisibility(View.VISIBLE);
						addphoto.setVisibility(View.GONE);
						cus_attachment_listview.setVisibility(View.GONE);
						cus_appointment_scrollview.setVisibility(View.VISIBLE);
						layoutProductContent.setVisibility(View.GONE);
						uploadLayout.setVisibility(View.GONE);
						setMenuNormal(txtOldMenu);
						setMenuSelected(txtMenu1);

						leadAsynctask = new LeadQuestionaireAsyncTask(context,
								appointmentId, dealerId);
						leadAsynctask.execute();
					} else {
						dissmisspDialog();
						Toast.makeText(mContext,
								Constants.TOAST_CONNECTION_ERROR,
								Toast.LENGTH_SHORT).show();
					}
				} else {
					dissmisspDialog();
					Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				dissmisspDialog();
				Toast.makeText(mContext, Constants.TOAST_INTERNET,
						Toast.LENGTH_SHORT).show();
			}

		}

		public void dissmisspDialog() {

			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
		}
	}

	class SavePaymentAsyncTask extends AsyncTask<String, Void, Void> {

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
			pDialog = new ActivityIndicator(CustomerAppointmentsActivity.this);
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
			pDialog.dismiss();
			try {
				if(jsonResultText != null){

					JSONArray jsonArray = jsonResultText.getJSONArray("SP");
					JSONObject resultObj = jsonArray.getJSONObject(0);
					String status = resultObj.getString("Status");
					if (status.equalsIgnoreCase("SUCCESS")) {
						Toast.makeText(CustomerAppointmentsActivity.this,
								"Your payment is saved sucessfully.",
								Toast.LENGTH_SHORT).show();

						layoutProposalContent.setVisibility(View.GONE);
						addphoto.setVisibility(View.GONE);
						cus_attachment_listview.setVisibility(View.GONE);
						layoutcontent.setVisibility(View.VISIBLE);
						addphoto.setVisibility(View.GONE);
						cus_attachment_listview.setVisibility(View.GONE);
						cus_appointment_scrollview.setVisibility(View.VISIBLE);
						layoutProductContent.setVisibility(View.GONE);
						uploadLayout.setVisibility(View.GONE);
						setMenuNormal(txtOldMenu);
						setMenuSelected(txtMenu6);
						LoadSalesResults();

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

	class PaymentConfigAsyncTask extends AsyncTask<String, Void, Void> {

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
			pDialog = new ActivityIndicator(CustomerAppointmentsActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				sendGet();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			boolean bCheck = false, bCrietcard = false, bCash = false, bFinancing = false;
			for (int i = 0; i < methodArray.size(); i++) {
				if (methodArray.get(i).equalsIgnoreCase("Check")) {
					check.setVisibility(View.VISIBLE);
					bCheck = true;
				} else if (methodArray.get(i).equalsIgnoreCase("CreditCard")) {
					crietcard.setVisibility(View.VISIBLE);
					bCrietcard = true;
				} else if (methodArray.get(i).equalsIgnoreCase("Cash")) {
					cash.setVisibility(View.VISIBLE);
					bCash = true;
				} else if (methodArray.get(i).equalsIgnoreCase("Financed")) {
					financing.setVisibility(View.VISIBLE);
					bFinancing = true;
				}
			}
			if (bCash) {
				int cashPosition = methodArray.indexOf("Cash");
				currentValue = idArray.get(cashPosition);
				checkClickValue = 0;
				cashchecknumber.setVisibility(View.GONE);
				payment_number_edit.setVisibility(View.GONE);
				financing_company_spinner.setVisibility(View.GONE);
				payment_number_edit.setText("");
				payment_done.setVisibility(View.VISIBLE);
				cash.setBackgroundResource(R.drawable.cash_click);
				check.setBackgroundResource(R.drawable.check);
				crietcard.setBackgroundResource(R.drawable.credit_card);
				financing.setBackgroundResource(R.drawable.financing);

			} else if (bCheck) {
				int checkPosition = methodArray.indexOf("Check");
				currentValue = idArray.get(checkPosition);
				cash.setBackgroundResource(R.drawable.cash);
				check.setBackgroundResource(R.drawable.check_click);
				crietcard.setBackgroundResource(R.drawable.credit_card);
				financing.setBackgroundResource(R.drawable.financing);
				cashchecknumber.setVisibility(View.VISIBLE);
				payment_number_edit.setVisibility(View.VISIBLE);
				financing_company_spinner.setVisibility(View.GONE);
				payment_number_edit.setText("");
				payment_done.setVisibility(View.VISIBLE);
				cashchecknumber.setText("Check Number");
				payment_number_edit.setInputType(InputType.TYPE_CLASS_NUMBER);
				checkClickValue = 1;
			} else if (bFinancing) {
				int checkPosition = methodArray.indexOf("Financed");
				currentValue = idArray.get(checkPosition);
				cash.setBackgroundResource(R.drawable.cash);
				check.setBackgroundResource(R.drawable.check);
				crietcard.setBackgroundResource(R.drawable.credit_card);
				financing.setBackgroundResource(R.drawable.financing_click);
				cashchecknumber.setVisibility(View.VISIBLE);
				payment_number_edit.setVisibility(View.VISIBLE);
				payment_number_edit.setText("");
				financing_company_spinner.setVisibility(View.VISIBLE);
				payment_done.setVisibility(View.VISIBLE);
				cashchecknumber.setText("Authorization Number");
				payment_number_edit.setInputType(InputType.TYPE_CLASS_TEXT);
				checkClickValue = 3;
				financingCompanyNameAsyncTask = new FinancingCompanyAsyncTask(
						CustomerAppointmentsActivity.this, dealerId).execute();
			} else if (bCrietcard) {
				checkClickValue = 2;
				Toast.makeText(getApplicationContext(), "Progress",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void sendGet() throws Exception {
		HttpEntity httpEntity = null;
		idArray.clear();
		methodArray.clear();

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(Constants.PAYMENT_METHODS_CONFIG_URL
				+ dealerId + "&IsSalesApp=True");

		// add request header
		request.addHeader(Constants.AUTHORIZATION,
				Constants.PAYMENT_METHODS_KEY);
		HttpResponse httpResponse = client.execute(request);
		httpEntity = httpResponse.getEntity();
		response = EntityUtils.toString(httpEntity);
		JSONObject jsonObj = new JSONObject(response);
		paymentConfigJsonArray = jsonObj.getJSONArray(Constants.PM);
		for (int i = 0; i < paymentConfigJsonArray.length(); i++) {
			JSONObject c = paymentConfigJsonArray.getJSONObject(i);
			ModuleStatus = c.getString(Constants.MODULESTATUS);
			dealerAccountNumber = c.getString(Constants.DEALERACCOUNTNUMBER);
			dealerSecurityKey = c.getString(Constants.DEALERKEY);
			JSONArray phone = c.getJSONArray(Constants.METHODS);
			for (int j = 0; j < phone.length(); j++) {
				JSONObject methodss = phone.getJSONObject(j);
				idArray.add(methodss.getString(Constants.ID));
				methodArray.add(methodss.getString(Constants.PAYMENTMETHOD));
			}
		}
	}

	@Override
	public void DispoSubDispoQstns() {
		// TODO Auto-generated method stub
		bodyLayout.removeAllViews();
		bodyLayout.setVisibility(View.VISIBLE);
		countone = 0;
		counttwo = 0;
		String lastdispoId = "";
		String lastSubdispoId = "";
		int five = (int) getResources().getDimension(R.dimen.five);
		TextView text_appnt_result = new TextView(context);
		text_select_date = new TextView(context);
		Button btn_questionnaire = new Button(context);
		layout_hotQuotes = new LinearLayout(context);
		layout_hotQuotes.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layout_hotQuotes.setLayoutParams(params);
		btn_select_date = new ImageView(context);
		LinearLayout.LayoutParams lpAnswer1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		text_appnt_result.setLayoutParams(lpAnswer1);
		lpAnswer1.setMargins(five, five, five, five);
		text_appnt_result.setPadding(five, 10, five, 10);
		text_appnt_result.setTextColor(Color.WHITE);
		text_appnt_result.setText(context.getResources().getString(
				R.string.appnt_result));
		text_appnt_result.setBackground(getResources().getDrawable(
				R.drawable.selector_main_list_btn));
		text_appnt_sub_result = new TextView(context);
		text_appnt_sub_result.setLayoutParams(lpAnswer1);
		lpAnswer1.setMargins(five, five, five, five);
		text_appnt_sub_result.setPadding(five, 10, five, 10);
		text_appnt_sub_result.setTextColor(Color.WHITE);
		text_appnt_sub_result.setText(context.getResources().getString(
				R.string.appnt_subresult));
		text_appnt_sub_result.setBackground(getResources().getDrawable(
				R.drawable.selector_main_list_btn));
		spinner_Appnt_Result = new Spinner(context);
		spinner_Appnt_Result.setLayoutParams(lpAnswer1);
		spinner_Appnt_Result.setPrompt("Please Select Appointment Result");

		spinner_Appnt_Sub_Result = new Spinner(context);
		spinner_Appnt_Sub_Result.setLayoutParams(lpAnswer1);

		if(eventType.equalsIgnoreCase("SALES")){

			for (int i = 0; i < Singleton.getInstance().salesDispoArray.size(); i++) {
				String id = Singleton.getInstance().salesDispoArray.get(i).getDispoId();
				lastdispoId = Singleton.getInstance().dispoId;
				if (lastdispoId != "") {
					if (id.equals(lastdispoId)) {
						Singleton.getInstance().lastSelected = i;
						break;
					}else {
						Singleton.getInstance().lastSelected = 0;
					}
				} else {
					Singleton.getInstance().lastSelected = 0;
				}
			}


			if (Singleton.getInstance().lastSelected != 0) {
				for (int j = 0; j < Singleton.getInstance().salesDispoArray
						.get(Singleton.getInstance().lastSelected).getSubDispo()
						.size(); j++) {
					String id = Singleton.getInstance().salesDispoArray
							.get(Singleton.getInstance().lastSelected).getSubDispo().get(j)
							.getSubDispoId();
					lastSubdispoId = Singleton.getInstance().SubDispoId;
					if (lastSubdispoId != "") {
						if (id.equals(lastSubdispoId)) {
							Singleton.getInstance().lastSubDispoSelected = j;
							break;
						}else {
							Singleton.getInstance().lastSubDispoSelected = 0;
						}
					} else {
						Singleton.getInstance().lastSubDispoSelected = 0;
					}
				}
			} else {
				Singleton.getInstance().lastSubDispoSelected = 0;
			}


			AppntResultAdapter = new ArrayAdapter<DispoModel>(
					context, android.R.layout.simple_spinner_dropdown_item,
					Singleton.getInstance().salesDispoArray) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(droidSansBold);
					return v;
				}

				@Override
				public View getDropDownView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(droidSans);
					return v;
				}
			};


			if (Singleton.getInstance().salesDispoArray.size() > 0) {
				AppntSubResultAdapter = new ArrayAdapter<SubDispoModel>(context,
						android.R.layout.simple_spinner_dropdown_item,
						Singleton.getInstance().salesDispoArray.get(Singleton
								.getInstance().lastSelected).getSubDispo()) {
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View v = super.getView(position, convertView, parent);
						((TextView) v).setTypeface(droidSansBold);
						return v;
					}

					@Override
					public View getDropDownView(int position, View convertView,
							ViewGroup parent) {
						View v = super.getView(position, convertView, parent);
						((TextView) v).setTypeface(droidSans);
						return v;
					}
				};
				AppntSubResultAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			}

		}else{


			for (int i = 0; i < Singleton.getInstance().serviceDispoArray.size(); i++) {
				String id = Singleton.getInstance().serviceDispoArray.get(i).getDispoId();
				lastdispoId = Singleton.getInstance().dispoId;
				if (lastdispoId != "") {
					if (id.equals(lastdispoId)) {
						Singleton.getInstance().lastSelected = i;
						break;
					}else{
						Singleton.getInstance().lastSelected = 0;
					}
				} else {
					Singleton.getInstance().lastSelected = 0;
				}
			}


			if (Singleton.getInstance().lastSelected != 0) {
				for (int j = 0; j < Singleton.getInstance().serviceDispoArray
						.get(Singleton.getInstance().lastSelected).getSubDispo()
						.size(); j++) {
					String id = Singleton.getInstance().serviceDispoArray
							.get(Singleton.getInstance().lastSelected).getSubDispo().get(j)
							.getSubDispoId();
					lastSubdispoId = Singleton.getInstance().SubDispoId;
					if (lastSubdispoId != "") {
						if (id.equals(lastSubdispoId)) {
							Singleton.getInstance().lastSubDispoSelected = j;
							break;
						}else{
							Singleton.getInstance().lastSubDispoSelected = 0;
						}
					} else {
						Singleton.getInstance().lastSubDispoSelected = 0;
					}
				}
			} else {
				Singleton.getInstance().lastSubDispoSelected = 0;
			}


			AppntResultAdapter = new ArrayAdapter<DispoModel>(
					context, android.R.layout.simple_spinner_dropdown_item,
					Singleton.getInstance().serviceDispoArray) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(droidSansBold);
					return v;
				}

				@Override
				public View getDropDownView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(droidSans);
					return v;
				}
			};


			if (Singleton.getInstance().serviceDispoArray.size() > 0) {
				AppntSubResultAdapter = new ArrayAdapter<SubDispoModel>(context,
						android.R.layout.simple_spinner_dropdown_item,
						Singleton.getInstance().serviceDispoArray.get(Singleton
								.getInstance().lastSelected).getSubDispo()) {
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						View v = super.getView(position, convertView, parent);
						((TextView) v).setTypeface(droidSansBold);
						return v;
					}

					@Override
					public View getDropDownView(int position, View convertView,
							ViewGroup parent) {
						View v = super.getView(position, convertView, parent);
						((TextView) v).setTypeface(droidSans);
						return v;
					}
				};
				AppntSubResultAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			}


		}

		AppntResultAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_Appnt_Result.setAdapter(AppntResultAdapter);
		spinner_Appnt_Result.setSelection(Singleton.getInstance().lastSelected);

		spinner_Appnt_Result
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				countone += 1;
				if(eventType.equalsIgnoreCase("SALES")){
					DispoId = Singleton.getInstance().salesDispoArray.get(
							position).getDispoId();
				}else{
					DispoId = Singleton.getInstance().serviceDispoArray.get(
							position).getDispoId();
				}

				Singleton.getInstance().lastSelected = position;
				TextView text = (TextView) spinner_Appnt_Result
						.getSelectedView();
				if (countone > 1) {
					text_HotQuotes = text.getText().toString();
					if (text.getText().toString().equals("Hot Quote")) {
						layout_hotQuotes.removeAllViews();
						spinner_Appnt_Sub_Result
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
						layout_hotQuotes
						.addView(spinner_Appnt_Sub_Result);
						layout_hotQuotes.addView(btn_select_date);
						layout_hotQuotes.addView(text_select_date);
					} else {
						layout_hotQuotes.removeView(btn_select_date);
						layout_hotQuotes.removeView(text_select_date);
						spinner_Appnt_Sub_Result
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
					}
					if(eventType.equalsIgnoreCase("SALES")){
						setAdapter(position,Singleton.getInstance().salesDispoArray);
					}else{
						setAdapter(position,Singleton.getInstance().serviceDispoArray);
					}
					
				} else {
					if (text.getText().toString().equals("Hot Quote")) {
						layout_hotQuotes.removeAllViews();
						spinner_Appnt_Sub_Result
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));
						layout_hotQuotes
						.addView(spinner_Appnt_Sub_Result);
						layout_hotQuotes.addView(btn_select_date);
						layout_hotQuotes.addView(text_select_date);
					} else {
						layout_hotQuotes.removeView(btn_select_date);
						layout_hotQuotes.removeView(text_select_date);
						spinner_Appnt_Sub_Result
						.setLayoutParams(new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.WRAP_CONTENT));

					}
					if(eventType.equalsIgnoreCase("SALES")){
						setAdapter(position,Singleton.getInstance().salesDispoArray);
					}else{
						setAdapter(position,Singleton.getInstance().serviceDispoArray);
					}
					spinner_Appnt_Sub_Result.setSelection(Singleton
							.getInstance().lastSubDispoSelected);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		spinner_Appnt_Sub_Result.setAdapter(AppntSubResultAdapter);

		spinner_Appnt_Sub_Result
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				Log.d("LastSelected",
						Singleton.getInstance().lastSelected + "");
				counttwo += 1;
				if (counttwo > 1) {
					if(eventType.equalsIgnoreCase("SALES")){
						SubDispoId = Singleton.getInstance().salesDispoArray
								.get(Singleton.getInstance().lastSelected).getSubDispo()
								.get(position).getSubDispoId();
					}else{
						SubDispoId = Singleton.getInstance().serviceDispoArray
								.get(Singleton.getInstance().lastSelected).getSubDispo()
								.get(position).getSubDispoId();
					}
					Singleton.getInstance().lastSubDispoSelected = position;
				} else {
					if(eventType.equalsIgnoreCase("SALES")){
						SubDispoId = Singleton.getInstance().salesDispoArray
								.get(Singleton.getInstance().lastSelected).getSubDispo()
								.get(position).getSubDispoId();
					}else{
						SubDispoId = Singleton.getInstance().serviceDispoArray
								.get(Singleton.getInstance().lastSelected).getSubDispo()
								.get(position).getSubDispoId();
					}

					Singleton.getInstance().lastSubDispoSelected = position;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		edit_notes = new EditText(context);
		LinearLayout.LayoutParams lpEdit = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 200);
		lpEdit.setMargins(five, five, five, five);
		edit_notes.setLayoutParams(lpEdit);
		edit_notes.setEllipsize(TruncateAt.START);
		edit_notes.setGravity(Gravity.START);
		edit_notes.setImeOptions(EditorInfo.IME_ACTION_DONE);
		edit_notes.setSingleLine(false);
		edit_notes.setMinLines(4);
		// edit_notes.setText(Singleton.getInstance().lastNotes);
		edit_notes.setBackground(getResources().getDrawable(
				R.drawable.resolve_text_background));

		LinearLayout.LayoutParams lpButton = new LinearLayout.LayoutParams(
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
		lpButton.setMargins(five, 10, five, five);
		lpButton.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;

		btn_select_date
		.setImageResource(R.drawable.selector_date_image_pressed_btn);
		btn_select_date.setLayoutParams(lpButton);
		btn_questionnaire.setBackground(getResources().getDrawable(
				R.drawable.selector_main_list_btn));
		btn_questionnaire.setLayoutParams(lpButton);
		btn_questionnaire.setText(getResources().getString(
				R.string.appnt_questionaire));
		btn_questionnaire.setTextColor(Color.WHITE);
		if (Constants.isLock.equalsIgnoreCase("True")) {
			spinner_Appnt_Result.setEnabled(false);
			spinner_Appnt_Sub_Result.setEnabled(false);
			edit_notes.setEnabled(false);
			btn_select_date.setEnabled(false);

		} else {
			spinner_Appnt_Result.setEnabled(true);
			spinner_Appnt_Sub_Result.setEnabled(true);
			edit_notes.setEnabled(true);
			btn_select_date.setEnabled(true);
		}
		btn_questionnaire.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Singleton.getInstance().lastNotes = edit_notes.getText()
						.toString();
				Log.d("appntResultId & eventId", appointmentResultId + "###"
						+ eventId);
				if (Constants.isLock.equalsIgnoreCase("True")) {
					dispoAsyncTask = new DispoQuestionnaireAsyncTask(context,
							dealerId, appointmentResultId, DispoId);
					dispoAsyncTask.execute();
				} else {
					if (Singleton.getInstance().lastSelected != 0) {
						try {
							JSONObject obj = getSaveDispoObj();
							ArrayList<JSONObject> reqArrPro = new ArrayList<JSONObject>();
							obj.put(Constants.KEY_APPT_RESULT_ID,
									appointmentResultId);
							if (text_HotQuotes.equals("Hot Quote")) {
								obj.put("HotQuoteFollowUpDate",
										text_select_date.getText().toString());
							} else {
								obj.put("HotQuoteFollowUpDate", "");
							}
							reqArrPro.add(obj);
							saveDispoAsyncTask = new SaveDispoAsyncTask(
									context, reqArrPro, dealerId,
									appointmentResultId, DispoId);
							saveDispoAsyncTask.execute();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(context,
								"Please Select Appointment Result",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		btn_select_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDate();
			}
		});
		bodyLayout.addView(text_appnt_result);
		bodyLayout.addView(spinner_Appnt_Result);
		bodyLayout.addView(text_appnt_sub_result);
		layout_hotQuotes.addView(btn_select_date);
		layout_hotQuotes.addView(text_select_date);
		layout_hotQuotes.removeView(btn_select_date);
		// btn_select_date.setVisibility(View.GONE);
		layout_hotQuotes.addView(spinner_Appnt_Sub_Result);
		// layout_hotQuotes.addView(btn_select_date);
		btn_select_date.setVisibility(View.VISIBLE);
		// bodyLayout.addView(btn_select_date);
		bodyLayout.addView(layout_hotQuotes);
		bodyLayout.addView(edit_notes);
		bodyLayout.addView(btn_questionnaire);

	}

	public void getDate() {
		Time currDate = new Time(Time.getCurrentTimezone());
		currDate.setToNow();
		DatePickerDialog dateDialog = new DatePickerDialog(context,
				pickerListener, currDate.year, currDate.month,
				currDate.monthDay);
		dateDialog.show();
	}

	public JSONObject getSaveDispoObj() {
		JSONObject obj = new JSONObject();
		// ArrayList<JSONObject> reqArrPro = new ArrayList<JSONObject>();
		try {
			obj.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
			obj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
			obj.put(Constants.KEY_DISPO_ID, DispoId);
			obj.put("SubDispoId", SubDispoId);
			obj.put("AppointmentResultNotes", edit_notes.getText().toString());
			// reqArrPro.add(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	public DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@Override
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			monthofday = selectedMonth;
			day = selectedDay;
			String monthName = new DateFormatSymbols().getMonths()[monthofday];
			Log.d("Date", day + " " + monthName.substring(0, 3) + " " + year);
			text_select_date.setText(day + " " + monthName.substring(0, 3)
					+ " " + year);
		}
	};

	private void setAdapter(int position,ArrayList<DispoModel> array) {
		AppntSubResultAdapter = new ArrayAdapter<SubDispoModel>(context,
				android.R.layout.simple_spinner_dropdown_item,
				array.get(position).getSubDispo()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(droidSansBold);
				return v;
			}

			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(droidSans);
				return v;
			}
		};


		if (array.get(position).getSubDispo()
				.size() == 0) {
			SubDispoId = "";
			spinner_Appnt_Sub_Result.setVisibility(View.GONE);
			text_appnt_sub_result.setVisibility(View.GONE);
		}
		else
		{
			spinner_Appnt_Sub_Result.setVisibility(View.VISIBLE);
			text_appnt_sub_result.setVisibility(View.VISIBLE);
			AppntSubResultAdapter.notifyDataSetChanged();
			spinner_Appnt_Sub_Result.setAdapter(AppntSubResultAdapter);
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1000) {
			if (resultCode == RESULT_OK) {
				uploadPhotoArray = data
						.getStringArrayListExtra("UPLOADPHOTOURL");
				bodyLayout.removeAllViews();
				layoutcontent.setVisibility(View.VISIBLE);
				uploadLayout.setVisibility(View.VISIBLE);
				layoutProductContent.setVisibility(View.GONE);
				addphoto.setVisibility(View.GONE);
				galleryImageUpload.setVisibility(View.VISIBLE);
				cus_attachment_listview.setVisibility(View.GONE);
				cus_appointment_scrollview.setVisibility(View.VISIBLE);
				for (int i = 0; i < uploadPhotoArray.size(); i++) {
					discriptionForImage.add("");
					allBit.add(resizeBitmap(uploadPhotoArray.get(i),
							(int) getResources().getDimension(R.dimen.hundred),
							(int) getResources().getDimension(R.dimen.hundred)));
				}
				addDynamicView();
			}
		}
	}

	@Override
	public void postQuestionnaire() {
		bodyLayout.removeAllViews();
		bodyLayout.setVisibility(View.VISIBLE);
		TextView text_Questions = null;
		int five = (int) getResources().getDimension(R.dimen.five);
		LinearLayout.LayoutParams lpAnswer1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lpAnswer1.setMargins(five, five, five, five);
		LinearLayout.LayoutParams btn_params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
		btn_params.setMargins(five, 10, five, five);
		btn_params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
		depoPosition = 0;
		for (final DispoQuestionnaireModel modelQuestions : Singleton
				.getInstance().DispoQuestionsArray) {
			String mResponseChoiceId = modelQuestions.getResponseChoiceId();
			text_Questions = new TextView(context);
			text_Questions.setLayoutParams(lpAnswer1);
			text_Questions.setText(modelQuestions.getQuestion());
			if (modelQuestions.getQuestionType().equals("Dropdown")) {

				int tempResponseChoiceId = 0;
				spinner_qstn = new Spinner(context);
				spinner_qstn.setLayoutParams(lpAnswer1);
				spinner_qstn.setId(depoPosition);
				Log.d("QID", modelQuestions.getQuestionId() + "");

				ArrayList<String> spinnerList = new ArrayList<String>();
				Log.d("HASHMAP", Singleton.getInstance().dispoHashMap.toString());
				Log.d("tag", Singleton.getInstance().dispoHashMap
						.get(String.valueOf(modelQuestions.getQuestionId())).size()+"");
				for (int j = 0; j < Singleton.getInstance().dispoHashMap
						.get(String.valueOf(modelQuestions.getQuestionId())).size(); j++) {
					spinnerList
					.add(Singleton.getInstance().dispoHashMap
							.get(String.valueOf(modelQuestions.getQuestionId()))
							.get(j).ChoiceText);
					if (mResponseChoiceId.equals(Singleton.getInstance().dispoHashMap
							.get(String.valueOf(modelQuestions.getQuestionId()))
							.get(j).Id)) {
						tempResponseChoiceId=j;
					}
				}
				
				if(spinnerList.size()>0){
					ArrayAdapter<String> spinnerAdap = new ArrayAdapter<String>(
							context, android.R.layout.simple_spinner_dropdown_item,
							spinnerList);

					spinner_qstn.setAdapter(spinnerAdap);
					spinner_qstn.setSelection(tempResponseChoiceId);
					/*spinner_qstn.setSelection(spinnerList.indexOf(modelQuestions
							.getResponseText()));*/
					bodyLayout.addView(text_Questions);
				}		
				bodyLayout.addView(spinner_qstn);
				
				spinner_qstn
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						String text = spinner_qstn.getItemAtPosition(
								position).toString();

						if (Singleton.getInstance().dispoHashMap.size() > 0) {
							for (int j = 0; j < Singleton.getInstance().dispoHashMap
									.get(String.valueOf(Singleton
											.getInstance().DispoQuestionsArray
											.get(parent.getId())
											.getQuestionId())).size(); j++) {
								if (Singleton.getInstance().dispoHashMap
										.get(String.valueOf(Singleton
												.getInstance().DispoQuestionsArray
												.get(parent.getId())
												.getQuestionId())).get(
														j).ChoiceText
														.equals(parent
																.getItemAtPosition(
																		position)
																		.toString())) {
									String i = Singleton.getInstance().dispoHashMap
											.get(String.valueOf(Singleton
													.getInstance().DispoQuestionsArray
													.get(parent.getId())
													.getQuestionId()))
													.get(j).Id;
									SpinList.put(parent.getId(), i);
									Log.d("SPIN LIST", SpinList.toString());
									SpinChoiceList.put(parent.getId(), text);
									Log.d("SPIN LIST", SpinChoiceList.toString());
								}
							}
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
					}
				});
				
			} else if (modelQuestions.getQuestionType().equals("Textbox")) {
				text_qstns = new EditText(context);
				text_qstns.setId(modelQuestions.getQuestionId());
				EditList.put(modelQuestions.getQuestionId(), text_qstns);
				text_qstns.setLayoutParams(lpAnswer1);
				text_qstns.setText(modelQuestions.getResponseText());
				bodyLayout.addView(text_Questions);
				bodyLayout.addView(text_qstns);
			}
			depoPosition += 1;

			if (Constants.isLock.equalsIgnoreCase("True")) {
				if (spinner_qstn != null) {
					spinner_qstn.setEnabled(false);
				}
				if (text_qstns != null) {
					text_qstns.setEnabled(false);
				}

			} else {
				if (spinner_qstn != null) {
					spinner_qstn.setEnabled(true);
				}
				if (text_qstns != null) {
					text_qstns.setEnabled(true);
				}

			}
		}

		Button button_save_answer = new Button(context);
		button_save_answer.setText(getResources().getString(R.string.save));
		button_save_answer.setTextColor(Color.WHITE);
		button_save_answer.setLayoutParams(btn_params);
		button_save_answer.setBackground(getResources().getDrawable(
				R.drawable.selector_main_list_btn));
		button_save_answer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					int s = 0;
					ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
					for (DispoQuestionnaireModel modelQuestions : Singleton
							.getInstance().DispoQuestionsArray) {
						JSONObject obj = new JSONObject();
						obj.put(Constants.KEY_LOGIN_DEALER_ID, dealerId);
						obj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeId);
						obj.put(Constants.KEY_APPT_RESULT_ID, appointmentResultId);
						obj.put(Constants.KEY_DISPO_QUESTION_ID,
								modelQuestions.getQuestionId());
						obj.put(Constants.KEY_DISPO_RESPONSE_ID,
								modelQuestions.getResponseId());
						if (modelQuestions.getQuestionType().equals("Dropdown")) {
							Log.d("SpinList", SpinList.get(s)+"");
							Log.d("SpinList", SpinChoiceList.get(s)+"");
							obj.put(Constants.KEY_DISPO_RESPONSE_CHOICE_ID,
									SpinList.get(s));
							obj.put(Constants.KEY_DISPO_RESPONSE_TEXT,
									SpinChoiceList.get(s));
						} else if (modelQuestions.getQuestionType().equals(
								"Textbox")) {
							obj.put(Constants.KEY_DISPO_RESPONSE_TEXT, EditList
									.get(modelQuestions.getQuestionId())
									.getText().toString());
							obj.put(Constants.KEY_DISPO_RESPONSE_CHOICE_ID, "");
						}
						arrayList.add(obj);
						s = s+1;
					}
					Log.d("ArrayList", arrayList.toString());
					saveDispoQstnTask = new SaveDispoQstnAsyncTask(context,
							arrayList);
					saveDispoQstnTask.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		if (Constants.isLock.equalsIgnoreCase("True")) {
			button_save_answer.setEnabled(false);
			button_save_answer.setBackground(context.getResources()
					.getDrawable(R.drawable.resolve_disable_background));
		} else {
			button_save_answer.setEnabled(true);
			button_save_answer.setBackground(context.getResources()
					.getDrawable(R.drawable.selector_prospect_save_button));
		}

		if (Singleton.getInstance().DispoQuestionsArray.size() > 0) {
			bodyLayout.addView(button_save_answer);
		}
	}

	@Override
	public void loadPostSalesResults() {
		LoadSalesResults();
		// DispoSubDispo();

	}

	public String getTimeDiff(Date dateOne, Date dateTwo) {
		String diff = "";
		long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		diff = String.format(
				"%d %d ",
				TimeUnit.MILLISECONDS.toHours(timeDiff),
				TimeUnit.MILLISECONDS.toMinutes(timeDiff)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(timeDiff)));
		minuteTime = TimeUnit.MILLISECONDS.toMinutes(timeDiff);
		// diff = String.format("%d hour(s) %d min(s)",
		// TimeUnit.MILLISECONDS.toHours(timeDiff),TimeUnit.MILLISECONDS.toMinutes(timeDiff)
		// - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		return diff;
	}

	public ArrayList<ProposalCartModel> getCartListFromSharedPrefference(
			String appResultId) {
		if (preferenceSettings.contains(appResultId)) {
			mproposalCartList.clear();
			try {

				mproposalCartList = (ArrayList<ProposalCartModel>) mObjetserilizer
						.deserialize(preferenceSettings.getString(
								appResultId,
								mObjetserilizer
								.serialize(new ArrayList<ProposalCartModel>())));
				Singleton.getInstance().proposalCartList.clear();

				Singleton.getInstance().proposalCartList = mproposalCartList;
				Log.d("SINN",
						Singleton.getInstance().proposalCartList.toString()
						+ " test");
			}

			catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mproposalCartList;
	}

	@Override
	public void step2Listener() {
		// TODO Auto-generated method stub

		LeadTypeId = Singleton.getInstance().appointmentArray.get(position)
				.getLeadTypeId();
		AppointmentTypeId = Singleton.getInstance().appointmentArray.get(
				position).getAppointmentTypeId();
		loadApptTypeLeadType_spinner();
		appointmentQuestionireTask = new AppointmentQuestionnaireAsyncTask(
				context, appointmentId, dealerId, LeadTypeId, AppointmentTypeId);
		appointmentQuestionireTask.execute();

	}

	@Override
	public void onProposalExecute(ActivityIndicator mdialog) {
		// TODO Auto-generated method stub
		Constants.isProposalList = false;
		if (Singleton.getInstance().quotedProductModel.size() > 0) {
			Constants.isProposalList = true;
		}

		initProducts();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (txtOldMenu == txtMenu4) {
			//resumeProducts();
			switch (layoutProductContent.getVisibility()) {
			case 0:
				if (preferenceSettings.contains(Constants.PREF_RECENT_PRODUCT_KEY)) {
					mRecentProductsList.clear();
					try {
						mRecentProductsList = (ArrayList<SpProductSubCatAndMaterialModel>) mObjetserilizer
								.deserialize(preferenceSettings
										.getString(
												Constants.PREF_RECENT_PRODUCT_KEY,
												mObjetserilizer
												.serialize(new ArrayList<SpProductSubCatAndMaterialModel>())));
						Collections.reverse(mRecentProductsList);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				SalesProcessProductGridViewAdapter salesProcessProductGridAdapter = new SalesProcessProductGridViewAdapter(
						this, mRecentProductsList, true);
				gridViewRecentProducts.setAdapter(salesProcessProductGridAdapter);
				break;
			}

			if(Constants.isSelectProduct){
				layoutProductContent.setVisibility(View.GONE);
				Constants.isProposalList = false;
				initProducts();
			}else{
				Constants.isProposal = true;
				layoutcontent.setVisibility(View.GONE);
				uploadLayout.setVisibility(View.GONE);
				setMenuNormal(txtOldMenu);
				setMenuSelected(txtMenu4);
				layoutProposalContent.setVisibility(View.VISIBLE);
				layoutProductContent.setVisibility(View.GONE);
				getProposalAsyncTask = new GetProposalAsyncTask(context, dealerId,
						appointmentResultId, proposalListview, "check",
						productTotal);
				getProposalAsyncTask.execute();
			}


		}
		System.gc();
	}
}