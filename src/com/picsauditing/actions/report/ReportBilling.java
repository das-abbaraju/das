package com.picsauditing.actions.report;


@SuppressWarnings("serial")
public class ReportBilling extends ReportAccount {
	
	public ReportBilling() {
		orderByDefault = "paymentExpires";
	}
	
	public void buildQuery() {
		super.buildQuery();
		sql.addField("c.membershipDate");
		sql.addField("c.paymentExpires");

		sql.addJoin("LEFT JOIN invoice_fee f1 ON c.membershipLevelID = f1.id");
		sql.addJoin("LEFT JOIN invoice_fee f2 ON c.newMembershipLevelID = f2.id");
		
		sql.addField("f2.fee");
		sql.addField("f2.defaultAmount");
		sql.addField("a.creationDate");
		sql.addField("c.lastUpgradeDate");
		
		sql.addWhere("c.mustPay = 'Yes'");
		
		getFilter().setShowBillingState(true);
		String billingState = "All";
		if (getFilter().getBillingState() != null)
			billingState = getFilter().getBillingState();
		

		String where = "";
		// Show activations and reactivations
		if (billingState.equals("All") || billingState.equals("Activations")) {
			where += "(a.active = 'N' AND c.renew = 1)";
		}
		// Show renewals
		if (billingState.equals("All") || billingState.equals("Renewals")) {
			if (where.length() > 0) where += " OR ";
			where += "(a.active = 'Y' AND c.renew = 1 AND c.paymentExpires < ADDDATE(NOW(), INTERVAL 1 MONTH))";
		}
		// Show upgrades
		if (billingState.equals("All") || billingState.equals("Upgrades")) {
			// A note about non-renewal upgrades: just because a contractor doesn't want to renew
			// at the end of the year, doesn't mean they shouldn't be charged an upgrade fee.
			// However, we (the billing department) should be vigilent about reviewing upgrades 
			// on non-renewable contractor accounts.
			if (where.length() > 0) where += " OR ";
			where += "(a.active = 'Y' AND f2.defaultAmount > f1.defaultAmount)";
		}
		
		sql.addField("CASE " +
				"WHEN a.active = 'N' THEN 'Activations' " +
				"WHEN f2.defaultAmount > f1.defaultAmount THEN 'Upgrades' " +
				"WHEN c.paymentExpires < ADDDATE(NOW(), INTERVAL 1 MONTH) THEN 'Renewals' " +
				"ELSE 'Other' END billingStatus");
		sql.addWhere(where);
		
	}
}
