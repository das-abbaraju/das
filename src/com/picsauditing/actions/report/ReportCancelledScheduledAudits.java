package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportCancelledScheduledAudits extends ReportContractorAudits {
	public void prepare() throws Exception {
		super.prepare();

		getFilter().setAllowMailReport(false);
		getFilter().setShowPrimaryInformation(false);
		getFilter().setShowTradeInformation(false);
	}

	public void buildQuery() {
		super.buildQuery();

		sql.addWhere("scheduledDate > NOW()");
		sql.addWhere("NOT EXISTS (SELECT 'x' FROM contractor_audit_operator cao " +
									"WHERE ca.id = cao.auditID AND cao.visible = 1)");

		orderByDefault = "scheduledDate ASC";
		filteredDefault = true;
	}

}