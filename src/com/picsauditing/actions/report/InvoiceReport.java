package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;



@SuppressWarnings("serial")
public class InvoiceReport extends ReportContractorInvoice {
	
	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("i.paid = 0");
		sql.addWhere("c.renew = 1");
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
