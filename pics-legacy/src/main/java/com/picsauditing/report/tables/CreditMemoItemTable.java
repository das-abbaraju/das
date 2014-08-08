package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceItem;
import com.picsauditing.jpa.entities.TransactionItem;

public class CreditMemoItemTable extends AbstractTable {

    public static final String CreditMemo = "CreditMemo";
	public static final String Fee = "Fee";

	public CreditMemoItemTable() {
		super("invoice_item");
		addFields(InvoiceItem.class);
		addFields(TransactionItem.class);
	}

	protected void addJoins() {
        addRequiredKey(new ReportForeignKey(CreditMemo, new CreditMemoTable(),
                new ReportOnClause("invoiceID","id"))).setMinimumImportance(FieldImportance.Average);
		addRequiredKey(new ReportForeignKey(Fee, new InvoiceFeeTable(),
				new ReportOnClause("feeID","id"))).setMinimumImportance(FieldImportance.Average);
	}
}
