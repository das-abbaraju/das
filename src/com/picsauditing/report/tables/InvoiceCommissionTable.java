package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.InvoiceCommission;

public class InvoiceCommissionTable extends AbstractTable {

	public static final String Invoice = "Invoice";
	public static final String AccountUser = "AccountUser";

	public InvoiceCommissionTable() {
		super("invoice_commission");
		addFields(InvoiceCommission.class);
	}

	protected void addJoins() {
		addRequiredKey(new ReportForeignKey(Invoice, new InvoiceTable(), new ReportOnClause("invoiceID")));
		addRequiredKey(new ReportForeignKey(AccountUser, new AccountUserTable(), new ReportOnClause("accountUserID")));
		
//		ReportForeignKey userKey = new ReportForeignKey(AccountUser, new UserTable(), new ReportOnClause("userID"));
//		userKey.setCategory(FieldCategory.Commission);
//		addOptionalKey(userKey).setMinimumImportance(FieldImportance.Required);
	}
}