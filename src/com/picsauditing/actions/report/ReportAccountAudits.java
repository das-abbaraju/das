package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;

public class ReportAccountAudits extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addAudit(AuditType.PQF);
		sql.addField("c.main_trade");
		sql.addField("a.industry");

		toggleFilters();

		if (filtered == null)
			filtered = true;

		return super.execute();
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}

}
