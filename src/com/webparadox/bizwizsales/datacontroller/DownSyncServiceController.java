package com.webparadox.bizwizsales.datacontroller;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.webparadox.bizwizsales.MainActivity;
import com.webparadox.bizwizsales.helper.ServiceHelper;
import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.libraries.Utilities;
import com.webparadox.bizwizsales.models.AppointmentTypeModel;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;
import com.webparadox.bizwizsales.models.MyHotQuotesModel;

public class DownSyncServiceController {
	public Context mcontext;
	public SharedPreferences userData;
	public String mDealerId;
	public String mEmployeeId;
	public DatabaseHandler dbHandler;
	public ServiceHelper serviceHelper;
	public JSONObject jsonResultText, jsonDataResultText;

	public AsyncTask<JSONObject, Void, Void> loadOfflineKPIAsyncTask;
	public AsyncTask<JSONObject, Void, Void> calendarAsyncTask;
	AsyncTask<String, Void, Void> loadDropDownAsyncTask;
	AsyncTask<Void, Void, Void> eventConfigAsyncTask;
	public LeadTypeOfflineAsync leadTypeAsnc;
	ArrayList<JSONObject> reqArr;

	public DownSyncServiceController(Context ctx) {
		this.mcontext = ctx;

		dbHandler = new DatabaseHandler(mcontext);
		userData = mcontext.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, 0);

		mDealerId = userData.getString(Constants.KEY_LOGIN_DEALER_ID, "");
		mEmployeeId = userData.getString(Constants.KEY_LOGIN_EMPLOYEE_ID, "");
		JSONObject reqObj = new JSONObject();
		reqArr = new ArrayList<JSONObject>();
		try {
			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, mDealerId);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, mEmployeeId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqArr.add(reqObj);
		if (Utilities.isNetworkConnected(mcontext)) {
			downloadDataFromServer();
		}

	}

	private final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				dbHandler.removeCommonRow(Constants.TABLE4_JSON_DATA,
						Constants.ADD_PROSPECT_DROPDOWN_WEBSERVICE,
						Constants.WEBSERVICE_ADD_PROSPECT_DROPDOWN);
				loadDropDownAsyncTask = new LoadDropDownItemsAsyncTask(
						Constants.URL_PROSPECT_CONFIG + mDealerId, mcontext).execute();
				break;
			case 2:
				/**
				 * Async Task for LoadOfflineKPIAsyncTask
				 */
				loadOfflineKPIAsyncTask = new LoadOfflineKPIAsyncTask(mcontext,
						Constants.LOAD_KPIVALUE_OFFLINE,
						Constants.REQUEST_TYPE_POST, reqArr).execute();
				break;
			case 3:
				leadTypeAsnc = new LeadTypeOfflineAsync(mcontext, mDealerId);
				leadTypeAsnc.execute();
				break;
			case 4:
				eventConfigAsyncTask = new EventConfigAsyncTask(mcontext,
						mDealerId).execute();
				break;
			default:
				break;
			}
		}
	};

	private void downloadDataFromServer() {
		// TODO Auto-generated method stub
		/**
		 * Async Task for Calendar 7 days events
		 */
		Calendar mcalenda = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
		String curentDateString = df.format(mcalenda.getTime());

		JSONObject reqObj = new JSONObject();
		ArrayList<JSONObject> reqArr1 = new ArrayList<JSONObject>();
		try {

			reqObj.put(Constants.KEY_LOGIN_DEALER_ID, mDealerId);
			reqObj.put(Constants.KEY_LOGIN_EMPLOYEE_ID, mEmployeeId);
			reqObj.put(Constants.KEY_CALENDAR_STARTDATE, curentDateString);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		reqArr1.add(reqObj);
		calendarAsyncTask = (CalendarAsyncTask) new CalendarAsyncTask(mcontext,
				Constants.URL_CALENDAR_LIST_PAGINATION,
				Constants.REQUEST_TYPE_POST, reqArr1).execute();
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
			JSONArray localJsonArray = null;
			Log.i("**Service**", "-//-");
			Log.i("LoadOfflineKPIAsyncTask", "" + responseJson);
			if(responseJson != null){
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

					}

					if (Singleton.getInstance().leadTypeModel.size() == 0) {
						handler.sendEmptyMessage(3);
					}
				} catch (Exception e) {

				}
			}

		}
	}

	class CalendarAsyncTask extends AsyncTask<JSONObject, Void, Void> {

		Context mContext;
		String mRequestUrl, mMethodType;
		ServiceHelper serviceHelper;
		String mKey;
		JSONObject responseJson, responseJsonKPI;
		ArrayList<JSONObject> mRequestJson;

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
			Log.i("**Service**", "-//-");
			Log.i("CalendarAsyncTask", "" + responseJson);
			parseListJsonDate(responseJson);

			handler.sendEmptyMessage(1);

		}
	}

	private void parseListJsonDate(JSONObject responseJson2) {
		// TODO Auto-generated method stub
		dbHandler.clearCalendarTable();
		JSONObject calendarJsonObject;
		if(responseJson2 != null){
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

			if(responseJson != null){
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
			}

		}
	}

	class LoadDropDownItemsAsyncTask extends AsyncTask<String, Void, Void> {
		String mRequestUrl;
		Context mContext;
		
		public LoadDropDownItemsAsyncTask(String requestUrl, Context context) {
			this.mRequestUrl = requestUrl;
			this.mContext = context;
		}

		@Override
		protected void onPreExecute() {
			jsonDataResultText = new JSONObject();
			serviceHelper = new ServiceHelper(mContext);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			Log.i("load dropdown updates", mRequestUrl);
			try {
				jsonDataResultText = serviceHelper.jsonSendHTTPRequest(
						Constants.EMPTY_STRING, this.mRequestUrl,
						Constants.REQUEST_TYPE_GET);

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

			handler.sendEmptyMessage(2);

		}
	}

	// LeadType Asynctask
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

			super.onPreExecute();
		}

		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			responseJson = serviceHelper.jsonSendHTTPRequest(
					mRequestJson.toString(), requestUrl,
					Constants.REQUEST_TYPE_GET);
			return null;
		}

		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (responseJson != null) {
				if (responseJson.has(Constants.QC)) {
					try {
						localJsonArray = responseJson
								.getJSONObject(Constants.QC);

						localInnerJsonArray2 = localJsonArray
								.getJSONArray(Constants.LEAD_TYPE);

						if (localInnerJsonArray2.length() > 0) {
							dbHandler.clearLeadTypeTable();
							for (int j = 0; j < localInnerJsonArray2.length(); j++) {
								leadTypeModel = new LeadTypeModel();
								JSONObject obj2 = localInnerJsonArray2
										.getJSONObject(j);
								dbHandler.insertValueLeadType(
										obj2.getString(Constants.ID),
										obj2.getString(Constants.TYPENAME));

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
			handler.sendEmptyMessage(4);

		}

	}
}
