package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.TransactionItem;

public class InvoiceItemTable extends AbstractTable {

    public static final String Invoice = "Invoice";
	public static final String Fee = "Fee";

	public InvoiceItemTable() {
		super("invoice_item");
		addFields(InvoiceItem.class);
		addFields(TransactionItem.class);
	}

	protected void addJoins() {
        addRequiredKey(new ReportForeignKey(Invoice, new InvoiceTable(),
                new ReportOnClause("invoiceID","id"))).setMinimumImportance(FieldImportance.Average);
		addRequiredKey(new ReportForeignKey(Fee, new InvoiceFeeTable(),
				new ReportOnClause("feeID","id"))).setMinimumImportance(FieldImportance.Average);
	}
}
