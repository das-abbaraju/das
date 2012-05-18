package com.picsauditing.actions.report;

import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAnnualAddendum extends ReportContractorAuditOperator {

	@Override
	protected void buildQuery() {
		super.buildQuery();
		sql.addWhere("ca.auditTypeID = 11");

		sql.addGroupBy("c.id");

		String auditFor = Strings.implodeForDB(getFilter().getAuditFor(), ",");
		if (!Strings.isEmpty(auditFor))
			sql.addWhere("ca.auditFor IN (" + auditFor + ")");

		getFilter().setShowVerifiedAnnualUpdates(true);

		getFilter().setShowTaxID(false);
		getFilter().setShowWaitingOn(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditor(false);
		getFilter().setShowLocation(false);
		getFilter().setShowCreatedDate(false);
		getFilter().setPendingPqfAnnualUpdate(false);

	}

	public void setVerifiedAnnualUpdateFilter(String columnName) {
		if (getFilter().getVerifiedAnnualUpdate() > 0) {
			if (getFilter().getVerifiedAnnualUpdate() == 1) {
				sql.addWhere(columnName + " IS NOT NULL");
			} else
				sql.addWhere(columnName + " IS NULL");
		}
	}
}
