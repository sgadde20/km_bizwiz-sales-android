package com.webparadox.bizwizsales.datacontroller;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

import com.webparadox.bizwizsales.models.AppointmentDateTimeModel;
import com.webparadox.bizwizsales.models.AppointmentQuestionnaireModel;
import com.webparadox.bizwizsales.models.AppointmentQuestionnaireQuestionOptionModel;
import com.webparadox.bizwizsales.models.AppointmentRadioButtonModel;
import com.webparadox.bizwizsales.models.AppointmentTypeModel;
import com.webparadox.bizwizsales.models.CalendarListPaginationModel;
import com.webparadox.bizwizsales.models.CustomerAttachmentModel;
import com.webparadox.bizwizsales.models.CustomerDetailsAppointmentsModel;
import com.webparadox.bizwizsales.models.CustomerDetailsProjectModel;
import com.webparadox.bizwizsales.models.CustomerFollowUpsModel;
import com.webparadox.bizwizsales.models.CustomerNotesModel;
import com.webparadox.bizwizsales.models.DiscountsModel;
import com.webparadox.bizwizsales.models.DispoChoiceOptions;
import com.webparadox.bizwizsales.models.DispoModel;
import com.webparadox.bizwizsales.models.DispoQuestionnaireModel;
import com.webparadox.bizwizsales.models.EventConfigurationAppntTypeModel;
import com.webparadox.bizwizsales.models.EventConfigurationVisitTypeModel;
import com.webparadox.bizwizsales.models.GetEmployeesModel;
import com.webparadox.bizwizsales.models.HomeScreenModel;
import com.webparadox.bizwizsales.models.LeadQuestionaireModel;
import com.webparadox.bizwizsales.models.LeadTypeModel;
import com.webparadox.bizwizsales.models.MyHotQuotesModel;
import com.webparadox.bizwizsales.models.NotesModel;
import com.webparadox.bizwizsales.models.NotesTypeModel;
import com.webparadox.bizwizsales.models.ProposalCartModel;
import com.webparadox.bizwizsales.models.ProposalListModel;
import com.webparadox.bizwizsales.models.QuotedProuctsModel;
import com.webparadox.bizwizsales.models.SequalEmailVideosModel;
import com.webparadox.bizwizsales.models.SmartSearchModel;
import com.webparadox.bizwizsales.models.SpProductCategoryModel;
import com.webparadox.bizwizsales.models.SpProductSubCatAndMaterialModel;
import com.webparadox.bizwizsales.models.SubDispoModel;

public class Singleton {
	private static Singleton instance = null;

