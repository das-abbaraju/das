package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditTypeClass;

@SuppressWarnings("serial")
public class ReportPolicyList extends ReportContractorAudits {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceCerts);
	}

	@Override
	public void buildQuery() {
		auditTypeClass = AuditTypeClass.Policy;

		super.buildQuery();
		
		if(permissions.isOperator()) {
			sql.addField("cao.status AS CaoStatus");
			sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id");
			sql.addWhere("cao.opID = "+permissions.getAccountId());
		}
		getFilter().setShowPolicyType(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowConAuditor(false);
		getFilter().setShowCaoStatus(true);
	}
}
