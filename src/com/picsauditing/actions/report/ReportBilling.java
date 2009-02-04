package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportBilling extends ReportAccount {
	
	public ReportBilling() {
		orderByDefault = "paymentExpires";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("c.membershipDate");
		sql.addField("f2.fee");
		sql.addJoin("LEFT JOIN invoice_fee f1 ON c.membershipLevelID = f1.id");
		sql.addJoin("LEFT JOIN invoice_fee f2 ON c.newMembershipLevelID = f2.id");
		
		sql.addWhere("c.mustPay = 'Yes'");
		
		// Show activations and reactivations
		String where = "(a.active = 'N' AND c.renew = 1)";
		// Show renewals
		where += " OR (a.active = 'Y' AND c.renew = 1 AND c.paymentExpires < NOW())";
		// Show upgrades
		where += " OR (a.active = 'Y' AND f2.fee > f1.fee)";
		sql.addWhere(where);
		
		// A note about non-renewal upgrades: just because a contractor doesn't want to renew
		// at the end of the year, doesn't mean they shouldn't be charged an upgrade fee.
		// However, we (the billing department) should be vigilent about reviewing upgrades 
		// on non-renewable contractor accounts.
	}
}
