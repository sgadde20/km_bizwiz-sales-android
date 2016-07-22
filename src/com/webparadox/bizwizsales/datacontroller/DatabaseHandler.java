package com.webparadox.bizwizsales.datacontroller;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.webparadox.bizwizsales.libraries.Constants;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;
import com.webparadox.bizwizsales.models.CustomerDetailsAppointmentsModel;
import com.webparadox.bizwizsales.models.EventConfigurationAppntTypeModel;
import com.webparadox.bizwizsales.models.EventConfigurationVisitTypeModel;
import com.webparadox.bizwizsales.models.HomeScreenModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;
import com.webparadox.bizwizsales.models.MyHotQuotesModel;
import com.webparadox.bizwizsales.models.OfflineCalendarEntryModel;

public class DatabaseHandler extends SQLiteOpenHelper {

	//Database Version
	private static final int DATABASE_VERSION = 1;
	//Database Name
	private static final String DATABASE_NAME = "BizWizSales";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//Create Table
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE_TEST + "("
				+ Constants.KEY_ID + " INTEGER PRIMARY KEY," 
				+ Constants.KEY_CUSTOMER_ID + " TEXT," 
				+ Constants.KEY_EMPLOYEE_ID + " TEXT," 
				+ Constants.KEY_DELEAR_ID + " TEXT," 
				+ Constants.KEY_DESCRIPTION + " TEXT,"
				+ Constants.KEY_PATH + " TEXT" + ")";

		String CREATE_CONTACTS_TABLE1 = "CREATE TABLE IF NOT EXISTS " + Constants.TABLE1_ADD_PROSPECT + "("
				+ Constants.ADD_PROSPECT_ID + " INTEGER PRIMARY KEY," 
				+ Constants.ADD_PROSPECT_TYPE + " TEXT," 
				+ Constants.ADD_PROSPECT_FIRSTNAME + " TEXT," 
				+ Constants.ADD_PROSPECT_LASTNAME + " TEXT," 
				+ Constants.ADD_PROSPECT_COMPANYNAME + " TEXT,"
				+ Constants.ADD_PROSPECT_COMPANYTYPE + " TEXT," 
				+ Constants.ADD_PROSPECT_COMPANYSUBTYPE + " TEXT,"
				+ Constants.ADD_PROSPECT_EMAIL + " TEXT,"
				+ Constants.ADD_PROSPECT_ADDRESS + " TEXT," 
				+ Constants.ADD_PROSPECT_ZIP + " TEXT,"
				+ Constants.ADD_PROSPECT_CITY + " TEXT," 
				+ Constants.ADD_PROSPECT_STATE + " TEXT,"
				+ Constants.ADD_PROSPECT_COUNTRY + " TEXT" + ")";

		String CREATE_CONTACTS_TABLE2 = "CREATE TABLE IF NOT EXISTS " +  Constants.TABLE2_ADD_PROSPECT_CONTACT + "("
				+ Constants.ADD_PROSPECT_CONTACT_ID + " INTEGER PRIMARY KEY," 
				+ Constants.ADD_PROSPECT_CONTACT_REFERENCE_ID + " TEXT," 
				+ Constants.ADD_PROSPECT_CONTACT_PHONE + " TEXT," 
				+ Constants.ADD_PROSPECT_CONTACT_PHONETYPE + " TEXT" + ")";

		String CREATE_CONTACTS_TABLE3 = "CREATE TABLE IF NOT EXISTS " +  Constants.TABLE3_HOME + "("
				+ Constants.HOME_MY_MTD_SALES + " TEXT," 
				+ Constants.HOME_MY_YTD_SALES + " TEXT," 
				+ Constants.HOME_MY90_DAY_ADL + " TEXT" + ")";

		String CREATE_CONTACTS_TABLE4 = "CREATE TABLE " +  Constants.TABLE4_JSON_DATA + "("
				+  Constants.ADD_PROSPECT_DROPDOWN_S_ID + " INTEGER PRIMARY KEY," 
				+  Constants.ADD_PROSPECT_DROPDOWN_WEBSERVICE + " TEXT,"
				+  Constants.ADD_PROSPECT_DROPDOWN_JSONDATA + " TEXT" + ")";

		String mCreateTableCalendar = "CREATE TABLE IF NOT EXISTS "+Constants.TABLE_CALENDAR
				+ " (EVENT_TYPE TEXT ,EVENT_NOTES TEXT,CUSTOMER_ID TEXT,CUSTOMER_NAME TEXT " +
				",ADDRESS TEXT ,CITY TEXT ,STATE TEXT ,ZIP TEXT ,FORMATTED_APPT_DATE TEXT ,APPT_TIME TEXT ," +
				"LEAD_OR_VISIT_TYPE TEXT ,GROUP_CATEGORY TEXT ,EVENT_ID TEXT );";


