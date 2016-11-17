package com.webparadox.bizwizsales.libraries;

import java.util.ArrayList;

import com.bugsense.trace.BugSenseHandler;
import com.webparadox.bizwizsales.LoginActivity;
import com.webparadox.bizwizsales.R;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;

public class Constants {

	public static String APPID="1";
	//public static String BASE_URL = "https://sbizwizsync.teambizwiz.com/"; // LiveBaseURL
	//public static String BASE_URL = "https://c-mobile.teambizwiz.com/"; // DevelopmentBaseURL
	public static String BASE_URL = "https://devmobile.teambizwiz.com/";
	//public static String BASE_URL = "https://prelivemobile.teambizwiz.com/";
	
	
	public static String BASE_URL_SALES = BASE_URL + "SalesApp/";
	public static String DeleteProductUrl = BASE_URL_SALES
			+ "delete_product.aspx";

	public static String URL_LOGIN = BASE_URL + "mobile_login.aspx";
	public static String URL_HOME_SCREEN = BASE_URL_SALES + "sales_home.aspx";
	public static String URL_HOME_SCREEN_KPI = BASE_URL_SALES
			+ "sales_inspector_kpi.aspx";
	public static String URL_CALENDAR = BASE_URL_SALES
			+ "sales_rep_day_calendar.aspx";
	public static String URL_CALENDAR_LIST_PAGINATION = BASE_URL_SALES
			+ "paginated_calendar.aspx";
	public static String URL_PROSPECT_CONFIG = BASE_URL_SALES
			+ "ProspectConfig.aspx?DealerId=";
	public static String URL_ADD_PROFESSIONAL_CONTACT_AND_PROSPECT = BASE_URL
			+ "create_new_customer.aspx";
	public static String URL_MY_HOT_QUOTES = BASE_URL_SALES
			+ "listbox_details.aspx";
	public static String URL_CUSTOMER_APPOINTMENTS = BASE_URL_SALES
			+ "sa_customer_appointments.aspx";
	public static String URL_CUSTOMER_PROJECTS = BASE_URL_SALES
			+ "sa_customer_projects.aspx";
	// Add Customer Note Types
	public static String NOTE_TYPE_URL = BASE_URL + "note_types.aspx";
	public static String SAVE_NOTE_TYPE_URL = BASE_URL + "save_notes.aspx";
	public static String ORDER_BY_ORDINAL = "OrderByOrdinal";
	// CREATE EVENT
	public static String CREATE_EVENT_URL = BASE_URL_SALES
			+ "create_event.aspx";
	// Customer Phone Number
	public static String GET_PHONENUMBER_URL = BASE_URL
			+ "customer_phone_numbers.aspx";

	// GET APPOINTMENT RESULT ID
	public static String URL_GET_APPOINTMENT_RESULT_ID = BASE_URL_SALES
			+ "get_appointmentresultId.aspx?DealerId=";

	public static String URL_GET_DISPO_QUESTIONNAIRE = BASE_URL_SALES
			+ "dispo_questionnaire.aspx?DealerId=";

	public static String URL_SAVE_DISPO = BASE_URL_SALES + "save_dispo.aspx";

	public static String URL_SAVE_DISPO_QUESTIONNAIRE = BASE_URL_SALES
			+ "save_dispo_questionnaire.aspx";
	// Support URL Bizwiz
	public static final String SUPPORT_URL = "http://support.teambizwiz.com/access/unauthenticated?return_to=http%3A%2F%2Fsupport.teambizwiz.com%2Fhc&theme=hc";
	public static final String SUPPORT_STRING = "support.teambizwiz.com."; // Call
	// Bizwiz
	// Number
	// APPNT TYPE AND CUSTOMER VISIT TYPES
	public static final String EVENT_CONFIGURATION_URL = BASE_URL_SALES
			+ "event_configuration.aspx";
	public static final String CALL_BIZWIZ = "tel:877-358-3100";
	// Url - get city and state from zip code
	public static String URL_GET_CITY_AND_STATE_FROM_ZIPCODE = "http://maps.googleapis.com/maps/api/geocode/json?address=";
	// Url -get SequelEmail Videos
	public static final String SEQUEL_EMAIL_VIDEOS = BASE_URL_SALES
			+ "sa_customer_sequel_emails.aspx";
	// Upload Photo
	public static String SEND_PHOTO_URL = BASE_URL
			+ "UploadCustomerAttachmentHandler.ashx";
	public static String SAVE_APPOINTMENT_QUESTIONNAIRE_URL = BASE_URL_SALES
			+ "save_appt_questionnaire.aspx";
	// Url -get SalesProcess Products
	public static final String SALES_PROCESS_PRODUCTS = BASE_URL
			+ "products_picker.aspx?DealerId=";
	// URL -post SendSequelEmail Videos
	public static final String SEND_SEQUEL_EMAIL_VIDEOS = BASE_URL_SALES
			+ "schedule_email_app_save.aspx";
	public static String EmailProposalUrl = BASE_URL_SALES
			+ "email_proposal.aspx";
	public static String SAVE_LEAD_QUESTIONARY_URL = BASE_URL_SALES
			+ "save_lead_questionnaire.aspx";
	public static String LeadQuestionaryResult = "LQR"; 

	// GET FINANCING COMPANY
	public static String URL_GET_FINANCING_COMPANY = BASE_URL_SALES
			+ "company_financing_types.aspx?DealerId=";

	// GET CHILD EMPLOYEE
	public static String URL_GET_CHILD_EMPLOYEE = BASE_URL_SALES
			+ "sa_child_employees.aspx?DealerId=";

	// Edit Customer Info
	public static String EDIT_PROSPECT_URL = BASE_URL_SALES + "customer_details.aspx?DealerId=";

	// Credit Card Types
	public static String CC_TYPES = BASE_URL + "cc_types.aspx?DealerId=";
	
	//Load Offline Kpi Values
	public static String LOAD_KPIVALUE_OFFLINE = BASE_URL_SALES + "offline_customer_info.aspx";

	// Request Type
	public static String REQUEST_TYPE_POST = "POST";
	public static String REQUEST_TYPE_GET = "GET";

	public static int swipe_Min_Distance = 150;
	public static int swipe_Max_Distance = 800;
	public static int swipe_Min_Velocity = 150;

	public static final int REQUEST_CALLED = 123;
	public static final int REQUEST_EMAILED = 124;

	// Empty String
	public static String EMPTY_STRING = "";

	// Symbols
	public static String SYMBOL_AND = "&";
	public static String SYMBOL_EQUAL_TO = "=";

	// Network Connection Message
	public static String NETWORK_CONNECTION_WIFI = "Wifi enabled";
	public static String NETWORK_CONNECTION_MOBILEDATA = "Mobile data enabled";
	public static String NETWORK_CONNECTION_NOCONNECTION = "Not connected to Internet";
	public static String NETWORK_NOT_AVAILABLE = "Intenet connection not available. Please check your internet connection.";
	public static String PERMISSION_DENIED = "Permission Denied";
	public static String SELECT_DATE_TIME = "Please Select Days And Hours";

	//Webservice reference 
	public static String WEBSERVICE_ADD_PROSPECT_DROPDOWN = "100";


