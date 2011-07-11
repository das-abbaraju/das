package com.picsauditing.mail;

import com.picsauditing.actions.report.ReportAccount;

@SuppressWarnings("serial")
public class ReportEmailWebinar extends ReportAccount {

	public void prepare() throws Exception {
		super.prepare();

		getFilter().setShowContractor(true);
		getFilter().setShowRegistrationDate(true);
		getFilter().setAllowMailMerge(true);
	}

	public void buildQuery() {
		super.buildQuery();

		sql.addField("c.score");
		sql.addField("a.dbaName");
		sql.addWhere("a.status = 'Active'");

		filteredDefault = true;
	}
}