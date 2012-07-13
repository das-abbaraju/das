package com.picsauditing.report.models;

import com.picsauditing.report.tables.PaymentCommissionTable;

public class PaymentCommissionModel extends InvoiceCommissionModel {
	public PaymentCommissionModel() {
		super();
		
		PaymentCommissionTable paymentCommissionTable = new PaymentCommissionTable(parentTable.getPrefix(), parentTable.getAlias());
		rootTable.addAllFieldsAndJoins(paymentCommissionTable);

		parentTable = paymentCommissionTable;
	}
}