package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;


public class ReportContractorsWithForcedFlags extends ReportAccount {
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.ForcedFlagsReport);
		
		//skipPermissions = true;
		//if (!permissions.isOperator())
			// SelectAccount.setPermissions already adds this table for operators
		sql.addJoin("JOIN generalcontractors gc on gc.subid = a.id");
		sql.addJoin("JOIN accounts o on gc.genid = o.id");
		if (permissions.isCorporate())
			sql.addJoin("JOIN facilities f ON f.opID = gc.genID AND f.corporateID = " + permissions.getAccountId());
		
		sql.addField("o.name AS opName");
		sql.addField("o.id AS opId");
		sql.addField("lower(gc.forceFlag) AS lflag");
		sql.addField("gc.forceend");
		sql.addWhere("gc.forceFlag IS NOT null");
		this.orderBy = "o.name, a.name";
		
		return super.execute();
	}
}