		String CREATE_HOME_LOAD_SH = "CREATE TABLE IF NOT EXISTS " +  Constants.TABLE_HOME_LOADSH + "("
				+  Constants.HOME_LOAD_API_SH_LISTBOXID + " TEXT," 
				+  Constants.HOME_LOAD_API_SH_LISTBOXNAME + " TEXT," +  Constants.HOME_LOAD_API_SH_LISTBOXCOUNT + " TEXT,"
				+  Constants.HOME_LOAD_API_SH_ORDINAL + " TEXT" + ")";

		String CREATE_HOME_LOAD_LB1 = "CREATE TABLE IF NOT EXISTS " +  Constants.TABLE_HOME_LOADLB_DETAILS + "("
				+  Constants.HOME_LOAD_API_LB1ID + " TEXT," 
				+  Constants.HOME_LOAD_API_LB1_CUSTOMERID + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_CUSTOMERFULLNAME + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_ADDRESS + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_CITY + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_STATE + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_ZIP + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_FOLLOWUPS + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_JOBS + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_APPTS + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_DATELASTEVENT + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_DATE + " TEXT,"
				+  Constants.HOME_LOAD_API_LB1_TIME+ " TEXT" + ")";


		String CREATE_CUSTOMER_DETAILS = "CREATE TABLE IF NOT EXISTS " +  Constants.TABLE_CUSTOMER_DETAILS + "("
				+  Constants.CUSTOMER_DETAILS_EVENT_TYPE + " TEXT," 
				+  Constants.CUSTOMER_DETAILS_APPT_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_APPT_TYPE + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_SALES_REP + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_LEAD_TYPE + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_RESULT + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_SUB_RESULT + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_DISPO_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_SUB_DISPO_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_AMOUNT + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_FORMATED_APP_DATE + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_APPT_TIME + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_START_DATE_TIME + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_LEAD_TYPE_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_APPT_TYPE_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_EVENT_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_APPT_RESULT_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_LOCK_SAVING_DISPO + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_DELEAR_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_CUSTOMER_ID + " TEXT,"
				+  Constants.CUSTOMER_DETAILS_EMPLOYEE_ID+ " TEXT" + ")";

		String TableOffLineCalendarEntry = "CREATE TABLE IF NOT EXISTS "+ Constants.TABLE_OFF_LINE_CALENDAR_ENTRY
				+ " (DEALEAR_ID TEXT ,EMPLOYEE_ID TEXT,EVENT_EMPLOYEE_ID TEXT,CUSTOMER_ID TEXT,EVENT_TYPE TEXT " +
				",TYPE_ID TEXT ,EVENT_DATE TEXT ,NOTES TEXT ,START_TIME TEXT ,END_TIME TEXT ,LEAD_TYPE_ID TEXT ," +
				"EVENT_ID TEXT);";
		String TableOfflineEventConfig = "CREATE TABLE IF NOT EXISTS "+ Constants.TABLE_OFF_LINE_EVENT_CONFIG
				+ " ("+Constants.EC_APPNT_TPYE_ID+" TEXT ,"+Constants.EC_APPNT_TPYE_NAME+" TEXT, "+Constants.EVENT_TYPE+" TEXT, "+Constants.DEFAULT_APPNT_DURATION+" TEXT);";

		String TableOfflineLeadType = "CREATE TABLE IF NOT EXISTS "+ Constants.TABLE_OFF_LINE_LEAD_TYPE
				+ " ("+Constants.COL_LEADTYPE_ID+" TEXT ,"+Constants.COL_LEADTYPE_NAME+" TEXT);";


