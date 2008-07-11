package com.picsauditing.actions.report;

public class ReportContractorAssignment extends ReportAccount {
	protected boolean assignedCon = false;
	protected boolean filterAssignedCon = true;

	public String execute() throws Exception {
		// if (conAuditorId == 0) {
		if (assignedCon == false) {
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
		
		return super.execute();
	}

	public boolean isAssignedCon() {
		return assignedCon;
	}

	public void setAssignedCon(boolean assignedCon) {
		this.assignedCon = assignedCon;
	}

	public boolean isFilterAssignedCon() {
		return filterAssignedCon;
	}

	public void setFilterAssignedCon(boolean filterAssignedCon) {
		this.filterAssignedCon = filterAssignedCon;
	}

}
