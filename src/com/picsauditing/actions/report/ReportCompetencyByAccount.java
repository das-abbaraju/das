package com.picsauditing.actions.report;

import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportCompetencyByAccount extends ReportCompetencyByEmployee {

	public ReportCompetencyByAccount() {
		orderByDefault = "name";
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql = new SelectSQL("(" + sql.toString() + ") t");
		sql.addGroupBy("accountID");

		sql.addField("name");
		sql.addField("accountID");
		sql.addField("count(*) employeeCount");
		sql.addField("sum(skilled) skilled");
		sql.addField("sum(required) required");

		filter.setShowFirstName(false);
		filter.setShowLastName(false);
		filter.setShowEmail(false);
		filter.setShowSsn(false);
	}

}
