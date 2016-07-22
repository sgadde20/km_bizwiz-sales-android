package com.webparadox.bizwizsales.models;

public class AppointmentTypeModel {
	public String id = "";
	public String typeName = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return typeName;
	}
}
