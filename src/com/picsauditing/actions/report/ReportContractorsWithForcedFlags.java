package com.picsauditing.actions.report;


public class ReportContractorsWithForcedFlags extends ReportAccount {
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		sql.addJoin("accounts o on gc.genid = o.id");
		
		sql.addField("o.name");
		sql.addField("gc.forceFlag");
		sql.addField("gc.forceend");
		sql.addField("c.name");
		sql.addWhere("gc.forceFlag is not null");
		sql.addOrderBy("o.name, c.name");
		
		if (permissions.seesAllContractors())
			sql.addJoin("generalcontractors gc on gc.subid = a.id");

		else if (permissions.isOperator() || permissions.isCorporate())
			sql.addJoin("generalcontractors gc on gc.subid = " + permissions.getAccountId());

		return super.execute();
	}
}
