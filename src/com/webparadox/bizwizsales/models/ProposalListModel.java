package com.webparadox.bizwizsales.models;

public class ProposalListModel {

	private String AppointmentResultId;
	private String Disposition;
	private String DispoId;
	private String SubDispo;
	private String SubDispoId;
	private String ProposalAmount;
	private String WorkOrderNotes;
	
	private String SignatureExists;
	private String SignatureURL;
	
	
	public String getAppointmentResultId() {
		return AppointmentResultId;
	}
	public void setAppointmentResultId(String appointmentResultId) {
		AppointmentResultId = appointmentResultId;
	}
	public String getDisposition() {
		return Disposition;
	}
	public void setDisposition(String disposition) {
		Disposition = disposition;
	}
	public String getDispoId() {
		return DispoId;
	}
	public void setDispoId(String dispoId) {
		DispoId = dispoId;
	}
	public String getSubDispo() {
		return SubDispo;
	}
	public void setSubDispo(String subDispo) {
		SubDispo = subDispo;
	}
	public String getSubDispoId() {
		return SubDispoId;
	}
	public void setSubDispoId(String subDispoId) {
		SubDispoId = subDispoId;
	}
	public String getProposalAmount() {
		return ProposalAmount;
	}
	public void setProposalAmount(String proposalAmount) {
		ProposalAmount = proposalAmount;
	}
	public String getWorkOrderNotes() {
		return WorkOrderNotes;
	}
	public void setWorkOrderNotes(String workOrderNotes) {
		WorkOrderNotes = workOrderNotes;
	}
	public String getSignatureExists() {
		return SignatureExists;
	}
	public void setSignatureExists(String signatureExists) {
		SignatureExists = signatureExists;
	}
	public String getSignatureURL() {
		return SignatureURL;
	}
	public void setSignatureURL(String signatureURL) {
		SignatureURL = signatureURL;
	}
	
}
