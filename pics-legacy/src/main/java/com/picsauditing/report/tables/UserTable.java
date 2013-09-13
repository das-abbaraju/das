package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class UserTable extends AbstractTable {
	public static final String Account = "Account";
	public static final String LoginLog = "LoginLog";
	public static final String Group = "Group";

	public UserTable() {
		super("users");
		addPrimaryKey(FieldType.UserID);
		addFields(User.class);

        addCreationDate().setImportance(FieldImportance.Low);
	}

	protected void addJoins() {
		ReportForeignKey accountKey = new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID"));
		accountKey.setMinimumImportance(FieldImportance.Average);
		addRequiredKey(accountKey);

		ReportForeignKey loginLog = new ReportForeignKey(LoginLog, new UserLoginLogTable(), new ReportOnClause("id","userID"));
		loginLog.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(loginLog);

		ReportForeignKey group = new ReportForeignKey(Group, new UserGroupTable(), new ReportOnClause("id","userID"));
		group.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(group);
	}
}