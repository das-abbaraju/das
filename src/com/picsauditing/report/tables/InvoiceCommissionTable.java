package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceCommission;

public class InvoiceCommissionTable extends AbstractTable {

	public static final String Invoice = "Invoice";
	public static final String User = "User";

	public InvoiceCommissionTable() {
		super("invoice_commission");
		addFields(InvoiceCommission.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("invoiceID")));

		ReportForeignKey userKey = new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID"));
		userKey.setCategory(FieldCategory.Commission);
		addOptionalKey(userKey).setMinimumImportance(FieldImportance.Required);
	}
}