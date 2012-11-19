package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.OperatorAccount;

public class OperatorTable extends AbstractTable {
	public static final String Account = "Account";
	public static final String Reporting = "Reporting";

	public OperatorTable() {
		super("operators");
		addFields(OperatorAccount.class);
	}

	public void addJoins() {
		addRequiredKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("id", "id",
				ReportOnClause.ToAlias + ".type IN ('Operator','Corporate')")));

		addOptionalKey(new ReportForeignKey(Reporting, new AccountTable(), new ReportOnClause("reportingID", "id")));
	}
}
