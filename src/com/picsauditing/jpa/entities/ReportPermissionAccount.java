package com.picsauditing.jpa.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "report_permission_account")
public class ReportPermissionAccount extends BaseTable {

	private Report report;
	private Account account;
	private boolean editable;

	public ReportPermissionAccount() {
	}

	public ReportPermissionAccount(Account account, Report report) {
		this.report = report;
		this.account = account;
	}

	@ManyToOne
	@JoinColumn(name = "reportID", nullable = false)
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	@ManyToOne
	@JoinColumn(name = "accountID", nullable = false)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@javax.persistence.Column(name = "editable", nullable = false)
	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}