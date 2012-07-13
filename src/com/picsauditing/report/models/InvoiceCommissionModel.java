package com.picsauditing.report.models;

import com.picsauditing.report.tables.InvoiceCommissionTable;

public class InvoiceCommissionModel extends InvoiceModel {
	public InvoiceCommissionModel() {
		super();
		
		InvoiceCommissionTable invoiceCommissionTable = new InvoiceCommissionTable(parentTable.getPrefix(), parentTable.getAlias());
		rootTable.addAllFieldsAndJoins(invoiceCommissionTable);

		parentTable = invoiceCommissionTable;
	}
}