	//Database Value
	public static final String TABLE_TEST = "BizWizTable";
	public static final String TABLE1_ADD_PROSPECT = "Table1AddProspect";
	public static final String TABLE2_ADD_PROSPECT_CONTACT = "Table2AddProspectContact";
	public static final String TABLE3_HOME = "Table3Home";
	public static final String TABLE4_JSON_DATA = "Table4AllWebservicejson";
	public static final String TABLE_CALENDAR = "BizWizCalendar";
	public static final String TABLE_HOME_LOADSH = "TableHomeLoadSH";
	public static final String TABLE_HOME_LOADLB_DETAILS  = "TableHomeLoadLbDetails";
	public static final String TABLE_CUSTOMER_DETAILS  = "TableCustomerDetails";
	public static final String TABLE_OFF_LINE_CALENDAR_ENTRY = "BizWizOffLineCalendarEntry";
	public static final String TABLE_OFF_LINE_EVENT_CONFIG = "BizWizOffLineEventConfig";
	public static final String TABLE_OFF_LINE_LEAD_TYPE = "BizWizOffLineLeadType";
	 
// TABLE_CUSTOMER_DETAILS  Column
	public static final String CUSTOMER_DETAILS_EVENT_TYPE="EventType";
	public static final String CUSTOMER_DETAILS_APPT_ID="AppointmentId";
	public static final String CUSTOMER_DETAILS_APPT_TYPE="AppointmentType";
	public static final String CUSTOMER_DETAILS_SALES_REP="SalesRep";
	public static final String CUSTOMER_DETAILS_LEAD_TYPE="LeadType";
	public static final String CUSTOMER_DETAILS_RESULT="Result";
	public static final String CUSTOMER_DETAILS_SUB_RESULT="SubResult";
	public static final String CUSTOMER_DETAILS_DISPO_ID="DispoId";
	public static final String CUSTOMER_DETAILS_SUB_DISPO_ID="SubDispositionId";
	public static final String CUSTOMER_DETAILS_AMOUNT="Amount";
	public static final String CUSTOMER_DETAILS_FORMATED_APP_DATE="FormattedApptDate";
	public static final String CUSTOMER_DETAILS_APPT_TIME="ApptTime";
	public static final String CUSTOMER_DETAILS_START_DATE_TIME="StartDateTime";
	public static final String CUSTOMER_DETAILS_LEAD_TYPE_ID="LeadTypeId";
	public static final String CUSTOMER_DETAILS_APPT_TYPE_ID="AppointmentTypeId";
	public static final String CUSTOMER_DETAILS_EVENT_ID="EventId";
	public static final String CUSTOMER_DETAILS_APPT_RESULT_ID="AppointmentResultId";
	public static final String CUSTOMER_DETAILS_LOCK_SAVING_DISPO="LockSavingDispo";
	public static final String CUSTOMER_DETAILS_DELEAR_ID="DelearId";
	public static final String CUSTOMER_DETAILS_CUSTOMER_ID="CustomerId";
	public static final String CUSTOMER_DETAILS_EMPLOYEE_ID="EmployeeId";
	//Column Name
	public static final String KEY_ID = "id";
	public static final String KEY_CUSTOMER_ID = "customerId";
	public static final String KEY_EMPLOYEE_ID = "employeeId";
	public static final String KEY_DELEAR_ID = "delearId";
	public static final String KEY_PATH = "path";
	public static final String KEY_DESCRIPTION = "description";

	//Table 1 Add Prospect Column
	public static final String ADD_PROSPECT_ID = "Id";
	public static final String ADD_PROSPECT_TYPE = "Type";
	public static final String ADD_PROSPECT_FIRSTNAME = "FirstName";
	public static final String ADD_PROSPECT_LASTNAME = "LastName";
	public static final String ADD_PROSPECT_COMPANYNAME = "CompanyName";
	public static final String ADD_PROSPECT_COMPANYTYPE = "CompanyType";
	public static final String ADD_PROSPECT_COMPANYSUBTYPE = "CompanySubType";
	public static final String ADD_PROSPECT_EMAIL = "Email";
	public static final String ADD_PROSPECT_ADDRESS = "Address";
	public static final String ADD_PROSPECT_ZIP = "Zip";
	public static final String ADD_PROSPECT_CITY = "City";
	public static final String ADD_PROSPECT_STATE = "State";
	public static final String ADD_PROSPECT_COUNTRY = "Country";

	//Table 2 Add Prospect Contact Column
	public static final String ADD_PROSPECT_CONTACT_ID = "Id";
	public static final String ADD_PROSPECT_CONTACT_REFERENCE_ID = "ReferenceId";
	public static final String ADD_PROSPECT_CONTACT_PHONE = "Phone";
	public static final String ADD_PROSPECT_CONTACT_PHONETYPE = "PhoneType";

	//Table 3 Add Home Column
	public static final String HOME_S_ID = "sId";
	public static final String HOME_MY_MTD_SALES = "MyMTDSales";
	public static final String HOME_MY_YTD_SALES = "MyYTDSales";
	public static final String HOME_MY90_DAY_ADL = "My90DayADL";

	//Table 4 Add Home Column
	public static final String ADD_PROSPECT_DROPDOWN_S_ID = "sId";
	public static final String ADD_PROSPECT_DROPDOWN_WEBSERVICE = "WebserviceName";
	public static final String ADD_PROSPECT_DROPDOWN_JSONDATA = "JsonData";

	//Table 5 Add Home Column KPI
	public static final String HOME_LOAD_API_ID = "HomeLoadApiId";
	public static final String HOME_LOAD_API_MTDSALES = "MTDSales";
	public static final String HOME_LOAD_API_YTDSALES = "YTDSales";
	public static final String HOME_LOAD_API_ADL = "ADL";

	//Table 6 Add Home Column SH
	public static final String HOME_LOAD_API_SH_LISTBOXID = "ListBoxId";
	public static final String HOME_LOAD_API_SH_LISTBOXNAME = "ListBoxName";
	public static final String HOME_LOAD_API_SH_LISTBOXCOUNT = "ListBoxCount";
	public static final String HOME_LOAD_API_SH_ORDINAL = "Ordinal";

	//Table 7 Add Home Column LB1
	public static final String HOME_LOAD_API_LB1ID = "LbId";
	public static final String HOME_LOAD_API_LB1_CUSTOMERID = "CustomerId";
	public static final String HOME_LOAD_API_LB1_CUSTOMERFULLNAME = "CustomerFullName";
	public static final String HOME_LOAD_API_LB1_ADDRESS = "Address";
	public static final String HOME_LOAD_API_LB1_CITY = "City";
	public static final String HOME_LOAD_API_LB1_STATE = "State";
	public static final String HOME_LOAD_API_LB1_ZIP = "Zip";
	public static final String HOME_LOAD_API_LB1_FOLLOWUPS = "Followups";
	public static final String HOME_LOAD_API_LB1_JOBS = "Jobs";
	public static final String HOME_LOAD_API_LB1_APPTS = "Appts";
	public static final String HOME_LOAD_API_LB1_DATELASTEVENT = "DateLastEvent";
	public static final String HOME_LOAD_API_LB1_DATE = "Date";
	public static final String HOME_LOAD_API_LB1_TIME = "Time";

	// Check Network Connection
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;

	// Value
	public static String VALUE_CONTENT_TYPE = "application/json";
	public static String VALUE_AUTHENTICATION = "kbh:b1zw1zMob1leApps";
	public static String VALUE_ENCODING_AUTHENTICATION = getEncodingAuthenticationKey();

	// Key
	public static String KEY_CONTENT_TYPE = "Content-Type";
	public static String KEY_ACCEPT = "Accept";
	public static String KEY_AUTHENTICATION = "Authorization";
	public static String KEY_ERROR = "error";

