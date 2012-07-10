package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class User extends BaseTable {

	public User(String prefix, String alias, String foreignKey) {
		super("users", prefix, alias, alias + ".id = " + foreignKey);
	}

	public User(String alias, String foreignKey) {
		super("users", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer);
		addField(prefix + "Name", alias + ".name", FilterType.String).addRenderer("UsersManage.action?user={0}\">{1}",
				new String[] { prefix + "ID", prefix + "Name" });
		addField(prefix + "Phone", alias + ".phone", FilterType.String);
		addField(prefix + "Email", alias + ".email", FilterType.String);
	}

	public void addJoins() {
		addJoin(new Account(prefix + "Account", alias + ".accountID"));
	}
}