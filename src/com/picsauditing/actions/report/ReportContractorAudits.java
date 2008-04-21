package com.picsauditing.actions.report;

public class ReportContractorAudits extends ReportAccount {

	public String execute() throws Exception {
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.auditID");
		sql.addField("ca.createdDate");
		sql.addField("ca.auditStatus");
		sql.addField("ca.expiresDate");
		sql.addField("ca.completedDate");
		sql.addField("ca.closedDate");
		sql.addField("ca.auditLocation");
		sql.addField("ca.percentComplete");
		sql.addField("ca.percentVerified");

		sql.addJoin("LEFT JOIN audit_type atype ON atype.auditTypeID = ca.auditTypeID");
		sql.addField("atype.auditName");
		sql.addField("atype.isScheduled");
		sql.addField("atype.hasAuditor");
		sql.addField("atype.hasRequirements");
		
		sql.addJoin("LEFT JOIN users auditor ON auditor.id = ca.auditorID");
		sql.addField("auditor.name auditor_name");

		return super.execute();
	}
}
