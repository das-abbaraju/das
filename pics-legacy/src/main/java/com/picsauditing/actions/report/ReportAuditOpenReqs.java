package com.picsauditing.actions.report;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAuditOpenReqs extends ReportContractorAuditOperator {
	public String execute() throws Exception {
		buildQuery();
		run(sql);

		return SUCCESS;
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		sql.addJoin("JOIN contractor_audit_file caf on caf.auditID = ca.id");
		sql.addJoin("JOIN workflow wf ON wf.id = atype.workflowID");

		sql.addWhere("cao.status IN ('Submitted', 'Resubmitted')");
		sql.addWhere("wf.hasRequirements");
		sql.addWhere("caf.reviewed = 0");
		sql.addWhere("a.status = 'Active'");
		
		if (permissions.isOperatorCorporate()) {
			String ids = Strings.implodeForDB(permissions.getAllInheritedGroupIds());
			sql.addWhere("(atype.assignAudit IN (" + ids +") or atype.editAudit IN (" + ids + "))");
		}

		sql.addField("caf.description");
		sql.addField("caf.creationDate AS uploadDate");

		orderByDefault = "ca.assignedDate DESC";

		getFilter().setShowAnyAuditor(true);
		getFilter().setShowAuditor(false);
		getFilter().setShowClosingAuditor(false);
		getFilter().setShowStatus(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}
}
