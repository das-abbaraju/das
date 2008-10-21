package com.picsauditing.actions.report;

public class ReportContractorAssignment extends ReportAccount {

	public String execute() throws Exception {

		// if (conAuditorId == 0) {
		if (getFilter().isShowAssignedCon() == false) {
			sql.addWhere("c.welcomeAuditor_id IS NULL");
		} else {
			sql.addWhere("c.welcomeAuditor_id > 0");
		}
		// }
		sql.addField("a.dateCreated");
		sql.addField("c.accountDate");
		sql.addField("a.state");
		sql.addField("c.welcomeAuditor_id");
		if (orderBy == null)
			orderBy = "a.dateCreated DESC";

		getFilter().setShowAssignedCon(true);
		
		return super.execute();
	}
}