		db.execSQL(CREATE_CONTACTS_TABLE);
		db.execSQL(CREATE_CONTACTS_TABLE1);
		db.execSQL(CREATE_CONTACTS_TABLE2);
		db.execSQL(CREATE_CONTACTS_TABLE3);
		db.execSQL(CREATE_CONTACTS_TABLE4);
		db.execSQL(mCreateTableCalendar);
		db.execSQL(CREATE_HOME_LOAD_SH);
		db.execSQL(CREATE_HOME_LOAD_LB1);
		db.execSQL(CREATE_CUSTOMER_DETAILS);
		db.execSQL(TableOffLineCalendarEntry);
		db.execSQL(TableOfflineEventConfig);
		db.execSQL(TableOfflineLeadType);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_TEST);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE1_ADD_PROSPECT);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE2_ADD_PROSPECT_CONTACT);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE3_HOME);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE4_JSON_DATA);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_HOME_LOADSH);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_CALENDAR);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_HOME_LOADLB_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_CUSTOMER_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_OFF_LINE_CALENDAR_ENTRY);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_OFF_LINE_EVENT_CONFIG);
		db.execSQL("DROP TABLE IF EXISTS " +  Constants.TABLE_OFF_LINE_LEAD_TYPE);
		onCreate(db);
	}

	//Insert Value to Table1 Add Prospect
	public void insertValueAddProspect(String type, String firstName, String lastName, String companyName,String companyType,String companySubType,String email,String address,String zip,String city,String state, ArrayList<String> phoneType, ArrayList<String> phone) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constants.ADD_PROSPECT_TYPE, type);
		values.put(Constants.ADD_PROSPECT_FIRSTNAME, firstName);
		values.put(Constants.ADD_PROSPECT_LASTNAME, lastName);
		values.put(Constants.ADD_PROSPECT_COMPANYNAME, companyName);
		values.put(Constants.ADD_PROSPECT_COMPANYTYPE, companyType);
		values.put(Constants.ADD_PROSPECT_COMPANYSUBTYPE, companySubType);
		values.put(Constants.ADD_PROSPECT_EMAIL, email);
		values.put(Constants.ADD_PROSPECT_ADDRESS, address);
		values.put(Constants.ADD_PROSPECT_ZIP, zip);
		values.put(Constants.ADD_PROSPECT_CITY, city);
		values.put(Constants.ADD_PROSPECT_STATE, state);
		db.insert( Constants.TABLE1_ADD_PROSPECT, null, values);
		db.close();
		String maxId = getMaxIDfromAddProspect();
		for(int i=0;i<phone.size();i++){
			insertValueAddProspectContact(maxId, phone.get(i), phoneType.get(i));
		}
	}

	//Insert Value to Table2 Add Prospect
	public void insertValueAddProspectContact(String referceId, String phone, String phoneType) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constants.ADD_PROSPECT_CONTACT_REFERENCE_ID, referceId);
		values.put(Constants.ADD_PROSPECT_CONTACT_PHONE, phone);
		values.put(Constants.ADD_PROSPECT_CONTACT_PHONETYPE, phoneType);
		db.insert( Constants.TABLE2_ADD_PROSPECT_CONTACT, null, values);
		db.close();
		getProspectCount(6);
	}

	//Insert Value to Table3 Home
	public void insertValueHome(String MyMTDSales, String MyYTDSales, String My90DayADL) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constants.HOME_MY_MTD_SALES, MyMTDSales);
		values.put(Constants.HOME_MY_YTD_SALES, MyYTDSales);
		values.put(Constants.HOME_MY90_DAY_ADL, My90DayADL);
		db.insert( Constants.TABLE3_HOME, null, values);
		db.close();
	}

	//Insert Value to Table4 HomeList
	public void insertValueJsonData( String webserviceName,String jsonData) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put( Constants.ADD_PROSPECT_DROPDOWN_WEBSERVICE, webserviceName);
		values.put( Constants.ADD_PROSPECT_DROPDOWN_JSONDATA,jsonData );
		db.insert( Constants.TABLE4_JSON_DATA, null, values);
		db.close();
	}

	//Insert Value to Table4 insertValue HomeSH
	public void insertValueforHomeSH(String ListBoxId,String ListBoxName,String ListBoxCount,String Ordinal) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put( Constants.HOME_LOAD_API_SH_LISTBOXID, ListBoxId);
		values.put( Constants.HOME_LOAD_API_SH_LISTBOXNAME,ListBoxName );
		values.put( Constants.HOME_LOAD_API_SH_LISTBOXCOUNT,ListBoxCount );
		values.put( Constants.HOME_LOAD_API_SH_ORDINAL,Ordinal );
		db.insert( Constants.TABLE_HOME_LOADSH, null, values);
		db.close();
	}

	//Insert Value
	public String adddata(String customerId, String employeeId, String delearId, String path,String discription) {
		String statusId = "";
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constants.KEY_CUSTOMER_ID, customerId);
		values.put(Constants.KEY_EMPLOYEE_ID, employeeId);
		values.put(Constants.KEY_DELEAR_ID, delearId);
		values.put(Constants.KEY_DESCRIPTION, discription);
		values.put(Constants.KEY_PATH, path);
		db.insert( Constants.TABLE_TEST, null, values);
		db.close();
		statusId  = getId(customerId);
		return statusId;
	}

	//Get MaxID from Addprospect Table1
	public String getMaxIDfromAddProspect(){
		String maxId = "";
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT MAX("+Constants.ADD_PROSPECT_ID+") FROM "+ Constants.TABLE1_ADD_PROSPECT;
		Cursor cursor = db.rawQuery(query, null);
		if(cursor != null){
			cursor.moveToFirst();
			maxId = cursor.getString(0);
		}
		return maxId;
	}



	//Get Max ID
	public String getId(String customerId){
		String id = "";
		String maxIdQuery = "SELECT MAX("+ Constants.KEY_ID+") FROM "+  Constants.TABLE_TEST +" where "+ Constants.KEY_CUSTOMER_ID + "= "+customerId;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(maxIdQuery, null);
		if(cursor != null && cursor.moveToFirst()){
			id = cursor.getString(0);
		}
		return id;
	}

	//Get Offline AddProspect Response
	public JSONObject getJsonValue(String dealerID,String employeeID) {
		JSONObject jsonRequestText = null;
		JSONObject customerJsonData = new JSONObject();
		String countQuery = "SELECT  * FROM " +  Constants.TABLE1_ADD_PROSPECT ;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		/*if(cursor!=null && cursor.getCount()>0){
			if (cursor.moveToFirst()) {
				do {
					try {
					jsonRequestText = new JSONObject();
					customerJsonData = new JSONObject();
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_DEALER_ID,dealerID);
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_EMPLOYEE_ID,employeeID);
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_TYPE, cursor.getString(1));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_FIRST_NAME,cursor.getString(2));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_LAST_NAME,cursor.getString(3));
					if (cursor.getString(1).equalsIgnoreCase("Professional Contact")) {
						customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMPANY_NAME,cursor.getString(4));
						customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMAPNY_TYPE_ID, cursor.getString(5));
						customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMAPNY_SUB_TYPE_ID,cursor.getString(6));
					}
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_EMAIL, cursor.getString(7));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_STREET, cursor.getString(8));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_ZIP, cursor.getString(9));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_CITY, cursor.getString(10));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_STATE, cursor.getString(11));
					customerJsonData.put(
							Constants.KEY_ADDPROSPECT_CUSTOMER_PHONE_NUMBER,
							getPhoneArray(cursor.getString(0)));
					jsonRequestText.put(Constants.KEY_ADDPROSPECT_REQUEST,
							customerJsonData);
				} catch (JSONException e) {
					Log.e(Constants.KEY_ERROR, e.toString());
				}
				} while (cursor.moveToNext());
			}
		}*/

		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			try {
				jsonRequestText = new JSONObject();
				customerJsonData = new JSONObject();
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_DEALER_ID,dealerID);
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_EMPLOYEE_ID,employeeID);
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_TYPE, cursor.getString(1));
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_FIRST_NAME,cursor.getString(2));
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_LAST_NAME,cursor.getString(3));
				if (cursor.getString(1).equalsIgnoreCase("Professional Contact")) {
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMPANY_NAME,cursor.getString(4));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMAPNY_TYPE_ID, cursor.getString(5));
					customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_COMAPNY_SUB_TYPE_ID,cursor.getString(6));
				}
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_EMAIL, cursor.getString(7));
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_STREET, cursor.getString(8));
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_ZIP, cursor.getString(9));
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_CITY, cursor.getString(10));
				customerJsonData.putOpt(Constants.KEY_ADDPROSPECT_STATE, cursor.getString(11));
				customerJsonData.put(
						Constants.KEY_ADDPROSPECT_CUSTOMER_PHONE_NUMBER,
						getPhoneArray(cursor.getString(0)));
				jsonRequestText.put(Constants.KEY_ADDPROSPECT_REQUEST,
						customerJsonData);
			} catch (JSONException e) {
				Log.e(Constants.KEY_ERROR, e.toString());
			}
			cursor.close();
		} 
		return jsonRequestText;
	}

	//Get Offline AddProspect phoneNumber Response
	public JSONArray getPhoneArray(String id){
		JSONArray phoneNumberArray = new JSONArray();
		JSONObject phoneNumberJsonData = null;
		SQLiteDatabase db = this.getWritableDatabase();
		String phoneQuery = "SELECT * FROM " +  Constants.TABLE2_ADD_PROSPECT_CONTACT + " where " + Constants.KEY_ID + "= " + id ;
		Cursor cursor = db.rawQuery(phoneQuery, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				phoneNumberJsonData = new JSONObject();
				try {
					phoneNumberJsonData.putOpt(Constants.KEY_ADDPROSPECT_PHONE_NUMBERTYPE_ID,cursor.getString(2));
					phoneNumberJsonData.putOpt(Constants.KEY_ADDPROSPECT_PHONE_NUMBER,cursor.getString(1));
				} catch (JSONException e) {
					Log.e(Constants.KEY_ERROR, e.toString());
				}
				phoneNumberArray.put(phoneNumberJsonData);
			} while (cursor.moveToNext());
		}
		return phoneNumberArray;
	}

	//Delete Row from both Addprospect and AddprospectContact
	public void removeRowAddprospectContact() {
		//Delete Addprospect
		String id = null;
		String countQuery = "SELECT  * FROM " +  Constants.TABLE1_ADD_PROSPECT ;
		SQLiteDatabase db2 = this.getWritableDatabase();
		Cursor cursor = db2.rawQuery(countQuery, null);
		if(cursor != null && !cursor.isClosed()){
			cursor.moveToFirst();
			id = cursor.getString(0);
		}
		cursor.close();
		String prospectQuery = "DELETE FROM " +  Constants.TABLE1_ADD_PROSPECT + " where " + Constants.ADD_PROSPECT_ID + "= " + id ;
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(prospectQuery);

		//Delete Addprospect Contact
		String contactQuery = "DELETE FROM " +  Constants.TABLE2_ADD_PROSPECT_CONTACT + " where " + Constants.ADD_PROSPECT_CONTACT_ID + "= " + id ;
		SQLiteDatabase db1 = this.getWritableDatabase();
		db1.execSQL(contactQuery);
	}

	//Get Row Count
	public int getCount() {
		String countQuery = "SELECT  * FROM " +  Constants.TABLE_TEST ;
		int count = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null && !cursor.isClosed()){
			count = cursor.getCount();
			cursor.close();
		} 
		return count;
	}

	//Get Row Count
	public int getTestCount() {
		String countQuery = "SELECT  * FROM " +  Constants.TABLE1_ADD_PROSPECT ;
		int count = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null && !cursor.isClosed()){
			count = cursor.getCount();
			cursor.close();
		} 
		return count;
	}

	//Get Prospect Count for Offline
	public void getProspectCount(int countID) {
		String countQuery = "SELECT  * FROM " + Constants.TABLE_HOME_LOADSH  + " where " +  Constants.HOME_LOAD_API_SH_LISTBOXID + "= " + countID;
		String value = "";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null && !cursor.isClosed()){
			cursor.moveToFirst();
			value = cursor.getString(2);
			cursor.close();
		} 
		updateProspectHome(Integer.parseInt(value),countID);
	}

	public void updateProspectHome(int value,int countID){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constants.HOME_LOAD_API_SH_LISTBOXCOUNT, value+1);
		db.update(Constants.TABLE_HOME_LOADSH, values, Constants.HOME_LOAD_API_SH_LISTBOXID+"="+countID, null);
		db.close();	
	}

	//Get offline Home KPI Value
	public ArrayList<String> getHomeKPIValue() {
		String countQuery = "SELECT  * FROM " +  Constants.TABLE3_HOME ;
		ArrayList<String> homeKpiArrayList = new ArrayList<String>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null && !cursor.isClosed() && cursor.getCount()>0){
			cursor.moveToFirst();
			homeKpiArrayList.add(cursor.getString(0));
			homeKpiArrayList.add(cursor.getString(1));
			homeKpiArrayList.add(cursor.getString(2));
			cursor.close();
		} 
		return homeKpiArrayList;
	}

	//Get offline Home SH Value
	public void getHomeSHValue() {
		String countQuery = "SELECT  * FROM " +  Constants.TABLE_HOME_LOADSH ;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null && !cursor.isClosed() && cursor.getCount()>0){
			if (cursor.moveToFirst()) {
				do {
					HomeScreenModel homObj = new HomeScreenModel();
					homObj.mListboxId = cursor.getString(cursor.getColumnIndex(Constants.HOME_LOAD_API_SH_LISTBOXID));
					homObj.mListboxName = cursor.getString(cursor.getColumnIndex(Constants.HOME_LOAD_API_SH_LISTBOXNAME));
					homObj.mListboxCount = cursor.getString(cursor.getColumnIndex(Constants.HOME_LOAD_API_SH_LISTBOXCOUNT));
					homObj.mListboxOrdinal = cursor.getString(cursor.getColumnIndex(Constants.HOME_LOAD_API_SH_ORDINAL));
					Singleton.getInstance().homeList.add(homObj);
				} while (cursor.moveToNext());
			}
		} 
	}


	//Get Get AddProspectDropdown
	//Get Row Count
	public JSONObject getAddProspectDropdown(String id) {
		JSONObject jsonResultData = null;
		String countQuery = "SELECT  * FROM " +  Constants.TABLE4_JSON_DATA + " where " +  Constants.ADD_PROSPECT_DROPDOWN_WEBSERVICE + "= " + id;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		if(cursor != null && cursor.getCount()>0){
			cursor.moveToFirst();
			try {
				jsonResultData = new JSONObject(cursor.getString(2));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cursor.close();
		} 
		return jsonResultData;
	}

	/*	//Delete Query
	public void removeRow(String id) {
		String countQuery = "DELETE FROM " +  Constants.TABLE_TEST + " where " + Constants.KEY_ID + "= " + id ;
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(countQuery);
	}*/

	//Delete Query
	public void removeCommonRow(String tableName,String columnName,String id) {
		String countQuery = "DELETE FROM " +  tableName + " where " + columnName + "= " + id ;
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(countQuery);
	}

	public List<ListValue> getFavList(String customerId){
		String selectQuery = "SELECT  * FROM " +  Constants.TABLE_TEST + " where " + Constants.KEY_CUSTOMER_ID + "= " + customerId;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		List<ListValue> FavList = new ArrayList<ListValue>();
		if (cursor.moveToFirst()) {
			do {
				ListValue list = new ListValue();
				list.setId(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID)));
				list.setcustomerId(cursor.getString(cursor.getColumnIndex(Constants.KEY_CUSTOMER_ID)));
				list.setemployeeId(cursor.getString(cursor.getColumnIndex(Constants.KEY_EMPLOYEE_ID)));
				list.setdelearId(cursor.getString(cursor.getColumnIndex(Constants.KEY_DELEAR_ID)));
				list.setdescription(cursor.getString(cursor.getColumnIndex(Constants.KEY_DESCRIPTION)));
				list.setpath(cursor.getString(cursor.getColumnIndex(Constants.KEY_PATH)));
				FavList.add(list);
			} while (cursor.moveToNext());
		}
		return FavList;
	}

	public void removeFromRow(String tableName) {
		String countQuery = "DELETE FROM " +  tableName;
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(countQuery);
		db.close();
	}
	public void addCalendarData(CalendarListPaginationModel mCalendarModel) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();

		values.put(Constants.COL_EVENT_TYPE, mCalendarModel.mEventType);
		values.put(Constants.COL_EVENT_NOTES, mCalendarModel.mEventNotes);
		values.put(Constants.COL_CUSTOMER_ID, mCalendarModel.mCustomerId);
		values.put(Constants.COL_CUSTOMER_NAME, mCalendarModel.mCustomerName);
		values.put(Constants.COL_ADDRESS, mCalendarModel.mAddress);
		values.put(Constants.COL_CITY, mCalendarModel.mCity);
		values.put(Constants.COL_STATE, mCalendarModel.mState);
		values.put(Constants.COL_ZIP, mCalendarModel.mZip);
		values.put(Constants.COL_FORMATTED_APPT_DATE, mCalendarModel.mFormattedApptDate);
		values.put(Constants.COL_APPT_TIME, mCalendarModel.mApptTime);
		values.put(Constants.COL_LEAD_OR_VISIT_TYPE, mCalendarModel.mLeadOrVisitType);
		values.put(Constants.COL_GROUP_CATEGORY, mCalendarModel.mGroupCategory);
		values.put(Constants.COL_EVENT_ID, mCalendarModel.mEventId);


		db.insert(Constants.TABLE_CALENDAR , null, values);
		db.close();
	}

	public ArrayList<CalendarListPaginationModel> getAllCalendarData() {
		ArrayList<CalendarListPaginationModel> arrCalendarData = new ArrayList<CalendarListPaginationModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(Constants.TABLE_CALENDAR, new String[] { "*" }, null,
				null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				CalendarListPaginationModel mCalendarModel=new CalendarListPaginationModel();
				mCalendarModel.mEventType = cursor.getString(0);
				mCalendarModel.mEventNotes = cursor.getString(1);
				mCalendarModel.mCustomerId = cursor.getString(2);
				mCalendarModel.mCustomerName = cursor.getString(3);
				mCalendarModel.mAddress = cursor.getString(4);
				mCalendarModel.mCity = cursor.getString(5);
				mCalendarModel.mState = cursor.getString(6);
				mCalendarModel.mZip = cursor.getString(7);
				mCalendarModel.mFormattedApptDate = cursor.getString(8);
				mCalendarModel.mApptTime = cursor.getString(9);
				mCalendarModel.mLeadOrVisitType = cursor.getString(10);
				mCalendarModel.mGroupCategory = cursor.getString(11);
				mCalendarModel.mEventId = cursor.getString(12);

				arrCalendarData.add(mCalendarModel);

			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed())
			cursor.close();
		return arrCalendarData;
	}


	public void clearCalendarTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Constants.TABLE_CALENDAR, null, null);
	}
	public void clearLbDetailsTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Constants.TABLE_HOME_LOADLB_DETAILS, null, null);
	}
	public void clearOffLineCalendarEntryTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Constants.TABLE_OFF_LINE_CALENDAR_ENTRY, null, null);
	}
	public void  addOfflineCalendarEntryData(OfflineCalendarEntryModel mofflineModel) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();

		values.put(Constants.COL_DEALEAR_ID,mofflineModel.mDealerId);
		values.put(Constants.COL_EMPLOYEE_ID,mofflineModel.mEmployeeId);
		values.put(Constants.COL_EVENT_EMPLOYEE_ID,mofflineModel.mEventEmployeeId);
		values.put(Constants.COL_EVENT_TYPE,mofflineModel.mEventType);
		values.put(Constants.COL_CUSTOMER_ID,mofflineModel.mCustomerId);
		values.put(Constants.COL_TYPE_ID,mofflineModel.mTypeId);
		values.put(Constants.COL_EVENT_DATE,mofflineModel.mEventDate);
		values.put(Constants.COL_NOTES,mofflineModel.mNotes);
		values.put(Constants.COL_START_TIME,mofflineModel.mStartTime);
		values.put(Constants.COL_END_TIME,mofflineModel.mEndTime);
		values.put(Constants.COL_LEAD_TYPE_ID,mofflineModel.mLeadTypeId);
		values.put(Constants.COL_EVENT_ID,mofflineModel.mEventId);


		db.insert(Constants.TABLE_OFF_LINE_CALENDAR_ENTRY , null, values);
		db.close();
	}
	public ArrayList<OfflineCalendarEntryModel> getAllOfflineCalendarEvents() {
		ArrayList<OfflineCalendarEntryModel> arrOfflineEntrys = new ArrayList<OfflineCalendarEntryModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(Constants.TABLE_OFF_LINE_CALENDAR_ENTRY, new String[] { "*" }, null,
				null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				OfflineCalendarEntryModel mOfflineCalendarModel=new OfflineCalendarEntryModel();
				mOfflineCalendarModel.mDealerId=cursor.getString(0).toString();
				mOfflineCalendarModel.mEmployeeId=cursor.getString(1).toString();
				mOfflineCalendarModel.mEventEmployeeId=cursor.getString(2).toString();
				mOfflineCalendarModel.mCustomerId=cursor.getString(3).toString();
				mOfflineCalendarModel.mEventType=cursor.getString(4).toString();
				mOfflineCalendarModel.mTypeId=cursor.getString(5).toString();
				mOfflineCalendarModel.mEventDate=cursor.getString(6).toString();
				mOfflineCalendarModel.mNotes=cursor.getString(7).toString();
				mOfflineCalendarModel.mStartTime=cursor.getString(8).toString();
				mOfflineCalendarModel.mEndTime=cursor.getString(9).toString();
				mOfflineCalendarModel.mLeadTypeId=cursor.getString(10).toString();
				mOfflineCalendarModel.mEventId=cursor.getString(11).toString();
				arrOfflineEntrys.add(mOfflineCalendarModel);

			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed())
			cursor.close();
		return arrOfflineEntrys;
	}
	public void  addLbDetails(MyHotQuotesModel model,String strLbKey) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = this.getWritableDatabase();

		values.put(Constants.HOME_LOAD_API_LB1ID,strLbKey);
		values.put(Constants.HOME_LOAD_API_LB1_CUSTOMERID,model.getCustomerId());
		values.put(Constants.HOME_LOAD_API_LB1_CUSTOMERFULLNAME,model.getCustomerFullName());
		values.put(Constants.HOME_LOAD_API_LB1_ADDRESS,model.getAddress());
		values.put(Constants.HOME_LOAD_API_LB1_CITY,model.getCity());
		values.put(Constants.HOME_LOAD_API_LB1_STATE,model.getState());
		values.put(Constants.HOME_LOAD_API_LB1_ZIP,model.getZip());
		values.put(Constants.HOME_LOAD_API_LB1_FOLLOWUPS,model.getFollowups());
		values.put(Constants.HOME_LOAD_API_LB1_JOBS,model.getJobs());
		values.put(Constants.HOME_LOAD_API_LB1_APPTS,model.getAppts());
		values.put(Constants.HOME_LOAD_API_LB1_DATELASTEVENT,model.getDateLastEvent());
		values.put(Constants.HOME_LOAD_API_LB1_DATE,model.getDate());
		values.put(Constants.HOME_LOAD_API_LB1_TIME,model.getTime());

		db.insert(Constants.TABLE_HOME_LOADLB_DETAILS , null, values);
		db.close();
	}
	public ArrayList<MyHotQuotesModel> getLbDetailsByLbId(String strLbKey) {
		ArrayList<MyHotQuotesModel> arrLbDetail = new ArrayList<MyHotQuotesModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(Constants.TABLE_HOME_LOADLB_DETAILS, new String[] { "*" },
				Constants.HOME_LOAD_API_LB1ID + "='" +strLbKey+ "'", null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MyHotQuotesModel mHotQuotes=new MyHotQuotesModel();
				mHotQuotes.setCustomerId(cursor.getString(1));
				mHotQuotes.setCustomerFullName(cursor.getString(2));
				mHotQuotes.setAddress(cursor.getString(3));
				mHotQuotes.setCity(cursor.getString(4));
				mHotQuotes.setState(cursor.getString(5));
				mHotQuotes.setZip(cursor.getString(6));
				mHotQuotes.setFollowups(cursor.getString(7));
				mHotQuotes.setJobs(cursor.getString(8));
				mHotQuotes.setAppts(cursor.getString(9));
				mHotQuotes.setDateLastEvent(cursor.getString(10));
				mHotQuotes.setDate(cursor.getString(11));
				mHotQuotes.setTime(cursor.getString(12));
				arrLbDetail.add(mHotQuotes);

			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed())
			cursor.close();
		return arrLbDetail;
	}
	//Insert Value to Table customer detail
	public void insertValueCustomerDetail(CustomerDetailsAppointmentsModel model,String strDelerId,String strEmployeeId,String strCustomerId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(Constants.CUSTOMER_DETAILS_EVENT_TYPE,model.getEventType());
		values.put(Constants.CUSTOMER_DETAILS_APPT_ID,model.getAppointmentId());
		values.put(Constants.CUSTOMER_DETAILS_APPT_TYPE,model.getAppointmentType());
		values.put(Constants.CUSTOMER_DETAILS_SALES_REP,model.getSalesRep());
		values.put(Constants.CUSTOMER_DETAILS_LEAD_TYPE,model.getLeadType());
		values.put(Constants.CUSTOMER_DETAILS_RESULT,model.getResult());
		values.put(Constants.CUSTOMER_DETAILS_SUB_RESULT,model.getSubResult());
		values.put(Constants.CUSTOMER_DETAILS_DISPO_ID,model.getDispoId());
		values.put(Constants.CUSTOMER_DETAILS_SUB_DISPO_ID,model.getSubDispositionId());
		values.put(Constants.CUSTOMER_DETAILS_AMOUNT,model.getAmount());
		values.put(Constants.CUSTOMER_DETAILS_FORMATED_APP_DATE,model.getFormattedApptDate());
		values.put(Constants.CUSTOMER_DETAILS_APPT_TIME,model.getApptTime());
		values.put(Constants.CUSTOMER_DETAILS_START_DATE_TIME,model.getStartDateTime());
		values.put(Constants.CUSTOMER_DETAILS_LEAD_TYPE_ID,model.getLeadTypeId());
		values.put(Constants.CUSTOMER_DETAILS_APPT_TYPE_ID,model.getAppointmentTypeId());
		values.put(Constants.CUSTOMER_DETAILS_EVENT_ID,model.getEventId());
		values.put(Constants.CUSTOMER_DETAILS_APPT_RESULT_ID,model.getAppointmentResultId());
		values.put(Constants.CUSTOMER_DETAILS_LOCK_SAVING_DISPO ,model.getLocksavingDispo());
		values.put(Constants.CUSTOMER_DETAILS_DELEAR_ID,strDelerId);
		values.put(Constants.CUSTOMER_DETAILS_CUSTOMER_ID,strCustomerId);
		values.put(Constants.CUSTOMER_DETAILS_EMPLOYEE_ID,strEmployeeId);

		db.insert( Constants.TABLE_CUSTOMER_DETAILS, null, values);
		db.close();
	}

	//Delete table Event Configuration
	public void clearEventConfigTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Constants.TABLE_OFF_LINE_EVENT_CONFIG, null, null);
	}

	//Insert values to the Event configuration table
	public void insertValueEventConfig(String eventTypeId, String eventTypeName, String eventType, String eventDuration) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(Constants.EC_APPNT_TPYE_ID, eventTypeId);
		values.put(Constants.EC_APPNT_TPYE_NAME, eventTypeName);
		values.put(Constants.EVENT_TYPE, eventType);
		values.put(Constants.DEFAULT_APPNT_DURATION, eventDuration);
		db.insert( Constants.TABLE_OFF_LINE_EVENT_CONFIG, null, values);
		db.close();
	}

	//Get the event config for appointments values
	public ArrayList<EventConfigurationAppntTypeModel> getEventConfigVales() {
		ArrayList<EventConfigurationAppntTypeModel> arrEventConfigAppt = new ArrayList<EventConfigurationAppntTypeModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(Constants.TABLE_OFF_LINE_EVENT_CONFIG, new String[] { "*" },
				Constants.EVENT_TYPE + "='" +Constants.APPNT_TYPE+ "'", null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				EventConfigurationAppntTypeModel mEventConfigAppt=new EventConfigurationAppntTypeModel();
				mEventConfigAppt.setTypeId(cursor.getString(0));
				mEventConfigAppt.setTypeName(cursor.getString(1));
				mEventConfigAppt.setDefaultAppointmentDuration(cursor.getString(3));
				arrEventConfigAppt.add(mEventConfigAppt);
			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed())
			cursor.close();
		return arrEventConfigAppt;
	}
	//Get the event config for customer visits values
	public ArrayList<EventConfigurationVisitTypeModel> getEventConfigVisitValues() {
		ArrayList<EventConfigurationVisitTypeModel> arrEventConfigVisit = new ArrayList<EventConfigurationVisitTypeModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(Constants.TABLE_OFF_LINE_EVENT_CONFIG, new String[] { "*" },
				Constants.EVENT_TYPE + "='" +Constants.CUSTOMER_VISIT_TYPE+ "'", null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				EventConfigurationVisitTypeModel mEventConfigVisit=new EventConfigurationVisitTypeModel();
				mEventConfigVisit.setTypeId(cursor.getString(0));
				mEventConfigVisit.setTypeName(cursor.getString(1));
				mEventConfigVisit.setDefaultAppointmentDuration(cursor.getString(3));
				arrEventConfigVisit.add(mEventConfigVisit);
			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed())
			cursor.close();
		return arrEventConfigVisit;
	}

	//Delete table LeadType
	public void clearLeadTypeTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Constants.TABLE_OFF_LINE_LEAD_TYPE, null, null);
	}

	//Insert values to the Lead type table
	public void insertValueLeadType(String leadTypeId, String leadTypeName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(Constants.COL_LEADTYPE_ID, leadTypeId);
		values.put(Constants.COL_LEADTYPE_NAME, leadTypeName);
		db.insert( Constants.TABLE_OFF_LINE_LEAD_TYPE, null, values);
		db.close();
	}

	//Get the lead type values
	public ArrayList<LeadTypeModel> getLeadTypeVales() {
		ArrayList<LeadTypeModel> arrLeadType = new ArrayList<LeadTypeModel>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(Constants.TABLE_OFF_LINE_LEAD_TYPE, new String[] { "*" },
				null, null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				LeadTypeModel mLeadType=new LeadTypeModel();
				mLeadType.setId(cursor.getString(0));
				mLeadType.setTypeName(cursor.getString(1));
				arrLeadType.add(mLeadType);
			} while (cursor.moveToNext());
		}
		if (!cursor.isClosed())
			cursor.close();
		return arrLeadType;
	}
}