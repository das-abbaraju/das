package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class PaymentTable extends AbstractTable {

    public static final String Account = "Account";
    public static final String Item = "Item";

    public PaymentTable() {
		super("invoice");
		addFields(Payment.class);
        addFields(Transaction.class);
        addPrimaryKey();
        addCreationDate();
    }

    protected void addJoins() {
        ReportForeignKey accountJoin = new ReportForeignKey(Account, new AccountTable(),
                new ReportOnClause("accountID"));
        addRequiredKey(accountJoin);
        accountJoin.setMinimumImportance(FieldImportance.Average);
    }
}