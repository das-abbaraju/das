package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.util.PermissionQueryBuilder;

public class AccountModel extends AbstractModel {
	public AccountModel() {
		super();
		primaryTable = new AccountTable();
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
