package com.picsauditing.actions.report;

public class DelinquentAccounts extends ReportAccount {

	public String execute() throws Exception {
		loadPermissions();
		sql.addField("c.lastInvoiceDate");
		sql.addWhere("DATEDIFF(NOW(),c.lastInvoiceDate) > 30");
		sql.addWhere("(c.lastPayment IS NULL OR c.lastPayment < c.lastInvoiceDate)");
		sql.addWhere("a.active = 'Y'");

		setOrderBy("c.lastInvoiceDate ASC");

		return super.execute();
	}

	@Override
	protected void toggleFilters() {
		filterVisible = false;
	}

}
