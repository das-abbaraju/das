package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class UserTable extends BaseTable {

	public UserTable(String prefix, String alias, String foreignKey) {
		super("users", prefix, alias, alias + ".id = " + foreignKey);
	}

	public UserTable(String alias, String foreignKey) {
		super("users", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer);

		addFields(com.picsauditing.jpa.entities.User.class);

		Field userName;
		userName = addField(prefix + "Name", alias + ".name", FilterType.AccountName);
		userName.setUrl("UsersManage.action?user={" + prefix + "ID}\">{" + prefix + "Name}");
		userName.setWidth(300);
	}

	public void addJoins() {
		addJoin(new AccountTable(prefix + "Account", alias + ".accountID"));
	}
}