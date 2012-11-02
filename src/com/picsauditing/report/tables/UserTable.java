package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class UserTable extends AbstractTable {
	public static final String Account = "Account";

	public UserTable() {
		super("users");
		addPrimaryKey(FieldType.UserID);
		addFields(User.class);
		
		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		addField(creationDate);

	}

	protected void addJoins() {
		ReportForeignKey accountKey = new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID"));
		accountKey.setMinimumImportance(FieldImportance.Average);
		addOptionalKey(accountKey);
	}
}