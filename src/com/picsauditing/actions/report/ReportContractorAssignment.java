package com.picsauditing.actions.report;

public class ReportContractorAssignment extends ReportAccount {

	public String execute() throws Exception {
		if (conAuditorId == 0) {
			sql.addWhere("c.welcomeAuditor_id IS NULL");
		}
		sql.addField("c.accountDate");
		sql.addField("c.welcomeAuditor_id");
		return super.execute();
	}

}
