package com.picsauditing.report.tables;

public class PaymentTable extends AbstractTable {
	public static final String Account = "Account";

	public PaymentTable() {
		super("invoice");
		// super("invoice", "payment", "p", "i.tableType = 'P'");
		addFields(com.picsauditing.jpa.entities.Payment.class);
		// addField(prefix + "CreationDate", alias + ".creationDate",
		// FilterType.Date);
	}

	public void addJoins() {
		addKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID")));
	}
}