	public ArrayList<GetEmployeesModel> mEmployeeNames = new ArrayList<GetEmployeesModel>();
	// List - Home Screen
	public ArrayList<HomeScreenModel> homeList = new ArrayList<HomeScreenModel>();
	public String mtdSalesValue, ytdSalesValue, adlSalesValue;
	public Bitmap mOwnerPhoto;
	public ArrayList<CustomerAttachmentModel> cusAttachmentModel = new ArrayList<CustomerAttachmentModel>();
	// Followups
	public ArrayList<CustomerFollowUpsModel> cusFollowupModelList = new ArrayList<CustomerFollowUpsModel>();
	// Notes
	public ArrayList<CustomerNotesModel> cusNotesList = new ArrayList<CustomerNotesModel>();
	public ArrayList<CalendarListPaginationModel> selectedDayApptsData = new ArrayList<CalendarListPaginationModel>();
	// ADD APPNT
	public ArrayList<EventConfigurationAppntTypeModel> eventConfigAppntType = new ArrayList<EventConfigurationAppntTypeModel>();
	public ArrayList<EventConfigurationVisitTypeModel> eventConfigVisiType = new ArrayList<EventConfigurationVisitTypeModel>();
	// List - Calendar Screen
	public HashMap<String, ArrayList<CalendarListPaginationModel>> mcalendarMonthData = new HashMap<String, ArrayList<CalendarListPaginationModel>>();
	public ArrayList<String> mCalendarAppointmentsData = new ArrayList<String>();
	public ArrayList<String> mCalendarFollowUpsData = new ArrayList<String>();
	public ArrayList<String> mCalendarNotesData = new ArrayList<String>();
	public HashMap<String, ArrayList<CalendarListPaginationModel>> mCalendarListData = new HashMap<String, ArrayList<CalendarListPaginationModel>>();
	public ArrayList<CalendarListPaginationModel> mCalendarListPaginationData = new ArrayList<CalendarListPaginationModel>();
	public ArrayList<CalendarListPaginationModel> mCalendarMonthViewData = new ArrayList<CalendarListPaginationModel>();
	// List - Add Prospect and Add Professional Contact Screen
	public ArrayList<String> phoneTypeIdArray = new ArrayList<String>();
	public ArrayList<String> phoneTypeArray = new ArrayList<String>();
	public ArrayList<String> companyTypeIdArray = new ArrayList<String>();
	public ArrayList<String> companyTypeArray = new ArrayList<String>();
	public ArrayList<ArrayList<String>> companySubTypeIdArray = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<String>> companySubTypeArray = new ArrayList<ArrayList<String>>();
	public ArrayList<MyHotQuotesModel> hotQuotesList = new ArrayList<MyHotQuotesModel>();
	public ArrayList<MyHotQuotesModel> tempHotQuotesList = new ArrayList<MyHotQuotesModel>();
	// list - Customer Details screen
	public ArrayList<CustomerDetailsAppointmentsModel> appointmentArray = new ArrayList<CustomerDetailsAppointmentsModel>();
	public ArrayList<CustomerDetailsProjectModel> projectsArray = new ArrayList<CustomerDetailsProjectModel>();
	public ArrayList<NotesTypeModel> notesTypesArray = new ArrayList<NotesTypeModel>();
	public ArrayList<NotesModel> noteModel = new ArrayList<NotesModel>();
	public HashMap<String, ArrayList<NotesModel>> mNotesList = new HashMap<String, ArrayList<NotesModel>>();
	public ArrayList<SequalEmailVideosModel> sequalEmailVideosList = new ArrayList<SequalEmailVideosModel>();
	public ArrayList<AppointmentDateTimeModel> appntDateTime = new ArrayList<AppointmentDateTimeModel>();
	public ArrayList<AppointmentTypeModel> appointmentModel = new ArrayList<AppointmentTypeModel>();
	public ArrayList<LeadTypeModel> leadTypeModel = new ArrayList<LeadTypeModel>();
	public ArrayList<LeadQuestionaireModel> leadQuestionaryModel = new ArrayList<LeadQuestionaireModel>();
	public ArrayList<AppointmentQuestionnaireModel> appointmentQuestionnaireModel = new ArrayList<AppointmentQuestionnaireModel>();
	public ArrayList<AppointmentQuestionnaireQuestionOptionModel> apponintQuestionArrayModel = new ArrayList<AppointmentQuestionnaireQuestionOptionModel>();
	public HashMap<String, ArrayList<AppointmentQuestionnaireQuestionOptionModel>> appointmentQuestionOptionModel = new HashMap<String, ArrayList<AppointmentQuestionnaireQuestionOptionModel>>();

	public ArrayList<SpProductCategoryModel> salesProcessProductsList = new ArrayList<SpProductCategoryModel>();
	public ArrayList<SpProductSubCatAndMaterialModel> productsSubCatAndMaterialList = new ArrayList<SpProductSubCatAndMaterialModel>();

	public ArrayList<AppointmentRadioButtonModel> radioArrayModel = new ArrayList<AppointmentRadioButtonModel>();
	public HashMap<String, ArrayList<AppointmentRadioButtonModel>> appointmentQuestionRadioModel = new HashMap<String, ArrayList<AppointmentRadioButtonModel>>();

