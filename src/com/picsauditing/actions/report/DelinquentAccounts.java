package com.picsauditing.actions.report;

public class DelinquentAccounts extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		loadPermissions();
		sql.addField("c.lastInvoiceDate");
		sql.addWhere("DATEDIFF(NOW(),c.lastInvoiceDate) > 75");
		sql.addWhere("(c.lastPayment IS NULL OR c.lastPayment < c.lastInvoiceDate)");
		sql.addWhere("a.active = 'Y'");
		sql.addField("DATEDIFF(ADDDATE(c.lastInvoiceDate, 120),NOW()) AS DaysLeft");
		setOrderBy("c.lastInvoiceDate ASC");

		return super.execute();
	}

	@Override
	protected void toggleFilters() {
		filterVisible = false;
	}

}
