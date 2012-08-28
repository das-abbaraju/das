package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.util.PermissionQueryBuilder;

public class AccountModel extends AbstractModel {
	public AccountModel() {
		super();
		rootTable = new AccountTable();
		rootTable.addFields();
		rootTable.addJoins();

		defaultSort = rootTable.getAlias() + ".name";
		
		parentTable = rootTable;
	}

	@Override
	public String getWhereClause(Permissions permissions) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);

		return permQuery.toString();
	}
}