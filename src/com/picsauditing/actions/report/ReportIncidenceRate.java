package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportIncidenceRate extends ReportAnnualAddendum {

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.FatalitiesReport);
	}

	@Override
	public void buildQuery() {
		super.buildQuery();

		getFilter().setShowIncidenceRate(true);
		getFilter().setShowAuditFor(true);

		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addWhere("(os.recordableTotal*200000/os.manHours > " + getFilter().getIncidenceRate() + ")");
		sql.addField("os.location");
		sql.addField("os.description");
		sql.addField("os.SHAType");
		sql.addField("os.recordableTotal");
		sql.addField("os.manHours");
	}
}
