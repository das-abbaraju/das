package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.InvoiceFee;


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
		sql.addField("ROUND(f1.defaultAmount) as oldAmount");
		sql.addField("ROUND(f2.defaultAmount) as newAmount");
		sql.addField("c.ccOnFile");
		sql.addField("c.lastUpgradeDate");
		
		sql.addWhere("c.mustPay = 'Yes'");
		
		getFilter().setShowBillingState(true);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
		
		String billingState = "All";
		if (getFilter().getBillingState() != null)
			billingState = getFilter().getBillingState();
		
		String where = "";
		// Show activations and reactivations
		if (billingState.equals("All") || billingState.equals("Activations")) {
			where += "(a.status IN ('Pending','Deactivated') AND (c.renew = 1 OR (c.renew = 0 AND a.acceptsBids = 1)) AND (c.membershipLevelID IS NULL OR c.membershipLevelID = " + InvoiceFee.FREE + "))";
		}
		// Show renewals
		if (billingState.equals("All") || billingState.equals("Renewals")) {
			sql.addWhere("");
			if (where.length() > 0) where += " OR ";
			where += "(a.status = 'Active' AND c.renew = 1 AND f2.defaultAmount > 0 AND c.paymentExpires < ADDDATE(NOW(), INTERVAL 30 DAY))";
		}
		// Show upgrades
		if (billingState.equals("All") || billingState.equals("Upgrades")) {
			// A note about non-renewal upgrades: just because a contractor doesn't want to renew
			// at the end of the year, doesn't mean they shouldn't be charged an upgrade fee.
			// However, we (the billing department) should be vigilant about reviewing upgrades 
			// on non-renewable contractor accounts.
			if (where.length() > 0) where += " OR ";
			where += "(a.status = 'Active' AND f2.defaultAmount > f1.defaultAmount)";
		}
		
		sql.addField("CASE " +
				"WHEN a.status = 'Pending' THEN 'Activations' " +
				"WHEN c.paymentExpires < ADDDATE(NOW(), INTERVAL 45 DAY) THEN 'Renewals' " +
				"WHEN f2.defaultAmount > f1.defaultAmount THEN 'Upgrades' " +
				"ELSE 'Other' END billingStatus");
		sql.addWhere(where);
		
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
