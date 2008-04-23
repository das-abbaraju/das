package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditStatus;

public class ReportContractorAuditAuditor extends ReportContractorAudits {

	public String execute() throws Exception {
		loadPermissions();
		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" 
				+ AuditStatus.Active + "','" 
				+ AuditStatus.Pending + "','"
				+ AuditStatus.Submitted + "')");

		return super.execute();
	}
}
