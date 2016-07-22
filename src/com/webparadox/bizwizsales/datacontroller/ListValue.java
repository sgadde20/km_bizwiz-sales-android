package com.webparadox.bizwizsales.datacontroller;

public class ListValue {

	//private variables
	String id;
	String customerId;
	String employeeId;
	String delearId;
	String path;
	String description;

	// Empty constructor
	public ListValue(){

	}
	// constructor
	public ListValue(String id,String customerId,String employeeId,String delearId, String path, String description){
		this.id = id;
		this.customerId = customerId;
		this.employeeId = employeeId;
		this.delearId = delearId;
		this.path = path;
		this.description = description;
	}

	// getting id
	public String getId(){
		return this.id;
	}

	// setting id
	public void setId(String id){
		this.id = id;
	}

	//getting customerId
	public String getcustomerId(){
		return this.customerId;
	}

	// setting customerId
	public void setcustomerId(String customerId){
		this.customerId = customerId;
	}

	//getting employeeId
	public String getemployeeId(){
		return this.employeeId;
	}

	// setting employeeId
	public void setemployeeId(String employeeId){
		this.employeeId = employeeId;
	}

	//getting delearId
	public String getdelearId(){
		return this.delearId;
	}

	// setting delearId
	public void setdelearId(String delearId){
		this.delearId = delearId;
	}

	// getting path
	public String getpath(){
		return this.path;
	}

	// setting path
	public void setpath(String path){
		this.path = path;
	}

	// getting description
	public String getdescription(){
		return this.description;
	}

	// setting description
	public void setdescription(String description){
		this.description = description;
	}

}