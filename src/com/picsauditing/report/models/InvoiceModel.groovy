package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.InvoiceTable

public class InvoiceModel extends AbstractModel {
	public InvoiceModel(Permissions permissions) {
		super(permissions, new InvoiceTable())

		InvoiceTable invoiceTable = new InvoiceTable(parentTable.getPrefix(), parentTable.getAlias());
		invoiceTable.includeAllColumns();
		rootTable.addAllFieldsAndJoins(invoiceTable);

		parentTable = invoiceTable;

		rootTable.removeJoin("contractorOperator");
		rootTable.removeJoin("accountNaics");
		rootTable.removeJoin("contractorPQF");
		rootTable.getTable("contractorCustomerService").includeOnlyRequiredColumns();
	}
}
