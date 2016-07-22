package com.webparadox.bizwizsales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TimePicker;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.adapter.MyHotQuotesAdater;
import com.webparadox.bizwizsales.asynctasks.SavePhoneNumberAsynctask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.MyHotQuotesModel;

@SuppressLint("SimpleDateFormat")
public class MyHotQuotesActivity extends Activity implements OnClickListener {
	ImageView imageViewBack, logoBtn;
	ListView listviewMyHotQuotes;
	Typeface droidSans, droidSansBold;
	MyHotQuotesModel hotQuotesModel;
	MyHotQuotesAdater adapter;
	TextView textViewTitle;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String dealerID = "";
	String employeeID = "";
	Context context;
	AsyncTask<String, Void, Void> hotQuotesAsyncTask;
	ActivityIndicator pDialog;
	SavePhoneNumberAsynctask savePhoneAsyncTask;
	SearchView myHotQuotesSearchView;
	SmartSearchAsyncTask searchAsyncTask;
	String Id, listBoxName;

	private DrawerLayout mDrawerLayout;
	RelativeLayout layoutSlidingDrawer;
	TextView txtFilterDate,txtFilterTime;
	AutoCompleteTextView autoCompleteLocation;
	Button btnFilterGo, btnFilterClear;
	ImageView imgFilterDrawer;
	private Calendar calendar;
	private int year, month, day;
	int hour;
	int minute;
	String timeSet = "";
	private ArrayAdapter<String> autoCompleteAdapter;
	InputMethodManager imm ;
	DatabaseHandler dbHandler;
	String mFilterTime="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//		if (!getResources().getBoolean(R.bool.is_tablet)) {
		//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.myhotquotes_activity);
		dbHandler = new DatabaseHandler(this);
		Id = getIntent().getStringExtra("listBoxId");
		listBoxName = getIntent().getStringExtra("ListboxName");
		listviewMyHotQuotes = (ListView) findViewById(R.id.listview_myhotquotes);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDrawerOpened(View arg0) {
				// TODO Auto-generated method stub
				listviewMyHotQuotes.setClickable(false);
			}

