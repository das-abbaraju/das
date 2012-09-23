package com.picsauditing.report.tables;

public class InvoiceCommissionTable extends AbstractTable {

	public static final String Invoice = "Invoice";
	public static final String User = "User";

	public InvoiceCommissionTable() {
		super("invoice_commission");
		// , "invoiceCommission", "invcom", parentAlias +
		// ".id = invcom.invoiceID"
		addFields(com.picsauditing.jpa.entities.InvoiceCommission.class);
	}

	protected void addJoins() {
		addKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("invoiceID")));
		addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("userID"))).setMinimumImportance(
				FieldImportance.Required);
		// FieldCategory.Commission
	}
}