package com.picsauditing.report.models;

import com.picsauditing.report.tables.InvoiceTable;

public class InvoiceModel extends AccountContractorModel {
	public InvoiceModel() {
		super();

		InvoiceTable invoiceTable = new InvoiceTable(parentTable.getPrefix(), parentTable.getAlias());
		invoiceTable.includeAllColumns();
		rootTable.addAllFieldsAndJoins(invoiceTable);

		parentTable = invoiceTable;
		
		rootTable.removeJoin("accountNaics");
		rootTable.removeJoin("contractorPQF");
		rootTable.getTable("contractorCustomerService").includeOnlyRequiredColumns();
	}
}
