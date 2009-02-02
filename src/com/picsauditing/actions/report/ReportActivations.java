package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportActivations extends ReportAccount {
	
	public ReportActivations() {
		orderByDefault = "creationDate";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("mustPay='Yes'");
	}
}
