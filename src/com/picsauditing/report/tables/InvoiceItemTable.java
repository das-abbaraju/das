package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceItem;

public class InvoiceItemTable extends AbstractTable {

	public static final String Fee = "Fee";

	public InvoiceItemTable() {
		super("invoice_item");
		addFields(InvoiceItem.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Fee, new InvoiceFeeTable(),
				new ReportOnClause("feeID","id")));
	}
}
