package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.UserLoginLog;

public class UserLoginLogTable extends AbstractTable {

	public UserLoginLogTable() {
		super("loginlog");
		addFields(UserLoginLog.class);
	}

	protected void addJoins() {
	}
}