package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceCommission;

public class InvoiceCommissionTable extends AbstractTable {

	public static final String Invoice = "Invoice";
	public static final String AccountUser = "AccountUser";

	public InvoiceCommissionTable() {
		super("invoice_commission");
		addFields(InvoiceCommission.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("invoiceID"))).setMinimumImportance(FieldImportance.Average);
		addRequiredKey(new ReportForeignKey(AccountUser, new AccountUserTable(), new ReportOnClause("accountUserID"))).setMinimumImportance(FieldImportance.Average);
	}
}