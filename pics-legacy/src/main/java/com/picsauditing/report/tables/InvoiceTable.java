package com.picsauditing.report.tables;

import com.picsauditing.PICS.TaxService;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Transaction;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.util.Strings;

public class InvoiceTable extends AbstractTable {

	public static final String Account = "Account";
	public static final String Item = "Item";

	public InvoiceTable() {
		super("invoice");
		addFields(Invoice.class);
        addFields(Transaction.class);
        addPrimaryKey();
        addCreationDate();

        Field tax = new Field("Tax", "IFNULL((SELECT ii.amount FROM invoice_item ii JOIN invoice_fee fee ON ii.feeID = fee.id AND fee.feeClass IN (" +
                Strings.implodeForDB(TaxService.TAX_FEE_CLASSES) +
                ") WHERE ii.invoiceID = " + ReportOnClause.ToAlias + ".id),0)", FieldType.Float);
        tax.setImportance(FieldImportance.Required);
        addField(tax);

        Field taxlessTotalAmount = new Field("TaxlessTotalAmount", "(" +
                ReportOnClause.ToAlias +
                ".totalAmount - IFNULL((SELECT ii.amount FROM invoice_item ii JOIN invoice_fee fee ON ii.feeID = fee.id AND fee.feeClass IN (" +
                Strings.implodeForDB(TaxService.TAX_FEE_CLASSES) +
                ") WHERE ii.invoiceID = " +
                ReportOnClause.ToAlias +
                ".id),0))", FieldType.Float);
        taxlessTotalAmount.setImportance(FieldImportance.Required);
        addField(taxlessTotalAmount);
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