	// Vales - Login Screen
	public static String VALUE_SUCCESS = "SUCCESS";
	public static String VALUE_INVALID_API_CREDENTIAL = "INVALID API CREDENTIAL";
	public static String VALUE_INVALID_EMAIL_AND_PASSWORD = "INVALID EMAIL ADDRESS OR PASSWORD";

	// Key - Login Screen
	public static String KEY_LOGIN_EMAIL = "LoginEmail";
	public static String KEY_PASSWORD = "Password";
	public static String KEY_LOGIN_RESPONSE = "LS";
	public static String KEY_LOGIN_RESPONSE_MESSAGE = "ResponseMessage";
	public static String KEY_LOGIN_DEALER_ID = "DealerId";
	public static String KEY_LOGIN_EMPLOYEE_ID = "EmployeeId";
	public static String KEY_LOGIN_APPOINTMENT_ID = "AppointmentId";
	public static String KEY_LOGIN_CREATED_BY_EMPLOYEE_ID = "CreatedById";
	public static String KEY_EMPLOYEE_NAME = "EmployeeName";
	public static String KEY_EMPLOYEE_PHOTO_URL = "PhotoURL";
	public static String KEY_EMPLOYEE_CAN_CREATE_EVENTS = "CanCreateEvents";
	public static String KEY_EMPLOYEE_CAN_EDIT_EVENTS = "CanEditEvents";
	public static String KEY_APPT_NOTIFICATION_DAYS = "ApptNotificationDays";
	public static String KEY_APPT_NOTIFICATION_MINUTES = "ApptNotificationMinutes";
	public static String KEY_APPT_NOTIFICATION_HOURS = "ApptNotificationHours";
	public static String KEY_FOLLOW_UP_NOTIFICATION_DAYS = "FllwupNotificationDays";
	public static String KEY_FOLLOW_UP_NOTIFICATION_MINUTES = "FllwupNotificationMinutes";
	public static String KEY_FOLLOW_UP_NOTIFICATION_HOURS = "FllwupNotificationHours";
	public static String KEY_LOGIN_STATUS = "False";
	public static String KEY_UPDATE_DISPOS_TO_PROCEED = "UPDATE DISPOS TO PROCEED";

	// Key - Home Screen
	public static String KEY_HOME_RESPONSE = "SH";
	public static String KEY_LISTBOX_ID = "ListBoxId";
	public static String KEY_LISTBOX_NAME = "ListBoxName";
	public static String KEY_LISTBOX_COUNT = "ListBoxCount";
	public static String KEY_LISTBOX_ORDINAL = "Ordinal";
	public static String KEY_HOME_RESPONSE_KPI = "KPI";
	public static String KEY_HOME_MTD = "MTDSales";
	public static String KEY_HOME_YTD = "YTDSales";
	public static String KEY_HOME_ADL = "ADL";

	// Key - Calendar Screen
	public static String KEY_CALENDAR_CLANDER_VIEW = "CalendarView";
	public static String KEY_CALENDAR_DAY = "Day";
	public static String KEY_CALENDAR_LIST = "List";
	public static String KEY_CALENDAR_EVENTDATE = "EventDate";
	public static String KEY_CALENDAR_CURRENTMONTH = "CurrentMonth";
	public static String KEY_CALENDAR_CURRENTYEAR = "CurrentYear";
	public static String KEY_CALENDAR_STARTDATE = "StartDate";

	// Key - Calendar Json Variables
	public static String JSON_KEY_SC = "SC";
	public static String JSON_KEY_ADDRESS = "Address";
	public static String JSON_KEY_APPT_DATE_TIME = "ApptDateTime";
	public static String JSON_KEY_CITY = "City";
	public static String JSON_KEY_COMPANY_CONTACT = "CompanyContact";
	public static String JSON_KEY_COMPANY_NAME = "CompanyName";
	public static String JSON_KEY_CUSTOMER_ID = "CustomerId";
	public static String JSON_KEY_CUSTOMER_NAME = "CustomerName";
	public static String JSON_KEY_EVENT_NOTES = "EventNotes";
	public static String JSON_KEY_APPOINTMENT_ID = "AppointmentId";
	public static String JSON_KEY_EVENT_TYPE = "EventType";
	public static String JSON_KEY_HUSBAND = "Husband";
	public static String JSON_KEY_LAST_NAME = "LastName";
	public static String JSON_KEY_LEAD_OR_VISIT_TYPE = "LeadOrVisitType";
	public static String JSON_KEY_STATE = "State";
	public static String JSON_KEY_WIFE = "Wife";
	public static String JSON_KEY_ZIP = "Zip";
	public static String JSON_KEY_APPT_TIME = "ApptTime";
	public static String JSON_KEY_FORMATED_APPT_DATE = "FormattedApptDate";
	public static String JSON_KEY_GROUP_CATEGORY = "GroupCategory";

	// Key - Child Employee Json Variables
	public static String JSON_KEY_CE = "CE";
	public static String JSON_KEY_CHILD_EMPLOYEE_NAME = "ChildEmployeeName";
	public static String JSON_KEY_CHILD_EMPLOYEE_ID = "ChildEmployeeId";

	// Key - Add Prospect and Add Professional Contact Screen
	public static String KEY_PHONE_TYPE_RESPONSE = "PT";
	public static String KEY_PHONE_TYPE_ID = "Id";
	public static String KEY_PHONE_TYPE_PHONE_TYPE = "PhoneType";
	public static String KEY_COMPANY_TYPE_RESPONSE = "CT";
	public static String KEY_COMPANY_TYPE_ID = "Id";
	public static String KEY_COMPANY_TYPE_COMPANY_TYPE = "CompanyType";
	public static String KEY_COMPANY_TYPE_COMPANY_TYPE_ID = "CompanyTypeId";
	public static String KEY_COMPANY_SUB_TYPE_RESPONSE = "CST";
	public static String KEY_COMPANY_SUB_TYPE_ID = "Id";
	public static String KEY_COMPANY_SUB_TYPE_COMPANY_SUB_TYPE = "CompanySubType";
	public static String KEY_ADDPROSPECT_REQUEST = "CustomerData";
	public static String KEY_ADDPROSPECT_DEALER_ID = "DealerId";
	public static String KEY_ADDPROSPECT_EMPLOYEE_ID = "EmployeeId";
	public static String KEY_ADDPROSPECT_TYPE = "Type";
	public static String KEY_ADDPROSPECT_FIRST_NAME = "FirstName";
	public static String KEY_ADDPROSPECT_LAST_NAME = "LastName";
	public static String KEY_ADDPROSPECT_COMPANY_NAME = "CompanyName";
	public static String KEY_ADDPROSPECT_COMAPNY_TYPE_ID = "CompanyTypeId";
	public static String KEY_ADDPROSPECT_COMPANY_SUB_TYPE_RESPONSE = "SubType";
	public static String KEY_ADDPROSPECT_COMAPNY_SUB_TYPE_ID = "CompanySubTypeId";
	public static String KEY_ADDPROSPECT_EMAIL = "Email";
	public static String KEY_ADDPROSPECT_STREET = "Street";
	public static String KEY_ADDPROSPECT_ZIP = "Zip";
	public static String KEY_ADDPROSPECT_CITY = "City";
	public static String KEY_ADDPROSPECT_STATE = "State";
	public static String KEY_ADDPROSPECT_CUSTOMER_PHONE_NUMBER = "CustomerPhoneNumber";
	public static String KEY_ADDPROSPECT_PHONE_NUMBERTYPE_ID = "TypeId";
	public static String KEY_ADDPROSPECT_PHONE_NUMBER = "PhoneNumber";
	public static String KEY_ADDPROSPECT_RESPONSE = "SC";
	public static String KEY_ADDPROSPECT_STATUS = "Status";
	public static String KEY_ADDPROSPECT_CUSTOMER_ID = "CustomerId";
	public static String KEY_ADD_PROFESSIONAL_TYPE_VALUE = "Professional Contact";
	public static String KEY_ADD_PROSPECT_TYPE_VALUE = "Prospect";
	public static String KEY_COUNTRY = "country";
	public static String KEY_RESULTS = "results";
	public static String KEY_FORMATTED_ADDRESS = "formatted_address";

