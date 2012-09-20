package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.FilterType;

public class UserTable extends ReportTable {

	public UserTable(String name) {
		super("users", name);
	}

	public void fill(Permissions permissions) {
		addPrimaryKey(FilterType.UserID);
		addFields(com.picsauditing.jpa.entities.User.class, FieldImportance.Average);
//		addJoin(new AccountTable(prefix + "Account", alias + ".accountID"));
	}
}