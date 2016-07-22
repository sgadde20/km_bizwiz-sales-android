package com.webparadox.bizwizsales.models;

public class PaymentType {
	public int id;
	public String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "{Id:" + id + ",Type:" + type + "}";
	}
}
