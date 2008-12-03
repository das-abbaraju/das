package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportAnnualAddendum extends ReportAccount {
	protected String auditFor;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.auditID");
		sql.addWhere("ca.auditTypeID = 11");

		return super.execute();
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}

}
