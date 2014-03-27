package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceOperatorCommission;

public class InvoiceOperatorCommissionTable extends AbstractTable {

	public static final String Invoice = "Invoice";
	public static final String Account = "Account";

	public InvoiceOperatorCommissionTable() {
		super("invoice_operator_commission");
		addFields(InvoiceOperatorCommission.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("invoiceID"))).setMinimumImportance(FieldImportance.Average);
		addRequiredKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("opID")));
	}
}