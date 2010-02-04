package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportAuditOpenReqs extends ReportContractorAudits {

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addJoin("JOIN contractor_audit_file caf on caf.auditID = ca.id");
		sql.addWhere("ca.closingAuditorID=" + permissions.getUserId() + " OR (ca.closingAuditorID IS NULL AND ca.auditorID=" + permissions.getUserId()+")");
		sql.addWhere("ca.auditStatus = 'Submitted'");
		sql.addWhere("atype.hasRequirements = 1");
		sql.addWhere("caf.reviewed = 0");
		sql.addWhere("a.status = 'Active'");
		
		sql.addField("caf.description");
		sql.addField("caf.creationDate AS uploadDate");
		
		orderByDefault = "ca.assignedDate DESC";

		getFilter().setShowAuditor(false);
		getFilter().setShowStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}
}
