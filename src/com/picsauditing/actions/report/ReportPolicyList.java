package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportPolicyList extends ReportContractorAudits {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceCerts);
	}

	@Override
	public void buildQuery() {
		showOnlyAudits = false;

		super.buildQuery();

		sql.addWhere("atype.classType = 'Policy'");
		getFilter().setShowPolicyType(true);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowConAuditor(false);
	}
}
