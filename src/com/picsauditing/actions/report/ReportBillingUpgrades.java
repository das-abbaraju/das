package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportBillingUpgrades extends ReportBilling {
	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("c.lastPaymentAmount < c.newBillingAmount");
		sql.addWhere("paymentExpires > DATE_ADD(CURDATE(),INTERVAL 90 DAY) OR lastInvoiceDate > lastPayment");
	}

}
