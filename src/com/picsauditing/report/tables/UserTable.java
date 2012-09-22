package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.FilterType;

public class UserTable extends ReportTable {
	public static final String Account = "Account";

	public UserTable() {
		super("users");
		addPrimaryKey(FilterType.UserID);
		addFields(User.class);
		addOptionalKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID")));
	}
}