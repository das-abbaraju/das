package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class InvoiceTable extends AbstractTable {

	public static final String Account = "Account";

	public InvoiceTable() {
		super("invoice");
		addFields(Invoice.class);
		addCurrency();
	}

	private void addCurrency() {
		Field currency = new Field("Currency", "currency", FieldType.String);
		currency.setCategory(FieldCategory.Invoicing);
		addField(currency);
		
		Field invoiceID = new Field("InvoiceID", "id", FieldType.Number);
		invoiceID.setCategory(FieldCategory.Invoicing);
		addField(invoiceID);
	}

	protected void addJoins() {
		ReportForeignKey accountJoin = new ReportForeignKey(Account, new AccountTable(),
				new ReportOnClause("accountID"));
		addRequiredKey(accountJoin);
		accountJoin.setMinimumImportance(FieldImportance.Required);
	}
}