	// Key - My Hot Quotes Screen
	public static String KEY_MYQUOTES_RESPONSE = "LB";
	public static String KEY_MYQUOTES_CUSTOMER_ID = "CustomerId";
	public static String KEY_MYQUOTES_CUSTOMER_NAME = "CustomerFullName";
	public static String KEY_MYQUOTES_ADDRESS = "Address";
	public static String KEY_MYQUOTES_CITY = "City";
	public static String KEY_MYQUOTES_STATE = "State";
	public static String KEY_MYQUOTES_ZIP = "Zip";
	public static String KEY_MYQUOTES_FOLLOWUPS = "Followups";
	public static String KEY_MYQUOTES_JOBS = "Jobs";
	public static String KEY_MYQUOTES_APPTS = "Appts";
	public static String KEY_MYQUOTES_DATELASTEVENT = "DateLastEvent";


	// key - Customer Details Appointments json varibales
	public static String KEY_APPOINTMENTS_RESPONSE = "CA";
	public static String KEY_NEW_VIEW_NOTE = "CN";

	public static String KEY_EVENT_TYPE = "EventType";
	public static String KEY_APPOINTMENT_ID = "AppointmentId";
	public static String KEY_APPOINTMENT_TYPE = "AppointmentType";
	public static String KEY_SALES_REP = "SalesRep";
	public static String KEY_LEAD_TYPE = "LeadType";
	public static String KEY_RESULT = "Result";
	public static String KEY_SUB_RESULT = "SubResult";
	public static String KEY_DISPO_ID = "DispoId";
	public static String KEY_SUB_DISPO_ID = "SubDispositionId";
	public static String KEY_AMOUNT = "Amount";
	public static String KEY_FORMATTED_APPT_DATE = "FormattedApptDate";
	public static String KEY_APPT_TMIE = "ApptTime";
	public static String KEY_LOCKSAVINGDISPO = "LockSavingDispo";

	// Key - customer Attachment
	public static String JSON_KEY_CUSTOMER_ATTACHMNT = "CA";
	public static String CUSTOMER_ATTACHMENT_URL = BASE_URL
			+ "customer_mobile_attachments.aspx";
	public static String JSON_KEY_ATTACHMENT_URL = "AttachmentURL";
	public static String JSON_KEY_ATTACHEMT_DESCRIPTION = "AttachmentDescription";
	public static String JSON_KEY_ATTCHMENT_ADDED = "AttachmentAdded";
	public static String JSON_KEY_ATTACHMEMT_TYPE = "AttachmentType";

	public static String Attachments = "Attachments";

	public static String Attachment = "Attachment";

	public static String Pending_Documents = "Pending Documents(50)";
	// Key - customer Details Projects json variables
	public static String isLock = "";
	public static String KEY_PROJECT_RESPONSE = "CP";

	public static String KEY_PROJECT_PRO_ID = "ProjectId";
	public static String KEY_PROJECT_PRO_TYPE = "ProjectType";
	public static String KEY_PROJECT_PRO_AMOUNT = "ProjectAmount";
	public static String KEY_PROJECT_FORMATTED_INSTALL_DATE = "FormattedInstallDate";
	public static String KEY_PROJECT_CANCEL_REASON = "CancelReason";
	public static String KEY_PROJECT_FOREMAN = "Foreman";
	public static String KEY_PROJECT_BALANCE_DUE = "BalanceDue";
	public static String KEY_PROJECT_INSTALL_DAYS = "InstallDays";
	public static String KEY_PROJECT_SALESNAME = "Salesman";
	public static String KEY_PROJECT_START_DATE = "StartDate";
	public static String KEY_PROJECT_END_DATE = "EndDate";

	// key - Customer Phone number variables
	public static String JSON_KEY_PHONE = "Phone";
	public static String JSON_KEY_TYPENAME = "TypeName";
	public static String JSON_KEY_PHONENUMBER_ID = "PhoneNumberId";
	public static String JSON_KEY_CUSTOMER_PHONE = "CP";
	public static String JSON_KEY_LASTNAME = "LastName";
	public static String JSON_KEY_NOTES = "Notes";

	// Key - Sequal Json Variables

	public static String JSON_KEY_EMAIL_ID = "EmailId";
	public static String JSON_KEY_SEQUAL_EMAIL = "SequelEmail";
	public static String JSON_KEY_VIDEO_URL = "VideoURL";
	public static String JSON_KEY_IMAGE_URL = "ImageURL";
	public static String JSON_KEY_DATE_SENT = "DateSent";
	public static String JSON_KEY_DATE_TO_SEND = "DateToSend";
	public static String JSON_KEY_EMAILSCHEDULER = "BizwizEmailScheduler";

	// key - Followups variables
	public static String GET_CUSTOMER_FOLLOWUPS_URL = BASE_URL_SALES
			+ "sa_customer_followups.aspx";
	public static String JSON_KEY_CUSTOMER_FOLLOWUPS = "CF";
	public static String JSON_KEY_FOLLOWUP_ID = "FollowUpId";
	public static String JSON_KEY_FOLLOWUP_EMPLOYEE = "FollowupEmployee";
	public static String JSON_KEY_FOLLOWUP_DATE = "FollowUpDate";
	public static String JSON_KEY_FOLLOWUP_TIME = "FollowupTime";
	public static String JSON_KEY_FOLLOWUP_NOTES = "Notes";
	public static String JSON_KEY_FOLLOWUP_ISCOMPLETED = "IsCompleted";
	public static String JSON_KEY_FOLLOWUP_RESOLVED_NOTE = "ResolvedNotes";
	public static String JSON_KEY_FOLLOWUP_COMPLETED_DATE = "FollowUpCompletedDate";
	public static String JSON_KEY_FOLLOWUP_COMPLETED_TIME = "FollowupCompletedTime";
	public static String COMPLETED = "COMPLETED";
	public static String COMPLETION_NOTE = "Completion notes";
	public static String FOLLOWUP_NOTE = "Follow up notes";
	public static Boolean IsSaving = false;
	// key - Notes Types variables

	public static String NOTES_TYPE_RESPONSE_KEY = "NT";
	public static String NOTES_TYPE_ID = "NoteTypeId";
	public static String NOTES_TYPE = "NoteType";
	public static String NOTES_TABlE_ID = "TableId";

	public static String NOTES_TABlE_ROW_ID = "TableRowId";

	public static String NOTES = "Notes";

	// key - Notes save variables
	public static String SAVE_NOTES_RESPONSE_KEY = "SN";

	public static String SAVE_NOTES_STATUS = "Status";

	public static String SAVE_NOTES_SUCCESS = "SUCCESS";

	public static String SAVE_NOTES_FAILURE = "FAILURE";

	public static String VIEW_CUSTOMER_NOTE_URL = BASE_URL
			+ "view_customer_notes.aspx";
	public static String KEY_VIEW_NOTE = "SC";
	public static String jSON_KEY_ADJUSTED_CREATED_DATE = "AdjustedDateCreated";
	public static String JSON_KEY_NOTE = "Note";

