package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportBilling extends ReportAccount {
	
	public ReportBilling() {
		orderByDefault = "paymentExpires";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("c.payingFacilities");
		sql.addField("c.paymentMethod");
		sql.addField("c.paymentMethodStatus");
		sql.addField("c.membershipDate");
		sql.addField("c.paymentExpires");
		sql.addField("c.lastInvoiceDate");
		sql.addField("c.lastPayment");
		sql.addField("c.billingAmount");
		sql.addField("c.newBillingAmount");
		
		sql.addWhere("mustPay='Yes'");
		sql.addWhere("a.active = 'Y'");
	}
}
