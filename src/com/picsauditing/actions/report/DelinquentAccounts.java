package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class DelinquentAccounts extends ReportContractorInvoice {

	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("i.dueDate < NOW()");
		sql.addWhere("i.paid = 0");
		sql.addWhere("a.active = 'Y'");
		sql.addField("DATEDIFF(ADDDATE(i.dueDate, 90),NOW()) AS DaysLeft");
		getFilter().setShowVisible(false);
		if(permissions.isAdmin())
			getFilter().setShowCcOnFile(true);
	}
}