	// KEY - EVENT CONFIGURATION

	public static String EVENT_CONFIGUATION_KEY = "EC";

	public static String DEFAULT_APPNT_DURATION = "DefaultAppointmentDuration";

	public static String APPNT_TYPE = "AppointmentType";

	public static String CUSTOMER_VISIT_TYPE = "CustomerVisitType";

	public static String EVENT_NOTES_TYPE = "NotesType";
	public static String EC_APPNT_TPYE_ID = "TypeId";
	public static String EC_APPNT_TPYE_NAME = "TypeName";

	// key - create appnt variables

	public static String EVENT_TYPE = "EventType";
	public static String EVENT_EMPLOYEE_ID = "EventEmployeeId";

	public static String EVENT_DATE = "EventDate";
	public static String EVENT_START_TIME = "StartTime";
	public static String EVENT_END_TIME = "EndTime";
	public static String EVENT_NOTES = "Notes";

	public static String EVENT_ID = "EventId";
	public static String LEAD_TYPE_ID = "LeadtypeId";

	// KEY - DISPO QUESTIONNAIRE VARIABLES

	public static String KEY_DISPO_QUESTIONNAIRE = "ARQ";
	public static String KEY_DISPO_QUESTION_ID = "QuestionId";
	public static String KEY_DISPO_QUESTION = "Question";
	public static String kEY_DISPO_QUESTION_TYPE = "QuestionType";
	public static String KEY_DISPO_CHOICE_OPTIONS = "ChoiceOptions";
	public static String KEY_DISPO_RESPONSE_ID = "ResponseId";
	public static String KEY_DISPO_RESPONSE_TEXT = "ResponseText";
	public static String KEY_DISPO_RESPONSE_CHOICE_ID = "ResponseChoiceId";
	public static int ResponseChoiceId;
	public static String KEY_DISPO_CHOICE_ID = "Id";
	public static String KEY_DISPO_CHOICE_TEXT = "ChoiceText";
	public static String KEY_DISPO_ORDINAL = "Ordinal";

	// KEY - SAVE DISPO

	public static String KEY_SAVE_DISPO = "SR";

	// KEY - CREATE EVENT VARIABLES

	public static String CREATE_EVENT_KEY = "CE";

	// key - settings shared preference

	public static String PREF_SWITCH_APPNT_CHECKED = "PREF_SWITCH_APPNT_CHECKED";
	public static String PREF_SWITCH_FOLLOW_CHECKED = "PREF_SWITCH_FOLLOW_CHECKED";
	public static String PREF_CHECKBOX_APPNT_CHECKED = "PREF_CHECKBOX_APPNT_CHECKED";
	public static String PREF_CHECKBOX_FOLLOW_CHECKED = "PREF_CHECKBOX_FOLLOW_CHECKED";
	public static String PREF_TEXT_APPNT_NOTIFY = "PREF_TEXT_APPNT_NOTIFY";
	public static String PREF_TEXT_FOLLOW_NOTIFY = "PREF_TEXT_FOLLOW_NOTIFY";
	public static String PREF_SWITCH_SHOW_PRICE = "PREF_SWITCH_SHOW_PRICE";
	public static String PREF_RECENT_PRODUCT_KEY = "Recent Products";
	public static String PREF_AUTO_SYNC = "PREF_AUTO_SYNC";
	
	// CREATE FOLLOW UP VARIABLES
	public static String CREATE_FOLLOWUPS_URL = BASE_URL
			+ "create_new_followup.aspx";
	public static String NEW_FOLLOWUP = "NF";
	public static String JSON_KEY_STATUS = "Status";
	public static String GET_DEALER_EMPLOYEDD_URL = BASE_URL
			+ "dealer_employees.aspx?DealerId=";
	public static String DEALER_EMPLOYEE = "DE";
	public static String JSON_KEY_SECTION_ID = "SectionId";
	public static String SECTION_ID = "58";
	public static String TOAST_NO_FOLLOWUP_NOTES = "Please enter the notes.";
	public static String TOAST_NO_FOLLOWUP_DATE = "Please select the date.";
	public static String TOAST_NO_FOLLOWUP_TIME = "Please select the time.";
	public static String TOAST_NO_FOLLOWUP_EMP = "No followup emp";

	public static String SAVE_PHONE_NUMBER_URL = BASE_URL
			+ "save_phone_number.aspx";
	public static String JSON_KEY_SAVE_PHONENUMBER = "SP";
	public static String JSON_KEY_PHONETYPE_ID = "PhoneTypeId";

	public static String GET_PHONETYPE_URL = BASE_URL
			+ "phone_types.aspx?DealerId=";

	public static String RESOLVE_FOLLOWUP_URL = BASE_URL
			+ "resolve_followup.aspx";

	public static String RESOLVE_FOLLOEUP = "RF";

	// KEY - Appointment Questionnaire
	public static String APPO_QUESTIONNAIRES_URL = BASE_URL_SALES
			+ "appt_questionnaire.aspx";
	public static String APPOINTMENT_QUESTIONNAIRE = "Appointment Questionnaire";
	public static String QUESTION_ID = "QuestionId";
	public static String QUESTION = "Question";
	public static String QUESTION_TYPE = "QuestionType";
	public static String CHOICE_OPTION = "ChoiceOptions";
	public static String RESPONSE_ID = "ResponseId";
	public static String RESPONSE_TEXT = "ResponseText";
	public static String RESPONSE_CHOICE_ID = "ResponseChoiceId";
	public static String ID = "Id";
	public static String CHOICE_TEXT = "ChoiceText";

	public static String QUESTIONNAIRE_CONFIG = BASE_URL_SALES
			+ "questionnaire_config.aspx";

	public static String KEY_LEADTYPE_ID = "LeadTypeId";
	public static String KEY_APPOINTMENT_TYPE_ID = "AppointmentTypeId";
	public static String KEY_EVENT_ID = "EventId";
	public static String KEY_APPT_RESULT_ID = "AppointmentResultId";

	public static String TYPENAME = "TypeName";

	public static String APPOINTMENT_TYPE = "APPOINTMENTTYPE";

	public static String LEAD_TYPE = "LEADTYPE";

	public static String QC = "QC";
	public static String SR = "SR";

	// Key - lead questionaire

	public static String LEAD_QUESTIONNAIRES_URL = BASE_URL_SALES
			+ "lead_questionnaire.aspx";
	public static String LEAD_QUESTIONNAIRES = "LQ";
	public static String QUESTIONS = "Question";
	public static String QUESTION_RESPONSE = "QuestionResponse";
	public static String ORDINAL = "Ordinal";
	public static String CUSTOMER_QUESTIONARE = "Lead Details";

	// Save Call State
	public static String SAVE_CALL_DEALERID = "DealerId";
	public static String SAVE_CALL_EMPLOYEEID = "EmployeeId";
	public static String SAVE_CALL_CUSTOMERID = "CustomerId";
	public static String SAVE_CALL_STARTDATETIME = "StartDateTime";
	public static String SAVE_CALL_ENDDATETIME = "EndDateTime";
	public static String SAVE_CALL_STATUS = "SaveCallHistory";
	// Save Call Url
	public static String SAVE_PHONE_STATE = BASE_URL
			+ "salesapp/callhistory_app_save.aspx";

	// Save Email
	public static String SEND_EMAIL_URL = BASE_URL
			+ "salesapp/customer_email_app_view.aspx";

