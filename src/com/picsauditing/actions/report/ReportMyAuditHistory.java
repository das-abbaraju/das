package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportMyAuditHistory extends ReportContractorAudits {
	public String execute() throws Exception {
		if (!forceLogin()) return LOGIN;

		skipPermissions = true;
		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" + AuditStatus.Active + "')");

		if (orderBy != null)
			orderBy = "ca.closedDate DESC";
		
		if(filtered == null)
			filtered = false;
		
		getFilter().setShowAuditor(false);
		getFilter().setShowVisible(false);
		
		return super.execute();
	}

}
