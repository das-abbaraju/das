package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportAuditDataUpdate extends ReportContractorAudits {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AuditVerification);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addField("pq.updateDate");
		sql.addField("pq.answer");
		sql.addField("pt.question");
		sql.addJoin("JOIN pqfdata pq on pq.auditID = ca.id");
		sql.addJoin("JOIN pqfquestion_text pt on pt.questionID = pq.questionID");
		sql.addWhere("pq.updateDate > ca.completedDate");
		sql.addWhere("a.status = 'Active'");
		if(getFilter().getAuditTypeID() == null) {
			sql.addWhere("atype.id = 1");
		}
		orderByDefault = "pq.updateDate DESC";
		
		getFilter().setShowPolicyType(true);
	}
}
