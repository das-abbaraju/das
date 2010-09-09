package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportPolicyList extends ReportContractorAuditOperator {

	public ReportPolicyList() {
		super();
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceCerts);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		// TODO: Inheritance changes
		if (permissions.hasPermission(OpPerms.AllContractors)) {
			if (getFilter().getOperatorSingle() > 0) {
				sql.addField("cao.status as caoStatus");
				sql.addJoin("JOIN operators o ON o.inheritInsuranceCriteria = cao.opID AND o.id = "
						+ getFilter().getOperatorSingle());
			} else {
				sql.addGroupBy("ca.id");
			}
		}

		getFilter().setShowOperatorSingle(true);
		getFilter().setShowAMBest(true);

	}
}
