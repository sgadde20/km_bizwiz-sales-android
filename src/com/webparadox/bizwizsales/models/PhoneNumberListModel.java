package com.webparadox.bizwizsales.models;

public class PhoneNumberListModel {
	String customerId;
	String typeName;
	String phoneNumberId;
	String phone;
	String phoneTypeId;
	String FirstName;
	String LastName;
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getPhoneNumberId() {
		return phoneNumberId;
	}
	public void setPhoneNumberId(String phoneNumberId) {
		this.phoneNumberId = phoneNumberId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoneTypeId() {
		return phoneTypeId;
	}
	public void setPhoneTypeId(String phoneTypeId) {
		this.phoneTypeId = phoneTypeId;
	}
	public void setFirstName(String FirstName) {
		this.FirstName = FirstName;
	}
	public void setLastName(String LastName) {
		this.LastName = LastName;
	}
	
}
