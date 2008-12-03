package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditStatus;

@SuppressWarnings("serial")
public class ReportMyAuditHistory extends ReportContractorAudits {
	
	@Override
	public void buildQuery() {
		skipPermissions = true;
		super.buildQuery();

		sql.addWhere("ca.auditorID=" + permissions.getUserId());
		sql.addWhere("ca.auditStatus IN ('" + AuditStatus.Active + "')");
		orderByDefault = "ca.closedDate DESC";
		
		getFilter().setShowAuditor(false);
		getFilter().setShowVisible(false);
	}

}
