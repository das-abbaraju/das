package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;

@SuppressWarnings("serial")
public class ReportSalesRepresentatives extends ReportAccount {
	
	@Override
	protected void checkPermissions() throws Exception {
		permissions.hasPermission(OpPerms.UserRolePicsOperator);
	}
	
	@Override
	public void buildQuery() {
		sql.setFromTable("users u");
		sql.addJoin("JOIN account_user au ON au.userid = u.id");
		sql.addJoin("JOIN accounts a ON a.id = au.accountid");
		sql.addJoin("JOIN contractor_info c ON c.requestedbyid = a.id");
		sql.addField("u.name AS userName");
		sql.addField("a.name AS accountName");
		sql.addField("au.ownerPercent");
		sql.addField("COUNT(*) AS countCons");
		sql.addWhere("au.role = 'PICSSalesRep'");
		if(!permissions.hasPermission(OpPerms.UserRolePicsOperator, OpType.Edit)) {
			sql.addWhere("u.id = "+ permissions.getUserId());
		}
		sql.addGroupBy("c.requestedbyid, au.id");
		sql.addOrderBy("u.name");
	}
}
