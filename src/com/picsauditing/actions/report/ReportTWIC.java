package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportTWIC extends ReportAccount {

	public ReportTWIC() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}

	protected void buildQuery() {
		super.buildQuery();

		sql.addField("e.id employeeID");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("e.title");
		sql.addField("e.twicExpiration");

		sql.addJoin("JOIN employee e on a.id = e.accountID");

		// getFilter().setShowAccountName(true);
		// getFilter().setShowIndustry(true);
		// getFilter().setShowOperator(false);
		// getFilter().setShowTrade(false);
		// getFilter().setShowRegistrationDate(false);

	}

}
