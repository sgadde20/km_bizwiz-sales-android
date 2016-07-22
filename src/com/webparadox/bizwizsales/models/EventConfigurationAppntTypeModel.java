package com.webparadox.bizwizsales.models;


public class EventConfigurationAppntTypeModel {

	String typeId;
	String typeName;
	String defaultAppointmentDuration;

	public String getDefaultAppointmentDuration() {
		return defaultAppointmentDuration;
	}

	public void setDefaultAppointmentDuration(String defaultAppointmentDuration) {
		this.defaultAppointmentDuration = defaultAppointmentDuration;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
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
