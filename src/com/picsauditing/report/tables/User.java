package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.Field;

public class User extends BaseReportTable {

	public User(String prefix, String alias, String foreignKey) {
		super("users", prefix, alias, alias + ".id = " + foreignKey);
	}

	public User(String alias, String foreignKey) {
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
		addJoin(new Account(prefix + "Account", alias + ".accountID"));
	}
}