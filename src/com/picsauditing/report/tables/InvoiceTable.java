package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class InvoiceTable extends AbstractTable {

	public static final String Account = "Account";

	public InvoiceTable() {
		super("invoice");
		addFields(Invoice.class);

		Field currency = new Field("Currency", "currency", FilterType.String);
		currency.setCategory(FieldCategory.Invoicing);
		addField(currency);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Account, new AccountTable(), new ReportOnClause("accountID")));
	}
}
