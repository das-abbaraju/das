package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Payment;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class PaymentTable extends AbstractTable {

    public static final String Account = "Account";
    public static final String Item = "Item";

    public PaymentTable() {
		super("invoice");
		addFields(Payment.class);
        addCurrency();

        addCreationDate();

        Field invoiceStatus = new Field("Status", "status", FieldType.TransactionStatus);
        invoiceStatus.setWidth(100);
        invoiceStatus.setImportance(FieldImportance.Average);
        addField(invoiceStatus);

        Field invoiceTotalAmount = new Field("TotalAmount", "totalAmount", FieldType.Float);
        invoiceTotalAmount.setWidth(100);
        invoiceTotalAmount.setImportance(FieldImportance.Average);
        addField(invoiceTotalAmount);

        Field invoiceAmountApplied = new Field("AmountApplied", "amountApplied", FieldType.Float);
        invoiceAmountApplied.setWidth(100);
        invoiceAmountApplied.setImportance(FieldImportance.Average);
        addField(invoiceAmountApplied);
    }

    private void addCurrency() {
        Field currency = new Field("Currency", "currency", FieldType.String);
        currency.setImportance(FieldImportance.Average);
        addField(currency);

        Field invoiceID = new Field("id", "id", FieldType.Number);
        invoiceID.setImportance(FieldImportance.Average);
        addField(invoiceID);
    }

    protected void addJoins() {
        ReportForeignKey accountJoin = new ReportForeignKey(Account, new AccountTable(),
                new ReportOnClause("accountID"));
        addRequiredKey(accountJoin);
        accountJoin.setMinimumImportance(FieldImportance.Average);
    }
}