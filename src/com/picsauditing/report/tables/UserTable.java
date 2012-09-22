package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class UserTable extends ReportTable {

	public UserTable() {
		super("users");
	}

	protected void defineFields() {
		addPrimaryKey(FilterType.UserID);
		addFields(com.picsauditing.jpa.entities.User.class, FieldImportance.Average);
//		addJoin(new AccountTable(prefix + "Account", alias + ".accountID"));
	}
}