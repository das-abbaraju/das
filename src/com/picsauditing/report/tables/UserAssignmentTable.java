package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.UserAssignment;

public class UserAssignmentTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String User = "User";

	public UserAssignmentTable() {
		super("user_assignment");
		addFields(UserAssignment.class);
	}

	protected void addJoins() {
		ReportForeignKey csrKey = addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause(
				"userID", "id")));
		csrKey.setMinimumImportance(FieldImportance.Average);
		csrKey.setCategory(FieldCategory.CustomerService);

		addOptionalKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("conID")));
	}
}
