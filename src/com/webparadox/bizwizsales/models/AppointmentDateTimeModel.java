package com.webparadox.bizwizsales.models;

import android.util.Log;

public class AppointmentDateTimeModel {

	String ApptTime;
	String FormattedApptDate;
	String AppntId;

	public String getAppntId() {
		return AppntId;
	}

	public void setAppntId(String appntId) {
		Log.d("AppnId", appntId);
		AppntId = appntId;
	}

	public String getApptTime() {
		return ApptTime;
	}

	public void setApptTime(String apptTime) {
		ApptTime = apptTime;
	}

	public String getFormattedApptDate() {
		Log.d("FormattedApptDate", FormattedApptDate);
		return FormattedApptDate;
	}

	public void setFormattedApptDate(String formattedApptDate) {
		FormattedApptDate = formattedApptDate;
	}

}
