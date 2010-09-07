package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportMyAuditHistory extends ReportContractorAudits {

	@Override
	public void buildQuery() {
		skipPermissions = true;
		super.buildQuery();

		sql.addWhere("(ca.auditorID=" + permissions.getUserId() + " OR ca.closingAuditorID=" + permissions.getUserId()
				+ ")");
		sql.addWhere("ca.id IN (SELECT auditID FROM contractor_audit_operator cao where status IN ('Complete','Approved') )");
		orderByDefault = "ca.closedDate DESC";

		getFilter().setShowAuditor(false);
		getFilter().setShowStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}

}
