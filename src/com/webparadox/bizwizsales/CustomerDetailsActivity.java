package com.webparadox.bizwizsales;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.adapter.CustomerDetailAppointmentAdapter;
import com.webparadox.bizwizsales.adapter.CustomerDetailProjectAdapter;
import com.webparadox.bizwizsales.adapter.CustomerNotesAdapter;
import com.webparadox.bizwizsales.asynctasks.CheckUserAccessAsynctask;
import com.webparadox.bizwizsales.asynctasks.CustomerFollowUpsAsyncTask;
import com.webparadox.bizwizsales.asynctasks.DispoQuestionnaireAsyncTask;
import com.webparadox.bizwizsales.asynctasks.EmailAsyncTask;
import com.webparadox.bizwizsales.asynctasks.GetDealsEmployeeAsyncTask;
import com.webparadox.bizwizsales.asynctasks.NotesAsyncTask;
import com.webparadox.bizwizsales.asynctasks.PhonenumbersAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SaveDispoAsyncTask;
import com.webparadox.bizwizsales.asynctasks.SaveEmailAsynctask;
import com.webparadox.bizwizsales.asynctasks.SavePhoneNumberAsynctask;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.dialogs.CreateFollowUpDialog;
import com.webparadox.bizwizsales.dialogs.CreateFollowUpDialog.RefreshFollowup;
import com.webparadox.bizwizsales.dialogs.FollowUpResolveDialog;
import com.webparadox.bizwizsales.dialogs.FollowUpResolveDialog.ReloadFollowup;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.CustomerDetailsAppointmentsModel;
import com.webparadox.bizwizsales.models.CustomerDetailsProjectModel;
import com.webparadox.bizwizsales.models.GetEmployeesModel;
import com.webparadox.bizwizsales.models.NotesTypeModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CustomerDetailsActivity extends Activity implements
		RefreshFollowup, ReloadFollowup {
	RelativeLayout layoutAppnts, layoutProjects;
	ImageView imageViewBack, logoBtn;
	static Typeface droidSans;
	static Typeface droidSansBold;
	GridView mGridView;
	ListView listView_appointments;
	Integer[] grid_images = { R.drawable.ic_customer_details_home,
			R.drawable.ic_customer_details_call,
			R.drawable.ic_customer_details_msg,
			R.drawable.ic_customer_details_editor,
			R.drawable.ic_customer_details_followups,
			R.drawable.ic_customer_details_attach,
			R.drawable.ic_customer_details_media,
			R.drawable.ic_customer_details_camera };
	ImageButton imageButton_map, imageBtnAddAppnts;
	TextView textCustomerName, textCustomerAddress, textAppointments,
			textProjects,txtCustomerId;
	// APPNT DIALOG RESOURCES
	TextView text_date, text_time;
	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String dealerID = "";
	String employeeID = "";
	String appointmentId = "";
	String employeeName = "";
	String customerAddress, CustomerId,customerId, customerName, appntId;
	String APPID="1";
	static Context context;
	ActivityIndicator pDialog;
	CustomerDetailAppointmentAdapter appointmentAdapter;
	CustomerDetailProjectAdapter projectAdapter;
	LinearLayout FollowupTopLayout, HomeTopLayout, NotesLayout;
	ImageButton layout_customer_notes_button, customer_followup_top_button,
			editprospect;
	boolean apptsIsPressed, proIsPressed, isFollowup, isNotes, isAppnt, isHome,
			isAppointment;
	// This flag is to show the followup from the calendar screen
	boolean dispFollowup;
	FollowUpResolveDialog followupResolverDialog;
	ArrayList<JSONObject> reqArr, reqArrPro;
	AsyncTask<JSONObject, Void, Void> appointmentsTask;
	AsyncTask<JSONObject, Void, Void> projectTask;
	AsyncTask<Void, Integer, Void> cusAsyncTask;
	AsyncTask<Void, Void, Void> saveNotesTask, notesTypesAsync;
	AsyncTask<Void, Void, Void> appntResultIdTask;
	NotesAsyncTask noteTask;
	GetDealsEmployeeAsyncTask empTask;
	PhonenumbersAsyncTask phoneTask;
	AddNotesDialog notesDialog;
	CreateFollowUpDialog ceateFollowupDialog;
	public static Dialog mCalendarNotesDialog;
	int currentPostion = 0;
	int year;
	int month;
	int day;
	int hour;
	int minute;
	String timeSet = "";
	GridAdapter gridAdapter;
	int lastPosition = 0;
	SavePhoneNumberAsynctask savePhoneAsyncTask;
	SaveEmailAsynctask saveEmailAsyncTask;
	EmailAsyncTask emailAsyncTask;
	SearchView cusDetailsSearchView;
	SmartSearchAsyncTask searchAsyncTask;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		// This is to restrict landscape for phone
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.customer_detail_activity);
		context = this;
		mGridView = (GridView) findViewById(R.id.gridView1);
		mGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		FollowupTopLayout = (LinearLayout) findViewById(R.id.customer_followup_top);
		customer_followup_top_button = (ImageButton) findViewById(R.id.customer_followup_top_button);
		HomeTopLayout = (LinearLayout) findViewById(R.id.customer_home_top);
		NotesLayout = (LinearLayout) findViewById(R.id.layout_customer_notes);
		layout_customer_notes_button = (ImageButton) findViewById(R.id.layout_customer_notes_button);
		layoutAppnts = (RelativeLayout) findViewById(R.id.relativeLayout2);
		layoutProjects = (RelativeLayout) findViewById(R.id.relativeLayout3);
		listView_appointments = (ListView) findViewById(R.id.listview_appointments);
		imageViewBack = (ImageView) findViewById(R.id.back_icon);
		imageButton_map = (ImageButton) findViewById(R.id.imageButton_map);
		imageBtnAddAppnts = (ImageButton) findViewById(R.id.imagebtn_add_appnts);
		textAppointments = (TextView) findViewById(R.id.textViewAppointment);
		textProjects = (TextView) findViewById(R.id.textViewProject);
		textCustomerName = (TextView) findViewById(R.id.textView_title);
		textCustomerAddress = (TextView) findViewById(R.id.textview_address);
		txtCustomerId=(TextView) findViewById(R.id.textview_customer_id);
		editprospect = (ImageButton) findViewById(R.id.editprospect);
		Bundle extras = getIntent().getExtras();

		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);

		if (getIntent().hasExtra("CustomerFullName")) {
			customerName = extras.getString("CustomerFullName");
			customerAddress = extras.getString("CustomerAddress");
			CustomerId = extras.getString("CustomerId");
			currentPostion = extras.getInt("Position");
			dispFollowup = extras.getBoolean("dispFollowup");
		} else {
			CustomerId = userData.getString("CustomerId",
					Constants.EMPTY_STRING);
			customerName = userData.getString("Name", Constants.EMPTY_STRING);
			customerAddress = userData.getString("Address",
					Constants.EMPTY_STRING);
		}

		editor = userData.edit();
		editor.putString(Constants.JSON_KEY_CUSTOMER_ID, CustomerId);
		editor.commit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		appointmentId = userData.getString(Constants.KEY_APPOINTMENT_ID, "");
		employeeName = userData.getString(Constants.KEY_EMPLOYEE_NAME, "");
		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");
		textCustomerName.setTypeface(droidSans);
		textCustomerAddress.setTypeface(droidSans);
		textCustomerName.setText(customerName);
		textCustomerAddress.setText(customerAddress);
		textAppointments.setTypeface(droidSans);
		textProjects.setTypeface(droidSans);
		txtCustomerId.setTypeface(droidSans);
		txtCustomerId.setText("Customer # : "+CustomerId);
		mGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
		gridAdapter = new GridAdapter();
		mGridView.setAdapter(gridAdapter);

		mGridView.requestFocusFromTouch();
		mGridView.setItemChecked(0, true);
		isHome = true;

		cusDetailsSearchView = (SearchView) findViewById(R.id.cus_details_searchView);

		cusDetailsSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub

				if (query.trim().length() != 0) {

					searchAsyncTask = new SmartSearchAsyncTask(context,
							dealerID, employeeID, query);
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

		// listView_projects.setAdapter(projectAdapter);
		imageButton_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// TODO Auto-generated method stub
				Intent geoIntent = new Intent(
						android.content.Intent.ACTION_VIEW, Uri
								.parse("geo:0,0?q=" + customerAddress)); // Prepare
																			// intent
				startActivity(geoIntent);
			}
		});

		imageBtnAddAppnts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent calendarActivityIntent = new Intent(
						CustomerDetailsActivity.this, CalendarActivity.class);
				calendarActivityIntent
						.putExtra("PageInfo", CustomerDetailsActivity.this
								.getClass().getSimpleName());
				calendarActivityIntent.putExtra("CustomerId", CustomerId);
				calendarActivityIntent.putExtra("CustomerFullName",
						customerName);
				calendarActivityIntent.putExtra("CustomerAddress",
						customerAddress);
				startActivity(calendarActivityIntent);
			}
		});
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		editprospect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent editProspectIntent = new Intent(
						CustomerDetailsActivity.this,
						EditProspectActivity.class);
				editProspectIntent
						.putExtra("PageInfo", CustomerDetailsActivity.this
								.getClass().getSimpleName());
				editProspectIntent.putExtra("CustomerId", CustomerId);
				editProspectIntent.putExtra("CustomerFullName", customerName);
				editProspectIntent.putExtra("CustomerAddress", customerAddress);
				startActivity(editProspectIntent);
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

		FollowupTopLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Singleton.getInstance().mEmployeeNames.clear();
				GetEmployeesModel empModel = new GetEmployeesModel();
				empModel.mIds = employeeID;
				empModel.mEmployeeNames = employeeName;
				Singleton.getInstance().mEmployeeNames.add(empModel);
				empTask = new GetDealsEmployeeAsyncTask(context, dealerID,
						customerName, CustomerId, employeeID);
				empTask.execute();

			}
		});
		customer_followup_top_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Singleton.getInstance().mEmployeeNames.clear();
				GetEmployeesModel empModel = new GetEmployeesModel();
				empModel.mIds = employeeID;
				empModel.mEmployeeNames = employeeName;
				Singleton.getInstance().mEmployeeNames.add(empModel);
				empTask = new GetDealsEmployeeAsyncTask(context, dealerID,
						customerName, CustomerId, employeeID);
				empTask.execute();

			}
		});

		listView_appointments.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (isFollowup) {
					followupResolverDialog = new FollowUpResolveDialog(context,
							position, dealerID, CustomerId, employeeID,
							Singleton.getInstance().cusFollowupModelList
									.get(position).FollowUpId);
					followupResolverDialog.show();
				} else if (isNotes) {
					showNotesPopupWindow(((TextView) view
							.findViewById(R.id.textView_note_date)).getText()
							.toString(), ((TextView) view
							.findViewById(R.id.textView_note_time)).getText()
							.toString(), ((TextView) view
							.findViewById(R.id.textview_name)).getText()
							.toString(), ((TextView) view
							.findViewById(R.id.textView_notes)).getText()
							.toString());

				} else if (isAppnt) {
					Constants.isCheckOut = false;
					editor = userData.edit();
					if (Integer.valueOf(Singleton.getInstance().appointmentArray
							.get(position).getAppointmentResultId()) != 0) {
						editor.putString(
								Constants.KEY_APPT_RESULT_ID,
								Singleton.getInstance().appointmentArray.get(
										position).getAppointmentResultId());
						editor.commit();
						gotoCusAppntActivity(position);
					} else {
						appntResultIdTask = new AppointmentResultIdAsyncTask(
								context, dealerID, position, Singleton
										.getInstance().appointmentArray.get(
										position).getAppointmentId(), Singleton
										.getInstance().appointmentArray.get(
										position).getEventId(), employeeID);
						appntResultIdTask.execute();
					}
				}

			}
		});

		if (savedInstanceState != null) {
			String home = savedInstanceState.getString("isHome");
			String notes = savedInstanceState.getString("isNotes");
			String followups = savedInstanceState.getString("isFollowUp");
			if (home.equals("true")) {
				Log.d("HOME", "TRUE");
				isFollowup = false;
				isNotes = false;
				isHome = true;
				loadHome();
			} else if (notes.equals("true")) {
				Log.d("NOTES", "TRUE");
				isFollowup = false;
				isNotes = true;
				isHome = false;
				loadCustomerNotes();
			} else if (followups.equals("true")) {
				Log.d("FOLLOWUPS", "TRUE");
				isFollowup = true;
				isNotes = false;
				isHome = false;
				loadCustomerFollowUp();
			}

		} else {
			Log.d("HOME", "TRUE");
			isFollowup = false;
			isNotes = false;
			isHome = true;
			loadHome();

		}
		notesTypesAsync = new NotesTypeAsynTask(context).execute();
		JSONObject reqobjPro = new JSONObject();
		reqArrPro = new ArrayList<JSONObject>();
		try {
			reqobjPro.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqobjPro.put(Constants.KEY_CUSTOMER_ID, CustomerId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		reqArrPro.add(reqobjPro);
		layoutAppnts.setBackground(getResources().getDrawable(
				R.drawable.shape_myhotquotes_row_blue));
		layoutAppnts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!apptsIsPressed) {
					if (Utilities.isNetworkConnected(context)) {
						layoutAppnts.setBackground(getResources().getDrawable(
								R.drawable.shape_myhotquotes_row_blue));
						layoutProjects
								.setBackground(getResources().getDrawable(
										R.drawable.selector_main_list_btn));
						appointmentsTask = new AppointmentsTask(context,
								Constants.URL_CUSTOMER_APPOINTMENTS,
								Constants.REQUEST_TYPE_POST, reqArr).execute();
						proIsPressed = false;
						isAppnt = true;
					} else {
						Toast.makeText(context,
								Constants.NETWORK_NOT_AVAILABLE,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		layoutProjects.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!proIsPressed) {
					if (Utilities.isNetworkConnected(context)) {
						layoutProjects.setBackground(getResources()
								.getDrawable(
										R.drawable.shape_myhotquotes_row_blue));
						layoutAppnts.setBackground(getResources().getDrawable(
								R.drawable.selector_main_list_btn));
						projectTask = new ProjectAsynTask(context, reqArrPro)
								.execute();
						apptsIsPressed = false;
						isAppnt = false;
					} else {
						Toast.makeText(context,
								Constants.NETWORK_NOT_AVAILABLE,
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				mGridView.requestFocusFromTouch();
				mGridView.setSelection(position);

				switch (position) {
				case 0:
					if (!isHome) {
						lastPosition = position;
						mGridView.setSelection(position);
						isFollowup = false;
						isNotes = false;
						isHome = true;
						loadHome();
					}
					break;
				case 1:
					mGridView.requestFocusFromTouch();
					mGridView.setItemChecked(lastPosition, true);
					isFollowup = false;
					isHome = false;
					callToNumber();
					break;
				case 2:
					mGridView.requestFocusFromTouch();
					mGridView.setItemChecked(lastPosition, true);
					sentToEmail();
					break;
				case 3:
					if (!isNotes) {
						lastPosition = position;
						mGridView.requestFocusFromTouch();
						mGridView.setSelection(position);
						isFollowup = false;
						isNotes = true;
						isHome = false;
						loadCustomerNotes();
					}

					break;
				case 4:

					if (!isFollowup) {
						isAppnt = false;
						lastPosition = position;
						mGridView.requestFocusFromTouch();
						mGridView.setSelection(position);
						isFollowup = true;
						isNotes = false;
						isHome = false;
						loadCustomerFollowUp();
					}

					break;
				case 5:
					mGridView.requestFocusFromTouch();
					mGridView.setItemChecked(lastPosition, true);
					editor.putString(Constants.JSON_KEY_CUSTOMER_ID, CustomerId);
					editor.putString(Constants.KEY_LOGIN_DEALER_ID, dealerID);
					editor.putString(Constants.JSON_KEY_CUSTOMER_NAME,
							customerName);
					editor.commit();
					Intent cusAttachmentIntent = new Intent(context,
							CusAttachmentActivity.class);
					startActivity(cusAttachmentIntent);
					break;
				case 6:
					mGridView.requestFocusFromTouch();
					mGridView.setItemChecked(lastPosition, true);
					Intent sequalVideoIntent = new Intent(
							getApplicationContext(), SequelVideoActivity.class);
					sequalVideoIntent
							.putExtra("CustomerFullName", customerName);
					sequalVideoIntent.putExtra("CustomerAddress",
							customerAddress);
					sequalVideoIntent.putExtra("CustomerId", CustomerId);
					startActivity(sequalVideoIntent);
					break;
				case 7:
					mGridView.requestFocusFromTouch();
					mGridView.setItemChecked(lastPosition, true);
					Intent galleryIntent = new Intent(
							CustomerDetailsActivity.this,
							GalleryMainActivity.class);
					galleryIntent.putExtra("CustomerId", CustomerId);
					startActivity(galleryIntent);
					break;

				default:
					break;
				}
			}

		});
		// This will show the follow up when follow up button clicked in
		// calendar screen
		if (dispFollowup) {
			// mGridView.getChildAt(4).performClick();
			mGridView.setItemChecked(4, true);
			mGridView.performItemClick(mGridView.getChildAt(4), 4, mGridView
					.getAdapter().getItemId(4));
		}
		
	/*	if(Singleton.getInstance().hotQuotesList.get(currentPostion).equals("SALES")){
			
		}*/
		
		//Check user access
		CheckUserAccessAsynctask cuaTask = new CheckUserAccessAsynctask(context, dealerID, employeeID);
		cuaTask.execute();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Customer Details Activity");

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub

		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method

	}

	protected void gotoCusAppntActivity(int position) {
		Constants.isProposalList = false;
		Constants.isSelectProduct = false;
		Constants.isLock = Singleton.getInstance().appointmentArray.get(
				position).getLocksavingDispo();
		Intent cusAppointmentIntent = new Intent(context,
				CustomerAppointmentsActivity.class);
		editor.putString("Name", customerName);
		editor.putString("Address", customerAddress);
		editor.putString("DealerId", dealerID);
		editor.putString("AppointmentId", appointmentId);
		editor.putString("EmployeeId", employeeID);
		editor.putString("Position", "" + position);
		editor.putString("CustomerId", CustomerId);
		editor.putString("EventId", Singleton.getInstance().appointmentArray
				.get(position).getEventId());
		editor.putString("AppointmentTypeId",
				Singleton.getInstance().appointmentArray.get(position)
						.getAppointmentTypeId());
		editor.putString("Amount", Singleton.getInstance().appointmentArray
				.get(position).getAmount());
		editor.commit();
		startActivity(cusAppointmentIntent);

	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		savedInstanceState.putString("isHome", String.valueOf(isAppnt));
		savedInstanceState.putString("isNotes", String.valueOf(isNotes));
		savedInstanceState.putString("isFollowUp", String.valueOf(isFollowup));
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}

	private class GridAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return grid_images.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return grid_images[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imgButton;
			LayoutInflater inflater;
			if (convertView == null) {
				inflater = (LayoutInflater) getApplicationContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(
						R.layout.customer_details_grid_row, null);
			}
			if (position == 0) {
				convertView.setActivated(true);
			}
			imgButton = (ImageView) convertView.findViewById(R.id.imageButton1);
			imgButton.setImageResource(grid_images[position]);
			return convertView;
		}

	}

	public void sentToEmail() {
		emailAsyncTask = new EmailAsyncTask(this, dealerID, CustomerId);
		emailAsyncTask.execute();
	}

	private void loadCustomerNotes() {
		HomeTopLayout.setVisibility(View.GONE);
		FollowupTopLayout.setVisibility(View.GONE);
		NotesLayout.setVisibility(View.VISIBLE);
		NotesLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				notesDialog = new AddNotesDialog(context);
				notesDialog.show();
			}
		});

		layout_customer_notes_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (notesDialog != null) {
					notesDialog = new AddNotesDialog(context);
					notesDialog.show();
				}
			}
		});

		noteTask = new NotesAsyncTask(context, listView_appointments,
				CustomerId, dealerID);
		noteTask.execute();

	}

	public void callToNumber() {

		// TODO Auto-generated method stub
		final JSONObject reqObj_data = new JSONObject();
		final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, CustomerId);
		
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqData.add(reqObj_data);
		phoneTask = new PhonenumbersAsyncTask(context,
				Constants.EDIT_PROSPECT_URL + dealerID + "&CustomerId="
						+ CustomerId + "&AppId=" + APPID, Constants.REQUEST_TYPE_GET, reqData);
		phoneTask.execute();

	}

	public void loadCustomerFollowUp() {

		HomeTopLayout.setVisibility(View.GONE);
		FollowupTopLayout.setVisibility(View.VISIBLE);
		NotesLayout.setVisibility(View.GONE);
		final JSONObject reqObj_data = new JSONObject();
		final ArrayList<JSONObject> reqData = new ArrayList<JSONObject>();
		try {
			reqObj_data.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj_data.put(Constants.JSON_KEY_CUSTOMER_ID, CustomerId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqData.add(reqObj_data);
		cusAsyncTask = new CustomerFollowUpsAsyncTask(context, reqData,
				listView_appointments);
		cusAsyncTask.execute();

	}

	public void loadHome() {

		FollowupTopLayout.setVisibility(View.GONE);
		HomeTopLayout.setVisibility(View.VISIBLE);
		NotesLayout.setVisibility(View.GONE);
		layoutAppnts.setBackground(getResources().getDrawable(
				R.drawable.shape_myhotquotes_row_blue));
		layoutProjects.setBackground(getResources().getDrawable(
				R.drawable.selector_main_list_btn));
		JSONObject reqObj = new JSONObject();
		reqArr = new ArrayList<JSONObject>();
		try {
			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
			reqObj.put(Constants.KEY_CUSTOMER_ID, CustomerId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqArr.add(reqObj);
		isAppnt = true;
		appointmentsTask = new AppointmentsTask(context,
				Constants.URL_CUSTOMER_APPOINTMENTS,
				Constants.REQUEST_TYPE_POST, reqArr).execute();

	}

	public void showToast() {
		Toast.makeText(getApplicationContext(), "IN Progress",
				Toast.LENGTH_SHORT).show();
	}

	private void gotoHomeActivity() {
		// TODO Auto-generated method stub
		Intent backIntent = new Intent(CustomerDetailsActivity.this,
				MainActivity.class);
		backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(backIntent);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	private class AppointmentsTask extends AsyncTask<JSONObject, Void, Void> {
		Context mContext;
		String mRequestUrl, mMethodType;
		ServiceHelper serviceHelper;
		JSONObject responseJson;
		ArrayList<JSONObject> mRequestJson;
		ArrayList<String> dates = new ArrayList<String>();

		public AppointmentsTask(Context context,
				String uRL_CUSTOMER_APPOINTMENTS, String rEQUEST_TYPE_POST,
				ArrayList<JSONObject> reqArr) {
			this.mContext = context;
			this.mRequestUrl = uRL_CUSTOMER_APPOINTMENTS;
			this.mMethodType = rEQUEST_TYPE_POST;
			this.mRequestJson = reqArr;
			serviceHelper = new ServiceHelper(context);
			apptsIsPressed = true;
			// TODO Auto-generated constructor stub
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
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), mRequestUrl, mMethodType);
			Log.i("Appointments", "[" + mRequestJson.toString() + mRequestUrl
					+ mMethodType + "]");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				JSONArray localJsonArray;
				try {
					if (responseJson != null) {
						Singleton.getInstance().clearCustomerAppointmentsList();
						localJsonArray = responseJson
								.getJSONArray(Constants.KEY_APPOINTMENTS_RESPONSE);
						for (int i = 0; i < localJsonArray.length(); i++) {
							CustomerDetailsAppointmentsModel appointmentModel = new CustomerDetailsAppointmentsModel();
							JSONObject obj = localJsonArray.getJSONObject(i);
							appointmentModel.setEventType(obj
									.getString(Constants.KEY_EVENT_TYPE));
							appointmentModel.setAppointmentId(obj
									.getString(Constants.KEY_APPOINTMENT_ID));
							appointmentModel.setAppointmentType(obj
									.getString(Constants.KEY_APPOINTMENT_TYPE));
							appointmentModel.setSalesRep(obj
									.getString(Constants.KEY_SALES_REP));
							appointmentModel.setLeadType(obj
									.getString(Constants.KEY_LEAD_TYPE));
							appointmentModel.setResult(obj
									.getString(Constants.KEY_RESULT));
							appointmentModel.setSubResult(obj
									.getString(Constants.KEY_SUB_RESULT));
							appointmentModel.setDispoId(obj
									.getString(Constants.KEY_DISPO_ID));
							appointmentModel.setSubDispositionId(obj
									.getString(Constants.KEY_SUB_DISPO_ID));
							appointmentModel.setAmount(obj
									.getString(Constants.KEY_AMOUNT));
							appointmentModel
									.setAppointmentTypeId(obj
											.getString(Constants.KEY_APPOINTMENT_TYPE_ID));
							appointmentModel.setEventId(obj
									.getString(Constants.EVENT_ID));
							appointmentModel
									.setFormattedApptDate(obj
											.getString(Constants.KEY_FORMATTED_APPT_DATE));
							dates.add(obj
									.getString(Constants.KEY_FORMATTED_APPT_DATE));
							appointmentModel.setApptTime(obj
									.getString(Constants.KEY_APPT_TMIE));
							appointmentModel.setLeadTypeId(obj
									.getString(Constants.KEY_LEADTYPE_ID));
							appointmentModel
									.setAppointmentTypeId(obj
											.getString(Constants.KEY_APPOINTMENT_TYPE_ID));
							appointmentModel.setEventId(obj
									.getString(Constants.KEY_EVENT_ID));
							appointmentModel.setAppointmentResultId(obj
									.getString(Constants.KEY_APPT_RESULT_ID));

							appointmentModel.setLocksavingDispo(obj
									.getString(Constants.KEY_LOCKSAVINGDISPO));

							Singleton.getInstance().appointmentArray
									.add(appointmentModel);
						}

						appointmentAdapter = new CustomerDetailAppointmentAdapter(
								mContext);
						listView_appointments.setAdapter(appointmentAdapter);
						// appointmentAdapter.notifyDataSetChanged();

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			proIsPressed = false;
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	private class ProjectAsynTask extends AsyncTask<JSONObject, Void, Void> {

		Context mContext;
		ServiceHelper seriveHelper;
		JSONObject responseJson;
		ArrayList<JSONObject> jsonReqObj;

		public ProjectAsynTask(Context context, ArrayList<JSONObject> reqArrPro) {
			this.mContext = context;
			this.jsonReqObj = reqArrPro;
			seriveHelper = new ServiceHelper(this.mContext);
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
			responseJson = seriveHelper.jsonSendHTTPRequest(
					jsonReqObj.toString(), Constants.URL_CUSTOMER_PROJECTS,
					Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pDialog != null && pDialog.isShowing()) {
				pDialog.dismiss();
				JSONArray localJsonArray;
				try {
					if(responseJson != null){

						if (responseJson.has(Constants.KEY_PROJECT_RESPONSE)
								&& responseJson.length() != 0) {
							Singleton.getInstance().clearCustomerProjectList();
							localJsonArray = responseJson
									.getJSONArray(Constants.KEY_PROJECT_RESPONSE);
							for (int i = 0; i < localJsonArray.length(); i++) {
								JSONObject jsonObj = localJsonArray
										.getJSONObject(i);
								CustomerDetailsProjectModel model = new CustomerDetailsProjectModel();
								model.setProjectId(jsonObj
										.getString(Constants.KEY_PROJECT_PRO_ID));
								model.setProjectType(jsonObj
										.getString(Constants.KEY_PROJECT_PRO_TYPE));
								model.setProjectAmount(jsonObj
										.getString(Constants.KEY_PROJECT_PRO_AMOUNT));
								model.setFormattedInstallDate(jsonObj
										.getString(Constants.KEY_PROJECT_FORMATTED_INSTALL_DATE));
								model.setCancelReason(jsonObj
										.getString(Constants.KEY_PROJECT_CANCEL_REASON));
								model.setForeman(jsonObj
										.getString(Constants.KEY_PROJECT_FOREMAN));
								model.setBalanceDue(jsonObj
										.getString(Constants.KEY_PROJECT_BALANCE_DUE));
								model.setInstallDays(jsonObj
										.getString(Constants.KEY_PROJECT_INSTALL_DAYS));
								model.setSalesMan(jsonObj
										.getString(Constants.KEY_PROJECT_SALESNAME));
								model.setStartDate(jsonObj
										.getString(Constants.KEY_PROJECT_START_DATE));
								model.setEndDate(jsonObj
										.getString(Constants.KEY_PROJECT_END_DATE));
								Singleton.getInstance().projectsArray.add(model);
							}
							projectAdapter = new CustomerDetailProjectAdapter(
									mContext);
							listView_appointments.setAdapter(projectAdapter);
							// projectAdapter.notifyDataSetChanged();

						} else {
							Toast.makeText(getApplicationContext(),
									Constants.KEY_ERROR, Toast.LENGTH_SHORT).show();
						}
					
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				super.onPostExecute(result);
			}
		}

	}

	public class AddNotesDialog extends Dialog implements OnClickListener {

		ServiceHelper helper;
		Context context;
		Button buttonClose;
		Button buttonSave;
		Spinner TypeSpinner, appntSpinner;
		String noteType = "";
		String tableId = "";
		String noteTypeId = "";
		EditText editTextNotes;
		String tableRowId = "";

		public AddNotesDialog(Context context) {
			super(context, android.R.style.Theme_Translucent_NoTitleBar);
			// TODO Auto-generated constructor stub
			this.context = context;
			showNotesDilaog();
			helper = new ServiceHelper(context);
		}

		private void showNotesDilaog() {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams WMLP = getWindow().getAttributes();
			WMLP.gravity = Gravity.CENTER_VERTICAL;
			WMLP.dimAmount = 0.7f;
			getWindow().setAttributes(WMLP);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			setCancelable(true);
			setCanceledOnTouchOutside(false);
			setContentView(R.layout.add_notes_dialog);
			TypeSpinner = (Spinner) findViewById(R.id.spinner1);
			appntSpinner = (Spinner) findViewById(R.id.spinner2);
			editTextNotes = (EditText) findViewById(R.id.textView1);
			TextView addnewcustomer = (TextView) findViewById(R.id.text_addcustomernotes);
			TextView inReference = (TextView) findViewById(R.id.textreferenceto);
			TextView notes = (TextView) findViewById(R.id.textnotes);
			addnewcustomer.setTypeface(droidSansBold);
			inReference.setTypeface(droidSans);
			notes.setTypeface(droidSans);
			editTextNotes.clearFocus();

			buttonClose = (Button) findViewById(R.id.add_notes_close_btn);
			buttonClose.setOnClickListener(this);
			buttonSave = (Button) findViewById(R.id.button_save);
			buttonSave.setBackground(context.getResources().getDrawable(
					R.drawable.resolve_disable_background));
			buttonSave.setOnClickListener(this);
			editTextNotes.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					editTextNotes.setCursorVisible(true);
					return false;
				}
			});

			editTextNotes.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub

					if (s.toString().trim().length() > 0) {
						buttonSave.setClickable(true);
						buttonSave
								.setBackground(context
										.getResources()
										.getDrawable(
												R.drawable.selector_prospect_save_button));

					} else {
						buttonSave.setClickable(false);
						buttonSave.setBackground(context.getResources()
								.getDrawable(
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

			final ArrayList<NotesTypeModel> string = new ArrayList<NotesTypeModel>();
			for (int i = 0; i < Singleton.getInstance().notesTypesArray.size(); i++) {
				if (i == 0) {
					string.add(Singleton.getInstance().notesTypesArray.get(0));
				}

			}
			Log.d("string size", string.toString());
			ArrayAdapter<NotesTypeModel> dataAdapter = new ArrayAdapter<NotesTypeModel>(
					context, R.layout.spinner_text, string) {

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

			ArrayAdapter<NotesTypeModel> dataAdapterAppnt = new ArrayAdapter<NotesTypeModel>(
					context, R.layout.spinner_text,
					Singleton.getInstance().notesTypesArray) {

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

			ArrayAdapter<CustomerDetailsAppointmentsModel> appntAdapter = new ArrayAdapter<CustomerDetailsAppointmentsModel>(
					context, R.layout.spinner_text,
					Singleton.getInstance().appointmentArray) {

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

			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			if (Singleton.getInstance().appointmentArray.size() > 0) {
				TypeSpinner.setAdapter(dataAdapterAppnt);
			} else {
				TypeSpinner.setAdapter(dataAdapter);
			}
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			dataAdapterAppnt
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			appntAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			appntSpinner.setAdapter(appntAdapter);
			TypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					if (TypeSpinner.getSelectedItem().toString()
							.equals("Customer")) {
						noteType = string.get(position).getNoteType();
						tableId = string.get(position).getTableId();
						noteTypeId = string.get(position).getNoteTypeId();
						Log.d("noteType tableId noteTypeId",
								string.get(position).getNoteType()
										+ string.get(position).getTableId()
										+ string.get(position).getNoteTypeId());
					} else {
						noteType = Singleton.getInstance().notesTypesArray
								.get(position).noteType;
						tableId = Singleton.getInstance().notesTypesArray
								.get(position).tableId;
						noteTypeId = Singleton.getInstance().notesTypesArray
								.get(position).noteTypeId;
					}
					if (noteType.equals("Appointment")) {
						appntSpinner.setVisibility(View.VISIBLE);

					} else {
						appntSpinner.setVisibility(View.GONE);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
			appntSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							tableRowId = Singleton.getInstance().appointmentArray
									.get(position).getAppointmentId();
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
			case R.id.add_notes_close_btn:
				dismiss();
				break;
			case R.id.button_save:
				if (noteType.equals("Customer")) {
					tableRowId = CustomerId;
				} else {

				}
				JSONObject reqobjPro = new JSONObject();
				ArrayList<JSONObject> reqArrNotes = new ArrayList<JSONObject>();
				try {
					reqobjPro.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
					reqobjPro.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
					reqobjPro.put(Constants.NOTES_TABlE_ID, tableId);
					reqobjPro.put(Constants.NOTES_TYPE_ID, noteTypeId);
					reqobjPro.put(Constants.NOTES_TABlE_ROW_ID, tableRowId);
					reqobjPro.put(Constants.NOTES, editTextNotes.getText()
							.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				reqArrNotes.add(reqobjPro);
				if (!editTextNotes.getText().toString().isEmpty()) {
					saveNotesTask = new SaveNotesAsynTask(context,
							Constants.SAVE_NOTE_TYPE_URL, reqArrNotes)
							.execute();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please give some notes", Toast.LENGTH_SHORT)
							.show();
				}
				break;
			default:
				break;
			}

		}

	}

	public class SaveNotesAsynTask extends AsyncTask<Void, Void, Void> {

		ServiceHelper helper;
		JSONObject responseJson;
		Context mContext;
		ArrayList<JSONObject> reqarray;

		public SaveNotesAsynTask(Context context, String sAVE_NOTE_TYPE_URL,
				ArrayList<JSONObject> reqArrNotes) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			helper = new ServiceHelper(context);
			this.reqarray = reqArrNotes;

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
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest(reqarray.toString(),
					Constants.SAVE_NOTE_TYPE_URL, Constants.REQUEST_TYPE_POST);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (pDialog != null) {
				pDialog.dismiss();

				try {
					if(responseJson != null){

						if (responseJson.has(Constants.SAVE_NOTES_RESPONSE_KEY)) {
							JSONObject obj = responseJson
									.getJSONObject(Constants.SAVE_NOTES_RESPONSE_KEY);
							String status = obj
									.getString(Constants.SAVE_NOTES_STATUS);
							if (status.equals(Constants.SAVE_NOTES_SUCCESS)) {

								Toast.makeText(mContext,
										Constants.SAVE_NOTES_SUCCESS,
										Toast.LENGTH_SHORT).show();
								notesDialog.dismiss();

								isFollowup = false;
								loadCustomerNotes();
							} else {
								Toast.makeText(mContext,
										Constants.SAVE_NOTES_FAILURE,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(mContext,
									Constants.TOAST_CONNECTION_ERROR,
									Toast.LENGTH_SHORT).show();
						}
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
			Singleton.getInstance().cusNotesList.clear();
			super.onPostExecute(result);

		}

	}

	public class NotesTypeAsynTask extends AsyncTask<Void, Void, Void> {
		ServiceHelper helper;
		JSONArray responseJsonArray;
		JSONObject responseJson;
		CustomerNotesAdapter notesAdapter;
		Context context;

		public NotesTypeAsynTask(Context context) {
			this.context = context;
			helper = new ServiceHelper(context);
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
			pDialog = new ActivityIndicator(context);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest("",
					Constants.NOTE_TYPE_URL, Constants.REQUEST_TYPE_POST);
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (pDialog != null) {
				pDialog.dismiss();
				JSONArray responseJsonArray;
				try {
					if(responseJson != null){

						if (responseJson.has(Constants.NOTES_TYPE_RESPONSE_KEY)
								&& responseJson.length() != 0) {
							responseJsonArray = responseJson
									.getJSONArray(Constants.NOTES_TYPE_RESPONSE_KEY);
							Singleton.getInstance().notesTypesArray.clear();
							for (int i = 0; i < responseJsonArray.length(); i++) {
								JSONObject object = responseJsonArray
										.getJSONObject(i);
								NotesTypeModel model = new NotesTypeModel();
								model.setNoteTypeId(object
										.getString(Constants.NOTES_TYPE_ID));
								model.setNoteType(object
										.getString(Constants.NOTES_TYPE));
								model.setTableId(object
										.getString(Constants.NOTES_TABlE_ID));
								Singleton.getInstance().notesTypesArray.add(model);
							}

						}
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}

	}

	@Override
	public void refreshFollowUp() {
		// TODO Auto-generated method stub

		isFollowup = true;
		loadCustomerFollowUp();
	}

	@Override
	public void reloadFollowups() {
		// TODO Auto-generated method stub
		isFollowup = true;
		loadCustomerFollowUp();
	}

	public static void showNotesPopupWindow(String date, String time,
			String name, String notes) {

		mCalendarNotesDialog = new Dialog(context);
		mCalendarNotesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCalendarNotesDialog.setContentView(R.layout.customer_notes_popup_view);
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

		TextView txtCusName = (TextView) mCalendarNotesDialog
				.findViewById(R.id.text_cusomerName);
		txtApptNotes.setText(notes);
		txtApptTitle.setText(date);
		txtApptDate.setText(time);
		txtCusName.setText(name);

		txtApptTitle.setTypeface(droidSansBold);
		txtApptNotes.setTypeface(droidSans);
		txtApptDate.setTypeface(droidSans);
		txtCusName.setTypeface(droidSansBold);
		mCalendarNotesDialog.setCanceledOnTouchOutside(true);
		mCalendarNotesDialog.show();

	}

	private void showAppntsPopupWindow(String txt_date, String txt_time,
			String txt_result) {
		final Dialog appntPopUp = new Dialog(context);
		appntPopUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
		appntPopUp.setContentView(R.layout.appnt_popup_view);
		appntPopUp.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		appntPopUp.findViewById(R.id.appnt_popup_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						appntPopUp.dismiss();
					}
				});
		Button Btn_date = (Button) appntPopUp.findViewById(R.id.btn_date);
		Button Btn_time = (Button) appntPopUp.findViewById(R.id.btn_Time);
		Button Btn_save = (Button) appntPopUp
				.findViewById(R.id.button_save_appnt);
		text_date = (TextView) appntPopUp.findViewById(R.id.text_date);
		text_time = (TextView) appntPopUp.findViewById(R.id.text_time);
		TextView textview_result = (TextView) appntPopUp
				.findViewById(R.id.text_appnt_notes);
		textview_result.setText(txt_result);
		text_date.setText(txt_date);
		text_time.setText(txt_time);

		Btn_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Calendar cal = Calendar.getInstance();
				hour = cal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE);
				TimePickerDialog timeDialog = new TimePickerDialog(context,
						timePickerListener, hour, minute, false);
				timeDialog.show();
			}
		});
		Btn_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Time currDate = new Time(Time.getCurrentTimezone());
				currDate.setToNow();
				DatePickerDialog dateDialog = new DatePickerDialog(context,
						datePickerListener, currDate.year, currDate.month,
						currDate.monthDay);
				dateDialog.show();
			}
		});
		Btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
		appntPopUp.show();
	}

	DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int selected_year,
				int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			year = selected_year;
			month = monthOfYear;
			day = dayOfMonth;
			String monthName = new DateFormatSymbols().getMonths()[month];
			text_date.setText(monthName.substring(0, 3) + " " + day + " "
					+ year);
		}
	};

	TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
			// TODO Auto-generated method stub
			hour = hourOfDay;
			minute = minutes;

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

			text_time.setText(hour + ":" + minutes + timeSet);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy hh:mm aa");
		final String endTime = sdf.format(new Date());
		if (requestCode == Constants.REQUEST_CALLED || requestCode == 0) {
			savePhoneAsyncTask = new SavePhoneNumberAsynctask(this, dealerID,
					employeeID, Singleton.getInstance().getCustomerId(),
					Singleton.getInstance().getStartTime(), endTime);
			savePhoneAsyncTask.execute();
		} else if (requestCode == Constants.REQUEST_EMAILED) {
			saveEmailAsyncTask = new SaveEmailAsynctask(this, dealerID,
					employeeID, CustomerId, endTime);
			saveEmailAsyncTask.execute();
		}
	}

	public class AppointmentResultIdAsyncTask extends
			AsyncTask<Void, Void, Void> {
		Context cntx;
		String dealerId, appntId, eventId, employeeId, dispoId;
		ActivityIndicator pDialog;
		ServiceHelper helper;
		JSONObject responseJson;
		DispoQuestionnaireAsyncTask dispoAsyncTask;
		String appointmentResultId = "";
		SaveDispoAsyncTask saveDispoAsyncTask;
		JSONObject reqObjList;
		ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();
		int position;

		public AppointmentResultIdAsyncTask(Context context, String dealerID2,
				int position, String appointmentId, String eventId2,
				String employeeID2) {
			this.cntx = context;
			this.dealerId = dealerID2;
			this.appntId = appointmentId;
			this.eventId = eventId2;
			this.employeeId = employeeID2;
			this.position = position;
			helper = new ServiceHelper(cntx);
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
			pDialog = new ActivityIndicator(cntx);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			responseJson = helper.jsonSendHTTPRequest("",
					Constants.URL_GET_APPOINTMENT_RESULT_ID + dealerId
							+ "&AppointmentId=" + appntId + "&EventId="
							+ eventId + "&EmployeeId=" + employeeId,
					Constants.REQUEST_TYPE_GET);
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

						if (responseJson.has(Constants.KEY_APPNT_RESULT)
								& responseJson != null) {
							JSONObject obj = responseJson
									.getJSONObject(Constants.KEY_APPNT_RESULT);
							appointmentResultId = obj.getString(Constants.ID);
							if (appointmentResultId != null) {
								editor = userData.edit();
								editor.putString(Constants.KEY_APPT_RESULT_ID,
										appointmentResultId);
								editor.commit();
								gotoCusAppntActivity(position);
							}
						}
					
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
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
		if (appointmentsTask != null) {
			appointmentsTask.cancel(true);
			appointmentsTask = null;
		}
		if (projectTask != null) {
			projectTask.cancel(true);
			projectTask = null;
		}
		if (cusAsyncTask != null) {
			cusAsyncTask.cancel(true);
			cusAsyncTask = null;
		}
		if (saveNotesTask != null) {
			saveNotesTask.cancel(true);
			saveNotesTask = null;
		}
		if (notesTypesAsync != null) {
			notesTypesAsync.cancel(true);
			notesTypesAsync = null;
		}
		if (empTask != null) {
			empTask.cancel(true);
			empTask = null;
		}
		if (noteTask != null) {
			noteTask.cancel(true);
			noteTask = null;
		}
		if (phoneTask != null) {
			phoneTask.cancel(true);
			phoneTask = null;
		}
		if (savePhoneAsyncTask != null) {
			savePhoneAsyncTask.cancel(true);
			savePhoneAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (appntResultIdTask != null) {
			appntResultIdTask.cancel(true);
			appntResultIdTask = null;
		}
		if (saveEmailAsyncTask != null) {
			saveEmailAsyncTask.cancel(true);
			saveEmailAsyncTask = null;
		}
		if (emailAsyncTask != null) {
			emailAsyncTask.cancel(true);
			emailAsyncTask = null;
		}

		if (followupResolverDialog != null) {
			if (followupResolverDialog.resolveTask != null) {
				followupResolverDialog.resolveTask.cancel(true);
				followupResolverDialog.resolveTask = null;
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
	}
}