	public static String SAVE_EMAIL_CUSTOMER_EMAIL = "CustomerEmail";
	public static String SAVE_EMAIL_EMAIL = "Email";
	public static String SAVE_EMAIL_STATUS = "SaveEmailHistory";
	public static String SAVE_EMAIL_SENT_DATE = "EmailSentDate";
	public static String SAVE_EMAIL_EMAIL_SUBJECT = "EmailSubject";
	public static String SAVE_EMAIL_EMAIL_BODY = "EmailBody";

	public static String SAVE_EMAIL_STATE = BASE_URL
			+ "salesapp/email_history_app_save.aspx";

	// Key - SpProductCategory Json Variables
	public static String JSON_KEY_SALES_PROCESS_CATEGORY = "EC";
	public static String JSON_KEY_SALES_PROCESS_CATEGORY_NAME = "CategoryName";
	public static String JSON_KEY_SALES_PROCESS_CATEGORY_ID = "CategoryId";
	public static String JSON_KEY_SALES_PROCESS_CATEGORY_IMAGE_URL = "CatgeoryImageURL";
	public static String JSON_KEY_SALES_PROCESS_SUB_CATEGORY = "SubCategory";
	public static String JSON_KEY_SALES_PROCESS_SUB_CATEGORY_NAME = "SubcategoryName";
	public static String JSON_KEY_SALES_PROCESS_SUB_CATEGORY_ID = "SubcategoryId";
	public static String JSON_KEY_SALES_PROCESS_MATERIAL = "Material";
	public static String JSON_KEY_SALES_PROCESS_MATERIAL_NAME = "MaterialName";
	public static String JSON_KEY_SALES_PROCESS_MATERIAL_ID = "MaterialId";
	public static String JSON_KEY_SALES_PROCESS_PRODUCT_DESCRIPTION = "ProductDescription";
	public static String JSON_KEY_SALES_PROCESS_PRODUCT_IMAGE_URL = "ProductImageURL";
	public static String JSON_KEY_SALES_PROCESS_PRODUCT_VIDEO_URL = "ProductVideoURL";
	public static String JSON_KEY_SALES_PROCESS_UNIT_SELLING_PRICE = "UnitSellingPrice";
	public static String JSON_KEY_SALES_PROCESS_PRODUCT_TYPE = "Type";

	// Key Send PushNotification message
	public static String SEND_PUSH_NOTIFICATION_URL = BASE_URL
			+ "save_device.aspx";

	public static String SEND_PUSH_NOTIFICATION_DELEARID = "DealerId";
	public static String SEND_PUSH_NOTIFICATION_EMPLOYEEID = "EmployeeId";
	public static String SEND_PUSH_NOTIFICATION_DEVICEID = "DeviceId";
	public static String SEND_PUSH_NOTIFICATION_DEVICETYPE = "DeviceType";

	public static String KEY_PUSH_NOTIFICATION_RESPONSE = "SD";

	// Save Settings with enable notification
	public static String ENABLE_PUSH_NOTIFICATION_SETTINGS = BASE_URL
			+ "save_notification_setting.aspx";

	public static String ENABLE_PUSH_NOTIFICATION_DELEARID = "DealerId";
	public static String ENABLE_PUSH_NOTIFICATION_EMPLOYEEID = "EmployeeId";
	public static String ENABLE_PUSH_NOTIFICATION_NOTIFICATION_TYPE = "NotificationType";
	public static String ENABLE_PUSH_NOTIFICATION_NOTIFICATION_DAYS = "NotificationDays";
	public static String ENABLE_PUSH_NOTIFICATION_NOTIFICATION_HOURS = "NotificationHours";
	public static String ENABLE_PUSH_NOTIFICATION_NOTIFICATION_MINUTE = "NotificationMinutes";
	public static String ENABLE_PUSH_NOTIFICATION_NOTIFICATION_STATUS = "Status";

	public static String KEY_ENABLE_PUSH_NOTIFICATION_RESPONSE = "EN";

	// Save location
	public static String SAVE_LOCATION_URL = BASE_URL_SALES
			+ "gps_coordinate_app_save.aspx";
	public static String LATITUDE = "latitude";
	public static String LONGITUDE = "longitude";
	public static String SAVE_GPS_COORDINATES = "SaveGPSCoordinate";

	// Smart search
	public static String SMART_SEARCH_URL = BASE_URL + "smart_search.aspx";
	public static String SEARCH_TEXT = "SearchText";
	public static String SS = "SS";
	public static String SMART_SEARCH = "Smart Search";

	public static String URL_LOAD_DISPO_SUB_DISPO = BASE_URL_SALES
			+ "load_dispo_subdispo.aspx?";

	// key - Dispo Sub Disp variables

	public static String DISPO_KEY_SALES = "SALES";
	public static String DISPO_KEY_SERVICE = "SERVICE";
	public static String DISPO_KEY_ID = "Id";

	public static String DISPO_KEY_DISPO_NAME = "DispoName";
	public static String DISPO_KEY_SUB_DISPO_NAME = "SubDispoName";
	public static String DISPO_KEY_SUB_DISPO_OPTIONS = "SubDispoOptions";

	// Save summary
	public static String SAVE_SUMMARY_URL = BASE_URL_SALES
			+ "save_product.aspx";
	public static String QUOTED_QUANTITY = "QuotedQuantity";
	public static String QUOTED_AMOUNT = "QuotedAmount";
	public static String BOUND_PRODUCT_ID = "BoundProductId";
	public static String SAVE_PROPOSAL = "SP";
	public static Boolean isCheckOut = false;
	public static String DESCRIPTION = "Description";
	public static String DEALER_SETTING_URL = BASE_URL + "dealer_settings.aspx";
	public static String DEALER_SETTING = "DS";
	public static Uri signPath;
	public static String salesAppContractText = "SalesAppContractText";
	public static String step2Label = "Step2Label";
	public static String termString = "";
	public static String setp2labelString = "";

	// Proposal items

	public static String GenerateProposalUrl = BASE_URL_SALES
			+ "generate_proposal.aspx";
	public static String PROPOSAL = "PROPOSAL";
	public static String disposition = "Disposition";
	public static String subDispo = "SubDispo";
	public static String subDispoId = "SubDispoId";
	public static String ProposalAmount = "ProposalAmount";
	public static String WorkOrderNotes = "WorkOrderNotes";
	public static String QuotedProducts = "QuotedProucts";
	public static String ProductQuoted = "ProductQuoted";
	public static String BoundproductId = "BoundproductId";
	public static String Quantity = "Quantity";
	public static String Discounts = "Discounts";
	public static String DiscountId = "DiscountId";
	public static String DiscountText = "DiscountText";
	public static String DiscountAmount = "DiscountAmount";
	public static Boolean isProposalList = true;
	public static Boolean isScrolling = false;
	public static Boolean isSavedPayment = false;
	public static String BoundProductNotes = "BoundProductNotes";
	public static String ProductImageURL = "ProductImageURL";
	public static String MaterialId = "MaterialId";
	public static String Type = "Type";
	public static String SignatureExists = "SignatureExists";
	public static String SignatureURL = "SignatureURL";
	public static Boolean isSelectProduct = false;
	public static Boolean isProposal = false;
	public static String EmailProposal = "EP";

	// KEY - APPOINTMENT RESULT VARIABLES
	public static String KEY_APPNT_RESULT = "AR";

