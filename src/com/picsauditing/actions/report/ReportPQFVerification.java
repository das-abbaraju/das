package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

public class ReportPQFVerification extends ReportAccount {
	private static final long serialVersionUID = 6697393552632136569L;

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.AuditVerification);
		
		sql.setType(SelectAccount.Type.Contractor);
		/**
		 * select  from audit_operator ao
		 * join generalcontractors gc on gc.genID = ao.opID
		 * join contractor_audit ca on ca.auditTypeID = ao.auditTypeID and ca.conID = gc.subID
		 * where ao.auditTypeID in (1,11) and ao.canSee = 1 and ao.requiredForFlag in ('Amber','Red')
		 * and (ca.auditStatus = 'Pending' or (ca.auditStatus = 'Submitted' and ao.requiredAuditStatus = 'Active'))
		 */
		SelectSQL subSelect = new SelectSQL("audit_operator ao");
		subSelect.addField("ca.conID");
		subSelect.addJoin("JOIN generalcontractors gc on gc.genID = ao.opID");
		subSelect.addJoin("JOIN contractor_audit ca on ca.auditTypeID = ao.auditTypeID and ca.conID = gc.subID");
		subSelect.addWhere("ao.auditTypeID in (1,11)");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ao.requiredForFlag in ('Amber','Red')");
		subSelect.addWhere("ca.auditStatus = 'Pending' or (ca.auditStatus = 'Submitted' and ao.requiredAuditStatus = 'Active')");
		sql.addWhere("a.id IN (" + subSelect.toString() + ")");
		
		sql.addJoin("LEFT JOIN users csr ON csr.id = c.welcomeAuditor_id");
		sql.addField("csr.name csr_name");

		return super.execute();
	}
}
