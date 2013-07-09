package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class InvoiceTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String Item = "Item";

	public InvoiceTable() {
		super("invoice");
		addFields(Invoice.class);
		addCurrency();

		Field invoiceStatus = new Field("Status", "status", FieldType.TransactionStatus);
		invoiceStatus.setCategory(FieldCategory.Invoicing);
		invoiceStatus.setWidth(100);
        invoiceStatus.setImportance(FieldImportance.Average);
		addField(invoiceStatus);

		Field invoiceTotalAmount = new Field("TotalAmount", "totalAmount", FieldType.Float);
		invoiceTotalAmount.setCategory(FieldCategory.Invoicing);
		invoiceTotalAmount.setWidth(100);
        invoiceTotalAmount.setImportance(FieldImportance.Average);
		addField(invoiceTotalAmount);

		Field invoiceAmountApplied = new Field("AmountApplied", "amountApplied", FieldType.Float);
		invoiceAmountApplied.setCategory(FieldCategory.Invoicing);
		invoiceAmountApplied.setWidth(100);
        invoiceAmountApplied.setImportance(FieldImportance.Average);
		addField(invoiceAmountApplied);
	}

	private void addCurrency() {
		Field currency = new Field("Currency", "currency", FieldType.String);
		currency.setCategory(FieldCategory.Invoicing);
        currency.setImportance(FieldImportance.Average);
		addField(currency);

		Field invoiceID = new Field("InvoiceID", "id", FieldType.Number);
		invoiceID.setCategory(FieldCategory.Invoicing);
        invoiceID.setImportance(FieldImportance.Average);
		addField(invoiceID);
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
