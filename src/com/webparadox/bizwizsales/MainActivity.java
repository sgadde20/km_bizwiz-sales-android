package com.webparadox.bizwizsales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.webparadox.bizwizsales.adapter.MainMenuAdapter;
import com.webparadox.bizwizsales.asynctasks.DispoSubDispoAsynctask;
import com.webparadox.bizwizsales.asynctasks.LeadTypeAsync;
import com.webparadox.bizwizsales.asynctasks.SmartSearchAsyncTask;
import com.webparadox.bizwizsales.datacontroller.DatabaseHandler;
import com.webparadox.bizwizsales.datacontroller.Singleton;
import com.webparadox.bizwizsales.helper.BizWizUpdateReciver;
import com.webparadox.bizwizsales.helper.SendLocationService;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.helper.Utils;
import com.webparadox.bizwizsales.libraries.ActivityIndicator;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.AppointmentTypeModel;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;
import com.webparadox.bizwizsales.models.HomeScreenModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;
import com.webparadox.bizwizsales.models.MyHotQuotesModel;
import com.webparadox.bizwizsales.models.OfflineCalendarEntryModel;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements
SearchView.OnQueryTextListener, SearchView.OnCloseListener,
OnClickListener, android.view.View.OnClickListener {
	LayoutInflater inflator;
	ListAdapter listadapter;
	public ListView listview;
	Context mContext = MainActivity.this;
	ArrayList<String> arr_Title = new ArrayList<String>();
	ArrayList<String> arr_valuse = new ArrayList<String>();
	// private SearchView mSearchView;
	private LinearLayout drawerLinearLayout;
	Button calendar_but, addprospect_but;
	Intent nxtActivity;
	boolean isOpened = false;

	ImageView empPic;
	TextView empName;

	Context context;
	ServiceHelper serviceHelper;

	SharedPreferences userData;
	SharedPreferences.Editor editor;
	String dealerID = "";
	String employeeID = "";
	String employeeName = "";
	String empl_Image_String,goRecent;
	LinearLayout layout;
	public TextView mtdSalesValue, ytdSalesValue, adlSalesValue, mtdHead,
	ytdHead, adlHead;
	Typeface droidSans, droidSansBold;

	AsyncTask<JSONObject, Void, Void> homeAsyncTask;
	AsyncTask<JSONObject, Void, Void> loadOfflineKPIAsyncTask;
	AsyncTask<Void, Void, Void> eventConfigAsyncTask;
	AsyncTask<Void, Void, Void> leadTypeAsyncTask;
	public ActivityIndicator pDialog;

	// Navigation Drawer items
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	SmartSearchAsyncTask searchAsyncTask;
	LeadTypeAsync leadTypeAsnc;
	DispoSubDispoAsynctask dispoSubDispo;
	DatabaseHandler dbHandler;
	AsyncTask<String, Void, Void> addProspectAndAprofessionalContactAsyncTask;
	AsyncTask<String, Void, Void> loadDropDownAsyncTask;
	JSONObject jsonResultText, jsonDataResultText;
	ArrayList<JSONObject> reqArr;
	AsyncTask<JSONObject, Void, Void> calendarAsyncTask;
	JSONObject reqObj;
	Dialog dialog = null;

	int flagOfflineSize;
	ArrayList<OfflineCalendarEntryModel> offlineEvents =new ArrayList<OfflineCalendarEntryModel>();
	AsyncTask<JSONObject, Void, Void> SaveAppntAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// This is to restrict landscape for phone
		if (!getResources().getBoolean(R.bool.is_tablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		getActionBar().setTitle("");
		overridePendingTransition(R.anim.activity_open_translate,
				R.anim.activity_close_scale);
		setContentView(R.layout.activity_main);
		context = this;
		dbHandler = new DatabaseHandler(context);
		dbHandler.getWritableDatabase();

		startService(new Intent(MainActivity.this, SendLocationService.class));

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer_list_view);
		
		listview = (ListView) findViewById(R.id.listView1);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < Constants.mainMenuList.length; ++i) {
			list.add(Constants.mainMenuList[i]);
		}

		MainMenuAdapter adapter = new MainMenuAdapter(this, list,
				Constants.menu_icons);
		mDrawerList.setAdapter(adapter);

		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.shape_header));

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open, /* "open drawer" description for accessibility */
				R.string.drawer_close /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {

				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		serviceHelper = new ServiceHelper(context);

		droidSans = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		droidSansBold = Typeface.createFromAsset(getAssets(),
				"DroidSans-Bold.ttf");


		userData = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
		editor = userData.edit();
		dealerID = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		employeeID = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		employeeName = userData.getString(Constants.KEY_EMPLOYEE_NAME, "");
		empl_Image_String = userData.getString("Iamge_Employee", null);
		goRecent = userData.getString(Constants.KEY_UPDATE_DISPOS_TO_PROCEED, "");
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		Utils.pushOpenScreenEvent(this, "Main Activity");
		

	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method
	}

	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.activity_open_scale,
				R.anim.activity_close_translate);
	}

	public void loadAllData(){

		mtdSalesValue = (TextView) findViewById(R.id.textView_myMTD);
		ytdSalesValue = (TextView) findViewById(R.id.textView_myYTD);
		adlSalesValue = (TextView) findViewById(R.id.textView_myADL);
		mtdHead = (TextView) findViewById(R.id.textView1);
		ytdHead = (TextView) findViewById(R.id.textView_appt_time);
		adlHead = (TextView) findViewById(R.id.textview_appt_type);

		// layout = (LinearLayout) findViewById(R.id.semitransparent_layout);

		mtdSalesValue.setTypeface(droidSansBold);
		ytdSalesValue.setTypeface(droidSansBold);
		adlSalesValue.setTypeface(droidSansBold);
		mtdHead.setTypeface(droidSansBold);
		ytdHead.setTypeface(droidSansBold);
		adlHead.setTypeface(droidSansBold);

		reqObj = new JSONObject();
		reqArr = new ArrayList<JSONObject>();
		try {
			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqArr.add(reqObj);

		if (Utilities.isNetworkConnected(context)) {
			homeAsyncTask = new HomeAsyncTask(context,
					Constants.URL_HOME_SCREEN, Constants.REQUEST_TYPE_POST,
					reqArr).execute();
		} else {

			ArrayList<String> homeKpiArrayList = new ArrayList<String>();
			Singleton.getInstance().homeList.clear();
			homeKpiArrayList = dbHandler.getHomeKPIValue();
			dbHandler.getHomeSHValue();
			if (homeKpiArrayList.size() > 0) {
				mtdSalesValue.setText(homeKpiArrayList.get(0));
				ytdSalesValue.setText(homeKpiArrayList.get(1));
				adlSalesValue.setText(homeKpiArrayList.get(2));
			} else {
				Toast.makeText(context, Constants.NETWORK_NOT_AVAILABLE,
						Toast.LENGTH_SHORT).show();
			}

			Singleton.getInstance().leadTypeModel.clear();
			Singleton.getInstance().leadTypeModel = dbHandler.getLeadTypeVales();
			
			if(goRecent.equalsIgnoreCase(Constants.KEY_UPDATE_DISPOS_TO_PROCEED)){
				if(Singleton.getInstance().homeList.get(0).mListboxCount.equalsIgnoreCase("0")){
					Toast.makeText(getApplicationContext(),
							R.string.toast_for_no_recent_appointment, Toast.LENGTH_SHORT).show();
					//finish();
					editor = userData.edit();
					editor.putString(Constants.KEY_LOGIN_STATUS,"false");
					editor.commit();
					Intent intent = new Intent(MainActivity.this,LoginActivity.class);
					startActivity(intent);
					finish();

				}else{
					String tag = Singleton.getInstance().homeList.get(0).mListboxId;
					String titleName = Singleton.getInstance().homeList
							.get(0).mListboxName;
					if (Integer.valueOf(Singleton.getInstance().homeList
							.get(0).mListboxCount) != 0) {
						Intent HotQuotesActivity = new Intent(MainActivity.this,
								MyHotQuotesActivity.class);
						HotQuotesActivity.putExtra("listBoxId", tag);
						HotQuotesActivity.putExtra("ListboxName", titleName);
						startActivity(HotQuotesActivity);
						// finish();
					} else {
						Toast.makeText(getApplicationContext(),
								Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
					}
				}
			}else{
				listadapter = new ListAdapter(this);
				listview.setAdapter(listadapter);
			}	
		}

		listadapter = new ListAdapter(this);
		listview.setAdapter(listadapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				String tag = Singleton.getInstance().homeList.get(position).mListboxId;
				String titleName = Singleton.getInstance().homeList
						.get(position).mListboxName;
				if (Integer.valueOf(Singleton.getInstance().homeList
						.get(position).mListboxCount) != 0) {
					Intent HotQuotesActivity = new Intent(MainActivity.this,
							MyHotQuotesActivity.class);
					HotQuotesActivity.putExtra("listBoxId", tag);
					HotQuotesActivity.putExtra("ListboxName", titleName);
					startActivity(HotQuotesActivity);
					// finish();
				} else {
					Toast.makeText(getApplicationContext(),
							Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
				}
			}
		});

		// ImageView drawericon = (ImageView)
		// findViewById(R.id.imageView_slider_icon);
		// drawericon.setOnClickListener(this);

		empPic = (ImageView) findViewById(R.id.imageViewUser);
		empName = (TextView) findViewById(R.id.tv_username);
		empName.setTypeface(droidSans);
		// empPic.setImageBitmap(Singleton.getInstance().getOwnerPhoto());
		if (empl_Image_String != null) {
			empPic.setImageBitmap(decodeBase64(empl_Image_String));
		}
		empName.setText(employeeName);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					// mDrawerLayout.closeDrawer(drawerLinearLayout);
					Intent supportActivity = new Intent(MainActivity.this,
							SupportActivity.class);
					startActivity(supportActivity);
					finish();
					break;
				case 1:
					// mDrawerLayout.closeDrawer(drawerLinearLayout);
					Intent aboutActivity = new Intent(MainActivity.this,
							AboutActivity.class);
					startActivity(aboutActivity);
					finish();
					break;
				case 2:
					// /mDrawerLayout.closeDrawer(drawerLinearLayout);
					Intent settingsActivity = new Intent(MainActivity.this,
							SettingsActivity.class);
					startActivity(settingsActivity);
					finish();
					break;

				case 3:
					mDrawerLayout.closeDrawers();
					callUpSyncData();
					break;
					// Logout
				case 4:
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
					editor = userData.edit();
					editor.putString(Constants.KEY_LOGIN_STATUS, "False");
					editor.commit();
					Intent loginIntent = new Intent(MainActivity.this,
							LoginActivity.class);
					loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(loginIntent);
					MainActivity.this.finish();
					break;

				default:
					break;
				}
			}
		});

		calendar_but = (Button) findViewById(R.id.button_calendar);
		addprospect_but = (Button) findViewById(R.id.button_add_prospect);

		calendar_but.setOnClickListener(this);
		addprospect_but.setOnClickListener(this);
		if(Constants.isNetworkConnected(context)){
		dispoSubDispo = new DispoSubDispoAsynctask(context, dealerID);
		dispoSubDispo.execute();
		}
	}

	// public static Bitmap decodeBase64(String input) {
	// byte[] decodedByte = Base64.decode(input, 0);
	// BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inPurgeable = true;
	// Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByte, 0,
	// decodedByte.length, options);
	// return Bitmap.createScaledBitmap(bitmap, 50, 50, true);
	// }
	public void callUpSyncData() {
		JSONObject responseJson = new JSONObject();
		responseJson = dbHandler.getJsonValue(dealerID, employeeID);
		if (responseJson != null) {
			if (Utilities.isNetworkConnected(context)) {
				addProspectAndAprofessionalContactAsyncTask = new AddProspectAndAprofessionalContactAsyncTask(
						context,
						Constants.URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT,
						responseJson);
				addProspectAndAprofessionalContactAsyncTask.execute();
			} else {
				Toast.makeText(context, Constants.NETWORK_NOT_AVAILABLE,
						Toast.LENGTH_SHORT).show();
			}
		} 
		else {
			handler.sendEmptyMessage(1);
		}
	}

	class AddProspectAndAprofessionalContactAsyncTask extends
	AsyncTask<String, Void, Void> {

		String mRequestUrl, mRequestData;

		public AddProspectAndAprofessionalContactAsyncTask(Context context,
				String requestUrl, JSONObject responseJson) {
			this.mRequestUrl = requestUrl;
			this.mRequestData = responseJson.toString();
			serviceHelper = new ServiceHelper(context);
		}

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(MainActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				jsonResultText = serviceHelper.jsonSendHTTPRequest(
						this.mRequestData, this.mRequestUrl,
						Constants.REQUEST_TYPE_POST);
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// pDialog.dismiss();
			try {
				if(jsonResultText != null){

					if (jsonResultText.has(Constants.KEY_ADDPROSPECT_RESPONSE)) {
						JSONArray localJsonArray = jsonResultText
								.getJSONArray(Constants.KEY_ADDPROSPECT_RESPONSE);
						for (int i = 0; i < localJsonArray.length(); i++) {
							JSONObject localJsonObject = localJsonArray
									.getJSONObject(i);
							if (localJsonObject
									.has(Constants.KEY_ADDPROSPECT_STATUS)) {
								if (localJsonObject
										.getString(Constants.KEY_ADDPROSPECT_STATUS)
										.toString()
										.equalsIgnoreCase(Constants.VALUE_SUCCESS)) {
									dbHandler.removeRowAddprospectContact();
									handler.sendEmptyMessage(1);
								} else {
									Toast.makeText(
											context,
											localJsonObject
											.getString(
													Constants.KEY_ADDPROSPECT_STATUS)
													.toString(),
													Constants.TOASTMSG_TIME).show();
								}
							} else {
								Toast.makeText(context,
										Constants.TOAST_CONNECTION_ERROR,
										Constants.TOASTMSG_TIME).show();
							}
						}
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

	class LoadDropDownItemsAsyncTask extends AsyncTask<String, Void, Void> {
		String mRequestUrl;

		public LoadDropDownItemsAsyncTask(String requestUrl) {
			this.mRequestUrl = requestUrl;
		}

		@Override
		protected void onPreExecute() {
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.cancel();
				}
				pDialog = null;
			}
			pDialog = new ActivityIndicator(MainActivity.this);
			pDialog.setLoadingText("Loading....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				jsonDataResultText = serviceHelper.jsonSendHTTPRequest(
						Constants.EMPTY_STRING, this.mRequestUrl,
						Constants.REQUEST_TYPE_GET);
				Log.e(Constants.KEY_PHONE_TYPE_RESPONSE,
						jsonDataResultText.toString());
			} catch (Exception e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dbHandler.insertValueJsonData(
					Constants.WEBSERVICE_ADD_PROSPECT_DROPDOWN,
					jsonDataResultText.toString());
			if (Utilities.isNetworkConnected(context)) {
				/*
				 * homeAsyncTask = new HomeAsyncTask(context,
				 * Constants.URL_HOME_SCREEN, Constants.REQUEST_TYPE_POST,
				 * reqArr).execute();
				 */
				handler.sendEmptyMessage(2);
			} else {
				Toast.makeText(context, Constants.NETWORK_NOT_AVAILABLE,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static Bitmap decodeBase64(String input) {
		Bitmap bitmap;
		try {
			System.gc();
			byte[] decodedByte = Base64.decode(input, 0);
			bitmap = decodeAndScaleImage(decodedByte, 30, 30);

		} catch (Exception e) {
			// TODO: handle exception
			bitmap = null;
		}
		return bitmap;
	}

	public static Bitmap decodeAndScaleImage(byte[] bytes, int width, int height) {

		try {
			// decode the image file
			BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
			scaleOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, scaleOptions);

			// find the correct scale value as a power of 2.
			int scale = 1;
			while (scaleOptions.outWidth / scale / 2 >= width
					&& scaleOptions.outHeight / scale / 2 >= height) {
				scale *= 2;
			}

			// decode with the sample size
			BitmapFactory.Options outOptions = new BitmapFactory.Options();
			outOptions.inSampleSize = scale;
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					outOptions);
		} catch (Exception e) {
		}

		return null;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);

		SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		SearchView search = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
		search.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String query) {
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String query) {
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
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (item.getItemId() == R.id.action_search) {

		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	// /**
	// * A placeholder fragment containing a simple view.
	// */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public class ListAdapter extends BaseAdapter {
		public ListAdapter(MainActivity activity) {
			mContext = activity;
		}

		public int getCount() {
			return Singleton.getInstance().homeList.size();
		}

		public Object getItem(int i) {
			return Integer.valueOf(Singleton.getInstance().homeList.size());
		}

		public long getItemId(int i) {
			return i;
		}

		public class ViewHolder {
			LinearLayout layoutTitle;
			TextView txt_value;
			TextView txt_title;
		}

		public View getView(int i, View view, ViewGroup viewgroup) {

			ViewHolder viewholder;
			if (view == null) {
				viewholder = new ViewHolder();
				inflator = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflator.inflate(R.layout.main_list_view_selector,
						viewgroup, false);
				view.setTag(viewholder);
			} else {
				viewholder = (ViewHolder) view.getTag();
			}
			viewholder.txt_title = (TextView) view
					.findViewById(R.id.textView_customer_name);
			viewholder.txt_value = (TextView) view
					.findViewById(R.id.textView_valuse);
			viewholder.layoutTitle = (LinearLayout) view
					.findViewById(R.id.layout_list_row);

			viewholder.txt_title.setTypeface(droidSans);
			viewholder.txt_value.setTypeface(droidSansBold);

			viewholder.txt_title.setText(Singleton.getInstance().homeList
					.get(i).mListboxName);
			viewholder.txt_value.setText("("
					+ Singleton.getInstance().homeList.get(i).mListboxCount
					+ ")");
			return view;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageView_slider_icon:

			if (mDrawerLayout.isDrawerOpen(drawerLinearLayout)) {
				mDrawerLayout.closeDrawer(drawerLinearLayout);
			} else {
				mDrawerLayout.openDrawer(drawerLinearLayout);
			}

			break;

		case R.id.button_add_prospect:
			nxtActivity = new Intent(this, AddProspectActivity.class);
			startActivity(nxtActivity);
			finish();
			break;
		case R.id.button_calendar:
			nxtActivity = new Intent(this, CalendarActivity.class);
			nxtActivity.putExtra("PageInfo", MainActivity.this.getClass()
					.getSimpleName());
			startActivity(nxtActivity);
			finish();
			break;
		case R.id.emailCancel:
			dialog.dismiss();
			dialog.cancel();
			finish();
			break;

		case R.id.emailok:
			dialog.dismiss();
			dialog.cancel();
			loadAllData();
			break;
		default:
			break;
		}

	}

	public class HomeAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context mContext;
		String mRequestUrl, mMethodType;
		ServiceHelper serviceHelper;
		int mWhich;
		JSONObject responseJson, responseJsonKPI;
		ArrayList<JSONObject> mRequestJson;
		MainActivity mainAct = new MainActivity();

		public HomeAsyncTask(Context context, String requestUrl,
				String methodType, ArrayList<JSONObject> requestJson) {
			this.mContext = context;
			this.mRequestUrl = requestUrl;
			this.mRequestJson = requestJson;
			this.mMethodType = methodType;

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
			Log.i("request data doinbak", mRequestJson.toString());
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), mRequestUrl, mMethodType);
			responseJsonKPI = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), Constants.URL_HOME_SCREEN_KPI,
					mMethodType);

			// Log.e(Constants.KEY_LOGIN_RESPONSE_MESSAGE,
			// responseJson.toString());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			JSONObject localJsonObject;
			JSONArray localJsonArray;
			try {
				if(responseJson != null){
					if (responseJson.has(Constants.KEY_HOME_RESPONSE)
							&& responseJsonKPI.has(Constants.KEY_HOME_RESPONSE_KPI)) {
						Singleton.getInstance().clearHomeList();
						localJsonObject = responseJsonKPI
								.getJSONObject(Constants.KEY_HOME_RESPONSE_KPI);
						Singleton.getInstance().mtdSalesValue = localJsonObject
								.getString(Constants.KEY_HOME_MTD);
						Singleton.getInstance().ytdSalesValue = localJsonObject
								.getString(Constants.KEY_HOME_YTD);
						Singleton.getInstance().adlSalesValue = localJsonObject
								.getString(Constants.KEY_HOME_ADL);

						localJsonArray = responseJson
								.getJSONArray(Constants.KEY_HOME_RESPONSE);
						for (int i = 0; i < localJsonArray.length(); i++) {
							HomeScreenModel homObj = new HomeScreenModel();
							JSONObject jobj = localJsonArray.getJSONObject(i);
							homObj.mListboxId = jobj.get(Constants.KEY_LISTBOX_ID)
									.toString();
							homObj.mListboxName = jobj.get(
									Constants.KEY_LISTBOX_NAME).toString();
							homObj.mListboxCount = jobj.get(
									Constants.KEY_LISTBOX_COUNT).toString();
							homObj.mListboxOrdinal = jobj.get(
									Constants.KEY_LISTBOX_ORDINAL).toString();
							Singleton.getInstance().homeList.add(homObj);
						}
					} else if (responseJson
							.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE)
							.toString()
							.equalsIgnoreCase(
									Constants.VALUE_INVALID_API_CREDENTIAL)) {
						Toast.makeText(
								mContext,
								responseJson.getString(
										Constants.KEY_LOGIN_RESPONSE_MESSAGE)
										.toString(), Constants.TOASTMSG_TIME)
										.show();
					} else {
						Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
								Constants.TOASTMSG_TIME).show();
					}

					if (Singleton.getInstance().leadTypeModel.size() == 0) {
						leadTypeAsnc = new LeadTypeAsync(context, dealerID);
						leadTypeAsnc.execute();
					}
				
				}
			} catch (Exception e) {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}
			if(goRecent.equalsIgnoreCase(Constants.KEY_UPDATE_DISPOS_TO_PROCEED)){
				if(Singleton.getInstance().homeList.get(0).mListboxCount.equalsIgnoreCase("0")){
					Toast.makeText(getApplicationContext(),
							R.string.toast_for_no_recent_appointment, Toast.LENGTH_SHORT).show();
					//finish();
					editor = userData.edit();
					editor.putString(Constants.KEY_LOGIN_STATUS,"false");
					editor.commit();
					Intent intent = new Intent(MainActivity.this,LoginActivity.class);
					startActivity(intent);
					finish();

				}else{
					String tag = Singleton.getInstance().homeList.get(0).mListboxId;
					String titleName = Singleton.getInstance().homeList
							.get(0).mListboxName;
					if (Integer.valueOf(Singleton.getInstance().homeList
							.get(0).mListboxCount) != 0) {
						Intent HotQuotesActivity = new Intent(MainActivity.this,
								MyHotQuotesActivity.class);
						HotQuotesActivity.putExtra("listBoxId", tag);
						HotQuotesActivity.putExtra("ListboxName", titleName);
						startActivity(HotQuotesActivity);
						// finish();
					} else {
						Toast.makeText(getApplicationContext(),
								Constants.TOAST_NO_DATA, Toast.LENGTH_SHORT).show();
					}
				}
			}else{
				mainAct = (MainActivity) mContext;
				((BaseAdapter) mainAct.listview.getAdapter())
				.notifyDataSetChanged();

				mainAct = (MainActivity) mContext;
				((BaseAdapter) mainAct.listview.getAdapter())
				.notifyDataSetChanged();
				mainAct.mtdSalesValue
				.setText(Singleton.getInstance().mtdSalesValue);
				mainAct.ytdSalesValue
				.setText(Singleton.getInstance().ytdSalesValue);
				mainAct.adlSalesValue
				.setText(Singleton.getInstance().adlSalesValue);
			}
			pDialog.dismiss();
		}
	}

	public class LoadOfflineKPIAsyncTask extends
	AsyncTask<JSONObject, Void, Void> {

		Context mContext;
		String mRequestUrl, mMethodType;
		ServiceHelper serviceHelper;
		int mWhich;
		JSONObject responseJson, responseJsonKPI;
		ArrayList<JSONObject> mRequestJson;
		MainActivity mainAct = new MainActivity();

		public LoadOfflineKPIAsyncTask(Context context, String requestUrl,
				String methodType, ArrayList<JSONObject> requestJson) {
			this.mContext = context;
			this.mRequestUrl = requestUrl;
			this.mRequestJson = requestJson;
			this.mMethodType = methodType;

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
			Log.i("request data doinbak", mRequestJson.toString());
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), mRequestUrl, mMethodType);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			JSONArray localJsonArray = null;
			try {
				if (responseJson.has(Constants.KEY_MYQUOTES_RESPONSE)) {
					dbHandler.clearLbDetailsTable();
					localJsonArray = null;
					localJsonArray = responseJson
							.getJSONArray(Constants.KEY_MYQUOTES_RESPONSE);
					JSONObject lbJsonObj = localJsonArray.getJSONObject(0);
					Iterator<String> keys = lbJsonObj.keys();
					SimpleDateFormat sd = new SimpleDateFormat(
							"M/d/yyyy h:mm:ss a");
					SimpleDateFormat sdDate = new SimpleDateFormat("M/d/yyyy");
					SimpleDateFormat sdTime = new SimpleDateFormat("h:mm a");
					while (keys.hasNext()) {
						String key = (String) keys.next();
						JSONArray lbDetailJsonArray = lbJsonObj
								.getJSONArray(key);
						for (int i = 0; i < lbDetailJsonArray.length(); i++) {
							JSONObject detailObl = lbDetailJsonArray
									.getJSONObject(i);
							MyHotQuotesModel mModel = new MyHotQuotesModel();
							mModel.setCustomerId(detailObl.get(
									Constants.KEY_MYQUOTES_CUSTOMER_ID)
									.toString());
							mModel.setCustomerFullName(detailObl.get(
									Constants.KEY_MYQUOTES_CUSTOMER_NAME)
									.toString());
							mModel.setAddress(detailObl.get(
									Constants.KEY_MYQUOTES_ADDRESS).toString());
							mModel.setCity(detailObl.get(
									Constants.KEY_MYQUOTES_CITY).toString());
							mModel.setState(detailObl.get(
									Constants.KEY_MYQUOTES_STATE).toString());
							mModel.setZip(detailObl.get(
									Constants.KEY_MYQUOTES_ZIP).toString());
							mModel.setFollowups(detailObl.get(
									Constants.KEY_MYQUOTES_FOLLOWUPS)
									.toString());
							mModel.setJobs(detailObl.get(
									Constants.KEY_MYQUOTES_JOBS).toString());
							mModel.setAppts(detailObl.get(
									Constants.KEY_MYQUOTES_APPTS).toString());
							mModel.setDateLastEvent(detailObl.get(
									Constants.KEY_MYQUOTES_DATELASTEVENT)
									.toString());
							if (!detailObl
									.get(Constants.KEY_MYQUOTES_DATELASTEVENT)
									.toString().equals("")) {
								try {
									Date dt = sd
											.parse(detailObl
													.get(Constants.KEY_MYQUOTES_DATELASTEVENT)
													.toString());
									Calendar c = Calendar.getInstance();
									c.setTime(dt);
									Date ndt = c.getTime();

									mModel.setDate("" + sdDate.format(ndt));
									mModel.setTime("" + sdTime.format(ndt));

								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								mModel.setDate("");
								mModel.setTime("");
							}
							dbHandler.addLbDetails(mModel, key);
						}
					}
				}
				if (responseJson.has(Constants.KEY_HOME_RESPONSE)) {
					dbHandler.removeFromRow(Constants.TABLE_HOME_LOADSH);
					dbHandler.removeFromRow(Constants.TABLE3_HOME);
					localJsonArray = null;
					localJsonArray = responseJson
							.getJSONArray(Constants.KEY_HOME_RESPONSE);
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(i);
						dbHandler
						.insertValueforHomeSH(
								jobj.get(Constants.KEY_LISTBOX_ID)
								.toString(),
								jobj.get(Constants.KEY_LISTBOX_NAME)
								.toString(),
								jobj.get(Constants.KEY_LISTBOX_COUNT)
								.toString(),
								jobj.get(Constants.KEY_LISTBOX_ORDINAL)
								.toString());
					}
					localJsonArray = null;
					localJsonArray = responseJson
							.getJSONArray(Constants.KEY_HOME_RESPONSE_KPI);
					for (int i = 0; i < localJsonArray.length(); i++) {
						JSONObject jobj = localJsonArray.getJSONObject(i);
						dbHandler.insertValueHome(
								jobj.get(Constants.KEY_HOME_MTD).toString(),
								jobj.get(Constants.KEY_HOME_YTD).toString(),
								jobj.get(Constants.KEY_HOME_ADL).toString());
					}
				} else if (responseJson
						.getString(Constants.KEY_LOGIN_RESPONSE_MESSAGE)
						.toString()
						.equalsIgnoreCase(
								Constants.VALUE_INVALID_API_CREDENTIAL)) {
					Toast.makeText(
							mContext,
							responseJson.getString(
									Constants.KEY_LOGIN_RESPONSE_MESSAGE)
									.toString(), Constants.TOASTMSG_TIME)
									.show();
				} else {
					Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
							Constants.TOASTMSG_TIME).show();
				}

				/*if (Singleton.getInstance().leadTypeModel.size() == 0) {
					leadTypeAsnc = new LeadTypeAsync(context, dealerID);
					leadTypeAsnc.execute();
				}*/
			} catch (Exception e) {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Constants.TOASTMSG_TIME).show();
			}
			handler.sendEmptyMessage(5);
			pDialog.dismiss();
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
		if (homeAsyncTask != null) {
			homeAsyncTask.cancel(true);
			homeAsyncTask = null;
		}
		if (searchAsyncTask != null) {
			searchAsyncTask.cancel(true);
			searchAsyncTask = null;
		}
		if (leadTypeAsnc != null) {
			leadTypeAsnc.cancel(true);
			leadTypeAsnc = null;
		}
		if (dispoSubDispo != null) {
			dispoSubDispo.cancel(true);
			dispoSubDispo = null;
		}
		if (addProspectAndAprofessionalContactAsyncTask != null) {
			addProspectAndAprofessionalContactAsyncTask.cancel(true);
			addProspectAndAprofessionalContactAsyncTask = null;
		}
		if (loadDropDownAsyncTask != null) {
			loadDropDownAsyncTask.cancel(true);
			loadDropDownAsyncTask = null;
		}
		if (loadOfflineKPIAsyncTask != null) {
			loadOfflineKPIAsyncTask.cancel(true);
			loadOfflineKPIAsyncTask = null;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		MainActivity.this.finish();
		moveTaskToBack(true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.gc();
		if(goRecent.equalsIgnoreCase(Constants.KEY_UPDATE_DISPOS_TO_PROCEED)){
			dialog = new Dialog(MainActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
			WMLP.gravity = Gravity.CENTER;
			dialog.getWindow().setAttributes(WMLP);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(R.layout.loginalert);

			TextView messageText = (TextView)dialog.findViewById(R.id.alerttext);
			messageText.setText(R.string.login_update_response);

			Button okButton = (Button)dialog.findViewById(R.id.emailok);
			okButton.setOnClickListener(this);

			Button cencelButton = (Button)dialog.findViewById(R.id.emailCancel);
			cencelButton.setOnClickListener(this);

			dialog.show();	
		}else{
			loadAllData();
		}
	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (Utilities.isNetworkConnected(context)) {
					dbHandler.removeCommonRow(Constants.TABLE4_JSON_DATA,
							Constants.ADD_PROSPECT_DROPDOWN_WEBSERVICE,
							Constants.WEBSERVICE_ADD_PROSPECT_DROPDOWN);
					if(dbHandler.getTestCount()>0){
						callUpSyncData();
					}else{
						loadDropDownAsyncTask = new LoadDropDownItemsAsyncTask(
								Constants.URL_PROSPECT_CONFIG + dealerID).execute();
					}
					
				} else {
					Toast.makeText(context, Constants.NETWORK_NOT_AVAILABLE,
							Toast.LENGTH_SHORT).show();
				}
				break;
				//Upload offline calendar entry data
			case 2:
				uploadDataToServer();
				break;
			case 3:
				Calendar mcalenda = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy",
						Locale.US);
				String curentDateString = df.format(mcalenda.getTime());

				JSONObject reqObj = new JSONObject();
				ArrayList<JSONObject> reqArr1 = new ArrayList<JSONObject>();
				try {

					reqObj.put(Constants.KEY_LOGIN_DEALER_ID, dealerID);
					reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, employeeID);
					reqObj.put(Constants.KEY_CALENDAR_STARTDATE,
							curentDateString);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reqArr1.add(reqObj);
				calendarAsyncTask = (CalendarAsyncTask) new CalendarAsyncTask(
						MainActivity.this,
						Constants.URL_CALENDAR_LIST_PAGINATION,
						Constants.REQUEST_TYPE_POST, reqArr1).execute();
				break;
			case 4:
				loadOfflineKPIAsyncTask = new LoadOfflineKPIAsyncTask(context,
						Constants.LOAD_KPIVALUE_OFFLINE,
						Constants.REQUEST_TYPE_POST, reqArr).execute();
				break;
				// Download event configuration
			case 5:
				eventConfigAsyncTask = new EventConfigAsyncTask(context, dealerID).execute();
				break;
				// Download leadtype
			case 6:
				leadTypeAsyncTask = new LeadTypeOfflineAsync(context, dealerID).execute();
				break;

			default:
				break;
			}
		}
	};

	class CalendarAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context mContext;
		String mRequestUrl, mMethodType;
		ServiceHelper serviceHelper;
		String mKey;
		JSONObject responseJson, responseJsonKPI;
		ArrayList<JSONObject> mRequestJson;
		// ActivityIndicator pDialog;
		int mSwipeEvent;

		public CalendarAsyncTask(Context context, String requestUrl,
				String methodType, ArrayList<JSONObject> requestJson) {
			this.mContext = context;
			this.mRequestUrl = requestUrl;
			this.mRequestJson = requestJson;
			this.mMethodType = methodType;

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
			pDialog.setLoadingText("Loading Calendar data....");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {

			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), mRequestUrl, mMethodType);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			parseListJsonDate(responseJson);
			handler.sendEmptyMessage(4);
			// pDialog.dismiss();

		}
	}

	private void parseListJsonDate(JSONObject responseJson2) {
		// TODO Auto-generated method stub
		dbHandler.clearCalendarTable();
		JSONObject calendarJsonObject;
		try {
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

					dbHandler.addCalendarData(mlistModel);

				} else {
					CalendarListPaginationModel mlistModelHead = new CalendarListPaginationModel();
					mlistModelHead.mEventType = "isHeader";
					mlistModelHead.mFormattedApptDate = currentDynamicKey;
					Singleton.getInstance().mCalendarListPaginationData
					.add(mlistModelHead);

					dbHandler.addCalendarData(mlistModelHead);
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

						dbHandler.addCalendarData(mlistModel);
					}
				}

				Singleton.getInstance().mCalendarListData.put(
						currentDynamicKey,
						Singleton.getInstance().mCalendarListPaginationData);
				// Singleton.getInstance().mCalendarListPaginationData.clear();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	// Async task for event configuration
	public class EventConfigAsyncTask extends AsyncTask<Void, Void, Void> {
		Context ctx;
		ServiceHelper helper;
		JSONObject responseJson;
		String dealerId;

		public EventConfigAsyncTask(Context mContext, String dealerid) {
			this.ctx = mContext;
			this.dealerId = dealerid;
			helper = new ServiceHelper(mContext);
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
			pDialog.setLoadingText("Loading Events....");
			pDialog.show();
			super.onPreExecute();
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

				if (responseJson.has(Constants.EVENT_CONFIGUATION_KEY)) {

					JSONArray jsonarray = responseJson
							.getJSONArray(Constants.EVENT_CONFIGUATION_KEY);
					dbHandler.clearEventConfigTable();
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject objs = jsonarray.getJSONObject(i);
						String defaultTime = objs
								.getString(Constants.DEFAULT_APPNT_DURATION);
						Log.d("defaultTime", defaultTime);
						JSONArray arrays = objs
								.getJSONArray(Constants.APPNT_TYPE);
						if (arrays != null) {

							for (int j = 0; j < arrays.length(); j++) {
								JSONObject objAppnt = arrays.getJSONObject(j);
								dbHandler
								.insertValueEventConfig(
										objAppnt.getString(Constants.EC_APPNT_TPYE_ID),
										objAppnt.getString(Constants.EC_APPNT_TPYE_NAME),
										Constants.APPNT_TYPE,
										defaultTime);

							}
						}

						JSONArray arraysVisit = objs
								.getJSONArray(Constants.CUSTOMER_VISIT_TYPE);
						if (arraysVisit != null) {
							for (int a = 0; a < arraysVisit.length(); a++) {
								JSONObject objVisit = arraysVisit
										.getJSONObject(a);
								dbHandler
								.insertValueEventConfig(
										objVisit.getString(Constants.EC_APPNT_TPYE_ID),
										objVisit.getString(Constants.EC_APPNT_TPYE_NAME),
										Constants.CUSTOMER_VISIT_TYPE,
										defaultTime);

							}

						}

					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pDialog.dismiss();
			handler.sendEmptyMessage(6);
		}
	}

	//LeadType Asynctask
	public class LeadTypeOfflineAsync extends AsyncTask<Void, Void, Void> {
		Context mContext;
		JSONObject responseJson, localJsonArray;
		ServiceHelper serviceHelper;
		ArrayList<JSONObject> mRequestJson;
		String requestUrl, dealerId;
		JSONArray localInnerJsonArray1, localInnerJsonArray2;
		LeadTypeModel leadTypeModel;
		AppointmentTypeModel appointmentTypeModel;

		public LeadTypeOfflineAsync(Context context, String dealerId2) {
			// TODO Auto-generated constructor stub
			this.mContext = context;
			this.dealerId = dealerId2;
			serviceHelper = new ServiceHelper(this.mContext);

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			localInnerJsonArray1 = new JSONArray();
			localInnerJsonArray2 = new JSONArray();
			localJsonArray = new JSONObject();
			responseJson = new JSONObject();
			mRequestJson = new ArrayList<JSONObject>();
			requestUrl = Constants.QUESTIONNAIRE_CONFIG + "?" + "DealerId="
					+ dealerId;
			if (pDialog != null) {
				if (pDialog.isShowing()) {
					pDialog.dismiss();
					pDialog = null;
				} else {
					pDialog = null;
				}
			}
			pDialog = new ActivityIndicator(mContext);
			pDialog.setLoadingText("Loading Lead type....");
			pDialog.show();
			super.onPreExecute();
		}

		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper
					.jsonSendHTTPRequest(mRequestJson.toString(), requestUrl,
							Constants.REQUEST_TYPE_GET);
			return null;
		}

		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (responseJson != null) {
				if (responseJson.has(Constants.QC)) {
					try {
						localJsonArray = responseJson.getJSONObject(Constants.QC);



						localInnerJsonArray2 = localJsonArray
								.getJSONArray(Constants.LEAD_TYPE);

						if (localInnerJsonArray2.length() > 0) {
							dbHandler.clearLeadTypeTable();
							for (int j = 0; j < localInnerJsonArray2.length(); j++) {
								leadTypeModel = new LeadTypeModel();
								JSONObject obj2 = localInnerJsonArray2
										.getJSONObject(j);
								dbHandler.insertValueLeadType(obj2.getString(Constants.ID), obj2.getString(Constants.TYPENAME));

							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.d("CAL INTER", "CAL INTE");
				} else {
					Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
							Toast.LENGTH_SHORT).show();
				}

			} else {
				Toast.makeText(mContext, Constants.TOAST_CONNECTION_ERROR,
						Toast.LENGTH_SHORT).show();
			}
			pDialog.dismiss();

		}

	}

	/**
	 * Upload the calendar entry code block
	 */
	private void uploadDataToServer() {
		// TODO Auto-generated method stub
		offlineEvents.clear();
		offlineEvents = dbHandler.getAllOfflineCalendarEvents();
		flagOfflineSize=0;	
		Log.i("**Start**", "-Up Sync-");
		if (offlineEvents.size()>0) {
			if (Utilities.isNetworkConnected(context)) {
				startUpSync(flagOfflineSize);
			}
		} else {
			handler.sendEmptyMessage(3);
		}





	}

	private void startUpSync(int position) {
		// TODO Auto-generated method stub
		ArrayList<JSONObject> reqArrayList = forJsonRequest(offlineEvents.get(position));
		SaveAppntAsyncTask = new SaveAppntAsyncTask(context,
				reqArrayList).execute();

	}

	public class SaveAppntAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context context;
		ArrayList<JSONObject> reqObj;
		ServiceHelper helper;
		JSONObject responseJson;


		public SaveAppntAsyncTask(Context ctx, ArrayList<JSONObject> reqArrPro) {
			this.context = ctx;
			this.reqObj = reqArrPro;
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
			pDialog = new ActivityIndicator(mContext);
			pDialog.setLoadingText("Uploading bookings...");
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			Log.d("requestJson = ", "" + reqObj.toString());
			responseJson = helper.jsonSendHTTPRequest(reqObj.toString(),
					Constants.CREATE_EVENT_URL, Constants.REQUEST_TYPE_POST);
			Log.d("responseJson = ", "" + responseJson);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (flagOfflineSize < offlineEvents.size()) {
				if (Utilities.isNetworkConnected(context)) {
					startUpSync(flagOfflineSize);
				}
			} else {
				Log.i("**End**", "-Up Sync-");
				Log.i("**flagOfflineSize**", ""+flagOfflineSize);
				dbHandler.clearOffLineCalendarEntryTable();
				handler.sendEmptyMessage(3);
			}

		}

	}
	public ArrayList<JSONObject> forJsonRequest(OfflineCalendarEntryModel offlineModel){
		Log.i("**flagOfflineSize**", ""+flagOfflineSize);
		flagOfflineSize++;
		ArrayList<JSONObject> reqArrayList = new ArrayList<JSONObject>();
		JSONObject reqobjPro = new JSONObject();
		try {

			reqobjPro.put(Constants.KEY_LOGIN_DEALER_ID, offlineModel.mDealerId);
			reqobjPro.put(Constants.KEY_LOGIN_EMPLOYEE_ID,
					offlineModel.mEmployeeId);
			reqobjPro.put(Constants.EVENT_EMPLOYEE_ID, offlineModel.mEmployeeId);
			reqobjPro.put(Constants.EVENT_NOTES,offlineModel.mNotes);
			reqobjPro.put(Constants.EVENT_START_TIME,offlineModel.mStartTime);
			reqobjPro.put(Constants.EVENT_END_TIME,offlineModel.mEndTime);
			reqobjPro.put(Constants.EVENT_ID, offlineModel.mEventId);

			reqobjPro.put(Constants.EVENT_TYPE, offlineModel.mEventType);
			reqobjPro.put(Constants.KEY_CUSTOMER_ID, offlineModel.mCustomerId);
			reqobjPro.put(Constants.EC_APPNT_TPYE_ID, offlineModel.mTypeId);
			reqobjPro.put(Constants.EVENT_DATE,offlineModel.mEventDate);
			reqobjPro.put(Constants.LEAD_TYPE_ID, offlineModel.mLeadTypeId);

			reqArrayList.add(reqobjPro);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reqArrayList;
	}
}
