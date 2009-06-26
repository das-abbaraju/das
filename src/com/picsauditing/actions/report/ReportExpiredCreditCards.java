package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportExpiredCreditCards extends ReportAccount {

	public ReportExpiredCreditCards() {
		orderByDefault = "c.paymentExpires";
	}

	public void buildQuery() {
		super.buildQuery();

		sql.addWhere("c.ccExpiration < NOW()");

		sql.addField("c.ccExpiration");
		sql.addField("c.paymentExpires");
	}

	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}

}
