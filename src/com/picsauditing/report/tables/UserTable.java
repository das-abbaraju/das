package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.FilterType;

public class UserTable extends AbstractTable {
	public static final String Account = "Account";

	public UserTable() {
		super("users");
		addPrimaryKey(FilterType.UserID);
		addFields(User.class);
	}

	protected void addJoins() {
		ReportForeignKey accountKey = new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID"));
		accountKey.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(accountKey);
	}
}