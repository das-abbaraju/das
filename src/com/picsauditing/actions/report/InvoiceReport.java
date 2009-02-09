package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;


@SuppressWarnings("serial")
public class InvoiceReport extends ReportAccount {
	
	public InvoiceReport() {
		orderByDefault = "paymentExpires";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("paid = 0");
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
