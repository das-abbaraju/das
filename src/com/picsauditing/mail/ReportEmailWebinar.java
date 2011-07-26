package com.picsauditing.mail;

import com.picsauditing.actions.report.ReportAccount;

@SuppressWarnings("serial")
public class ReportEmailWebinar extends ReportAccount {

	public void prepare() throws Exception {
		super.prepare();

		getFilter().setShowRegistrationDate(true);
		getFilter().setAllowMailMerge(true);
		getFilter().setShowState(true);
		
		getFilter().setShowLicensedIn(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowTrade(false);
		getFilter().setShowRiskLevel(false);
		getFilter().setShowProductRiskLevel(false);
		getFilter().setShowService(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowSoleProprietership(false);
	}

	public void buildQuery() {
		super.buildQuery();

		sql.addField("c.score");
		sql.addField("a.dbaName");
		sql.addWhere("a.status = 'Active'");

		filteredDefault = true;
	}
}