package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ReportAccountAudits extends ReportAccount {

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addAudit(AuditType.PQF);
		if (permissions.isOperator())
			sql.addField("flags.waitingOn");

		filteredDefault = true;

		// Getting the certificate info per contractor is too difficult!
		/*
		String certTable = "SELECT contractor_id, count(*) certificateCount FROM certificates WHERE status = 'Approved'";
		if (permissions.isOperator())
			certTable += " AND operator_id = " + permissions.getAccountId();
		if (permissions.isCorporate())
			certTable += " AND operator_id IN (SELECT facilityID FROM facilities WHERE corporateID = "
					+ permissions.getAccountId() + ")";
		certTable += " GROUP BY contractor_id";
		sql.addJoin("LEFT JOIN (" + certTable + ") certs ON certs.contractor_id = a.id");
		sql.addField("certs.certificateCount");
		 */
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}
}
