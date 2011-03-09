package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectAccount;

public class ReportPQFVerification extends ReportAccount {
	private static final long serialVersionUID = 6697393552632136569L;
	
	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AuditVerification);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		
		sql.setType(SelectAccount.Type.Contractor);
		
		// As of January 2010 there are only 34 contractors that work for only free accounts 
		// and don't require verification. This is easier to verify all of them FOR NOW.
		// and ao.requiredAuditStatus = 'Active'
		
		sql.addJoin("LEFT JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");

		sql.addJoin("JOIN contractor_audit ca ON ca.conid = a.id "
				+ "AND ca.auditTypeID IN (1,11) AND ca.expiresDate > NOW()");
		sql.addJoin("JOIN contractor_audit_operator cao on cao.auditID = ca.id "
				+ "AND cao.visible = 1 AND cao.status IN ('Submitted','Resubmitted') AND cao.percentComplete = 100");
		sql.addJoin("JOIN accounts o ON o.id = cao.opID");
		sql.addField("MIN(cao.statusChangedDate) statusChangedDate");
		sql.addWhere("a.acceptsBids = 0");
		sql.addWhere("o.status IN ('Active')");
		sql.addGroupBy("ca.conid");
		orderByDefault = "cao.statusChangedDate";

		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
	}
}
