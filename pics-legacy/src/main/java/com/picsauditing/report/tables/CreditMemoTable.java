package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceCreditMemo;
import com.picsauditing.jpa.entities.Transaction;

public class CreditMemoTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String Item = "Item";

	public CreditMemoTable() {
		super("invoice");
		addFields(InvoiceCreditMemo.class);
        addFields(Transaction.class);
        addPrimaryKey();
        addCreationDate();
    }

	protected void addJoins() {
		ReportForeignKey accountJoin = new ReportForeignKey(Account, new AccountTable(),
				new ReportOnClause("accountID"));
		addRequiredKey(accountJoin);
		accountJoin.setMinimumImportance(FieldImportance.Average);

		addRequiredKey(new ReportForeignKey(Item, new InvoiceItemTable(), new ReportOnClause("id", "invoiceID")));
		accountJoin.setMinimumImportance(FieldImportance.Average);
	}
}
