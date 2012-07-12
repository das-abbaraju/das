package com.picsauditing.report.models;

import com.picsauditing.report.tables.InvoiceTable;

public class InvoiceModel extends AccountContractorModel {
	public InvoiceModel() {
		super();

		InvoiceTable invoiceTable = new InvoiceTable(parentTable.getPrefix(), parentTable.getAlias());
		primaryTable.addAllFieldsAndJoins(invoiceTable);
		// TODO: Find a better way of passing down the parent table
		parentTable = invoiceTable;
	}
}
