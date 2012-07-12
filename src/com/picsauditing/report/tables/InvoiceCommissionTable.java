package com.picsauditing.report.tables;

public class InvoiceCommissionTable extends AbstractTable {

	public InvoiceCommissionTable(String parentPrefix, String parentAlias) {
		super("invoice_commission", "invoiceCommission", "invcom", parentAlias + ".id = invcom.invoiceID");
		this.parentPrefix = parentPrefix;
		this.parentAlias = parentAlias;
	}

	public void addFields() {
		addFields(com.picsauditing.jpa.entities.InvoiceCommission.class);
	}

	public void addJoins() { 
		addLeftJoin(new UserTable(prefix + "RecipientUser", alias + ".userID"));
	}
}