			@Override
			public void onDrawerClosed(View arg0) {
				// TODO Auto-generated method stub
				imm.hideSoftInputFromWindow(autoCompleteLocation.getWindowToken(), 0);
				txtFilterDate.setText("Date");
				txtFilterTime.setText("Time");
				autoCompleteLocation.setText("");
				autoCompleteLocation.setHint("Location");
				listviewMyHotQuotes.setClickable(true);
			}
		});
		imgFilterDrawer = (ImageView) findViewById(R.id.buttonFilterdrawer);
		imgFilterDrawer.setOnClickListener(this);
		layoutSlidingDrawer = (RelativeLayout) findViewById(R.id.layoutDrawerLayout);
		txtFilterDate = (TextView) findViewById(R.id.editTextFilterDate);
		txtFilterTime = (TextView) findViewById(R.id.editTextFilterTime);
		autoCompleteLocation = (AutoCompleteTextView) findViewById(R.id.autoCompleteLocation);
		btnFilterGo = (Button) findViewById(R.id.buttonFilter);
		btnFilterClear = (Button) findViewById(R.id.buttonClearFilter);
		btnFilterGo.setOnClickListener(this);
		btnFilterClear.setOnClickListener(this);
		txtFilterDate.setOnClickListener(this);
		txtFilterTime.setOnClickListener(this);
		imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);

		autoCompleteLocation.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				imm.hideSoftInputFromWindow(autoCompleteLocation.getWindowToken(), 0);

			}
		});
	}

	public void loadInitView(){
		context = this;

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");

		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		textViewTitle = (TextView) findViewById(R.id.textView1);
		textViewTitle.setTypeface(droidSansBold);
		textViewTitle.setText(listBoxName);
		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		myHotQuotesSearchView=(SearchView)findViewById(R.id.my_hotquotes_searchView);
		myHotQuotesSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if(query.trim().length() !=0){

					searchAsyncTask=new SmartSearchAsyncTask(context,dealerID,employeeID,query);
					searchAsyncTask.execute();
				}else{
					Toast.makeText(context, Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});





		listviewMyHotQuotes.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Bundle bundle = new Bundle();
				bundle.putString("CustomerFullName",
						Singleton.getInstance().hotQuotesList.get(position)
						.getCustomerFullName());
				bundle.putString(
						"CustomerAddress",
						Singleton.getInstance().hotQuotesList.get(position)
						.getAddress()
						+ ", "
						+ Singleton.getInstance().hotQuotesList.get(
								position).getCity()
								+ ", "
								+ Singleton.getInstance().hotQuotesList.get(
										position).getState()
										+ ", "
										+ Singleton.getInstance().hotQuotesList.get(
												position).getZip());
				bundle.putString("CustomerId",
						Singleton.getInstance().hotQuotesList.get(position)
						.getCustomerId());
				bundle.putInt("Position",position);
				Intent customerDetailsIntent = new Intent(
						MyHotQuotesActivity.this, CustomerDetailsActivity.class);
				customerDetailsIntent.putExtras(bundle);
				startActivity(customerDetailsIntent);

			}
		});
		if (!Utilities.isConnectingToInternet(getApplicationContext())||!Utilities.isNetworkConnected(getApplicationContext())) {
			initSqliteData();
		}else {
			JSONObject reqObj = new JSONObject();
			ArrayList<JSONObject> reqArr = new ArrayList<JSONObject>();
			try {
				reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
				reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
				reqObj.put(Constants.KEY_LISTBOX_ID, Id);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			reqArr.add(reqObj);
			hotQuotesAsyncTask = new HotQuotesAsyncTask(context,
					Constants.URL_MY_HOT_QUOTES, Constants.REQUEST_TYPE_POST,
					reqArr).execute();
		}
		// Init();
		//adapter = new MyHotQuotesAdater(this);
		listviewMyHotQuotes.setAdapter(adapter);
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		logoBtn = (ImageView) findViewById(R.id.image_back_icon);
		logoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gotoHomeActivity();
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "My Hot Quotes Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}

	public class HotQuotesAsyncTask extends AsyncTask<String, Void, Void> {

		String mRequestUrl, mMethodType;
		ArrayList<JSONObject> mRequestJson;
		ServiceHelper serviceHelper;
		JSONObject responseJson;
		Context mContext;

		public HotQuotesAsyncTask(Context context, String uRL_MY_HOT_QUOTES,
				String rEQUEST_TYPE_POST, ArrayList<JSONObject> reqArr) {
			this.mContext = context;
			this.mMethodType = rEQUEST_TYPE_POST;
			this.mRequestUrl = uRL_MY_HOT_QUOTES;
			this.mRequestJson = reqArr;
			serviceHelper = new ServiceHelper(mContext);
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
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.i("request data doinbak", mRequestJson.toString());
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), mRequestUrl, mMethodType);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				JSONArray localJsonArray;
				ArrayList<String>Locations=new ArrayList<String>();
				SimpleDateFormat sd=new SimpleDateFormat("M/d/yyyy h:mm:ss a");
				SimpleDateFormat sdDate=new SimpleDateFormat("M/d/yyyy");
				SimpleDateFormat sdTime=new SimpleDateFormat("h:mm a");
				try {
					if(responseJson != null){
						if (responseJson.has(Constants.KEY_MYQUOTES_RESPONSE)) {
							if (responseJson.length() != 0) {
								Singleton.getInstance().clearMyHotQuotesList();
								Singleton.getInstance().tempHotQuotesList.clear();
								localJsonArray = responseJson
										.getJSONArray(Constants.KEY_MYQUOTES_RESPONSE);
								Locations.clear();
								for (int i = 0; i < localJsonArray.length(); i++) {
									MyHotQuotesModel model = new MyHotQuotesModel();
									JSONObject jobj = localJsonArray
											.getJSONObject(i);
									model.setCustomerId(jobj
											.getString(Constants.KEY_MYQUOTES_CUSTOMER_ID));
									model.setCustomerFullName(jobj
											.getString(Constants.KEY_MYQUOTES_CUSTOMER_NAME));
									model.setAddress(jobj
											.getString(Constants.KEY_MYQUOTES_ADDRESS));
									model.setCity(jobj
											.getString(Constants.KEY_MYQUOTES_CITY));
									model.setState(jobj
											.getString(Constants.KEY_MYQUOTES_STATE));
									model.setFollowups(jobj
											.getString(Constants.KEY_MYQUOTES_FOLLOWUPS));
									model.setJobs(jobj
											.getString(Constants.KEY_MYQUOTES_JOBS));
									model.setAppts(jobj
											.getString(Constants.KEY_MYQUOTES_APPTS));
									model.setDateLastEvent(jobj
											.getString(Constants.KEY_MYQUOTES_DATELASTEVENT));
									model.setZip(jobj
											.getString(Constants.KEY_MYQUOTES_ZIP));
									if (!jobj.getString(Constants.KEY_MYQUOTES_DATELASTEVENT).toString().equals("")) {
										try {
											Date dt = sd.parse(jobj
													.getString(Constants.KEY_MYQUOTES_DATELASTEVENT));
											Calendar c=Calendar.getInstance();
											c.setTime(dt);
											Date ndt=c.getTime();

											model.setDate(""+sdDate.format(ndt));
											model.setTime(""+sdTime.format(ndt));

										} catch (ParseException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}else {
										model.setDate("");
										model.setTime("");
									}
									Singleton.getInstance().hotQuotesList
									.add(model);
									Singleton.getInstance().tempHotQuotesList
									.add(model);
									Locations.add(jobj.getString(Constants.KEY_MYQUOTES_CITY));

								}
								initAutoCompleteView(Locations);
							}

						} else {

							Toast.makeText(getApplicationContext(),
									Constants.KEY_ERROR, Toast.LENGTH_SHORT).show();
						}
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.onPostExecute(result);
			}
		}

	}
	private void initSqliteData() {
		// TODO Auto-generated method stub
		Singleton.getInstance().clearMyHotQuotesList();
		Singleton.getInstance().tempHotQuotesList.clear();
		ArrayList<String>Locations=new ArrayList<String>();

		Singleton.getInstance().hotQuotesList=dbHandler.getLbDetailsByLbId("LB"+Id);
		Singleton.getInstance().tempHotQuotesList=dbHandler.getLbDetailsByLbId("LB"+Id);
		for (MyHotQuotesModel Model : Singleton.getInstance().hotQuotesList) {
			Locations.add(Model.getCity());
		}

		initAutoCompleteView(Locations);
	}
	public void initAutoCompleteView(ArrayList<String> city){
		adapter = new MyHotQuotesAdater(this);
		ArrayList<String> Locations = new ArrayList<String>(new LinkedHashSet<String>(city));
		autoCompleteAdapter=new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line,Locations);
		listviewMyHotQuotes.setAdapter(adapter);
		autoCompleteLocation.setThreshold(1);
		autoCompleteLocation.setAdapter(autoCompleteAdapter);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constants.REQUEST_CALLED || requestCode == 0){
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm aa");
			final String endTime = sdf.format(new Date());
			savePhoneAsyncTask = new SavePhoneNumberAsynctask(this,dealerID,employeeID,Singleton.getInstance().getCustomerId(),Singleton.getInstance().getStartTime(),endTime);
			savePhoneAsyncTask.execute();
		}
	}

	private void gotoHomeActivity() {
		// Intent backIntent = new Intent(MyHotQuotesActivity.this,
		// MainActivity.class);
		// startActivity(backIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
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
		if (hotQuotesAsyncTask != null) {
			hotQuotesAsyncTask.cancel(true);
			hotQuotesAsyncTask = null;
		}
		if (savePhoneAsyncTask != null) {
			savePhoneAsyncTask.cancel(true);
			savePhoneAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if(adapter != null){
			if (adapter.phoneTask != null) {
				adapter.phoneTask.cancel(true);
				adapter.phoneTask = null;
			}
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
		loadInitView();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonFilterdrawer:
			if (mDrawerLayout.isDrawerOpen(layoutSlidingDrawer)) {
				mDrawerLayout.closeDrawer(layoutSlidingDrawer);
				imm.hideSoftInputFromWindow(autoCompleteLocation.getWindowToken(), 0);
				txtFilterDate.setText("Date");
				txtFilterTime.setText("Time");
				autoCompleteLocation.setText("");
				autoCompleteLocation.setHint("Location");

			} else {
				mDrawerLayout.openDrawer(layoutSlidingDrawer);

			}
			break;
		case R.id.editTextFilterDate:
			calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);

			DatePickerDialog dateDialog =new DatePickerDialog(context, bizWizDatePickerListener, year, month, day);
			dateDialog.show();
			break;
		case R.id.editTextFilterTime:
			final Calendar cal = Calendar.getInstance();
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
			TimePickerDialog timeDialog = new TimePickerDialog(context,
					apptTimePickerListener, hour, minute, false);
			timeDialog.show();
			break;
		case R.id.buttonFilter:	
			imm.hideSoftInputFromWindow(autoCompleteLocation.getWindowToken(), 0);
			initListFilter();
			break;
		case R.id.buttonClearFilter:
			imm.hideSoftInputFromWindow(autoCompleteLocation.getWindowToken(), 0);
			txtFilterDate.setText("Date");
			txtFilterTime.setText("Time");
			autoCompleteLocation.setText("");
			autoCompleteLocation.setHint("Location");
			Singleton.getInstance().hotQuotesList.clear();
			Singleton.getInstance().hotQuotesList=(ArrayList<MyHotQuotesModel>) Singleton.getInstance().tempHotQuotesList.clone();
			listviewMyHotQuotes.setAdapter(adapter);
			mDrawerLayout.closeDrawer(layoutSlidingDrawer);
			break;
		}

	}

	private void initListFilter() {
		// TODO Auto-generated method stub
		String strFilterDate = txtFilterDate.getText().toString();
		String strFilterTime = txtFilterTime.getText().toString();
		String strFilterLocation = autoCompleteLocation.getText().toString();
		if (strFilterDate.equals("Date")) {
			strFilterDate = "";
		}
		if (strFilterTime.equals("Time")) {
			strFilterTime = "";
		}
		if (strFilterDate.length() > 0 || strFilterLocation.length() > 0|| strFilterTime.length() > 0) {

			if (strFilterDate.length() > 0 && strFilterLocation.length() > 0 && strFilterTime.length() > 0) {
				/** Filter by Date,Time and Location **/
				bizWizHotQuotesFilter(0, strFilterDate,mFilterTime, strFilterLocation);

			} else if (strFilterDate.length() > 0
					&& strFilterLocation.length() <= 0 && strFilterTime.length() <= 0) {
				/** Filter by Date **/
				bizWizHotQuotesFilter(1, strFilterDate, "","");

			} else if (strFilterLocation.length() > 0
					&& strFilterDate.length() <= 0 && strFilterTime.length() <= 0) {
				/** Filter by Location **/
				bizWizHotQuotesFilter(2, "","", strFilterLocation);
			}
			else if (strFilterTime.length() > 0
					&& strFilterDate.length() <= 0 && strFilterLocation.length() <= 0) {
				/** Filter by Time **/
				bizWizHotQuotesFilter(3, "",mFilterTime, "");
			}else if (strFilterDate.length() > 0 && strFilterTime.length() > 0
					&& strFilterLocation.length() <= 0) {
				/** Filter by Date & Time **/
				bizWizHotQuotesFilter(4, strFilterDate,mFilterTime, "");

			}else if (strFilterDate.length() > 0 && strFilterLocation.length() > 0
					&& strFilterTime.length() <= 0) {
				/** Filter by Date & Location **/
				bizWizHotQuotesFilter(5, strFilterDate, "",strFilterLocation);

			}else if (strFilterTime.length() > 0 && strFilterLocation.length() > 0
					&& strFilterDate.length() <= 0) {
				/** Filter by Time & Location **/
				bizWizHotQuotesFilter(6, "",mFilterTime,strFilterLocation);

			}

		} else {
			Toast.makeText(
					getApplicationContext(),
					"Please Enter the Appointment Date \nor Customer Location to Filter",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void bizWizHotQuotesFilter(int i, String filterDate,String filterTime, String filterLocation) {
		// TODO Auto-generated method stub
		ArrayList<MyHotQuotesModel> sortedHotQuotesList = new ArrayList<MyHotQuotesModel>();

		switch (i) {
		case 0:
			/* Filter By Date,Time and Location */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getCity().equals(filterLocation)&&model.getDate().equals(filterDate)&&model.getTime().equals(filterTime)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		case 1:
			/* Filter By Date */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getDate().equals(filterDate)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		case 2:
			/* Filter By Location */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getCity().equals(filterLocation)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		case 3:
			/* Filter By Time */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getTime().equals(filterTime)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		case 4:
			/* Filter By Date & Time */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getDate().equals(filterDate) && model.getTime().equals(filterTime)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		case 5:
			/* Filter By Date & Location */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getDate().equals(filterDate) && model.getCity().equals(filterLocation)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		case 6:
			/* Filter By Time & Location */
			for (int j = 0; j <Singleton.getInstance().hotQuotesList.size(); j++) {
				MyHotQuotesModel model=Singleton.getInstance().hotQuotesList.get(j);
				if (model.getTime().equals(filterTime) && model.getCity().equals(filterLocation)) {
					sortedHotQuotesList.add(model);
				}
			}
			break;
		}
		if (sortedHotQuotesList.size()>0) {
			Singleton.getInstance().hotQuotesList.clear();
			Singleton.getInstance().hotQuotesList=(ArrayList<MyHotQuotesModel>) sortedHotQuotesList.clone();
			listviewMyHotQuotes.setAdapter(adapter);
		}else {
			Toast.makeText(
					getApplicationContext(),
					"No result found",
					Toast.LENGTH_SHORT).show();
		}
		mDrawerLayout.closeDrawer(layoutSlidingDrawer);
	}
	DatePickerDialog.OnDateSetListener   bizWizDatePickerListener = new OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			txtFilterDate.setText(""+(monthOfYear+1)+"/"+dayOfMonth+"/"+year);
		}
	};
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
			mFilterTime=""+hour+":"+min+ " " + timeSet;
		}
	};
}
