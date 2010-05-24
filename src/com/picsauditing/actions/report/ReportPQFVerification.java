package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

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
		SelectSQL subSelect = new SelectSQL("generalcontractors gc");
		subSelect.addField("gc.subid");
		subSelect.addJoin("JOIN operators o ON o.id = gc.genid");
		subSelect.addJoin("JOIN audit_operator ao ON ao.opid = o.inheritAudits");
		subSelect.addWhere("ao.auditTypeID in (1,11)");
		subSelect.addWhere("ao.canSee = 1");
		
		// As of January 2010 there are only 34 contractors that work for only free accounts 
		// and don't require verification. This is easier to verify all of them FOR NOW.
		// and ao.requiredAuditStatus = 'Active'
		sql.addWhere("a.id IN (" + subSelect.toString() + ")");
		
		sql.addJoin("LEFT JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");
		
		sql.addJoin("JOIN contractor_audit ca ON ca.conid = a.id");
		sql.addWhere("ca.auditStatus IN ('Submitted','Resubmitted')");
		sql.addWhere("ca.auditTypeID IN (1,11)");
		sql.addField("MIN(ca.completedDate) as completedDate");
		sql.addWhere("a.acceptsBids = 0");
		sql.addGroupBy("ca.conid");
		orderByDefault = "ca.completedDate";

		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
	}
}