	public static String PAYMENT_METHODS_CONFIG_URL = BASE_URL
			+ "payment_configuration.aspx?DealerId=";
	public static String AUTHORIZATION = "Authorization";
	public static String PAYMENT_METHODS_KEY = "Basic a2JoOmIxencxek1vYjFsZUFwcHM=";
	public static String PM = "PM";
	public static String MODULESTATUS = "ModuleStatus";
	public static String DEALERACCOUNTNUMBER = "DealerAccountNumber";
	public static String DEALERKEY = "DealerKey";
	public static String METHODS = "Methods";
	public static String PAYMENTMETHOD = "PaymentMethod";
	public static String PAYMENT_SUCCESS = "SUCCESS";
	

	// Key - Financing Company Json Variables
	public static String JSON_KEY_FC = "FC";
	public static String JSON_KEY_FINANCING_COMPANY_NAME = "FinancingCompanyName";

	public static String SAVE_PAYMENT = BASE_URL
			+ "save_payment.aspx?DealerId=";

	// Toast messages
	public static int TOASTMSG_TIME = 1000;
	public static String TOAST_INTERNET = "Internet is not available. Please check your internet connection";
	public static String TOAST_CONNECTION_ERROR = "It seems to be communication failure. Please try again later.";
	public static String TOAST_NO_DATA = "No data available ";

	// Toast messages - Add Prospect and Add Professional Contact Screen
	public static String TOAST_COMPANY_NAME = "Please enter the Company Name";
	public static String TOAST_COMPANY_TYPE = "Please enter the Company Type";
	public static String TOAST_FIRST_NAME = "Please enter the First Name";
	public static String TOAST_LAST_NAME = "Please enter the Last Name";
	public static String TOAST_PHONE_NUMBER = "Please enter the Phone Number";
	public static String TOAST_EMAIL = "Please enter the Email";
	public static String TOAST_COMPANY_STREET_ADDRESS = "Please enter the Company Street Address";
	public static String PROSPECT_TOAST_COMPANY_STREET_ADDRESS = "Please enter the Street Address";
	public static String TOAST_ZIP = "Please enter the Zip";
	public static String TOAST_CITY = "Please enter the City";
	public static String TOAST_STATE = "Please enter the State";
	public static String TOAST_ADDED_PROSPECT = "Prospect is added successfully. Your customer id is : ";
	public static String TOAST_ADDED_PROFESSIONAL_CONTACT = "Professional contact is added successfully. Your customer id is : ";
	public static String TOAST_VALID_ZIPCODE = "Please enter the valid USA / Canada zipcode";
	public static String ENTER_PAYMENT = "Please Enter the Amount";
	public static String ENTER_CHECK_NUMBER = "Please Enter the Check Number";
	public static String ENTER_AUTHORIZATION_NUMBER = "Please Enter the Authorization Number";
	public static String TOAST_SECOND_NAME = "Please enter the Secondary Name";

	public static String DEALERID = "DealerId";
	public static String APPOINTMENTID = "AppointmentId";
	public static String EMPOLYEEID = "EmployeeId";
	public static String CUSTOMERID = "CustomerId";
	public static String AMOUNT = "Amount";
	public static String PAYMENTTYPEID = "PaymentTypeId";
	public static String PAYMENTMETHODID = "PaymentMethodId";
	public static String CREDITTYPEID = "CreditTypeId";
	public static String CREDITBATCHNUMBER = "CreditBatchNumber";
	public static String CHECKNUMBER = "CheckNumber";
	public static String DATE = "Date";
	public static String APPOINTMENT_RESULT_ID = "AppointmentResultId";
	public static String FINANCING_COMPANY_ID = "FinancingCompanyId";
	public static String FINANCING_APPROVAL_NUMBER = "FinancingApprovalNumber";

	public static String SAVE_PAYMENT_INFORMATION = BASE_URL_SALES
			+ "save_payment_info.aspx";
	public static String STATUS = "SUCCESS";

	public static String NOTE = "NOTE";
	public static String VISIT = "VISIT";
	public static String APPNT = "Appointment";

	// EditProspect
	public static String EDIT_PROSPECT_CONFIGUATION_KEY = "CD";

	public static String EDIT_PROSPECT_CONTACT1 = "Contact1";
	public static String EDIT_PROSPECT_CONTACT2 = "Contact2";
	public static String EDIT_PROSPECT_LASTNAME = "LastName";
	public static String EDIT_PROSPECT_COMPANYNAME = "CompanyName";
	public static String EDIT_PROSPECT_COMPANYCONTACT = "CompanyContact";
	public static String EDIT_PROSPECT_EMAIL = "Email";
	public static String EDIT_PROSPECT_PHYSICALADDRESS = "PhysicalAddress";
	public static String EDIT_PROSPECT_PHYSICALZIP = "PhysicalZip";
	public static String EDIT_PROSPECT_PHYSICALCITY = "PhysicalCity";
	public static String EDIT_PROSPECT_PHYSICALSTATE = "PhysicalState";
	public static String EDIT_PROSPECT_TYPEID = "TypeId";
	public static String EDIT_PROSPECT_SUPTYPEID = "SubTypeId";
	public static String EDIT_PROSPECT_PHONENUMBER = "PhoneNumbers";
	public static String EDIT_PROSPECT_CUSTOMERID = "CustomerId";
	public static String EDIT_PROSPECT_TYPENAME = "TypeName";
	public static String EDIT_PROSPECT_PHONENUMBERID = "PhoneNumberId";
	public static String EDIT_PROSPECT_PHONE = "Phone";
	public static String EDIT_PROSPECT_PHONETYPEID = "PhoneTypeId";

	// Update Prospect
	public static String SAVE_CONTACT = BASE_URL_SALES + "edit_customer.aspx";
	public static String TOAST_ADDED_UPDATE = "Sucessfully Updated";
	public static String EDIT_PROSPECT_CUSTOMERDATA = "CustomerData";
	public static String EDIT_PROSPECT_DELEARID = "DealerId";
	public static String EDIT_PROSPECT_EMPLOYEEID = "EmployeeId";
	public static String EDIT_PROSPECT_TYPE = "Type";
	public static String EDIT_PROSPECT_FIRST = "FirstName";
	public static String EDIT_PROSPECT_SECONDARY_CONTACT = "SecondaryContact";
	public static String EDIT_PROSPECT_STREET = "Street";
	public static String EDIT_PROSPECT_ZIP = "Zip";
	public static String EDIT_PROSPECT_CITY = "City";
	public static String EDIT_PROSPECT_STATE = "State";
	public static String EDIT_PROSPECT_PHONENUMBERS = "PhoneNumber";
	public static String EDIT_PROSPECT_CUSTOMERPHONENUMBERS = "CustomerPhoneNumber";

	// Share Preference Name
	public static final String SHARED_PREFERENCE_NAME = "bizwiz_sales_new";

	// Main Menu listview array list
	public static String[] mainMenuList = new String[] { "Support", "About",
		"Settings", "Sync" , "Logout"};

	public static Integer[] menu_icons = { R.drawable.ic_home_drawer_suppor,
		R.drawable.ic_home_drawer_info, R.drawable.ic_home_drawer_settings,R.drawable.ic_home_drawer_sync,
		R.drawable.ic_home_drawer_logout };

	// Get Base64 Encoded String
	private static String getEncodingAuthenticationKey() {
		// TODO Auto-generated method stub
		return "Basic "
		+ Base64.encodeToString(VALUE_AUTHENTICATION.getBytes(),
				Base64.NO_WRAP);
	}

	// Lokesh
	public static String KEY_CAL_CALENDAR_NOTES = "CALENDAR-NOTES";
	public static String KEY_CAL_NOTES = "NOTES";
	public static String KEY_CAL_FOLLOW_UPS = "FOLLOW-UP";

