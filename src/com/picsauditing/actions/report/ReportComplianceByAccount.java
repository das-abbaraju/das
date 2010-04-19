package com.picsauditing.actions.report;

import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportComplianceByAccount extends ReportComplianceByEmployee {

	public ReportComplianceByAccount() {
		orderByDefault = "name";
	}

	protected void buildQuery() {
		super.buildQuery();
		sql = new SelectSQL("(" + sql.toString() + ") t");
		sql.addGroupBy("accountID");

		sql.addField("name");
		sql.addField("sum(skilled) skilled");
		sql.addField("sum(required) required");
	}
}
