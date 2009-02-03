package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportBilling extends ReportAccount {
	
	public ReportBilling() {
		orderByDefault = "paymentExpires";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("c.membershipDate");
		sql.addField("c.newBillingAmount");
		
		sql.addWhere("a.active = 'Y'");
	}
}
