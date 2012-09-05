package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class InvoiceTable extends AbstractTable {

	public InvoiceTable(String parentPrefix, String parentAlias) {
		super("invoice", "invoice", "i", parentAlias + ".id = i.accountID AND i.tableType = 'I'");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	public void addFields() {
		addField(prefix + "Currency", alias + ".currency", FilterType.String, FieldCategory.Invoicing);

		addFields(com.picsauditing.jpa.entities.Invoice.class);
	}

	public void addJoins() {
	}
}
