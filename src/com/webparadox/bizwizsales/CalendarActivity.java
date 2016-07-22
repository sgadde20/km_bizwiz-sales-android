package com.webparadox.bizwizsales;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.adapter.CalMonthApptsAdapter;
import com.webparadox.bizwizsales.adapter.CalendarDaysAdapter;
import com.webparadox.bizwizsales.asynctasks.ChildEmployeeAsyncTask;
import com.webparadox.bizwizsales.asynctasks.PhonenumbersAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SavePhoneNumberAsynctask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.AppointmentDateTimeModel;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;
import com.webparadox.bizwizsales.models.EventConfigurationAppntTypeModel;
import com.webparadox.bizwizsales.models.EventConfigurationVisitTypeModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;
import com.webparadox.bizwizsales.models.OfflineCalendarEntryModel;

//import android.support.v7.widget.SearchView;

@SuppressLint({ "NewApi", "SimpleDateFormat" })
public class CalendarActivity extends FragmentActivity implements
OnQueryTextListener, OnCloseListener, android.view.View.OnClickListener {
	List<String> appntList;
	static Typeface droidSansBold, droidSans;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	public String dealerID = "";
	public String employeeID = "";
	public String employeeName = "";
	public String CanCreateEvents = "";
	Button imgMonth, imgList, imgFilterDrawer;
	static Button btnAppointments;
	static Button btnFollowups;
	public GregorianCalendar month, itemmonth;
	public CalendarAdapter adapter;
	public Handler handler;
	// calendar marker.
	public ArrayList<String> items;
	TextView txtDateView, txtHeaderDateView;
	private GestureDetector gesturedetector = null;
	RelativeLayout layoutMonthView, layoutListView;
	GridView calendarGridView;
	public ListView calendarListView;
	public LayoutInflater inflator, loaderInflator;
	public static Context mContext;
	View inflatorView;
	public ProgressDialog progressDialog;
	ServiceHelper mServiceHelper;
	public long longLastTimeMillis = 0L;
	public String strPreviousDate = "";
	public static boolean isListView, isPopupView, isCalendarLoadBtn = false,
			hasAnAppointment = false, isFilteredList;
	public int previousPosition = 0;
	String strNextRecordsDate = "";
	ImageView backBtn, logoBtn, imageViewAdd, imgPopUpClose;

	public ArrayList<CalendarListPaginationModel> selectedDayNxtApptsData = new ArrayList<CalendarListPaginationModel>();
	public CalMonthListAdapter mCustomAdapter;
	ArrayList<CalendarListPaginationModel> arr_month_data;

	// popup declarations
	public static LinearLayout popupLayout;
	ImageView popupClose, imageViewNextDate, imageViewPreviousDate;
	public static TextView popupDateTV;
	public static ListView calMonthLV;
	public static CalMonthApptsAdapter calmonthAdapter;
	String selectedDatestr;

	// POPUP ADD APPNTS
	PopupWindow popUpWindowAppnts;
	ImageView btn_add_appnt;
	AddAppntDialog addAppntDialog;
	AsyncTask<Void, Void, Void> eventConfigAsynTask;
	int year;
	int monthofday;
	int day;
	int hour;
	int minute;
	String timeSet = "";
	String customerId, customerName, CustomerAddress;

	LinearLayout layoutCalendarView;
	public static Dialog mCalendarNotesDialog;
	public CalendarDaysAdapter daysadapter;

	AsyncTask<JSONObject, Void, Void> calendarAsyncTask, SaveAppntAsyncTask;
	SavePhoneNumberAsynctask savePhoneAsyncTask;
	AsyncTask<Void, Void, Void> AppointmentTimesAsync;
	ActivityIndicator pDialog;
	String pageInfo;
	public String appnType[];
	SearchView calenderSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	Context context;
	private DrawerLayout mDrawerLayout;
	RelativeLayout layoutSlidingDrawer;
	TextView txtFilterTime;
	EditText editTxtFilterName;
	Button btnFilterGo, btnFilterClear;
	ArrayList<CalendarListPaginationModel> mfilteredAppts = new ArrayList<CalendarListPaginationModel>();
	public static String savedNoteTime;
	public static int selectedDatePosition;
	public static boolean isSaveNote;
	AsyncTask<Void, Void, Void> ChildEmployeeAsyncTask;
	public static Spinner spinnerChildEmployeeName;
	PhonenumbersAsyncTask phoneTask;
	DatabaseHandler dbHandler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// This is to restrict landscape for phone
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		dbHandler = new DatabaseHandler(this);
		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		employeeName = userData.getString(Constants.KEY_EMPLOYEE_NAME, "");
		CanCreateEvents = userData.getString(
				Constants.KEY_EMPLOYEE_CAN_CREATE_EVENTS, "");
		setContentView(R.layout.calendar_activity);
		
		ChildEmployeeAsyncTask = new ChildEmployeeAsyncTask(this, dealerID,
				employeeID, employeeName).execute();
		savedNoteTime = "";
		context = this;
		isListView = false;
		isPopupView = false;
		mContext = CalendarActivity.this;
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		pageInfo = getIntent().getStringExtra("PageInfo");
		customerId = getIntent().getStringExtra("CustomerId");
		customerName = getIntent().getStringExtra("CustomerFullName");
		CustomerAddress = getIntent().getStringExtra("CustomerAddress");
		backBtn = (ImageView) findViewById(R.id.back_icon);
		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		btn_add_appnt = (ImageView) findViewById(R.id.btn_add_appnt);
		spinnerChildEmployeeName = (Spinner) findViewById(R.id.spinner_child_employee);
		spinnerChildEmployeeName.setVisibility(View.VISIBLE);
		spinnerChildEmployeeName
		.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				// TODO Auto-generated method stub
				TextView selectedText = (TextView) parent.getChildAt(0);
				if (selectedText != null) {
					selectedText.setTextSize(17);
					selectedText.setTypeface(null, Typeface.BOLD);

				}
				employeeID = Singleton.getInstance().mChildEmployeeID
						.get(position);
				if (isListView) {
					Singleton.getInstance().mCalendarListData.clear();
					Singleton.getInstance().mCalendarListPaginationData
					.clear();

					Calendar mcalenda = Calendar.getInstance();
					SimpleDateFormat df = new SimpleDateFormat(
							"MM-dd-yyyy", Locale.US);
					String curentDateString = df.format(mcalenda
							.getTime());
					strNextRecordsDate = curentDateString;

					loadJsonDataForListView();
				} else {
					Singleton.getInstance().clearCalendarMonthData();
					Calendar mcalenda = Calendar.getInstance();
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd", Locale.US);
					String curentDateString = df.format(mcalenda
							.getTime());
					String[] separatedDay = (curentDateString)
							.split("-");
					String strCurrentYear = separatedDay[0]
							.replaceFirst("^0*", "");
					String strCurrentMonth = separatedDay[1]
							.replaceFirst("^0*", "");

					loadJsonDataForMontView(strCurrentYear,
							strCurrentMonth, 0);

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		// calenderSearchView=(SearchView)findViewById(R.id.calender_searchView);
		//
		// calenderSearchView.setOnQueryTextListener(new OnQueryTextListener() {
		//
		// @Override
		// public boolean onQueryTextSubmit(String query) {
		// // TODO Auto-generated method stub
		//
		// if(query.trim().length() !=0){
		//
		// searchAsyncTask=new
		// SmartSearchAsyncTask(context,dealerID,employeeID,query);
		// searchAsyncTask.execute();
		// }else{
		// Toast.makeText(context, Constants.TOAST_NO_DATA,
		// Toast.LENGTH_SHORT).show();
		// }
		// return false;
		// }
		//
		// @Override
		// public boolean onQueryTextChange(String newText) {
		// // TODO Auto-generated method stub
		// return false;
		// }
		// });
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		imgFilterDrawer = (Button) findViewById(R.id.buttonFilterdrawer);
		imgFilterDrawer.setOnClickListener(this);
		layoutSlidingDrawer = (RelativeLayout) findViewById(R.id.layoutDrawerLayout);
		txtFilterTime = (TextView) findViewById(R.id.editTextFilterTime);
		editTxtFilterName = (EditText) findViewById(R.id.editTextFilterName);
		btnFilterGo = (Button) findViewById(R.id.buttonFilter);
		btnFilterClear = (Button) findViewById(R.id.buttonClearFilter);
		txtFilterTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Calendar cal = Calendar.getInstance();
				hour = cal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE);
				TimePickerDialog timeDialog = new TimePickerDialog(context,
						apptTimePickerListener, hour, minute, false);
				timeDialog.show();

			}
		});
		btnFilterGo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				initListFilter();

			}

		});
		btnFilterClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isFilteredList = false;
				txtFilterTime.setText("Time");
				editTxtFilterName.setText("");
				mDrawerLayout.closeDrawer(layoutSlidingDrawer);
				mCustomAdapter = new CalMonthListAdapter(
						getApplicationContext(),
						Singleton.getInstance().mCalendarListPaginationData);
				calendarListView.addFooterView(inflatorView);
				calendarListView.setAdapter(mCustomAdapter);
			}
		});

		/*View.OnClickListener handler = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.btn_add_appnt:

					popUpWindowAppnts.showAsDropDown(v, 5, 0);
					break;

				default:
					break;
				}
			}
		};*/
		btn_add_appnt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popUpWindowAppnts.showAsDropDown(v, 5, 0);
			}
		});
		popupLayout = (LinearLayout) findViewById(R.id.cal_popup_layout);

		calMonthLV = (ListView) findViewById(R.id.cal_monthview_lv);
		calMonthLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				String strApptType = ((TextView) view
						.findViewById(R.id.textViewApptTitle)).getText()
						.toString();
				if (strApptType.equals("NOTES")
						|| strApptType.equals("CALENDAR-NOTES")) {
					showNotesPopupWindow("MonthAppts", ((TextView) view
							.findViewById(R.id.textViewApptTitle)).getText()
							.toString(), ((TextView) view
									.findViewById(R.id.textViewNotes)).getText()
									.toString(), ((TextView) view
											.findViewById(R.id.textViewApptTime)).getText()
											.toString(), "");
				}


				if (!strApptType.equals("CALENDAR-NOTES")
						&& !strApptType.equals("NOTES")) {
					Intent customerDetailsIntent = new Intent(
							CalendarActivity.this,
							CustomerDetailsActivity.class);

					customerDetailsIntent.putExtra("CustomerId",
							Singleton.getInstance().selectedDayApptsData
							.get(position).mCustomerId);

					//This flag is used to display the follow up in the customer detail screen
					boolean followupFlag = false;
					if(strApptType.equals("FOLLOW-UP")) {
						followupFlag = true;
					}
					customerDetailsIntent.putExtra("dispFollowup", followupFlag);

					customerDetailsIntent.putExtra("CustomerFullName",
							Singleton.getInstance().selectedDayApptsData
							.get(position).mCustomerName);

					customerDetailsIntent.putExtra(
							"CustomerAddress",
							Singleton.getInstance().selectedDayApptsData
							.get(position).mAddress
							+ ", "
							+ Singleton.getInstance().selectedDayApptsData
							.get(position).mCity
							+ ", "
							+ Singleton.getInstance().selectedDayApptsData
							.get(position).mState
							+ ", "
							+ Singleton.getInstance().selectedDayApptsData
							.get(position).mZip);
					startActivity(customerDetailsIntent);

				}

			}
		});
		popupLayout.setOnClickListener(this);
		mServiceHelper = new ServiceHelper(mContext);
		imgMonth = (Button) findViewById(R.id.buttonMonth);
		imgList = (Button) findViewById(R.id.buttonList);
		btnAppointments = (Button) findViewById(R.id.btn_appointments);
		btnFollowups = (Button) findViewById(R.id.btn_followups);
		// imageViewAdd = (ImageView) findViewById(R.id.imageViewadd);
		txtHeaderDateView = (TextView) findViewById(R.id.textView_dateview);
		// btnNotes = (Button) findViewById(R.id.btn_notes);
		// imageViewAdd.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		logoBtn.setOnClickListener(this);
		imgMonth.setOnClickListener(this);
		imgList.setOnClickListener(this);
		btnAppointments.setOnClickListener(this);
		btnFollowups.setOnClickListener(this);
		// btnNotes.setOnClickListener(this);
		if (!mContext.getResources().getBoolean(R.bool.is_tablet)) {
			popupClose = (ImageView) findViewById(R.id.popup_close);
			popupClose.setOnClickListener(this);
			imgPopUpClose = (ImageView) findViewById(R.id.imageViewPopupCancel);
			imgPopUpClose.setOnClickListener(this);
			imageViewNextDate = (ImageView) findViewById(R.id.imageViewNextDate);
			imageViewNextDate.setOnClickListener(this);
			imageViewPreviousDate = (ImageView) findViewById(R.id.imageViewPreviousDate);
			imageViewPreviousDate.setOnClickListener(this);
			popupDateTV = (TextView) findViewById(R.id.popup_dateview);
			popupDateTV.setTypeface(droidSansBold);
		}
		layoutMonthView = (RelativeLayout) findViewById(R.id.linearLayoutMonthView);
		layoutListView = (RelativeLayout) findViewById(R.id.linearLayoutListView);
		loaderInflator = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflatorView = loaderInflator.inflate(
				R.layout.calendar_list_load_more_view, null, false);
		// Singleton.getInstance().clearCalendarAppt_FollowUps_Notes();

		loadPopUpWindow();
		if (!Utilities.isConnectingToInternet(getApplicationContext())||!Utilities.isNetworkConnected(getApplicationContext())) {

			Singleton.getInstance().eventConfigAppntType.clear();
			Singleton.getInstance().eventConfigVisiType.clear();
			Singleton.getInstance().eventConfigAppntType = dbHandler.getEventConfigVales();
			Singleton.getInstance().eventConfigVisiType = dbHandler.getEventConfigVisitValues();
			initSqliteData();
		}else {
			eventConfigAsynTask = new EventConfigAsyncTask(mContext).execute();
			loadCurrentMontData();

			/*if (isOnCreate) {
				Calendar mcalenda = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
				String curentDateString = df.format(mcalenda.getTime());
				strNextRecordsDate = curentDateString;
				loadJsonDataForListView();
			}else {
				loadCurrentMontData();
				loadPopUpWindow();
			}*/

		}

	}



	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Calendar Activity");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method
	}

	private void loadCurrentMontData() {
		// TODO Auto-generated method stub
		Singleton.getInstance().clearCalendarMonthData();
		Calendar mcalenda = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String curentDateString = df.format(mcalenda.getTime());
		String[] separatedDay = (curentDateString).split("-");
		String strCurrentYear = separatedDay[0].replaceFirst("^0*", "");
		String strCurrentMonth = separatedDay[1].replaceFirst("^0*", "");
		getMonthData(strCurrentYear, strCurrentMonth, 0);

	}

	public PopupWindow popupWindow() {
		PopupWindow popupwindow = new PopupWindow(this);
		ListView listAppnttypes = new ListView(this);
		listAppnttypes.setAdapter(popUpAdapter(appnType));
		listAppnttypes
		.setOnItemClickListener(new AppntDropDownItemClickListener());
		popupwindow.setFocusable(true);
		// popupwindow.setWidth((int) getResources().getDimension(
		// R.dimen.twohundred50));
		popupwindow.setWidth((int) getResources()
				.getDimension(R.dimen.onefifty));
		popupwindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupwindow.setContentView(listAppnttypes);
		popupwindow.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.shape_popup));
		return popupwindow;

	}

	private ArrayAdapter<String> popUpAdapter(String appntTypeArray[]) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				CalendarActivity.this, android.R.layout.simple_list_item_1,
				appntTypeArray) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				String item = getItem(position);
				TextView listitem = new TextView(CalendarActivity.this);
				listitem.setText(item);
				listitem.setTypeface(droidSans);
				listitem.setTextColor(Color.BLACK);
				listitem.setPadding(10, 10, 10, 10);
				return listitem;
			}
		};
		return adapter;

	}

	@Override
	public void onClick(View v) {
		Paint paintNormal = new Paint();
		paintNormal.setARGB(0, 255, 255, 255);
		paintNormal.setFlags(Paint.UNDERLINE_TEXT_FLAG);

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonMonth:
			mfilteredAppts.clear();
			mCustomAdapter = new CalMonthListAdapter(getApplicationContext(),
					mfilteredAppts);
			if(mCustomAdapter != null && calendarListView != null){
				calendarListView.setAdapter(mCustomAdapter);
			}


			if (txtHeaderDateView.getVisibility() == View.INVISIBLE) {
				txtHeaderDateView.setVisibility(View.VISIBLE);
				// imageViewAdd.setVisibility(View.VISIBLE);
			}

			if (getResources().getBoolean(R.bool.is_tablet)) {
				int mOrientation = getResources().getConfiguration().orientation;
				switch (mOrientation) {
				case Configuration.ORIENTATION_LANDSCAPE:
					layoutCalendarView = (LinearLayout) findViewById(R.id.relativeLayoutCalendar);
					android.widget.LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
							0, LinearLayout.LayoutParams.MATCH_PARENT, (int) 1f);
					parms.setMargins(
							(int) getResources().getDimension(R.dimen.five),
							(int) getResources().getDimension(R.dimen.five),
							(int) getResources().getDimension(R.dimen.five),
							(int) getResources().getDimension(R.dimen.five));
					layoutCalendarView.setLayoutParams(parms);
					popupLayout.setVisibility(View.VISIBLE);
					break;
				case Configuration.ORIENTATION_PORTRAIT:
					popupLayout.setVisibility(View.VISIBLE);
					break;
				}
			}
			/*Singleton.getInstance().mCalendarListData.clear();
			Singleton.getInstance().mCalendarListPaginationData.clear();*/
			strNextRecordsDate = "";
			imgMonth.setBackgroundResource(R.drawable.ic_month_button_selected);
			imgList.setBackgroundResource(R.drawable.ic_list_button_normal);
			isListView = false;
			//	initMontView();
			if (Utilities.isNetworkConnected(getApplicationContext())) {
				loadPopUpWindow();
				loadCurrentMontData();
			}else {
				initSqliteData();
			}
			break;
		case R.id.buttonList:
			if (!isListView) {
				appntList = new ArrayList<String>();
				if (pageInfo.equals("CustomerDetailsActivity")
						|| pageInfo.equals("AddProspectActivity")) {
					appntList.add("Appointment");
					appntList.add("Visit");
					appntList.add("Note");
				} else {
					appntList.add("Note");
				}
				appnType = new String[appntList.size()];
				appntList.toArray(appnType);
				popUpWindowAppnts = popupWindow();
				txtHeaderDateView.setVisibility(View.INVISIBLE);
				mDrawerLayout
				.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				if (getResources().getBoolean(R.bool.is_tablet)) {
					int mOrientation = getResources().getConfiguration().orientation;
					switch (mOrientation) {
					case Configuration.ORIENTATION_LANDSCAPE:
						layoutCalendarView = (LinearLayout) findViewById(R.id.relativeLayoutCalendar);
						android.widget.LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,
								LinearLayout.LayoutParams.MATCH_PARENT,
								(int) 2.0f);
						layoutCalendarView.setLayoutParams(parms);
						popupLayout.setVisibility(View.GONE);
						break;
					case Configuration.ORIENTATION_PORTRAIT:
						popupLayout.setVisibility(View.GONE);

						break;
					}
				}
				//	Singleton.getInstance().mCalendarListPaginationData.clear();
				imgMonth.setBackgroundResource(R.drawable.ic_month_button_normal);
				imgList.setBackgroundResource(R.drawable.ic_list_button_selected);
				isListView = true;
				initListView();
				if (!Utilities.isNetworkConnected(getApplicationContext())) {
					initSqliteData();
				}



			}
			break;
		case R.id.btn_appointments:
			showSelectedDateAppointments();
			break;
		case R.id.btn_followups:
			selectedDatestr = CalendarAdapter.strSelectedDate;

			Singleton.getInstance().selectedDayApptsData.clear();
			if (arr_month_data != null) {
				for (int i = 0; i < arr_month_data.size(); i++) {
					if (arr_month_data.get(i).mFormattedApptDate
							.equals(selectedDatestr)
							&& (arr_month_data.get(i).mEventType
									.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
						Singleton.getInstance().selectedDayApptsData
						.add(arr_month_data.get(i));
					}
				}
			}
			calmonthAdapter = new CalMonthApptsAdapter(this, selectedDatestr,
					Singleton.getInstance().selectedDayApptsData);
			calMonthLV.setAdapter(calmonthAdapter);
			btnAppointments
			.setBackgroundResource(R.drawable.ic_appointments_normal);
			btnFollowups
			.setBackgroundResource(R.drawable.ic_followups_selected);

			break;
		case R.id.cal_popup_layout:
			popupLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.imageViewPopupCancel:
		case R.id.popup_close:
			if (!mContext.getResources().getBoolean(R.bool.is_tablet)) {
				isPopupView = false;
				isCalendarLoadBtn = false;
			}
			popupLayout.setVisibility(View.GONE);
			break;
		case R.id.imageViewNextDate:
			selectedDatestr = CalendarAdapter.strSelectedDate;
			String[] dateArr = selectedDatestr.split(" ");
			int nxtDate = CalendarAdapter.formatedDayString
					.indexOf(selectedDatestr);
			String nxtDateStr = CalendarAdapter.formatedDayString
					.get(nxtDate + 1);
			String[] dateArr2 = nxtDateStr.split(" ");
			if (!dateArr[0].equals(dateArr2[0])) {
				isCalendarLoadBtn = true;
				setNextMonth();
				selectedDatestr = nxtDateStr;
				Singleton.getInstance().selectedDayApptsData.clear();
				if (arr_month_data != null) {
					for (int i = 0; i < arr_month_data.size(); i++) {
						if (arr_month_data.get(i).mFormattedApptDate
								.equals(selectedDatestr)
								&& (!arr_month_data.get(i).mEventType
										.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
							Singleton.getInstance().selectedDayApptsData
							.add(arr_month_data.get(i));
						}
					}
				}
				calmonthAdapter = new CalMonthApptsAdapter(this,
						selectedDatestr,
						Singleton.getInstance().selectedDayApptsData);
				// calmonthAdapter.notifyDataSetChanged();
				calMonthLV.setAdapter(calmonthAdapter);
				btnAppointments
				.setBackgroundResource(R.drawable.ic_appointments_selected);
				btnFollowups
				.setBackgroundResource(R.drawable.ic_followups_normal);
				CalendarAdapter.strSelectedDate = selectedDatestr;
				popupDateTV.setText(selectedDatestr);

			} else {
				selectedDatestr = nxtDateStr;
				Singleton.getInstance().selectedDayApptsData.clear();
				if (arr_month_data != null) {
					for (int i = 0; i < arr_month_data.size(); i++) {
						if (arr_month_data.get(i).mFormattedApptDate
								.equals(selectedDatestr)
								&& (!arr_month_data.get(i).mEventType
										.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
							Singleton.getInstance().selectedDayApptsData
							.add(arr_month_data.get(i));
						}
					}
				}
				calmonthAdapter = new CalMonthApptsAdapter(this,
						selectedDatestr,
						Singleton.getInstance().selectedDayApptsData);
				calMonthLV.setAdapter(calmonthAdapter);
				btnAppointments
				.setBackgroundResource(R.drawable.ic_appointments_selected);
				btnFollowups
				.setBackgroundResource(R.drawable.ic_followups_normal);
				CalendarAdapter.strSelectedDate = selectedDatestr;
				popupDateTV.setText(selectedDatestr);
			}
			break;
		case R.id.imageViewPreviousDate:
			selectedDatestr = CalendarAdapter.strSelectedDate;
			String[] prevDateArr = selectedDatestr.split(" ");
			int preDate = CalendarAdapter.formatedDayString
					.indexOf(selectedDatestr);
			// if (preDate != 0 && preDate != -1) {
			if (preDate != 0) {
				String prevDateStr = CalendarAdapter.formatedDayString
						.get(preDate - 1);
				String[] prevDateArr2 = prevDateStr.split(" ");
				if (!prevDateArr[0].equals(prevDateArr2[0])) {
					isCalendarLoadBtn = true;
					setPreviousMonth();
					selectedDatestr = prevDateStr;
					Singleton.getInstance().selectedDayApptsData.clear();
					if (arr_month_data != null) {
						for (int i = 0; i < arr_month_data.size(); i++) {
							if (arr_month_data.get(i).mFormattedApptDate
									.equals(selectedDatestr)
									&& (!arr_month_data.get(i).mEventType
											.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
								Singleton.getInstance().selectedDayApptsData
								.add(arr_month_data.get(i));
							}
						}
					}
					calmonthAdapter = new CalMonthApptsAdapter(this,
							selectedDatestr,
							Singleton.getInstance().selectedDayApptsData);
					calMonthLV.setAdapter(calmonthAdapter);
					btnAppointments
					.setBackgroundResource(R.drawable.ic_appointments_selected);
					btnFollowups
					.setBackgroundResource(R.drawable.ic_followups_normal);
					CalendarAdapter.strSelectedDate = selectedDatestr;
					popupDateTV.setText(selectedDatestr);
				} else {
					selectedDatestr = prevDateStr;
					Singleton.getInstance().selectedDayApptsData.clear();
					if (arr_month_data != null) {
						for (int i = 0; i < arr_month_data.size(); i++) {
							if (arr_month_data.get(i).mFormattedApptDate
									.equals(selectedDatestr)
									&& (!arr_month_data.get(i).mEventType
											.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
								Singleton.getInstance().selectedDayApptsData
								.add(arr_month_data.get(i));
							}
						}
					}
					calmonthAdapter = new CalMonthApptsAdapter(this,
							selectedDatestr,
							Singleton.getInstance().selectedDayApptsData);
					calMonthLV.setAdapter(calmonthAdapter);
					btnAppointments
					.setBackgroundResource(R.drawable.ic_appointments_selected);
					btnFollowups
					.setBackgroundResource(R.drawable.ic_followups_normal);
					CalendarAdapter.strSelectedDate = selectedDatestr;
					popupDateTV.setText(selectedDatestr);
				}
			} else {
				String prevDateStr = CalendarAdapter.formatedDayString
						.get(preDate);
				Calendar cal = Calendar.getInstance();
				Date date = null;
				SimpleDateFormat dtformat = new SimpleDateFormat("MMMM dd, yyyy");
				try {
					date = dtformat.parse(prevDateStr);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cal.setTime(date);
				cal.add(Calendar.DAY_OF_YEAR, -1);
				String oneDayBefore = dtformat.format(cal.getTime());
				isCalendarLoadBtn = true;
				setPreviousMonth();
				selectedDatestr = oneDayBefore;
				Singleton.getInstance().selectedDayApptsData.clear();
				if (arr_month_data != null) {
					for (int i = 0; i < arr_month_data.size(); i++) {
						if (arr_month_data.get(i).mFormattedApptDate
								.equals(selectedDatestr)
								&& (!arr_month_data.get(i).mEventType
										.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
							Singleton.getInstance().selectedDayApptsData
							.add(arr_month_data.get(i));
						}
					}
				}
				calmonthAdapter = new CalMonthApptsAdapter(this,
						selectedDatestr,
						Singleton.getInstance().selectedDayApptsData);
				calMonthLV.setAdapter(calmonthAdapter);
				btnAppointments
				.setBackgroundResource(R.drawable.ic_appointments_selected);
				btnFollowups
				.setBackgroundResource(R.drawable.ic_followups_normal);
				CalendarAdapter.strSelectedDate = selectedDatestr;
				popupDateTV.setText(selectedDatestr);
			}
			break;
		case R.id.back_icon:
			if (!pageInfo.equals("CustomerDetailsActivity")) {
				Intent backIntent = new Intent(CalendarActivity.this,
						MainActivity.class);
				startActivity(backIntent);
				finish();
			} else {
				finish();
			}
			break;
		case R.id.image_back_icon:
			gotoHomeActivity();
			break;

		case R.id.buttonFilterdrawer:
			if (mDrawerLayout.isDrawerOpen(layoutSlidingDrawer)) {
				mDrawerLayout.closeDrawer(layoutSlidingDrawer);

			} else {
				mDrawerLayout.openDrawer(layoutSlidingDrawer);

			}
			break;
		}
	}

	private void loadPopUpWindow() {
		appntList = new ArrayList<String>();
		if (pageInfo.equals("CustomerDetailsActivity")
				|| pageInfo.equals("AddProspectActivity")) {
			appntList.add("Appointment");
			appntList.add("Visit");
			appntList.add("Note");
		} else {
			appntList.add("Note");
		}
		appnType = new String[appntList.size()];
		appntList.toArray(appnType);
		popUpWindowAppnts = popupWindow();

	}

	private void showSelectedDateAppointments() {
		// TODO Auto-generated method stub

		selectedDatestr = CalendarAdapter.strSelectedDate;
		Singleton.getInstance().selectedDayApptsData.clear();
		if (arr_month_data != null) {
			for (int i = 0; i < arr_month_data.size(); i++) {
				if (arr_month_data.get(i).mFormattedApptDate
						.equals(selectedDatestr)
						&& (!arr_month_data.get(i).mEventType
								.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
					Singleton.getInstance().selectedDayApptsData
					.add(arr_month_data.get(i));
				}
			}
		}

		calmonthAdapter = new CalMonthApptsAdapter(this, selectedDatestr,
				Singleton.getInstance().selectedDayApptsData);

		calMonthLV.setAdapter(calmonthAdapter);
		btnAppointments
		.setBackgroundResource(R.drawable.ic_appointments_selected);
		btnFollowups.setBackgroundResource(R.drawable.ic_followups_normal);
	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		if (!pageInfo.equals("CustomerDetailsActivity")) {
			Intent backIntent = new Intent(CalendarActivity.this,
					MainActivity.class);
			backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(backIntent);
			finish();
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		gotoHomeActivity();
	}

	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextChange(String query) {
		// TODO Auto-generated method stub

		if (query.trim().length() != 0) {

			searchAsyncTask = new SmartSearchAsyncTask(context, dealerID,
					employeeID, query);
			searchAsyncTask.execute();
		} else {
			Toast.makeText(context, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT)
			.show();
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public Runnable calendarUpdater = new Runnable() {
		@Override
		public void run() {
			items.clear();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			for (int i = 0; i < 7; i++) {
				itemmonth.add(GregorianCalendar.DATE, 1);
			}
			adapter.notifyDataSetChanged();
		}
	};

	@SuppressWarnings("deprecation")
	private void initMontView() {
		// TODO Auto-generated method stub
		if (getResources().getBoolean(R.bool.is_tablet)) {
			btn_add_appnt.setVisibility(View.VISIBLE);
		}
		txtHeaderDateView.setVisibility(View.VISIBLE);
		imgFilterDrawer.setVisibility(View.GONE);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		layoutListView.setVisibility(View.GONE);
		layoutMonthView.setVisibility(View.VISIBLE);

		if (savedNoteTime.length() <= 0 && savedNoteTime.equals("")) {
			savedNoteTime = "";
			month = (GregorianCalendar) GregorianCalendar.getInstance();
		} else {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
			Date date;
			try {
				date = df.parse(savedNoteTime);
				Calendar geo = Calendar.getInstance();
				geo.setTime(date);
				month = (GregorianCalendar) geo;
				savedNoteTime = "";
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TextView title = (TextView) findViewById(R.id.textView_dateview);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		itemmonth = (GregorianCalendar) month.clone();
		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month);
		daysadapter = new CalendarDaysAdapter(CalendarActivity.this);
		calendarGridView = (GridView) findViewById(R.id.gridview);
		calendarGridView.setAdapter(adapter);
		handler = new Handler();
		handler.post(calendarUpdater);
		GridView gridViewDays = (GridView) findViewById(R.id.gridViewdays);
		gridViewDays.setAdapter(daysadapter);
		gesturedetector = new GestureDetector(new MyGestureListener());
		calendarGridView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gesturedetector.onTouchEvent(event);
				if (gesturedetector.onTouchEvent(event)) {
					return true;
				}
				return false;
			}
		});
		calendarGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String selectedGridDate = CalendarAdapter.dayString
						.get(position);
				String[] separatedTime = selectedGridDate.split("-");
				String gridvalueString = separatedTime[2].replaceFirst("^0*",
						"");
				int gridvalue = Integer.parseInt(gridvalueString);
				if ((gridvalue > 10) && (position < 8)) {
					//setPreviousMonth();
				} else if ((gridvalue <= 15) && (position > 28)) {
					//setNextMonth();
				}
				else {
					selectedDatePosition = position;
					((CalendarAdapter) parent.getAdapter())
					.setSelected(v, position);
					previousPosition = position;
					Log.d("Calendar List Pagination",
							Singleton.getInstance().selectedDayApptsData.size()
							+ "");
				}

				// popupLayout.setVisibility(View.VISIBLE);
			}
		});
	}

	// this is to set the next month
	protected void setNextMonth() {
		CalendarAdapter.previousView = null;
		CalendarAdapter.curentDateString = CalendarAdapter.df
				.format(CalendarAdapter.selectedDate.getTime());
		String[] separatedDays = CalendarAdapter.curentDateString.split("-");
		CalendarAdapter.strCurrentDay = separatedDays[2]
				.replaceFirst("^0*", "");
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String curentDateString = df.format(month.getTime());
		String[] separatedDay = (curentDateString).split("-");
		String strCurrentYear = separatedDay[0].replaceFirst("^0*", "");
		String strCurrentMonth = separatedDay[1].replaceFirst("^0*", "");
		getMonthData(strCurrentYear, strCurrentMonth, 1);
	}

	protected void setPreviousMonth() {
		CalendarAdapter.previousView = null;
		CalendarAdapter.curentDateString = CalendarAdapter.df
				.format(CalendarAdapter.selectedDate.getTime());
		String[] separatedDays = CalendarAdapter.curentDateString.split("-");
		CalendarAdapter.strCurrentDay = separatedDays[2]
				.replaceFirst("^0*", "");
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String curentDateString = df.format(month.getTime());
		String[] separatedDay = (curentDateString).split("-");
		String strCurrentYear = separatedDay[0].replaceFirst("^0*", "");
		String strCurrentMonth = separatedDay[1].replaceFirst("^0*", "");
		getMonthData(strCurrentYear, strCurrentMonth, -1);

	}

	public void refreshCalendar() {
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		handler.post(calendarUpdater); // generate some calendar items
		/*
		 * txtDateView.setText(android.text.format.DateFormat.format("MMMM yyyy",
		 * month));
		 */
		TextView title = (TextView) findViewById(R.id.textView_dateview);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return gesturedetector.onTouchEvent(ev);
	}

	class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

		private static final int SWIPE_MIN_DISTANCE = 150;

		private static final int SWIPE_MAX_OFF_PATH = 100;

		private static final int SWIPE_THRESHOLD_VELOCITY = 100;

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,

				float velocityY) {

			float dX = e2.getX() - e1.getX();
			float dY = e1.getY() - e2.getY();
			if (Math.abs(dY) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(dX) >= SWIPE_MIN_DISTANCE) {
				if (!isPopupView && !isListView) {
					if (dX > 0) {
						// Right Swipe
						setPreviousMonth();
					} else {
						// Left Swipe
						setNextMonth();
					}
				}
				return true;

			} else if (Math.abs(dX) >= SWIPE_THRESHOLD_VELOCITY &&

					Math.abs(velocityY) >= SWIPE_THRESHOLD_VELOCITY &&

					Math.abs(dY) >= SWIPE_MIN_DISTANCE) {
				if (!isPopupView && !isListView) {
					if (dY > 0) {
						// Up Swipe

					} else {
						// Down Swipe
					}
				}
				return true;
			}
			return false;
		}
	}

	private void initListView() {
		// TODO Auto-generated method stub
		if (getResources().getBoolean(R.bool.is_tablet)) {
			btn_add_appnt.setVisibility(View.GONE);
		}
		isFilteredList = false;
		txtHeaderDateView.setVisibility(View.GONE);
		imgFilterDrawer.setVisibility(View.VISIBLE);
		layoutListView.setVisibility(View.VISIBLE);
		layoutMonthView.setVisibility(View.GONE);
		calendarListView = (ListView) findViewById(R.id.calendarlistView);
		calendarListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (isFilteredList) {
					// get data from filtered Appts arraylist
					if (view.getTag().toString().equals("isContent")) {
						String strApptType = ((TextView) view
								.findViewById(R.id.textViewApptTitle))
								.getText().toString();
						if (strApptType.equals("NOTES")
								|| strApptType.equals("CALENDAR-NOTES")) {
							showNotesPopupWindow("ListAppts", ((TextView) view
									.findViewById(R.id.textViewApptTitle))
									.getText().toString(), ((TextView) view
											.findViewById(R.id.textViewNotes))
											.getText().toString(), ((TextView) view
													.findViewById(R.id.textViewApptTime))
													.getText().toString(), mfilteredAppts
													.get(position).mFormattedApptDate);
						}
						if (!strApptType.equals("CALENDAR-NOTES")
								&& !strApptType.equals("NOTES")) {
							Intent customerDetailsIntent = new Intent(
									CalendarActivity.this,
									CustomerDetailsActivity.class);
							customerDetailsIntent.putExtra("CustomerId",
									mfilteredAppts.get(position).mCustomerId);
							//This flag is used to display the follow up in the customer detail screen
							boolean followupFlag = false;
							if(strApptType.equals("FOLLOW-UP")) {
								followupFlag = true;
							}
							customerDetailsIntent.putExtra("dispFollowup", followupFlag);

							customerDetailsIntent.putExtra("CustomerFullName",
									mfilteredAppts.get(position).mCustomerName);
							customerDetailsIntent.putExtra(
									"CustomerAddress",
									mfilteredAppts.get(position).mAddress
									+ ", "
									+ mfilteredAppts.get(position).mCity
									+ ", "
									+ mfilteredAppts.get(position).mState
									+ ", "
									+ mfilteredAppts.get(position).mZip);
							startActivity(customerDetailsIntent);

						}
					}
				} else {
					if (view.getTag().toString().equals("isContent")) {
						String strApptType = ((TextView) view
								.findViewById(R.id.textViewApptTitle))
								.getText().toString();
						if (strApptType.equals("NOTES")
								|| strApptType.equals("CALENDAR-NOTES")) {
							showNotesPopupWindow("ListAppts", ((TextView) view
									.findViewById(R.id.textViewApptTitle))
									.getText().toString(), ((TextView) view
											.findViewById(R.id.textViewNotes))
											.getText().toString(), ((TextView) view
													.findViewById(R.id.textViewApptTime))
													.getText().toString(), Singleton
													.getInstance().mCalendarListPaginationData
													.get(position).mFormattedApptDate);
						}
						if (!strApptType.equals("CALENDAR-NOTES")
								&& !strApptType.equals("NOTES")) {
							Intent customerDetailsIntent = new Intent(
									CalendarActivity.this,
									CustomerDetailsActivity.class);
							customerDetailsIntent.putExtra(
									"CustomerId",
									Singleton.getInstance().mCalendarListPaginationData
									.get(position).mCustomerId);

							//This flag is used to display the follow up in the customer detail screen
							boolean followupFlag = false;
							if(strApptType.equals("FOLLOW-UP")) {
								followupFlag = true;
							}
							customerDetailsIntent.putExtra("dispFollowup", followupFlag);

							customerDetailsIntent.putExtra(
									"CustomerFullName",
									Singleton.getInstance().mCalendarListPaginationData
									.get(position).mCustomerName);
							customerDetailsIntent.putExtra(
									"CustomerAddress",
									Singleton.getInstance().mCalendarListPaginationData
									.get(position).mAddress
									+ ", "
									+ Singleton.getInstance().mCalendarListPaginationData
									.get(position).mCity
									+ ", "
									+ Singleton.getInstance().mCalendarListPaginationData
									.get(position).mState
									+ ", "
									+ Singleton.getInstance().mCalendarListPaginationData
									.get(position).mZip);
							startActivity(customerDetailsIntent);

						}
					}
				}
			}
		});

		if (Utilities.isConnectingToInternet(getApplicationContext())||Utilities.isNetworkConnected(getApplicationContext())) {
			calendarListView.addFooterView(inflatorView);
			Singleton.getInstance().mCalendarListPaginationData.clear();
			if (strNextRecordsDate.equals("")) {
				Calendar mcalenda = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
				String curentDateString = df.format(mcalenda.getTime());
				strNextRecordsDate = curentDateString;
				loadJsonDataForListView();
			}
		}else {
			initSqliteData();
		}

		inflatorView.findViewById(R.id.buttonLoadmore).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						getNextRecordsDate(strNextRecordsDate);
						loadJsonDataForListView();
						// new loadMoreCalendarData().execute();
						calendarListView.setSelection(calendarListView
								.getAdapter().getCount() - 1);

					}
				});
	}

	public void getMonthData(String year, String month, int calendarSwipeEvent) {
		String strKey = year + "/" + month;
		switch (calendarSwipeEvent) {
		case 0:
			/*
			 * if
			 * (Singleton.getInstance().mcalendarMonthData.containsKey(strKey))
			 * { initMontView(); } else {
			 */
			loadJsonDataForMontView(year, month, calendarSwipeEvent);
			// }
			break;
		case 1:
		case -1:
			/*
			 * if
			 * (Singleton.getInstance().mcalendarMonthData.containsKey(strKey))
			 * { refreshCalendar(); } else {
			 */
			// Singleton.getInstance().clearCalendarAppt_FollowUps_Notes();
			loadJsonDataForMontView(year, month, calendarSwipeEvent);
			// }
			break;
		}
	}

	class CalendarAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context mContext;
		String mRequestUrl, mMethodType;
		ServiceHelper serviceHelper;
		String mKey;
		JSONObject responseJson, responseJsonKPI;
		ArrayList<JSONObject> mRequestJson;
		//		ActivityIndicator pDialog;
		int mSwipeEvent;

		public CalendarAsyncTask(Context context, String requestUrl,
				String methodType, ArrayList<JSONObject> requestJson,
				String Key, int SwipeEvent) {
			this.mContext = context;
			this.mRequestUrl = requestUrl;
			this.mRequestJson = requestJson;
			this.mMethodType = methodType;
			this.mKey = Key;
			this.mSwipeEvent = SwipeEvent;
			serviceHelper = new ServiceHelper(this.mContext);
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
			pDialog = new ActivityIndicator(mContext);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {

			// TODO Auto-generated method stub
			if (isListView) {
				runOnUiThread(new Runnable() {
					public void run() {
						responseJson = serviceHelper.jsonSendHTTPRequest(
								mRequestJson.toString(), mRequestUrl,
								mMethodType);
					}
				});
			} else {
				responseJson = serviceHelper.jsonSendHTTPRequest(
						mRequestJson.toString(), mRequestUrl, mMethodType);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (isListView) {
				parseListJsonDate(responseJson);
				pDialog.dismiss();
			} else {
				switch (mSwipeEvent) {
				case 0:
					// Singleton.getInstance().clearCalendarAppt_FollowUps_Notes();
					parseJsonDate(responseJson);
					initMontView();
					showSelectedDateAppointments();
					break;
				case -1:
				case 1:
					/*
					 * if (Singleton.getInstance().mcalendarMonthData
					 * .containsKey(mKey)) { refreshCalendar(); } else {
					 */
					// Singleton.getInstance().clearCalendarAppt_FollowUps_Notes();
					parseJsonDate(responseJson);
					refreshCalendar();
					calmonthAdapter.notifyDataSetChanged();
					// }
					break;
				}
			}

			if (calmonthAdapter != null) {
				selectedDatestr = CalendarAdapter.strSelectedDate;
				ArrayList<CalendarListPaginationModel> selectedDayApptsData = new ArrayList<CalendarListPaginationModel>();
				if (arr_month_data != null) {
					for (int i = 0; i < arr_month_data.size(); i++) {
						if (arr_month_data.get(i).mFormattedApptDate
								.equals(selectedDatestr)
								&& (!arr_month_data.get(i).mEventType
										.equals(Constants.KEY_CAL_FOLLOW_UPS))) {
							selectedDayApptsData.add(arr_month_data.get(i));
						}
					}
				}
				// calmonthAdapter.notifyDataSetChanged();
				calMonthLV.setAdapter(calmonthAdapter);
			}
			btnAppointments.performClick();
			//			calmonthAdapter.notifyDataSetChanged();
			pDialog.dismiss();
		}

		private void parseJsonDate(JSONObject responseJson2) {
			// TODO Auto-generated method stub
			if(responseJson2 != null){
				JSONObject localJsonObject;
				JSONArray localJsonArray;
				JSONArray calendarJsonArray;
				DateFormat df = new SimpleDateFormat("MM/dd/yyy");
				DateFormat jsonDateFormater = new SimpleDateFormat(
						"MM/dd/yyy hh:mm aaa");
				DateFormat headerDateFormat = new SimpleDateFormat(
						"EEE, MMM d, yyy", Locale.US);
				Calendar jsonCalendar = Calendar.getInstance();

				try {

					calendarJsonArray = responseJson
							.getJSONArray(Constants.JSON_KEY_SC);
					arr_month_data = new ArrayList<CalendarListPaginationModel>();
					Singleton.getInstance().mCalendarAppointmentsData.clear();
					Singleton.getInstance().mCalendarFollowUpsData.clear();
					Singleton.getInstance().mCalendarNotesData.clear();
					Singleton.getInstance().mCalendarMonthViewData.clear();



					if (calendarJsonArray.length() != 0) {
						ArrayList<String> monthCalAppts = new ArrayList<String>();
						ArrayList<String> monthCalFollowups = new ArrayList<String>();
						ArrayList<String> monthCalNotes = new ArrayList<String>();
						if (calendarJsonArray != null) {
							for (int j = 0; j < calendarJsonArray.length(); j++) {
								CalendarListPaginationModel mCalendarModel = new CalendarListPaginationModel();
								JSONObject calJsonObject = calendarJsonArray
										.getJSONObject(j);
								mCalendarModel.mAddress = calJsonObject.get(
										Constants.JSON_KEY_ADDRESS).toString();
								mCalendarModel.mFormattedApptDate = calJsonObject
										.get(Constants.JSON_KEY_FORMATED_APPT_DATE)
										.toString();
								mCalendarModel.mApptTime = calJsonObject.get(
										Constants.JSON_KEY_APPT_TIME).toString();
								mCalendarModel.mCity = calJsonObject.get(
										Constants.JSON_KEY_CITY).toString();

								mCalendarModel.mCustomerId = calJsonObject.get(
										Constants.JSON_KEY_CUSTOMER_ID).toString();
								mCalendarModel.mCustomerName = calJsonObject.get(
										Constants.JSON_KEY_CUSTOMER_NAME)
										.toString();
								mCalendarModel.mEventNotes = calJsonObject.get(
										Constants.JSON_KEY_EVENT_NOTES).toString();
								mCalendarModel.mEventType = calJsonObject.get(
										Constants.JSON_KEY_EVENT_TYPE).toString();

								mCalendarModel.mLeadOrVisitType = calJsonObject
										.get(Constants.JSON_KEY_LEAD_OR_VISIT_TYPE)
										.toString();
								mCalendarModel.mState = calJsonObject.get(
										Constants.JSON_KEY_STATE).toString();

								mCalendarModel.mZip = calJsonObject.get(
										Constants.JSON_KEY_ZIP).toString();
								/*
								 * if (!mCalendarModel.mApptDateTime.equals("")) {
								 * Date calendarDate;
								 * if(mCalendarModel.mApptDateTime.length() <= 10) {
								 * calendarDate = df
								 * .parse(mCalendarModel.mApptDateTime); } else {
								 * calendarDate = jsonDateFormater
								 * .parse(mCalendarModel.mApptDateTime); }
								 * 
								 * jsonCalendar.setTime(calendarDate);
								 * mCalendarModel.mDate = df
								 * .format(jsonCalendar.getTime());
								 * mCalendarModel.mTime = jsonCalendar.HOUR + ":" +
								 * jsonCalendar.MINUTE + jsonCalendar.AM_PM;
								 * mCalendarModel.mHeaderDate = headerDateFormat
								 * .format(jsonCalendar.getTime()); String[]
								 * separatedDay = (mCalendarModel.mDate
								 * .split("/")); mCalendarModel.mCalendarDay =
								 * separatedDay[1] .replaceFirst("^0*", ""); }
								 */
								//
								if (mCalendarModel.mEventType.equals("FOLLOW-UP")) {
									monthCalFollowups
									.add(mCalendarModel.mFormattedApptDate);
								} else if (mCalendarModel.mEventType
										.equals("CALENDAR-NOTES")
										|| mCalendarModel.mEventType
										.equals("NOTES")) {
									monthCalNotes
									.add(mCalendarModel.mFormattedApptDate);
								} else {
									monthCalAppts
									.add(mCalendarModel.mFormattedApptDate);
								}
								arr_month_data.add(mCalendarModel);

							}
						}
						Singleton.getInstance().mCalendarMonthViewData = arr_month_data;
						Singleton.getInstance().mCalendarAppointmentsData = monthCalAppts;
						Singleton.getInstance().mCalendarFollowUpsData = monthCalFollowups;
						Singleton.getInstance().mCalendarNotesData = monthCalNotes;

						Singleton.getInstance().mcalendarMonthData.put(mKey,
								arr_month_data);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void loadJsonDataForMontView(String year, String month,
			int calendarSwipeEvent) {
		String strKey = year + "/" + month;
		JSONObject reqObj = new JSONObject();
		ArrayList<JSONObject> reqArr = new ArrayList<JSONObject>();
		try {
			reqObj.put(Constants.KEY_CALENDAR_CLANDER_VIEW,
					Constants.KEY_CALENDAR_LIST);
			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
			reqObj.put(Constants.KEY_CALENDAR_EVENTDATE, "");
			reqObj.put(Constants.KEY_CALENDAR_CURRENTMONTH, month);
			reqObj.put(Constants.KEY_CALENDAR_CURRENTYEAR, year);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqArr.add(reqObj);

		calendarAsyncTask = (CalendarAsyncTask) new CalendarAsyncTask(
				CalendarActivity.this, Constants.URL_CALENDAR,
				Constants.REQUEST_TYPE_POST, reqArr, strKey, calendarSwipeEvent)
		.execute();
	}

	// Listview list adapter
	private class MyCustomAdapter extends BaseAdapter {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;
		private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

		private ArrayList<CalendarListPaginationModel> mListDAta = new ArrayList<CalendarListPaginationModel>();
		private LayoutInflater mInflater;

		private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();

		public MyCustomAdapter(
				ArrayList<CalendarListPaginationModel> mCalendarListPaginationData) {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.mListDAta = mCalendarListPaginationData;
		}

		/*
		 * public void addItem(final CalendarListPaginationModel item) {
		 * mListDAta.add(item); notifyDataSetChanged(); }
		 * 
		 * public void addSeparatorItem(final CalendarListPaginationModel item)
		 * { mListDAta.add(item); // save separator position
		 * mSeparatorsSet.add(mListDAta.size() - 1); notifyDataSetChanged(); }
		 */

		@Override
		public int getItemViewType(int position) {
			return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR
					: TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_MAX_COUNT;
		}

		@Override
		public int getCount() {
			return mListDAta.size();
		}

		@Override
		public CalendarListPaginationModel getItem(int position) {
			return mListDAta.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolders holder = null;
			int type = getItemViewType(position);
			System.out.println("getView " + position + " " + convertView
					+ " type = " + type);
			if (convertView == null) {
				holder = new ViewHolders();
				if (mListDAta.get(position).mEventType.equals("isHeader")) {
					convertView = mInflater.inflate(
							R.layout.calendar_list_header_view, null);
					holder.img_add_icon = (ImageView) convertView
							.findViewById(R.id.imageViewAddIcon);
					holder.txt_header_title_date = (TextView) convertView
							.findViewById(R.id.textView_header_title);
					holder.txt_header_day = (TextView) convertView
							.findViewById(R.id.textView1);

					holder.txt_header_title_date.setText(mListDAta
							.get(position).mFormattedApptDate);
					holder.txt_header_day.setText(getDayOfWeek(mListDAta
							.get(position).mFormattedApptDate));
				} else {
					convertView = mInflater.inflate(
							R.layout.calendar_list_view_row, null);
					holder.img_appt_separator_icon = (ImageView) convertView
							.findViewById(R.id.imageViewApptSeprator);
					holder.txt_Appt_Name = (TextView) convertView
							.findViewById(R.id.textViewApptTitle);
					holder.txt_Appt_Detail = (TextView) convertView
							.findViewById(R.id.textViewApptDetail);
					holder.txt_time = (TextView) convertView
							.findViewById(R.id.textViewApptTime);
					holder.txt_Customer_Name = (TextView) convertView
							.findViewById(R.id.textViewName);
					holder.txt_Customer_Address1 = (TextView) convertView
							.findViewById(R.id.textViewAddress1);
					holder.txt_Customer_Address2 = (TextView) convertView
							.findViewById(R.id.textViewAddress2);
					holder.img_call_icon = (ImageView) convertView
							.findViewById(R.id.imageViewCall);

					if (!(mListDAta.get(position).mEventType
							.equals("CALENDAR-NOTES"))
							|| !(mListDAta.get(position).mEventType
									.equals("NOTES"))) {
						holder.img_appt_separator_icon
						.setBackgroundResource(R.drawable.shape_circle_green);
						holder.txt_Appt_Name.setTextColor(getResources()
								.getColor(R.color.green_circle));
						// viewholder.txt_Appt_Detail.setTextColor(getResources().getColor(R.color.lite_orange_txt_color));

					} else {
						holder.img_appt_separator_icon
						.setBackgroundResource(R.drawable.shape_circle_orange);
						holder.txt_Appt_Name.setTextColor(getResources()
								.getColor(R.color.orange_circle));
						holder.img_call_icon.setVisibility(View.INVISIBLE);
					}

					holder.txt_Appt_Name
					.setText(mListDAta.get(position).mEventType);
					holder.txt_Appt_Detail
					.setText(mListDAta.get(position).mLeadOrVisitType);
				}

				convertView.setTag(holder);
			} else {
				holder = (ViewHolders) convertView.getTag();
			}
			return convertView;
		}

	}

	public static class ViewHolders {
		public ImageView img_add_icon, img_appt_separator_icon, img_call_icon;
		public TextView txt_Appt_Name, txt_Appt_Detail, txt_time;
		public TextView txt_header_title_date, txt_header_day;
		public TextView txt_Customer_Name, txt_Customer_Address1,
		txt_Customer_Address2;
		public RelativeLayout header;
		public LinearLayout layoutContent;
	}

	public String getNextRecordsDate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		Calendar c = Calendar.getInstance();
		Date nextdate = null;
		try {
			nextdate = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.setTime(nextdate);
		c.add(Calendar.DATE, 7);
		return strNextRecordsDate = df.format(c.getTime());
	}

	public void loadJsonDataForListView() {

		JSONObject reqObj = new JSONObject();
		ArrayList<JSONObject> reqArr = new ArrayList<JSONObject>();
		try {

			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
			reqObj.put(Constants.KEY_CALENDAR_STARTDATE, strNextRecordsDate);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqArr.add(reqObj);

		calendarAsyncTask = (CalendarAsyncTask) new CalendarAsyncTask(
				CalendarActivity.this, Constants.URL_CALENDAR_LIST_PAGINATION,
				Constants.REQUEST_TYPE_POST, reqArr, "List", 3690).execute();
	}

	public String getDayOfWeek(String date) {

		SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");
		DateFormat format2 = new SimpleDateFormat("EEEE");
		Date dt1;
		String finalDay = null;
		try {
			dt1 = format1.parse(date);
			finalDay = format2.format(dt1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finalDay;
	}

	private void parseListJsonDate(JSONObject responseJson2) {
		// TODO Auto-generated method stub
		//		dbHandler.clearCalendarTable();
		JSONObject calendarJsonObject;
		try {
			if(responseJson2 != null){

				calendarJsonObject = responseJson2
						.getJSONObject(Constants.JSON_KEY_SC);

				Iterator keys = calendarJsonObject.keys();
				List<String> keysList = new ArrayList<String>();
				while (keys.hasNext()) {
					keysList.add((String) keys.next());
				}
				Collections.sort(keysList, new StringDateComparator());
				// ArrayList<CalendarListPaginationModel> listarray = new
				// ArrayList<CalendarListPaginationModel>();
				for (int i = 0; i < keysList.size(); i++) {
					// loop to get the dynamic key
					String currentDynamicKey = (String) keysList.get(i);

					// get the value of the dynamic key
					JSONArray calendarJsonArray = calendarJsonObject
							.getJSONArray(currentDynamicKey);

					if (calendarJsonArray.length() == 0) {
						CalendarListPaginationModel mlistModel = new CalendarListPaginationModel();
						mlistModel.mEventType = "isHeader";
						mlistModel.mFormattedApptDate = currentDynamicKey;
						Singleton.getInstance().mCalendarListPaginationData
						.add(mlistModel);
						/*	mlistModel.mIsNewData="0";
						dbHandler.addCalendarData(mlistModel);*/

					} else {
						CalendarListPaginationModel mlistModelHead = new CalendarListPaginationModel();
						mlistModelHead.mEventType = "isHeader";
						mlistModelHead.mFormattedApptDate = currentDynamicKey;
						Singleton.getInstance().mCalendarListPaginationData
						.add(mlistModelHead);
						/*	mlistModelHead.mIsNewData="0";
						dbHandler.addCalendarData(mlistModelHead);*/
						for (int j = 0; j < calendarJsonArray.length(); j++) {
							CalendarListPaginationModel mlistModel = new CalendarListPaginationModel();
							JSONObject calJsonObject = calendarJsonArray
									.getJSONObject(j);
							mlistModel.mEventType = calJsonObject.getString(
									Constants.JSON_KEY_EVENT_TYPE).toString();
							mlistModel.mEventNotes = calJsonObject.getString(
									Constants.JSON_KEY_EVENT_NOTES).toString();
							mlistModel.mAddress = calJsonObject.getString(
									Constants.JSON_KEY_ADDRESS).toString();
							mlistModel.mApptTime = calJsonObject.getString(
									Constants.JSON_KEY_APPT_TIME).toString();
							mlistModel.mCity = calJsonObject.getString(
									Constants.JSON_KEY_CITY).toString();
							mlistModel.mCustomerId = calJsonObject.getString(
									Constants.JSON_KEY_CUSTOMER_ID).toString();
							mlistModel.mCustomerName = calJsonObject.getString(
									Constants.JSON_KEY_CUSTOMER_NAME).toString();
							mlistModel.mFormattedApptDate = calJsonObject
									.getString(
											Constants.JSON_KEY_FORMATED_APPT_DATE)
											.toString();
							mlistModel.mGroupCategory = calJsonObject.getString(
									Constants.JSON_KEY_GROUP_CATEGORY).toString();
							mlistModel.mLeadOrVisitType = calJsonObject.getString(
									Constants.JSON_KEY_LEAD_OR_VISIT_TYPE)
									.toString();
							mlistModel.mState = calJsonObject.getString(
									Constants.JSON_KEY_STATE).toString();
							mlistModel.mZip = calJsonObject.getString(
									Constants.JSON_KEY_ZIP).toString();

							Singleton.getInstance().mCalendarListPaginationData
							.add(mlistModel);
							/*mlistModel.mIsNewData="0";
							dbHandler.addCalendarData(mlistModel);*/


						}
					}

					Singleton.getInstance().mCalendarListData.put(
							currentDynamicKey,
							Singleton.getInstance().mCalendarListPaginationData);
					// Singleton.getInstance().mCalendarListPaginationData.clear();
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int currentPosition = calendarListView.getFirstVisiblePosition();
		mCustomAdapter = new CalMonthListAdapter(getApplicationContext(),
				Singleton.getInstance().mCalendarListPaginationData);
		calendarListView.setAdapter(mCustomAdapter);
		if (currentPosition>0) {
			calendarListView.setSelectionFromTop(currentPosition + 1, 0);
		}
		//Used for load calendar to  sqlite old one 
		/*if (!isOnCreate) {
			int currentPosition = calendarListView.getFirstVisiblePosition();
			mCustomAdapter = new CalMonthListAdapter(getApplicationContext(),
					Singleton.getInstance().mCalendarListPaginationData);
			calendarListView.setAdapter(mCustomAdapter);
			calendarListView.setSelectionFromTop(currentPosition + 1, 0);
		}else {
			isOnCreate=false;
			strNextRecordsDate="";
			loadCurrentMontData();
			loadPopUpWindow();
		}*/

	}

	public static void showNotesPopupWindow(String NoteType, String Appttype,
			String Notes, String Time, String Date) {
		String strApptDate = null;

		if (NoteType.equals("ListAppts")) {
			strApptDate = Date;
		} else {
			strApptDate = CalendarAdapter.strSelectedDate;
		}

		mCalendarNotesDialog = new Dialog(mContext);
		mCalendarNotesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCalendarNotesDialog.setContentView(R.layout.calendar_notes_popup_view);
		mCalendarNotesDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mCalendarNotesDialog.findViewById(R.id.notes_popup_close)
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCalendarNotesDialog.cancel();

			}
		});

		TextView txtApptTitle = (TextView) mCalendarNotesDialog
				.findViewById(R.id.popup_title);
		TextView txtApptNotes = (TextView) mCalendarNotesDialog
				.findViewById(R.id.textViewApptNotes);
		TextView txtApptDate = (TextView) mCalendarNotesDialog
				.findViewById(R.id.textViewApptDate);
		TextView txtApptTime = (TextView) mCalendarNotesDialog
				.findViewById(R.id.textViewApptTime);
		txtApptNotes.setText(Notes);
		txtApptTitle.setText(Appttype);
		txtApptTime.setText(Time);
		txtApptDate.setText(strApptDate);
		txtApptTitle.setTypeface(droidSansBold);
		txtApptNotes.setTypeface(droidSans);
		txtApptDate.setTypeface(droidSans);
		txtApptTime.setTypeface(droidSans);
		mCalendarNotesDialog.setCanceledOnTouchOutside(true);
		mCalendarNotesDialog.show();

	}

	public class AppntDropDownItemClickListener implements OnItemClickListener {
		String TAG = "AppntDropDownItemClickListener.java";

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Animation fadeAnimation = AnimationUtils.loadAnimation(
					getApplicationContext(), android.R.anim.fade_in);
			fadeAnimation.setDuration(10);
			view.startAnimation(fadeAnimation);
			popUpWindowAppnts.dismiss();

			String selectedItemText = ((TextView) view).getText().toString();

			if (selectedItemText.equals("Note")) {
				addAppntDialog = new AddAppntDialog(mContext, selectedItemText);
				addAppntDialog.show();
			} else if (selectedItemText.equals("Appointment")
					&& CanCreateEvents.equals("True")) {
				if (Singleton.getInstance().eventConfigAppntType != null
						&& !Singleton.getInstance().eventConfigAppntType
						.isEmpty()) {
					addAppntDialog = new AddAppntDialog(mContext,
							selectedItemText);
					addAppntDialog.show();
				} else {
					Toast.makeText(mContext, "No Data available",
							Toast.LENGTH_SHORT).show();
				}
			} else if (selectedItemText.equals("Visit")
					&& CanCreateEvents.equals("True")) {
				if (Singleton.getInstance().eventConfigVisiType != null) {
					addAppntDialog = new AddAppntDialog(mContext,
							selectedItemText);
					addAppntDialog.show();
				} else {
					Toast.makeText(mContext, "No Data available",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, Constants.PERMISSION_DENIED,
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	public class AddAppntDialog extends Dialog implements
	android.view.View.OnClickListener {
		Spinner TypeSpinner, intervalSpinner, leadTypeSpinner;
		Context ctx;
		String selectedItem, timeInterval, typeId, date, leadTypeId, leadType;
		TextView text_date, text_time, text_title, text_time_end;
		EditText edittext_notes;
		ArrayList<JSONObject> reqArrPro;
		Compare compare;
		Button btn_save;
		LinearLayout layout_time, layout_leadtype, layout_timer_end;

		public AddAppntDialog(Context context, String selectedItemText) {
			super(context, android.R.style.Theme_Translucent_NoTitleBar);
			this.ctx = context;
			this.selectedItem = selectedItemText;
			showAppntDialog();
			// TODO Auto-generated constructor stub
		}

		private void showAppntDialog() {
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
			layout_timer_end = (LinearLayout) findViewById(R.id.layout_timer_end);
			text_time_end = (TextView) findViewById(R.id.add_appnt_time_end_text);
			text_title.setTypeface(droidSansBold);
			text_title.setText(selectedItem);
			String date[] = CalendarAdapter.strSelectedDate.split(",");
			String dat[] = date[0].split(" ");
			if (date != null && dat != null) {
				text_date.setText(dat[0].substring(0, 3) + " " + dat[1] + ""
						+ date[1]);
			}
			Log.d("strSelectedDate", dat[0].substring(0, 3) + dat[1] + date[1]);
			ImageView Btn_date = (ImageView) findViewById(R.id.add_appnt_date_img);
			ImageView Btn_time = (ImageView) findViewById(R.id.add_appnt_time_img);
			ImageView Btn_time_end = (ImageView) findViewById(R.id.add_appnt_time_end_img);
			Btn_time_end.setOnClickListener(this);
			edittext_notes = (EditText) findViewById(R.id.textView1);
			edittext_notes.clearFocus();
			btn_save = (Button) findViewById(R.id.button_save_appnt);
			Button btn_cancel = (Button) findViewById(R.id.button_cancel_appnt);
			intervalSpinner = (Spinner) findViewById(R.id.spinner_time_interval);
			TypeSpinner = (Spinner) findViewById(R.id.spinner_appnt_type);
			leadTypeSpinner = (Spinner) findViewById(R.id.spinner_lead_type);
			if (selectedItem.equals("Appointment")) {
				TypeSpinner.setVisibility(View.VISIBLE);
				layout_time.setVisibility(View.VISIBLE);
				intervalSpinner.setVisibility(View.VISIBLE);
				layout_leadtype.setVisibility(View.VISIBLE);
			} else if (selectedItem.equals("Visit")) {
				TypeSpinner.setVisibility(View.VISIBLE);
				layout_time.setVisibility(View.VISIBLE);
				intervalSpinner.setVisibility(View.VISIBLE);
			} else if (selectedItem.equals("Note")) {
				layout_time.setVisibility(View.VISIBLE);
				layout_timer_end.setVisibility(View.VISIBLE);
				intervalSpinner.setVisibility(View.GONE);
			}

			btn_save.setOnClickListener(this);
			btn_save.setClickable(false);
			getAppointmentTime();
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

			ArrayAdapter<EventConfigurationAppntTypeModel> appntTypeAdapter = new ArrayAdapter<EventConfigurationAppntTypeModel>(
					ctx, R.layout.spinner_text,
					Singleton.getInstance().eventConfigAppntType) {

				public View getView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);

					((TextView) v).setTypeface(droidSansBold);
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

			ArrayAdapter<EventConfigurationVisitTypeModel> CustomerVisitAdapter = new ArrayAdapter<EventConfigurationVisitTypeModel>(
					ctx, R.layout.spinner_text,
					Singleton.getInstance().eventConfigVisiType) {

				public View getView(int position, View convertView,
						ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					((TextView) v).setTypeface(droidSansBold);
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

			ArrayAdapter<LeadTypeModel> leadTypeAdapter = new ArrayAdapter<LeadTypeModel>(
					ctx, R.layout.spinner_text,
					Singleton.getInstance().leadTypeModel) {
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
					View v = super.getDropDownView(position, convertView,
							parent);
					((TextView) v).setTypeface(droidSans);

					return v;
				}
			};

			leadTypeAdapter
			.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			leadTypeSpinner.setAdapter(leadTypeAdapter);
			appntTypeAdapter
			.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (selectedItem.equals("Appointment")) {
				TypeSpinner.setAdapter(appntTypeAdapter);
			} else if (selectedItem.equals("Visit")) {
				TypeSpinner.setAdapter(CustomerVisitAdapter);
			} else if (selectedItem.equals("Notes")) {
				TypeSpinner.setVisibility(View.GONE);
			}
			intervalAdapter
			.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			intervalSpinner.setAdapter(intervalAdapter);
			intervalSpinner.setSelection(5);

			TypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					if (selectedItem.equals("Appointment")) {
						typeId = Singleton.getInstance().eventConfigAppntType
								.get(position).getTypeId();
					} else if (selectedItem.equals("Visit")) {
						typeId = Singleton.getInstance().eventConfigVisiType
								.get(position).getTypeId();
					}

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
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

			leadTypeSpinner
			.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent,
						View view, int position, long id) {
					// TODO Auto-generated method stub
					leadType = Singleton.getInstance().leadTypeModel
							.get(position).typeName;
					leadTypeId = Singleton.getInstance().leadTypeModel
							.get(position).id;
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_appnt_date_img:
				Time currDate = new Time(Time.getCurrentTimezone());
				currDate.setToNow();
				DatePickerDialog dateDialog = new DatePickerDialog(mContext,
						datePickerListener, currDate.year, currDate.month,
						currDate.monthDay);
				dateDialog.show();
				break;
			case R.id.add_appnt_time_img:
				final Calendar cal = Calendar.getInstance();
				hour = cal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE);
				TimePickerDialog timeDialog = new TimePickerDialog(mContext,
						timePickerListener, hour, minute, false);
				timeDialog.show();
				break;
			case R.id.add_appnt_time_end_img:
				final Calendar cal_time_End = Calendar.getInstance();
				hour = cal_time_End.get(Calendar.HOUR_OF_DAY);
				minute = cal_time_End.get(Calendar.MINUTE);
				TimePickerDialog timeEndDialog = new TimePickerDialog(mContext,
						endTimePickerListener, hour, minute, false);
				timeEndDialog.show();
				break;

			case R.id.button_save_appnt:
				boolean isNoConflict = false;
				String newTime = null;
				try {

					Date date = null;
					SimpleDateFormat sdf;
					String txttime = text_date.getText().toString() + " "
							+ text_time.getText().toString();
					if (txttime.length() < 13) {

						sdf = new SimpleDateFormat("MMM dd yyyy");

					} else {
						sdf = new SimpleDateFormat("MMM dd yyyy hh:mm aa");
					}

					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd",
							Locale.US);
					date = sdf.parse(txttime);
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);

					savedNoteTime = df.format(calendar.getTime());
					isSaveNote = true;

					calendar.add(Calendar.MINUTE, Integer.valueOf(timeInterval));
					newTime = sdf.format(calendar.getTime());

					Log.d("Calendar Time", newTime);
					for (AppointmentDateTimeModel model : Singleton
							.getInstance().appntDateTime) {
						compare = new Compare(ctx, newTime,
								model.getApptTime(),
								model.getFormattedApptDate());
						if (compare.compare(ctx)) {
							// text_time.setText("");
							Log.d("INTERVAL", "TIME CONFLICT");
							isNoConflict = true;
						} else {
							Log.d("Data", "NUll");

						}

					}
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (!selectedItem.equals("Note")) {
					if (!isNoConflict) {
						saveAppnts();
					} else {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(
								ctx);
						alertDialog.setTitle("Time Conflict?");
						alertDialog
						.setMessage("Click OK to allow")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stub
								saveAppnts();
							}
						})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stub
								dialog.cancel();
							}
						}).show();

					}

				} else {
					try {

						if (Utilities.isNetworkConnected(ctx)) {

							ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();
							JSONObject reqobjPro = new JSONObject();
							reqobjPro.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
							reqobjPro.put(Constants.KEY_LOGIN_EMPLOYEE_ID,
									employeeID);
							reqobjPro.put(Constants.EVENT_EMPLOYEE_ID, employeeID);
							reqobjPro.put(Constants.EVENT_TYPE, Constants.NOTE);
							reqobjPro.put(Constants.KEY_CUSTOMER_ID, "0");
							reqobjPro.put(Constants.EC_APPNT_TPYE_ID, "0");
							reqobjPro.put(Constants.EVENT_DATE, text_date.getText()
									.toString());
							reqobjPro.put(Constants.EVENT_NOTES, edittext_notes
									.getText().toString().trim());
							reqobjPro.put(Constants.EVENT_START_TIME, text_time
									.getText().toString());
							reqobjPro.put(Constants.EVENT_END_TIME, text_time_end
									.getText().toString());
							reqobjPro.put(Constants.LEAD_TYPE_ID, "0");
							reqobjPro.put(Constants.EVENT_ID, "0");
							reqArrayList.add(reqobjPro);

							SaveAppntAsyncTask = new SaveAppntAsyncTask(ctx,
									reqArrayList, selectedItem).execute();
						}else {
							OfflineCalendarEntryModel mOfflineCalendarModel=new OfflineCalendarEntryModel();
							mOfflineCalendarModel.mDealerId=dealerID;
							mOfflineCalendarModel.mEmployeeId=employeeID;
							mOfflineCalendarModel.mEventEmployeeId=employeeID;
							mOfflineCalendarModel.mEventType=Constants.NOTE;
							mOfflineCalendarModel.mCustomerId="0";
							mOfflineCalendarModel.mTypeId="0";
							mOfflineCalendarModel.mEventDate=text_date.getText()
									.toString();
							mOfflineCalendarModel.mNotes=edittext_notes
									.getText().toString().trim();
							mOfflineCalendarModel.mStartTime=text_time
									.getText().toString();
							mOfflineCalendarModel.mEndTime= text_time_end
									.getText().toString();
							mOfflineCalendarModel.mLeadTypeId="0";
							mOfflineCalendarModel.mEventId= "0";
							dbHandler.addOfflineCalendarEntryData(mOfflineCalendarModel);
							Toast.makeText(ctx, "Booking saved", Toast.LENGTH_LONG).show();
							addAppntDialog.dismiss();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				break;
			case R.id.button_cancel_appnt:
				addAppntDialog.dismiss();
				break;
			default:
				break;
			}

		}

		private void saveAppnts() {
			try {
				JSONObject reqobj = requestObjAppnts();
				if (Utilities.isNetworkConnected(ctx)) {

					if (reqobj != null) {
						ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();
						if (selectedItem.equals("Appointment")) {
							reqobj.put(Constants.EVENT_TYPE, Constants.APPNT);
							reqobj.put(Constants.KEY_CUSTOMER_ID, customerId);
							reqobj.put(Constants.EC_APPNT_TPYE_ID, typeId);
							reqobj.put(Constants.LEAD_TYPE_ID, leadTypeId);
						} else if (selectedItem.equals("Visit")) {
							reqobj.put(Constants.EVENT_TYPE, Constants.VISIT);
							reqobj.put(Constants.KEY_CUSTOMER_ID, customerId);
							reqobj.put(Constants.EC_APPNT_TPYE_ID, typeId);
							reqobj.put(Constants.LEAD_TYPE_ID, "0");
						}
						reqArrayList.add(reqobj);

						SaveAppntAsyncTask = new SaveAppntAsyncTask(ctx,
								reqArrayList, selectedItem).execute();
					}
					else {
						Toast.makeText(ctx, "Data null", Toast.LENGTH_SHORT).show();
					}

				} else {
					// off line entry 

					OfflineCalendarEntryModel mOfflineCalendarModel=new OfflineCalendarEntryModel();
					mOfflineCalendarModel.mDealerId=reqobj.getString(Constants.KEY_LOGIN_DEALER_ID);
					mOfflineCalendarModel.mEmployeeId=reqobj.getString(Constants.KEY_LOGIN_EMPLOYEE_ID);
					mOfflineCalendarModel.mEventEmployeeId=reqobj.getString(Constants.EVENT_EMPLOYEE_ID);
					if (selectedItem.equals("Appointment")) {
						mOfflineCalendarModel.mEventType=Constants.APPNT;
						mOfflineCalendarModel.mLeadTypeId=leadTypeId;
					} else  if (selectedItem.equals("Visit")) {
						mOfflineCalendarModel.mEventType=Constants.VISIT;
						mOfflineCalendarModel.mLeadTypeId="0";
					}
					mOfflineCalendarModel.mCustomerId=customerId;
					mOfflineCalendarModel.mTypeId=typeId;
					mOfflineCalendarModel.mEventDate=reqobj.getString(Constants.EVENT_DATE);
					mOfflineCalendarModel.mNotes=reqobj.getString(Constants.EVENT_NOTES);
					mOfflineCalendarModel.mStartTime=reqobj.getString(Constants.EVENT_START_TIME);
					mOfflineCalendarModel.mEndTime=reqobj.getString(Constants.EVENT_END_TIME);
					mOfflineCalendarModel.mEventId= reqobj.getString(Constants.EVENT_ID);
					dbHandler.addOfflineCalendarEntryData(mOfflineCalendarModel);
					Toast.makeText(ctx, "Booking saved", Toast.LENGTH_LONG).show();
					addAppntDialog.dismiss();
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int selected_year,
					int monthOfYear, int dayOfMonth) {
				// TODO Auto-generated method stub
				year = selected_year;
				monthofday = monthOfYear;
				day = dayOfMonth;
				String monthName = new DateFormatSymbols().getMonths()[monthofday];
				text_date.setText(monthName.substring(0, 3) + " " + day + " "
						+ year);
				getAppointmentTime();

			}

		};

		public void getAppointmentTime() {
			ArrayList<JSONObject> objList = new ArrayList<JSONObject>();
			try {
				JSONObject reqObj = new JSONObject();
				reqObj.put(Constants.KEY_CALENDAR_CLANDER_VIEW, "Day");
				reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
				reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
				reqObj.put(Constants.KEY_CALENDAR_EVENTDATE, text_date
						.getText().toString());
				reqObj.put(Constants.KEY_CALENDAR_CURRENTMONTH, "");
				reqObj.put(Constants.KEY_CALENDAR_CURRENTYEAR, "");
				objList.add(reqObj);
				AppointmentTimesAsync = new AppointmentTimesAsync(ctx, objList);
				AppointmentTimesAsync.execute();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		TimePickerDialog.OnTimeSetListener endTimePickerListener = new TimePickerDialog.OnTimeSetListener() {

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
				String time_end = Hour + ":" + min + " " + timeSet;
				text_time_end.setText(Hour + ":" + min + " " + timeSet);
			}
		};
		TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

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
				String time = Hour + ":" + min + " " + timeSet;
				text_time.setText(Hour + ":" + min + " " + timeSet);
				if (!selectedItem.equals("Note")) {
					for (AppointmentDateTimeModel model : Singleton
							.getInstance().appntDateTime) {
						compare = new Compare(ctx, text_date.getText()
								.toString() + " " + time, model.getApptTime(),
								model.getFormattedApptDate());
						if (compare.compare(ctx)) {
							// text_time.setText("");
						}

					}
				}

			}
		};

		@SuppressLint("SimpleDateFormat")
		private JSONObject requestObjAppnts() throws ParseException {
			String newTime = null;
			JSONObject reqobjPro = new JSONObject();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
				Date date = null;
				String txttime = text_time.getText().toString();
				date = sdf.parse(txttime);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(Calendar.MINUTE, Integer.valueOf(timeInterval));
				newTime = sdf.format(calendar.getTime());
				Log.d("Calendar Time", newTime);
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			reqobjPro = new JSONObject();
			try {
				reqobjPro.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
				reqobjPro.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
				reqobjPro.put(Constants.EVENT_EMPLOYEE_ID, employeeID);
				reqobjPro.put(Constants.EVENT_DATE, text_date.getText()
						.toString());
				reqobjPro.put(Constants.EVENT_START_TIME, text_time.getText()
						.toString());
				reqobjPro.put(Constants.EVENT_END_TIME, newTime);
				reqobjPro.put(Constants.EVENT_NOTES, edittext_notes.getText()
						.toString().trim());
				reqobjPro.put(Constants.EVENT_ID, "0");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return reqobjPro;

		}

	}

	public class Compare {
		Date date;
		Date dateCompareOne;
		Date dateCompareTwo;

		String compareStringOne;
		String compareStringTwo;
		String[] appTime;
		String[] appDate;

		public Compare(Context context, String time, String apptTime,
				String apptDate) {
			this.compareStringOne = time;
			this.compareStringTwo = apptTime;
			appTime = apptTime.split("-");
			appDate = apptDate.split("#");
			// compareDates(compareStringOne, compareStringTwo);
		}

		public boolean compare(Context contex) {
			boolean isCompareFlag = false;
			try {
				if (appTime.length >= 2) {
					Date time11 = new SimpleDateFormat("MMM dd yyyy hh:mm aa")
					.parse(appDate[0].toString().replace(",", "") + " "
							+ appTime[0]);
					Calendar calendar1 = Calendar.getInstance();
					calendar1.setTime(time11);
					Date time22 = new SimpleDateFormat("MMM dd yyyy hh:mm aa")
					.parse(appDate[0].toString().replace(",", "") + " "
							+ appTime[1]);
					Calendar calendar2 = Calendar.getInstance();
					calendar2.setTime(time22);
					Date currentTime = new SimpleDateFormat(
							"MMM dd yyyy hh:mm aa").parse(compareStringOne);
					Calendar startingCalendar = Calendar.getInstance();
					startingCalendar.setTime(currentTime);
					startingCalendar.add(Calendar.DATE, 0);
					// let's say we have to check about 01:00:00
					String someRandomTime = compareStringOne;
					Date d = new SimpleDateFormat("MMM dd yyyy hh:mm aa")
					.parse(someRandomTime);
					Calendar calendar3 = Calendar.getInstance();
					calendar3.setTime(d);
					calendar3.add(Calendar.DATE, 0);
					if (startingCalendar.getTime().after(calendar1.getTime())
							&& startingCalendar.getTime().before(
									calendar1.getTime())) {
						calendar2.add(Calendar.DATE, 0);

						calendar3.add(Calendar.DATE, 0);
					}

					Log.d("Calendar1Time & Calendar2Time & X", calendar1
							.getTime().toString()
							+ calendar2.getTime().toString()
							+ calendar3.getTime().toString());
					Date x = calendar3.getTime();
					if (x.after(calendar1.getTime())
							&& x.before(calendar2.getTime())) {

						System.out.println("Time is in between..");
						isCompareFlag = true;
					} else {
						System.out.println("Time is not in between..");
						isCompareFlag = false;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return isCompareFlag;
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
							+ "?DealerId=" + dealerID,
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

	public class SaveAppntAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context context;
		ArrayList<JSONObject> reqObj;
		ServiceHelper helper;
		JSONObject responseJson;
		String selectedItem = "";

		public SaveAppntAsyncTask(Context ctx, ArrayList<JSONObject> reqArrPro,
				String selectedItem) {
			this.context = ctx;
			this.reqObj = reqArrPro;
			helper = new ServiceHelper(ctx);
			this.selectedItem = selectedItem;
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
			pDialog = new ActivityIndicator(mContext);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			responseJson = helper.jsonSendHTTPRequest(reqObj.toString(),
					Constants.CREATE_EVENT_URL, Constants.REQUEST_TYPE_POST);
			Log.d("responseJson = ", "" + responseJson);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (pDialog != null) {
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
								addAppntDialog.dismiss();
								if (!selectedItem.equals("Note")) {
									Intent calenIntent = new Intent(
											CalendarActivity.this,
											CustomerDetailsActivity.class);
									calenIntent
									.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
											| Intent.FLAG_ACTIVITY_NEW_TASK);
									calenIntent.putExtra("CustomerId",
											customerId);
									calenIntent.putExtra("CustomerFullName",
											customerName);
									calenIntent.putExtra("CustomerAddress",
											CustomerAddress);
									startActivity(calenIntent);
									finish();
								} else {
									Singleton.getInstance().mCalendarListPaginationData
									.clear();
									strNextRecordsDate = "";
									if (isListView) {
										initListView();
									} else {
										reloadMontview(savedNoteTime);
									}

								}
							} else if (status
									.equals(Constants.SAVE_NOTES_FAILURE)) {
								Toast.makeText(context,
										Constants.SAVE_NOTES_FAILURE,
										Toast.LENGTH_SHORT).show();
								addAppntDialog.dismiss();
							}
						} else {
							Toast.makeText(context,
									Constants.TOAST_CONNECTION_ERROR,
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(mContext, "Process failed.",
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public class AppointmentTimesAsync extends AsyncTask<Void, Void, Void> {

		Context context;
		ArrayList<JSONObject> reqObj;
		ServiceHelper helper;
		JSONObject responseJson;

		public AppointmentTimesAsync(Context ctx, ArrayList<JSONObject> objList) {
			this.context = ctx;
			this.reqObj = objList;
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


					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	class StringDateComparator implements Comparator<String> {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

		public int compare(String lhs, String rhs) {
			int val = 0;
			try {
				val = dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return val;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_CALLED || requestCode == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm aa");
			final String endTime = sdf.format(new Date());
			savePhoneAsyncTask = new SavePhoneNumberAsynctask(this, dealerID,
					employeeID, Singleton.getInstance().getCustomerId(),
					Singleton.getInstance().getStartTime(), endTime);
			savePhoneAsyncTask.execute();
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
		if (calendarAsyncTask != null) {
			calendarAsyncTask.cancel(true);
			calendarAsyncTask = null;
		}
		if (SaveAppntAsyncTask != null) {
			SaveAppntAsyncTask.cancel(true);
			SaveAppntAsyncTask = null;
		}
		if (AppointmentTimesAsync != null) {
			AppointmentTimesAsync.cancel(true);
			AppointmentTimesAsync = null;
		}
		if (savePhoneAsyncTask != null) {
			savePhoneAsyncTask.cancel(true);
			savePhoneAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (ChildEmployeeAsyncTask != null) {
			ChildEmployeeAsyncTask.cancel(true);
			ChildEmployeeAsyncTask = null;
		}
		if (eventConfigAsynTask != null) {
			eventConfigAsynTask.cancel(true);
			eventConfigAsynTask = null;
		}
		if (phoneTask != null) {
			phoneTask.cancel(true);
			phoneTask = null;
		}
		if(calmonthAdapter != null){
			if (calmonthAdapter.phoneTask != null) {
				calmonthAdapter.phoneTask.cancel(true);
				calmonthAdapter.phoneTask = null;
			}
		}
	}

	private void initListFilter() {
		// TODO Auto-generated method stub
		String strFilterTime = txtFilterTime.getText().toString();
		String strFilterName = editTxtFilterName.getText().toString();
		if (strFilterTime.equals("Time")) {
			strFilterTime = "";
		}
		if (strFilterTime.length() > 0 || strFilterName.length() > 0) {

			if (strFilterTime.length() > 0 && strFilterName.length() > 0) {
				/** Filter by Time and Name **/
				bizWizApptFilter(0, strFilterTime, strFilterName);

			} else if (strFilterTime.length() > 0
					&& strFilterName.length() <= 0) {
				/** Filter by Time **/
				bizWizApptFilter(1, strFilterTime, "");

			} else if (strFilterName.length() > 0
					&& strFilterTime.length() <= 0) {
				/** Filter by Name **/
				bizWizApptFilter(2, "", strFilterName);
			}

		} else {
			Toast.makeText(
					getApplicationContext(),
					"Please Enter the Appointment Time \nor Customer Name to Filter",
					Toast.LENGTH_SHORT).show();
		}

	}

	TimePickerDialog.OnTimeSetListener apptTimePickerListener = new TimePickerDialog.OnTimeSetListener() {

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
			String strTime = Hour + ":" + min + " " + timeSet;
			txtFilterTime.setText(strTime);
		}
	};

	public void bizWizApptFilter(int strFilterType, String strFilterTime,
			String strFilterName) {
		SimpleDateFormat mTimeFormater = new SimpleDateFormat("hh:mm aa");
		ArrayList<CalendarListPaginationModel> mOriginalData = Singleton
				.getInstance().mCalendarListPaginationData;

		mfilteredAppts.clear();
		switch (strFilterType) {
		case 0:
			for (CalendarListPaginationModel mData : mOriginalData) {
				if (!mData.mApptTime.equals("")) {
					String[] strApptTime = mData.mApptTime.split("-");

					try {
						Date startTime = mTimeFormater.parse(strApptTime[0]);
						Calendar calendarStartTime = Calendar.getInstance();
						calendarStartTime.setTime(startTime);

						Date endTime = mTimeFormater.parse(strApptTime[1]);
						Calendar calendarEndTime = Calendar.getInstance();
						calendarEndTime.setTime(endTime);

						Date filterTime = mTimeFormater.parse(strFilterTime);
						Calendar calendarFilterTime = Calendar.getInstance();
						calendarFilterTime.setTime(filterTime);

						Date filterDate = calendarFilterTime.getTime();
						if (filterDate.after(calendarStartTime.getTime())
								&& filterDate.before(calendarEndTime.getTime())) {

							if (!mData.mCustomerName.equals("")) {
								if (strFilterName.contains(",")) {
									if (mData.mCustomerName
											.toLowerCase()
											.replace(" ", "")
											.equals(strFilterName.toLowerCase()
													.replace(" ", ""))) {
										mfilteredAppts.add(mData);
									}
								} else {
									String[] strCustomerName = mData.mCustomerName
											.split("\\,");
									if (strCustomerName[0]
											.toLowerCase()
											.replace(" ", "")
											.equals(strFilterName.toLowerCase()
													.replace(" ", ""))
													|| strCustomerName[1]
															.toLowerCase()
															.replace(" ", "")
															.equals(strFilterName
																	.toLowerCase()
																	.replace(" ", ""))) {
										mfilteredAppts.add(mData);
									}
								}

							}
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			break;
		case 1:
			for (CalendarListPaginationModel mData : mOriginalData) {
				if (!mData.mApptTime.equals("")) {
					String[] strApptTime = mData.mApptTime.split("-");

					try {
						Date startTime = mTimeFormater.parse(strApptTime[0]);
						Calendar calendarStartTime = Calendar.getInstance();
						calendarStartTime.setTime(startTime);
						Date endTime = null;
						Calendar calendarEndTime = null;
						if(strApptTime.length>1){
							endTime = mTimeFormater.parse(strApptTime[1]);
							calendarEndTime = Calendar.getInstance();
							calendarEndTime.setTime(endTime);
						}

						Date filterTime = mTimeFormater.parse(strFilterTime);
						Calendar calendarFilterTime = Calendar.getInstance();
						calendarFilterTime.setTime(filterTime);

						Date filterDate = calendarFilterTime.getTime();

						if(endTime != null){
							if (startTime.compareTo(filterTime) == 0
									|| endTime.compareTo(filterTime) == 0
									|| filterDate
									.after(calendarStartTime.getTime())
									&& filterDate.before(calendarEndTime.getTime())) {
								mfilteredAppts.add(mData);
							}
						}else{
							if (startTime.compareTo(filterTime) == 0

									|| filterDate
									.after(calendarStartTime.getTime())
									) {
								mfilteredAppts.add(mData);
							}
						}



					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			break;
		case 2:
			for (CalendarListPaginationModel mData : mOriginalData) {
				if (!mData.mCustomerName.equals("")) {
					if (strFilterName.contains(",")) {
						if (mData.mCustomerName
								.toLowerCase()
								.replace(" ", "")
								.equals(strFilterName.toLowerCase().replace(
										" ", ""))) {
							mfilteredAppts.add(mData);
						}
					} else {
						String[] strCustomerName = mData.mCustomerName
								.split("\\,");
						if (strCustomerName.length == 2) {
							if (strCustomerName[0]
									.toLowerCase()
									.replace(" ", "")
									.equals(strFilterName.toLowerCase()
											.replace(" ", ""))
											|| strCustomerName[1]
													.toLowerCase()
													.replace(" ", "")
													.equals(strFilterName.toLowerCase()
															.replace(" ", ""))) {
								mfilteredAppts.add(mData);
							}
						} else if (strCustomerName.length == 1) {
							if (strCustomerName[0]
									.toLowerCase()
									.replace(" ", "")
									.equals(strFilterName.toLowerCase()
											.replace(" ", ""))) {
								mfilteredAppts.add(mData);
							}
						}

					}

				}

			}
			break;
		}
		if (mfilteredAppts.size() > 0) {
			mCustomAdapter = new CalMonthListAdapter(getApplicationContext(),
					mfilteredAppts);
			calendarListView.removeFooterView(inflatorView);
			calendarListView.setAdapter(mCustomAdapter);
			txtFilterTime.setText("Time");
			editTxtFilterName.setText("");
			mDrawerLayout.closeDrawer(layoutSlidingDrawer);
			isFilteredList = true;
		} else {
			isFilteredList = false;
			txtFilterTime.setText("Time");
			editTxtFilterName.setText("");
			mDrawerLayout.closeDrawer(layoutSlidingDrawer);
			Toast.makeText(getApplicationContext(),
					"There are no Appointments found.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public class CalMonthListAdapter extends BaseAdapter {
		ArrayList<CalendarListPaginationModel> mList;
		public Context mContext;
		LayoutInflater listinflate, listinflateHead;
		String mSelDate, dealerID;
		SharedPreferences userData;

		public CalMonthListAdapter(Context context,
				ArrayList<CalendarListPaginationModel> list) {
			// TODO Auto-generated constructor stub
			this.mList = list;
			this.mContext = context;
			userData = mContext.getSharedPreferences(
					Constants.SHARED_PREFERENCE_NAME, 0);
			dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView txt_header_title_date;
			TextView txt_header_day;
			ImageView img_add_icon;

		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (mList.get(position).mEventType.equals("isHeader")) {
				listinflateHead = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = listinflateHead.inflate(
						R.layout.calendar_list_header_view, parent, false);
				ImageView img_add_icon = (ImageView) convertView
						.findViewById(R.id.imageViewAddIcon);
				TextView txt_header_title_date = (TextView) convertView
						.findViewById(R.id.textView_header_title);
				TextView txt_header_day = (TextView) convertView
						.findViewById(R.id.textView1);

				txt_header_title_date
				.setText(mList.get(position).mFormattedApptDate);
				// TextView txt_header_day = (TextView) convertView
				// .findViewById(R.id.textView1);
				txt_header_day
				.setText(getDayOfWeek(Singleton.getInstance().mCalendarListPaginationData
						.get(position).mFormattedApptDate));
				img_add_icon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CalendarAdapter.strSelectedDate = mList.get(position).mFormattedApptDate;
						popUpWindowAppnts.showAsDropDown(v, 5, 0);
					}
				});

				convertView.setTag("isHeader");

			} else {
				listinflate = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = listinflate.inflate(
						R.layout.calendar_list_view_row, parent, false);

				final TextView eventType = (TextView) convertView
						.findViewById(R.id.textViewApptTitle);
				final TextView eventDate = (TextView) convertView
						.findViewById(R.id.textViewApptTime);
				TextView name = (TextView) convertView
						.findViewById(R.id.textViewName);
				TextView address1 = (TextView) convertView
						.findViewById(R.id.textViewAddress1);
				TextView address2 = (TextView) convertView
						.findViewById(R.id.textViewAddress2);
				final TextView notes = (TextView) convertView
						.findViewById(R.id.textViewNotes);
				ImageView imageIcon = (ImageView) convertView
						.findViewById(R.id.imageViewApptSeprator);
				ImageView imageCall = (ImageView) convertView
						.findViewById(R.id.imageViewCall);
				convertView.setTag("isContent");
				TextView leadOrVisitType = (TextView) convertView
						.findViewById(R.id.textViewApptDetail);

				imageCall.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final JSONObject reqObj_data = new JSONObject();
						final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
						try {
							reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID,
									dealerID);
							reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID,
									(mList.get(position).mCustomerId));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						reqData.add(reqObj_data);
						phoneTask = new PhonenumbersAsyncTask(
								CalMonthApptsAdapter.mContext,
								Constants.GET_PHONENUMBER_URL,
								Constants.REQUEST_TYPE_POST, reqData);
						phoneTask.execute();
					}
				});

				int selCircleIc = R.drawable.shape_circle_green;
				int selColor = R.color.green_circle;
				if (mList.get(position).mEventType.equals("CALENDAR-NOTES")
						|| mList.get(position).mEventType.equals("NOTES")) {
					selCircleIc = R.drawable.shape_circle_orange;
					selColor = R.color.orange_circle;
					name.setVisibility(View.GONE);
					address1.setVisibility(View.GONE);
					address2.setVisibility(View.GONE);
					notes.setVisibility(View.VISIBLE);
					imageCall.setVisibility(View.INVISIBLE);

					leadOrVisitType.setVisibility(View.INVISIBLE);
				}
				else if (mList.get(position).mEventType.equals("FOLLOW-UP")) {
					selCircleIc = R.drawable.shape_circle_blue;
				}
				imageIcon.setBackgroundResource(selCircleIc);

				eventType.setTextColor(selColor);
				eventType.setText(mList.get(position).mEventType);
				eventDate.setText(mList.get(position).mApptTime);
				name.setText(mList.get(position).mCustomerName);
				notes.setText(mList.get(position).mEventNotes);
				address1.setText(mList.get(position).mAddress);
				address2.setText(mList.get(position).mCity + ","
						+ mList.get(position).mState + ","
						+ mList.get(position).mZip);
				leadOrVisitType.setText(mList.get(position).mLeadOrVisitType);

			}

			return convertView;
		}

		public String getDayOfWeek(String date) {

			SimpleDateFormat format1 = new SimpleDateFormat("MMM dd, yyyy");
			DateFormat format2 = new SimpleDateFormat("EEEE");
			Date dt1;
			String finalDay = null;
			try {
				dt1 = format1.parse(date);
				finalDay = format2.format(dt1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return finalDay;
		}

	}

	public void reloadMontview(String savedNoteTime2) {
		String[] separatedDay = (savedNoteTime2).split("-");
		String strCurrentYear = separatedDay[0].replaceFirst("^0*", "");
		String strCurrentMonth = separatedDay[1].replaceFirst("^0*", "");
		getMonthData(strCurrentYear, strCurrentMonth, 0);
	}

	public static void loadChildEmployeeSpinner() {

		ArrayAdapter<String> childEmployeeSpinnerArrayAdapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item,
				Singleton.getInstance().mChildEmployeeName);
		childEmployeeSpinnerArrayAdapter
		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The
		// view
		spinnerChildEmployeeName.setAdapter(childEmployeeSpinnerArrayAdapter);

	}
	private void initSqliteData() {
		// TODO Auto-generated method stub
		arr_month_data = new ArrayList<CalendarListPaginationModel>();
		Singleton.getInstance().mCalendarAppointmentsData.clear();
		Singleton.getInstance().mCalendarFollowUpsData.clear();
		Singleton.getInstance().mCalendarNotesData.clear();
		Singleton.getInstance().mCalendarMonthViewData.clear();
		Singleton.getInstance().mCalendarListPaginationData.clear();

		ArrayList<String> monthCalAppts = new ArrayList<String>();
		ArrayList<String> monthCalFollowups = new ArrayList<String>();
		ArrayList<String> monthCalNotes = new ArrayList<String>();

		ArrayList<CalendarListPaginationModel> arrCalendarData = dbHandler.getAllCalendarData();
		Singleton.getInstance().mCalendarListPaginationData=arrCalendarData;
		if (isListView) {
			//Upload Sqlite Data to List View
			mCustomAdapter = new CalMonthListAdapter(getApplicationContext(),
					Singleton.getInstance().mCalendarListPaginationData);
			calendarListView.setAdapter(mCustomAdapter);
		}else {
			//Upload Sqlite Data to Month View
			for (CalendarListPaginationModel mCalendarModel : arrCalendarData) {
				if (!mCalendarModel.mEventType.equals("isHeader")) {
					if (mCalendarModel.mEventType.equals("FOLLOW-UP")) {
						monthCalFollowups
						.add(mCalendarModel.mFormattedApptDate);
					} else if (mCalendarModel.mEventType
							.equals("CALENDAR-NOTES")
							|| mCalendarModel.mEventType
							.equals("NOTES")) {
						monthCalNotes
						.add(mCalendarModel.mFormattedApptDate);
					} else {
						monthCalAppts
						.add(mCalendarModel.mFormattedApptDate);
					}
				}

				arr_month_data.add(mCalendarModel);
			}
			Singleton.getInstance().mCalendarMonthViewData = arr_month_data;
			Singleton.getInstance().mCalendarAppointmentsData = monthCalAppts;
			Singleton.getInstance().mCalendarFollowUpsData = monthCalFollowups;
			Singleton.getInstance().mCalendarNotesData = monthCalNotes;
			initMontView();
			showSelectedDateAppointments();
		}


	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}
