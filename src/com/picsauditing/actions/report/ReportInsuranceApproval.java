package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.search.SelectContractorAudit;

@SuppressWarnings("serial")
public class ReportInsuranceApproval extends ReportContractorAudits {

	public ReportInsuranceApproval() {
		sql = new SelectContractorAudit();
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceApproval, OpType.View);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addField("ca.expiresDate");
		sql.addField("ao.name as operatorName");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.auditID");
		sql.addJoin("JOIN accounts ao on ao.id = cao.opID");
		sql.addWhere("ca.auditStatus IN ('Submitted','Active')");
		sql.addWhere("cao.status = 'Pending'");
		sql.addWhere("atype.classType = 'Audit'");
		sql.addWhere("a.active = 'Y'");

		getFilter().setShowVisible(false);
		getFilter().setShowTrade(false);
		getFilter().setShowCompletedDate(false);
		getFilter().setShowClosedDate(false);
		getFilter().setShowExpiredDate(false);
		getFilter().setShowPercentComplete(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowPolicyType(true);
	}
}
