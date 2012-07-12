package com.picsauditing.report.models;

import com.picsauditing.report.tables.InvoiceCommissionTable;
import com.picsauditing.report.tables.PaymentCommissionTable;

public class PaymentCommissionModel extends InvoiceModel {
	public PaymentCommissionModel() {
		super();
		
		InvoiceCommissionTable invoiceCommissionTable = new InvoiceCommissionTable(parentTable.getPrefix(), parentTable.getAlias());
		primaryTable.addAllFieldsAndJoins(invoiceCommissionTable);
		// TODO: Find a better way of passing down the parent table
		parentTable = invoiceCommissionTable;
		
		PaymentCommissionTable paymentCommissionTable = new PaymentCommissionTable(parentTable.getPrefix(), parentTable.getAlias());
		primaryTable.addAllFieldsAndJoins(paymentCommissionTable);
	}
}