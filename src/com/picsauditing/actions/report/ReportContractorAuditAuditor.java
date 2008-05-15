package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditStatus;

public class ReportContractorAuditAuditor extends ReportContractorAudits {

	public String execute() throws Exception {
		loadPermissions();
		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" 
				+ AuditStatus.Pending + "','"
				+ AuditStatus.Submitted + "')");
		
		if (orderBy == null)
			orderBy = "ca.assignedDate DESC";

		return super.execute();
	}
}
