package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class TransactionTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String Item = "Item";

	public TransactionTable() {
		super("invoice");
		addFields(Invoice.class);
        addFields(Transaction.class);
        addPrimaryKey();
        addCreationDate();

        Field transactionType = new Field("TransactionType", "tableType", FieldType.String);
        transactionType.setImportance(FieldImportance.Required);
        addField(transactionType);
    }

	protected void addJoins() {
		ReportForeignKey accountJoin = new ReportForeignKey(Account, new AccountTable(),
				new ReportOnClause("accountID"));
		addRequiredKey(accountJoin);
	}
}
