package com.webparadox.bizwizsales.models;

public class CustomerDetailsProjectModel {
	String ProjectId, ProjectType, ProjectAmount, FormattedInstallDate,
			CancelReason, Foreman, BalanceDue,installDays,startDate,endDate,salesMan;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSalesMan() {
		return salesMan;
	}

	public void setSalesMan(String salesMan) {
		this.salesMan = salesMan;
	}

	public String getInstallDays() {
		return installDays;
	}

	public void setInstallDays(String installDays) {
		this.installDays = installDays;
	}

	public String getProjectId() {
		return ProjectId;
	}

	public void setProjectId(String projectId) {
		ProjectId = projectId;
	}

	public String getProjectType() {
		return ProjectType;
	}

	public void setProjectType(String projectType) {
		ProjectType = projectType;
	}

	public String getProjectAmount() {
		return ProjectAmount;
	}

	public void setProjectAmount(String projectAmount) {
		ProjectAmount = projectAmount;
	}

	public String getFormattedInstallDate() {
		return FormattedInstallDate;
	}

	public void setFormattedInstallDate(String formattedInstallDate) {
		FormattedInstallDate = formattedInstallDate;
	}

	public String getCancelReason() {
		return CancelReason;
	}

	public void setCancelReason(String cancelReason) {
		CancelReason = cancelReason;
	}

	public String getForeman() {
		return Foreman;
	}

	public void setForeman(String foreman) {
		Foreman = foreman;
	}

	public String getBalanceDue() {
		return BalanceDue;
	}

	public void setBalanceDue(String balanceDue) {
		BalanceDue = balanceDue;
	}
}
