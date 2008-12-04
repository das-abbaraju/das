package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ReportAccountAudits extends ReportAccount {

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addAudit(AuditType.PQF);
		sql.addField("c.main_trade");
		sql.addField("a.industry");
		if (permissions.isOperator())
			sql.addField("flags.waitingOn");

		filteredDefault = true;

		String certTable = "SELECT contractor_id, count(*) certificateCount FROM certificates WHERE status = 'Approved'";
		if (permissions.isOperator())
			certTable += " AND operator_id = " + permissions.getAccountId();
		if (permissions.isCorporate())
			certTable += " AND operator_id IN (SELECT facilityID FROM facilities WHERE corporateID = "
					+ permissions.getAccountId() + ")";
		certTable += " GROUP BY contractor_id";
		sql.addJoin("LEFT JOIN (" + certTable + ") certs ON certs.contractor_id = a.id");
		sql.addField("certs.certificateCount");

	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}
}
