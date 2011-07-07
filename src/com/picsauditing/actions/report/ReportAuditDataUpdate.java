package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportAuditDataUpdate extends ReportContractorAuditOperator {
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AuditVerification);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addField("pq.updateDate");
		sql.addField("pq.answer");
		sql.addField("translate('AuditQuestion',aq.id) question");
		sql.addJoin("JOIN pqfdata pq on pq.auditID = ca.id");
		sql.addJoin("JOIN audit_question aq on aq.id = pq.questionID");
		sql.addWhere("pq.updateDate > cao.statusChangedDate");
		sql.addWhere("a.status = 'Active'");
		if(getFilter().getAuditTypeID() == null) {
			sql.addWhere("atype.id = 1");
		}
		orderByDefault = "pq.updateDate DESC";
		
		getFilter().setShowPolicyType(true);
	}
}
