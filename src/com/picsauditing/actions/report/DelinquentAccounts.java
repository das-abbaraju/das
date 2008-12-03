package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class DelinquentAccounts extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addField("c.lastInvoiceDate");
		sql.addWhere("DATEDIFF(NOW(),c.lastInvoiceDate) > 75");
		sql.addWhere("(c.lastPayment IS NULL OR c.lastPayment < c.lastInvoiceDate)");
		sql.addWhere("a.active = 'Y'");
		sql.addWhere("c.mustPay = 'Yes'");
		sql.addField("DATEDIFF(ADDDATE(c.lastInvoiceDate, 120),NOW()) AS DaysLeft");
		setOrderBy("c.lastInvoiceDate ASC");
		
		getFilter().setShowVisible(false);

		return super.execute();
	}
}
