package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportContractorsWithForcedFlags extends ReportAccount {
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ForcedFlagsReport);
	}
	
	@Override
	protected void buildQuery() {
		skipPermissions = true;
		super.buildQuery();
		
		sql.addJoin("JOIN generalcontractors gc on gc.subid = a.id");
		sql.addJoin("JOIN accounts o on gc.genid = o.id");
		if(permissions.isOperator())
			sql.addWhere("gc.genID = "+ permissions.getAccountId());
		if (permissions.isCorporate())
			sql.addJoin("JOIN facilities f ON f.opID = gc.genID AND f.corporateID = " + permissions.getAccountId());
		
		sql.addField("o.name AS opName");
		sql.addField("o.id AS opId");
		sql.addField("lower(gc.forceFlag) AS lflag");
		sql.addField("gc.forceend");
		sql.addWhere("gc.forceFlag IS NOT null");
		orderByDefault = "o.name, a.name";
	}
}
