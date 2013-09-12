package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AccountUser;

public class AccountUserTable extends AbstractTable {
	
	public static final String User = "User";
	public static final String Account = "Account";
	
	public AccountUserTable() {
		super("account_user");
		addFields(AccountUser.class);
	}
	
	@Override
	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID")));
		addRequiredKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID")));
	}

}
