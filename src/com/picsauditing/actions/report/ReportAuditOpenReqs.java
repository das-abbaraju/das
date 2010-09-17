package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.Workflow;


@SuppressWarnings("serial")
public class ReportAuditOpenReqs extends ReportContractorAuditOperator {

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addJoin("JOIN contractor_audit_file caf on caf.auditID = ca.id");
		sql.addJoin("JOIN workflow wf ON wf.id = atype.workflowID");
		sql.addWhere("ca.closingAuditorID=" + permissions.getUserId() + " OR (ca.closingAuditorID IS NULL AND ca.auditorID=" + permissions.getUserId()+")");
		sql.addWhere("cao.status = 'Submitted'");
		sql.addWhere("wf.id = "+ Workflow.AUDIT_REQUIREMENTS_WORKFLOW);
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
