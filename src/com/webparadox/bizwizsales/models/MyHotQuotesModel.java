package com.webparadox.bizwizsales.models;

public class MyHotQuotesModel {

	String CustomerId, CustomerFullName, Address, City, State, Followups, Jobs,
			Appts, DateLastEvent,zip,Date,Time,JobStartDateTime, JobEndDateTime, JobStartDate, JobEndDate, JobStartTime, JobEndTime;

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCustomerId() {
		return CustomerId;
	}

	public void setCustomerId(String customerId) {
		CustomerId = customerId;
	}

	public String getCustomerFullName() {
		return CustomerFullName;
	}

	public void setCustomerFullName(String customerFullName) {
		CustomerFullName = customerFullName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCity() {
		return City;
	}

	public void setCity(String city) {
		City = city;
	}

	public String getState() {
		return State;
	}

	public void setState(String state) {
		State = state;
	}

	public String getFollowups() {
		return Followups;
	}

	public void setFollowups(String followups) {
		Followups = followups;
	}

	public String getJobs() {
		return Jobs;
	}

	public void setJobs(String jobs) {
		Jobs = jobs;
	}

	public String getAppts() {
		return Appts;
	}

	public void setAppts(String appts) {
		Appts = appts;
	}

	public String getDateLastEvent() {
		return DateLastEvent;
	}

	public void setDateLastEvent(String dateLastEvent) {
		DateLastEvent = dateLastEvent;
	}
	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		this.Date = date;
	}
	
	public String getJobStartDate(){
		return JobStartDate;
	}
	
	public void setJobStartDate(String jsdate){
		this.JobStartDate = jsdate;
	}
	
	public String getJobStartTime(){
		return JobStartTime;
	}
	
	public void setJobStartTime(String jstime){
		this.JobStartTime = jstime;
	}
	
	public String getJobEndDate(){
		return JobEndDate;
	}
	
	public void setJobEndDate(String jedate){
		this.JobEndDate = jedate;
	}
	
	public String getJobEndTime(){
		return JobEndTime;
	}
	
	public void setJobEndTime(String jetime){
		this.JobEndTime = jetime;
	}
	
	
	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		this.Time = time;
	}
	
	public void setJobStartDateTime(String startDateTime){
		JobStartDateTime = startDateTime;
	}
	
	public String getJobStartDateTime(){
		return JobStartDateTime;
	}
	
	public void setJobEndDateTime(String endDateTime){
		JobEndDateTime = endDateTime;
	}
	
	public String getJobEndDateTime(){
		return JobEndDateTime;
	}
}
