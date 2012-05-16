package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.Account;
import com.picsauditing.util.PermissionQueryBuilder;

public class QueryAccount extends ModelBase {
	public QueryAccount() {
		super();
		primaryTable = new Account();
		primaryTable.addFields();
		primaryTable.addJoins();

		defaultSort = primaryTable.getAlias() + ".name";
	}

	@Override
	public String getWhereClause(Permissions permissions) {
		PermissionQueryBuilder builder = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		return builder.toString();
	}
}
