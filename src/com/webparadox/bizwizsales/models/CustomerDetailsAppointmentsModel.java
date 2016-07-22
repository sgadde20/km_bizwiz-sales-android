package com.webparadox.bizwizsales.models;

public class CustomerDetailsAppointmentsModel implements
		Comparable<CustomerDetailsAppointmentsModel> {

	String eventType, appointmentId, appointmentType, SalesRep, LeadType,
			Result, SubResult, DispoId, SubDispositionId, Amount,
			FormattedApptDate, ApptTime,LeadTypeId,AppointmentTypeId,EventId,AppointmentResultId,locksavingDispo,startDateTime;

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public String getLocksavingDispo() {
		return locksavingDispo;
	}

	public void setLocksavingDispo(String locksavingDispo) {
		this.locksavingDispo = locksavingDispo;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}

	public String getSalesRep() {
		return SalesRep;
	}

	public void setSalesRep(String salesRep) {
		SalesRep = salesRep;
	}

	public String getLeadType() {
		return LeadType;
	}

	public void setLeadType(String leadType) {
		LeadType = leadType;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		Result = result;
	}

	public String getSubResult() {
		return SubResult;
	}

	public void setSubResult(String subResult) {
		SubResult = subResult;
	}

	public String getDispoId() {
		return DispoId;
	}

	public void setDispoId(String dispoId) {
		DispoId = dispoId;
	}

	public String getSubDispositionId() {
		return SubDispositionId;
	}

	public void setSubDispositionId(String subDispositionId) {
		SubDispositionId = subDispositionId;
	}

	public String getAmount() {
		return Amount;
	}

	public void setAmount(String amount) {
		Amount = amount;
	}

	public String getFormattedApptDate() {
		return FormattedApptDate;
	}

	public void setFormattedApptDate(String formattedApptDate) {
		FormattedApptDate = formattedApptDate;
	}

	public String getLeadTypeId() {
		return LeadTypeId;
	}

	public void setLeadTypeId(String LeadtypeId) {
		LeadTypeId = LeadtypeId;
	}
	public String getAppointmentTypeId() {
		return AppointmentTypeId;
	}

	public void setAppointmentTypeId(String AppointmenttypeId) {
		AppointmentTypeId = AppointmenttypeId;
	}
	public String getEventId() {
		return EventId;
	}

	public void setEventId(String Eventid) {
		EventId = Eventid;
	}
	public String getAppointmentResultId() {
		return AppointmentResultId;
	}

	public void setAppointmentResultId(String AppointmentresultId) {
		AppointmentResultId = AppointmentresultId;
	}
	public String getApptTime() {
		return ApptTime;
	}

	public void setApptTime(String apptTime) {
		ApptTime = apptTime;
	}

	public String getStartDateTime(){
		return startDateTime;
	}
	
	public void setStartDateTime(String startDatetime) {
		startDateTime = startDatetime;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getFormattedApptDate() + "\t \t" + appointmentType;
	}

	@Override
	public int compareTo(CustomerDetailsAppointmentsModel object) {
		if (getFormattedApptDate() == null
				|| object.getFormattedApptDate() == null)
			return 0;
		return getFormattedApptDate().compareTo(object.getFormattedApptDate());
	}

}
