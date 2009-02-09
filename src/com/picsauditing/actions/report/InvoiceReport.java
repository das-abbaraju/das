package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;


@SuppressWarnings("serial")
public class InvoiceReport extends ReportAccount {
	
	public InvoiceReport() {
		orderByDefault = "i.dueDate";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("i.id as invoiceId");
		sql.addField("i.totalAmount");
		sql.addField("i.dueDate");
		
		sql.addJoin("JOIN invoice i ON i.accountID = c.id");
		
		sql.addWhere("i.paid = 0");
		sql.addWhere("c.renew = 1");
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
