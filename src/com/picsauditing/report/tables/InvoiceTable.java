package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Invoice;

public class InvoiceTable extends AbstractTable {

	public static final String Account = "Account";

	public InvoiceTable() {
		super("invoice");
		addFields(Invoice.class);

		// addField(prefix + "Currency", alias + ".currency", FilterType.String,
		// FieldCategory.Invoicing);
		// TODO Move this to Models
		// ReportOnClause.FromAlias + ".tableType = 'I'
		// super("invoice", "payment", "p", "i.tableType = 'P'");
	}

	protected void addJoins() {
		addKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID")));
	}
}
