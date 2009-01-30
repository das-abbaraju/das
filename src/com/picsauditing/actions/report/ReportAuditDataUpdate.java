package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportAuditDataUpdate extends ReportContractorAudits {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AuditVerification);
	}

	@Override
	public void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;
		super.buildQuery();
		
		sql.addField("pq.updateDate");
		sql.addField("pq.answer");
		sql.addField("p.question");
		sql.addJoin("JOIN pqfdata pq on pq.auditID = ca.id");
		sql.addJoin("JOIN pqfquestions p on p.id = pq.questionID");
		sql.addWhere("pq.updateDate > ca.completedDate");
		orderByDefault = "pq.updateDate DESC";
		
		getFilter().setShowPolicyType(true);
	}
}
