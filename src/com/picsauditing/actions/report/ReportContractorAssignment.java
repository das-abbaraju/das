package com.picsauditing.actions.report;

public class ReportContractorAssignment extends ReportAccount {
	protected boolean assignedCon = false;

	public String execute() throws Exception {
		//if (conAuditorId == 0) {
			if (assignedCon == false) {
				sql.addWhere("c.welcomeAuditor_id IS NULL");
			} else {
				sql.addWhere("c.welcomeAuditor_id > 0");
			}
		//}
		sql.addField("c.accountDate");
		sql.addField("c.welcomeAuditor_id");
		return super.execute();
	}

	public boolean isAssignedCon() {
		return assignedCon;
	}

	public void setAssignedCon(boolean assignedCon) {
		this.assignedCon = assignedCon;
	}

}
