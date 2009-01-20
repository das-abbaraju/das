package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.search.SelectAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportPolicyVerification extends ReportAccount {
	private static final long serialVersionUID = 6697393552632136569L;
	
	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.InsuranceVerification);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		
		sql.addField("ca.auditStatus");
		sql.addField("at.auditName");
		sql.addField("ca.auditID");
		sql.setType(SelectAccount.Type.Contractor);
		sql.addJoin("JOIN contractor_audit ca on ca.conID = a.id");
		sql.addJoin("JOIN audit_type at on at.auditTypeID = ca.auditTypeID");
		sql.addWhere("at.classType = 'Policy'");
		sql.addWhere("ca.auditStatus IN ('Submitted','Resubmitted')");
		SelectSQL subSelect = new SelectSQL("audit_operator ao");
		subSelect.addField("ca.conID");
		subSelect.addJoin("JOIN generalcontractors gc on gc.genID = ao.opID");
		subSelect.addJoin("JOIN contractor_audit ca on ca.auditTypeID = ao.auditTypeID and ca.conID = gc.subID");
		subSelect.addJoin("JOIN audit_type at on at.auditTypeID = ca.auditTypeID");
		subSelect.addWhere("at.classType = 'Policy'");
		subSelect.addWhere("ao.canSee = 1");
		subSelect.addWhere("ao.requiredForFlag in ('Amber','Red')");
		subSelect.addWhere("ca.auditStatus IN ('Submitted','Resubmitted') and ao.requiredAuditStatus = 'Active'");
		sql.addWhere("a.id IN (" + subSelect.toString() + ")");
	}
}
