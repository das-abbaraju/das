package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.InvoiceCommissionTable;
import com.picsauditing.report.tables.InvoiceTable;

public class InvoiceCommissionModel extends AbstractModel {
	
	public InvoiceCommissionModel(Permissions permissions) {
		super(permissions, new InvoiceCommissionTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "InvoiceCommission");
		ModelSpec invoice = joinToInvoice(spec);
		joinToAccount(invoice);
	
		return spec;
	}
	
	private ModelSpec joinToInvoice(ModelSpec spec) {
		return spec.join(InvoiceCommissionTable.Invoice);
	}
	
	private ModelSpec joinToAccount(ModelSpec invoice) {
		ModelSpec account = invoice.join(InvoiceTable.Account);
		account.join(AccountTable.Contractor);
		account.join(AccountTable.Contact);
		
		return account;
	}
	
}