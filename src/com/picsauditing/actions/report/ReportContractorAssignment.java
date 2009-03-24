package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportContractorAssignment extends ReportAccount {

	@Override
	protected void buildQuery() {
		super.buildQuery();

		getFilter().setShowAssignedCon(true);
		if (getFilter().isAssignedCon()) {
			sql.addWhere("c.welcomeAuditor_id > 0");
		} else {
			sql.addWhere("c.welcomeAuditor_id IS NULL");
		}
		sql.addField("c.accountDate");
		sql.addField("c.welcomeAuditor_id");
		orderByDefault = "a.creationDate DESC";
	}
}
