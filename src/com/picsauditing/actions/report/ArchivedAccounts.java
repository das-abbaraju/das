package com.picsauditing.actions.report;

public class ArchivedAccounts extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		loadPermissions();
		sql.addField("a.contact");
		sql.addField("a.phone");
		sql.addField("a.phone2");
		sql.addField("c.lastInvoiceDate");
		sql.addWhere("DATEDIFF(NOW(),c.lastInvoiceDate) > 120");
		sql.addWhere("(c.lastPayment IS NULL OR c.lastPayment < c.lastInvoiceDate)");
		sql.addWhere("a.active = 'N'");
		setOrderBy("c.lastInvoiceDate ASC");

		return super.execute();
	}

	@Override
	protected void toggleFilters() {
		filterVisible = false;
	}

}
