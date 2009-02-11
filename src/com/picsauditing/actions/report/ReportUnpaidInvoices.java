package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;



@SuppressWarnings("serial")
public class ReportUnpaidInvoices extends ReportContractorInvoice {
	
	public void buildQuery() {
		super.buildQuery();
		sql.addWhere("i.paid = 0");
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
