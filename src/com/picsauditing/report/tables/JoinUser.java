package com.picsauditing.report.tables;

import com.picsauditing.report.fieldtypes.FilterType;

public class JoinUser extends BaseTable {
	public JoinUser(String alias, String foreignKey) {
		super("users", alias, alias + ".id = " + foreignKey);
		setLeftJoin();
	}

	protected void addDefaultFields() {
		addField(alias + "UserID", alias + ".id", FilterType.Number);
		addField(alias + "Name", alias + ".name", FilterType.String).addRenderer(
				"UsersManage.action?user={0}\">{1}", new String[] { alias + "UserID", alias + "Name" });
	}

	public void addFields() {
		addField(alias + "Phone", alias + ".phone", FilterType.String);
		addField(alias + "Email", alias + ".email", FilterType.String);
	}

	public void addJoins() {
		addJoin(new Account(alias + "Account", alias + ".accountID"));
	}

}
