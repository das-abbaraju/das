package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.UserTable

public class UserModel extends AbstractModel {
	static def joinSpec = [
		alias: "User",
		joins: [
			[
				key: UserTable.Account
			]
		]
	]

	public Map getJoinSpec() {
		return joinSpec;
	}

	public UserModel(Permissions permissions) {
		super(permissions, new UserTable())
	}
}
