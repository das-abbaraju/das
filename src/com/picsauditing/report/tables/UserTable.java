package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class UserTable extends AbstractTable {

	public UserTable(String alias, String foreignKey) {
		super("users", alias, alias, alias + ".id = " + foreignKey);
		includedColumnImportance = FieldImportance.Average;
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.UserID);
		addFields(com.picsauditing.jpa.entities.User.class);
	}

	public void addJoins() {
		addJoin(new AccountTable(prefix + "Account", alias + ".accountID"));
	}
}