package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditStatus;

public class ReportMyAuditHistory extends ReportContractorAudits {
	public String execute() throws Exception {
		loadPermissions();
		skipPermissions = true;
		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" + AuditStatus.Active + "')");
		sql.addWhere("a.isActive = 'Y'");

		if (orderBy != null)
			orderBy = "ca.closedDate DESC";
		
		if(filtered == null) 
			filtered = false;
				
		return super.execute();
	}

}
