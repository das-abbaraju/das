package com.picsauditing.actions.report;


public class ReportContractorsWithForcedFlags extends ReportAccount {
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		skipPermissions = true;
		
		if (permissions.seesAllContractors())
			sql.addJoin("JOIN generalcontractors gc on gc.subid = a.id");

		else if (permissions.isOperator() || permissions.isCorporate())
			sql.addJoin("JOIN generalcontractors gc on gc.subid = " + permissions.getAccountId());

		sql.addJoin("JOIN accounts o on gc.genid = o.id");
		
		sql.addField("o.name AS opName");
		sql.addField("o.id AS opId");
		sql.addField("lower(gc.forceFlag) AS lflag");
		sql.addField("gc.forceend");
		sql.addWhere("gc.forceFlag IS NOT null");
		sql.addOrderBy("o.name");
		
		return super.execute();
	}
}
