package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class DelinquentAccounts extends ReportAccount {

	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addField("c.lastInvoiceDate");
		sql.addWhere("DATEDIFF(NOW(),c.lastInvoiceDate) > 75");
		sql.addWhere("(c.lastPayment IS NULL OR c.lastPayment < c.lastInvoiceDate)");
		sql.addWhere("a.active = 'Y'");
		sql.addWhere("c.mustPay = 'Yes'");
		sql.addField("DATEDIFF(ADDDATE(c.lastInvoiceDate, 120),NOW()) AS DaysLeft");
		if (this.orderBy == null)
			this.orderBy = "c.lastInvoiceDate ASC";
		getFilter().setShowVisible(false);
	}
}