	public static String getConnectivityStatusString(Context context) {
		int conn = Constants.getConnectivityStatus(context);
		String status = null;
		if (conn == Constants.TYPE_WIFI) {
			status = NETWORK_CONNECTION_WIFI;
		} else if (conn == Constants.TYPE_MOBILE) {
			status = NETWORK_CONNECTION_MOBILEDATA;
		} else if (conn == Constants.TYPE_NOT_CONNECTED) {
			status = NETWORK_CONNECTION_NOCONNECTION;
		}
		return status;
	}

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

	// Email Pattern
	// public static String EMAIL_PATTERN =
	// "[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+\\.+[a-zA-Z0-9_-]+";

	//	public static boolean isEditTextContainEmail(String argEditText) {
	//
	//		try {
	//			Pattern pattern = Pattern
	//					.compile("^[_A-Za-z0-9._%+-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9.-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	//			Matcher matcher = pattern.matcher(argEditText);
	//			return matcher.matches();
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			return false;
	//		}
	//	}

	// Newly Added by Ramesh
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} else
			return true;
	}

	public static ArrayList<String> getStates() {
		ArrayList<String> statesArray = new ArrayList<String>();
		statesArray.add("AL - Alabama");
		statesArray.add("AK - Alaska");
		statesArray.add("AB - Alberta");
		statesArray.add("AZ - Arizona");
		statesArray.add("AR - Arkansas");
		statesArray.add("BC - British Columbia");
		statesArray.add("CA - California");
		statesArray.add("CO - Colorado");
		statesArray.add("CT - Connecticut");
		statesArray.add("DE - Delaware");
		statesArray.add("DC - District Of Columbia");
		statesArray.add("FL - Florida");
		statesArray.add("GA - Georgia");
		statesArray.add("GU - Guam");
		statesArray.add("HI - Hawaii");
		statesArray.add("ID - Idaho");
		statesArray.add("IL - Illinois");
		statesArray.add("IN - Indiana");
		statesArray.add("IA - Iowa");
		statesArray.add("KS - Kansas");
		statesArray.add("KY - Kentucky");
		statesArray.add("LA - Louisiana");
		statesArray.add("ME - Maine");
		statesArray.add("MB - Manitoba");
		statesArray.add("MD - Maryland");
		statesArray.add("MA - Massachusetts");
		statesArray.add("MI - Michigan");
		statesArray.add("MN - Minnesota");
		statesArray.add("MS - Mississippi");
		statesArray.add("MO - Missouri");
		statesArray.add("MT - Montana");
		statesArray.add("NE - Nebraska");
		statesArray.add("NV - Nevada");
		statesArray.add("NB - New Brunswick");
		statesArray.add("NH - New Hampshire");
		statesArray.add("NJ - New Jersey");
		statesArray.add("NM - New Mexico");
		statesArray.add("NY - New York");
		statesArray.add("NF - Newfoundland");
		statesArray.add("NC - North Carolina");
		statesArray.add("ND - North Dakota");
		statesArray.add("NT - Northwest Territories");
		statesArray.add("NS - Nova Scotia");
		statesArray.add("NU - Nunavut");
		statesArray.add("OH - Ohio");
		statesArray.add("OK - Oklahoma");
		statesArray.add("ON - Ontario");
		statesArray.add("OR - Oregon");
		statesArray.add("PA - Pennsylvania");
		statesArray.add("PE - Prince Edward Island");
		statesArray.add("PR - Puerto Rico");
		statesArray.add("QC - Quebec");
		statesArray.add("RI - Rhode Island");
		statesArray.add("SK - Saskatchewan");
		statesArray.add("SC - South Carolina");
		statesArray.add("SD - South Dakota");
		statesArray.add("TN - Tennessee");
		statesArray.add("TX - Texas");
		statesArray.add("UT - Utah");
		statesArray.add("VT - Vermont");
		statesArray.add("VI - Virgin Islands");
		statesArray.add("VA - Virginia");
		statesArray.add("WA - Washington");
		statesArray.add("WV - West Virginia");
		statesArray.add("WI - Wisconsin");
		statesArray.add("WY - Wyoming");
		statesArray.add("YT - Yukon Territory");
		return statesArray;
	}

	public static void initBugSense(LoginActivity loginActivity) {
		BugSenseHandler.initAndStartSession(loginActivity, "d891a48a");
	}

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}
	/**
	 * CALENDAR TABLE COLUMNS **
	 */
	public static String COL_EVENT_TYPE = "EVENT_TYPE";
	public static String COL_EVENT_NOTES = "EVENT_NOTES";
	public static String COL_CUSTOMER_ID = "CUSTOMER_ID";
	public static String COL_CUSTOMER_NAME = "CUSTOMER_NAME";
	public static String COL_ADDRESS = "ADDRESS";
	public static String COL_CITY = "CITY";
	public static String COL_STATE = "STATE";
	public static String COL_ZIP = "ZIP";
	public static String COL_FORMATTED_APPT_DATE = "FORMATTED_APPT_DATE";
	public static String COL_APPT_TIME = "APPT_TIME";
	public static String COL_LEAD_OR_VISIT_TYPE = "LEAD_OR_VISIT_TYPE";
	public static String COL_GROUP_CATEGORY = "GROUP_CATEGORY";
	public static String COL_EVENT_ID = "EVENT_ID";
	
	/****
	 * Offline Calendar Entry Table Columns
	 *  TEXT , TEXT, TEXT, TEXT, TEXT " +
				", TEXT , TEXT , TEXT , TEXT , TEXT , TEXT ," +
				" TEXT
	 */
	public static String COL_DEALEAR_ID="DEALEAR_ID";
	public static String COL_EMPLOYEE_ID="EMPLOYEE_ID";
	public static String COL_EVENT_EMPLOYEE_ID="EVENT_EMPLOYEE_ID";
	public static String COL_TYPE_ID="TYPE_ID";
	public static String COL_EVENT_DATE="EVENT_DATE";
	public static String COL_NOTES="NOTES";
	public static String COL_START_TIME="START_TIME";
	public static String COL_END_TIME="END_TIME";
	public static String COL_LEAD_TYPE_ID="LEAD_TYPE_ID";
	
	/**
	 * Lead type TABLE COLUMNS **
	 */
	public static String COL_LEADTYPE_ID = "Id";
	public static String COL_LEADTYPE_NAME = "TypeName";
	
	// Settings Auto sync
	public static ArrayList<String> getSyncTimes(){
		  ArrayList<String>arrTime=new ArrayList<String>();
		  arrTime.add("Never");
		  arrTime.add("30 Minutes");
		  arrTime.add("1 Hour");
		  arrTime.add("2 Hours");
		  arrTime.add("5 Hours");
		  arrTime.add("10 Hours");
		  arrTime.add("1 Day");
		  return arrTime;
		 }

		 public static int getSycTimesInMilliseconds(int position){
		  int Time=0;
		  switch (position) {
		  case 0:
		   //Never
		   Time=0;
		   break;
		  case 1:
		   //30 Minutes
		   Time=1800000;
		   break;

		  case 2:
		   //1 Hour
		   Time=3600000;
		   break;

		  case 3:
		   //2 Hours
		   Time=7200000;
		   break;

		  case 4:
		   //5 Hours
		   Time=18000000;
		   break;

		  case 5:
		   //10 Hours
		   Time=36000000;
		   break;

		  case 6:
		   //1 Day
		   Time=86400000;
		   break;
		  }
		  return Time;

		 }
}