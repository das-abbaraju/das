package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.Account;
import com.picsauditing.util.PermissionQueryBuilder;

public class QueryAccount extends ModelBase {
	public QueryAccount() {
		super();
		from = new Account();
		from.addFields();
		from.addJoins();

		defaultSort = from.getAlias() + ".name";
	}

	@Override
	public String getWhereClause(Permissions permissions) {
		PermissionQueryBuilder builder = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.SQL);
		return builder.toString();
	}
}
