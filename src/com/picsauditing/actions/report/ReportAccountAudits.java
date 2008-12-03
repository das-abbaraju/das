package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;

@SuppressWarnings("serial")
public class ReportAccountAudits extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addAudit(AuditType.PQF);
		sql.addField("c.main_trade");
		sql.addField("a.industry");
		if (permissions.isOperator())
			sql.addField("flags.waitingOn");
		
		if (filtered == null)
			filtered = true;

        String certTable = "SELECT contractor_id, count(*) certificateCount FROM certificates WHERE status = 'Approved'";
        if (permissions.isOperator())
            certTable += " AND operator_id = " + permissions.getAccountId();
        if (permissions.isCorporate())
            certTable += " AND operator_id IN (SELECT facilityID FROM facilities WHERE corporateID = " + permissions.getAccountId() + ")";
        certTable += " GROUP BY contractor_id";
        sql.addJoin("LEFT JOIN (" + certTable + ") certs ON certs.contractor_id = a.id");
        sql.addField("certs.certificateCount");
        
        return super.executeOld();
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}
}
