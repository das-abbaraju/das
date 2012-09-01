package com.picsauditing.report.models;

import com.picsauditing.report.tables.InvoiceCommissionTable;

public class InvoiceCommissionModel extends InvoiceModel {
	public InvoiceCommissionModel() {
		super();
		
		InvoiceCommissionTable invoiceCommissionTable = new InvoiceCommissionTable(parentTable.getPrefix(), parentTable.getAlias());
		invoiceCommissionTable.includeAllColumns();
		rootTable.addAllFieldsAndJoins(invoiceCommissionTable);

		parentTable = invoiceCommissionTable;
		
		rootTable.getTable("account").includeRequiredAndAverageColumns();
		rootTable.removeJoin("accountContact");
		rootTable.getTable("contractor").includeRequiredAndAverageColumns();
		rootTable.removeJoin("contractorCustomerService");
	}
}