	public ArrayList<SmartSearchModel> smartSearchModel = new ArrayList<SmartSearchModel>();
	//public ArrayList<DispoModel> DispoArray = new ArrayList<DispoModel>();
	public ArrayList<SubDispoModel> SubDispoArray = new ArrayList<SubDispoModel>();
	
	public ArrayList<DispoModel> salesDispoArray = new ArrayList<DispoModel>();
	public ArrayList<DispoModel> serviceDispoArray = new ArrayList<DispoModel>();

	public ArrayList<DispoQuestionnaireModel> DispoQuestionsArray = new ArrayList<DispoQuestionnaireModel>();

	public ArrayList<DispoChoiceOptions> dispoChoiceArray = new ArrayList<DispoChoiceOptions>();
	public HashMap<String, ArrayList<DispoChoiceOptions>> dispoHashMap = new HashMap<String, ArrayList<DispoChoiceOptions>>();

	public HashMap<String, ArrayList<SubDispoModel>> SubdispoHashMap = new HashMap<String, ArrayList<SubDispoModel>>();

	public ArrayList<ProposalListModel> proposalList = new ArrayList<ProposalListModel>();

	public ArrayList<String> mfinancingCompanyId = new ArrayList<String>();
	public ArrayList<String> mfinancingCompanyName = new ArrayList<String>();

	public ArrayList<String> mChildEmployeeName = new ArrayList<String>();
	public ArrayList<String> mChildEmployeeID = new ArrayList<String>();

	public String startTime, customerId, emailSubject = "", emailBody = "";
	public int lastSelected = 0;
	public int lastSubDispoSelected = 0;
	public String dispoId = "";
	public String SubDispoId = "";
	public String lastNotes = "";
	public String apiKeyValue = "";

	public boolean isShowPrice;
	public ArrayList<ProposalCartModel> proposalCartList = new ArrayList<ProposalCartModel>();
	public ArrayList<QuotedProuctsModel> quotedProductModel = new ArrayList<QuotedProuctsModel>();
	public ArrayList<DiscountsModel> discountModel = new ArrayList<DiscountsModel>();
	
	public String strApptResultId = "";
	public String strAppointmentId = "";
	public String strPaymentMethodId = "";


	private Singleton() {
	}

	public static Singleton getInstance() {
		if (instance == null) {
			synchronized (Singleton.class) {
				// Double check
				if (instance == null) {
					instance = new Singleton();
				}
			}
		}
		return instance;
	}

	public void clearHomeList() {
		homeList.clear();
	}

	public Bitmap getOwnerPhoto() {
		return mOwnerPhoto;
	}

	public void setOwnerPhoto(Bitmap bitmapPhoto) {
		mOwnerPhoto = bitmapPhoto;
	}

	public void clearCalendarListData() {
		mCalendarListData.clear();
	}

	public void clearCalendarMonthData() {
		mcalendarMonthData.clear();
	}

	// Clear List - Add Prospect and Add Professional Contact Screen
	public void clearCompanyPhoneTypes() {
		phoneTypeIdArray.clear();
		phoneTypeArray.clear();
		companyTypeIdArray.clear();
		companyTypeArray.clear();
		companySubTypeIdArray.clear();
		companySubTypeArray.clear();
	}

	// My Hot Quotes list

	public void clearMyHotQuotesList() {
		hotQuotesList.clear();
	}

	// Customer appointments clear list
	public void clearCustomerAppointmentsList() {
		appointmentArray.clear();
	}

	public void clearCustomerProjectList() {
		projectsArray.clear();
	}

	public void clearCusNotesList() {
		cusNotesList.clear();
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public void setApiKeyValue(String apiKeyValue) {
		this.apiKeyValue = apiKeyValue;
	}

	public String getApiKeyValue() {
		return apiKeyValue;
	}

}
