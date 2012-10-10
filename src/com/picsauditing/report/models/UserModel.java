package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.UserTable;

public class UserModel extends AbstractModel {
	public UserModel(Permissions permissions) {
		super(permissions, new UserTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "User");
		spec.join(UserTable.Account);
		return spec;
	}
}