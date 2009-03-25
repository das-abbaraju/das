package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;



@SuppressWarnings("serial")
public class ReportUnpaidInvoices extends ReportContractorInvoice {
	
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		sql.addWhere("i.paid = 0");
		sql.addWhere("i.totalAmount > 0");
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
