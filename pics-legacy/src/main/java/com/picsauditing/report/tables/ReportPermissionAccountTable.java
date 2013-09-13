package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ReportPermissionAccount;

public class ReportPermissionAccountTable extends AbstractTable {
	public static final String Report = "Report";
	public static final String Account = "Account";

	public ReportPermissionAccountTable() {
		super("report_permission_account");
		addFields(ReportPermissionAccount.class);
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID")));
		addOptionalKey(new ReportForeignKey(Report, new ReportTable(), new ReportOnClause("reportID")));
	}
}