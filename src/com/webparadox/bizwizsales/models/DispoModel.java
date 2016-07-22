package com.webparadox.bizwizsales.models;

import java.util.ArrayList;

public class DispoModel {
	String DispoId, DispoName;
	public ArrayList<SubDispoModel> SubDispo = new ArrayList<SubDispoModel>();

	public String getDispoId() {
		return DispoId;
	}

	public void setDispoId(String dispoId) {
		DispoId = dispoId;
	}

	public String getDispoName() {
		return DispoName;
	}

	public void setDispoName(String dispoName) {
		DispoName = dispoName;
	}
	
	public ArrayList<SubDispoModel> getSubDispo() {
		return SubDispo;
	}

	public void setSubDispo(ArrayList<SubDispoModel> SubDispo) {
		this.SubDispo = SubDispo;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return DispoName;
	}

}
