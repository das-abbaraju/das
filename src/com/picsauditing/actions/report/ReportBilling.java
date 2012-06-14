package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportBilling extends ReportAccount {

	public ReportBilling() {
		orderByDefault = "paymentExpires";
	}

	public void buildQuery() {
		super.buildQuery();
		sql.addField("c.membershipDate");
		sql.addField("c.paymentExpires");

		sql.addJoin("JOIN contractor_fee cf ON c.id = cf.conID");
		sql.addJoin("JOIN invoice_fee f ON cf.newLevel = f.id");

		sql.addField("GROUP_CONCAT(f.fee ORDER BY f.displayOrder SEPARATOR ', ') as fees");
		sql.addField("SUM(cf.currentAmount) as oldAmount");
		sql.addField("SUM(cf.newAmount) as newAmount");
		sql.addField("c.ccOnFile");
		sql.addField("c.lastUpgradeDate");

		sql.addWhere("c.mustPay = 'Yes' AND c.renew = 1");

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
			where += "(a.status IN ('Pending','Deactivated')) AND c.membershipDate IS NULL AND c.accountLevel = 'Full' AND NOT (cf.feeClass = 'ListOnly' AND cf.newAmount > 0)";
		}
		// Show renewals (only on non-bid only accounts)
		if (billingState.equals("All") || billingState.equals("Renewals")) {
			sql.addWhere("");
			if (where.length() > 0)
				where += " OR ";
			where += "(a.status IN ('Active', 'Deactivated') AND cf.newAmount > 0 AND c.paymentExpires < ADDDATE(NOW(), INTERVAL 30 DAY))";
		}
		// Show upgrades
		if (billingState.equals("All") || billingState.equals("Upgrades")) {
			// A note about non-renewal upgrades: just because a contractor
			// doesn't want to renew
			// at the end of the year, doesn't mean they shouldn't be charged an
			// upgrade fee.
			// However, we (the billing department) should be vigilant about
			// reviewing upgrades
			// on non-renewable contractor accounts.
			if (where.length() > 0)
				where += " OR ";
			where += "(a.status = 'Active' AND cf.newAmount > cf.currentAmount)";
		}

		sql.addField("CASE " + "WHEN a.status = 'Pending' THEN 'Activations' "
				+ "WHEN c.paymentExpires < ADDDATE(NOW(), INTERVAL 45 DAY) THEN 'Renewals' "
				+ "WHEN cf.newAmount > cf.currentAmount THEN 'Upgrades' ELSE 'Other' END billingStatus");
		sql.addWhere(where);
		sql.addWhere("cf.feeClass != CASE WHEN c.paymentExpires < ADDDATE(NOW(), INTERVAL 45 DAY) THEN 'ImportFee' ELSE '' END");
		sql.addWhere("f.maxFacilities > 0");
		sql.addGroupBy("a.id HAVING SUM(cf.newAmount) > 0");
	}

	@Override
	protected void checkPermissions() throws Exception {
		tryPermissions(OpPerms.Billing);
	}
}
