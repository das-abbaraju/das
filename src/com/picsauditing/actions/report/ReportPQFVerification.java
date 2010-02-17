package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
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
		/**
		 * select  from audit_operator ao
		 * join generalcontractors gc on gc.genID = ao.opID
		 * join contractor_audit ca on ca.auditTypeID = ao.auditTypeID and ca.conID = gc.subID
		 * where ao.auditTypeID in (1,11) and ao.canSee = 1 
		 * and ca.auditStatus IN ('Submitted','Resubmitted')
		 */
		SelectSQL subSelect = new SelectSQL("contractor_audit ca");
		subSelect.addField("ca.conID");
		subSelect.addJoin("JOIN generalcontractors gc ON gc.subid = ca.conid");
		subSelect.addJoin("JOIN operators o ON o.id = gc.genid");
		subSelect.addJoin("JOIN audit_operator ao ON ao.opid = o.inheritAudits ");
		subSelect.addWhere("ao.auditTypeID in (1,11)");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ca.auditTypeID = ao.auditTypeID");
		subSelect.addWhere("ca.auditStatus IN ('Submitted','Resubmitted')");
		
		// As of January 2010 there are only 34 contractors that work for only free accounts 
		// and don't require verification. This is easier to verify all of them FOR NOW.
		// and ao.requiredAuditStatus = 'Active'
		sql.addWhere("a.id IN (" + subSelect.toString() + ")");
		
		sql.addJoin("LEFT JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");
		
		sql.addJoin("JOIN contractor_audit ca1 on ca1.conID = a.id");
		sql.addWhere("ca1.auditTypeID = 1");
		sql.addField("ca1.completedDate");
		sql.addWhere("a.acceptsBids = 0");
		getFilter().setShowTradeInformation(false);
		getFilter().setShowPrimaryInformation(false);
	}
}
