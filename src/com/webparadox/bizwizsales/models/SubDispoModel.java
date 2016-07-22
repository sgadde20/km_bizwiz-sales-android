package com.webparadox.bizwizsales.models;

import android.util.Log;


public class SubDispoModel {
	String SubDispoId = "";
	String SubDispoName;

	public String getSubDispoId() {
		return SubDispoId;
	}

	public void setSubDispoId(String subDispoId) {
		SubDispoId = subDispoId;
	}

	public String getSubDispoName() {
		return SubDispoName;
	}

	public void setSubDispoName(String subDispoName) {
		SubDispoName = subDispoName;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		Log.d("SubDispoName", SubDispoName);
		return SubDispoName;
		
	}
}
