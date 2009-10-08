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
		 * where ao.auditTypeID in (1,11) and ao.canSee = 1 and ao.requiredForFlag in ('Amber','Red')
		 * and ca.auditStatus IN ('Submitted','Resubmitted') and ao.requiredAuditStatus = 'Active'
		 */
		SelectSQL subSelect = new SelectSQL("audit_operator ao");
		subSelect.addField("ca.conID");
		subSelect.addJoin("JOIN generalcontractors gc on gc.genID = ao.opID");
		subSelect.addJoin("JOIN contractor_audit ca on ca.auditTypeID = ao.auditTypeID and ca.conID = gc.subID");
		subSelect.addWhere("ao.auditTypeID in (1,11)");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ao.requiredForFlag in ('Amber','Red')");
		subSelect.addWhere("ca.auditStatus IN ('Submitted','Resubmitted') and ao.requiredAuditStatus = 'Active'");
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
