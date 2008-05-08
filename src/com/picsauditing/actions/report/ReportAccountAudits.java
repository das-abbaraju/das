package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectAccount;

public class ReportAccountAudits extends ReportAccount {

	public String execute() throws Exception {
		sql.addAudit(AuditType.PQF);
		sql.addAudit(AuditType.DESKTOP);
		sql.addAudit(AuditType.OFFICE);
		sql.addField("c.main_trade");
		sql.addField("a.industry");
		sql.addField("c.certs");

		return super.execute();
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}

	public boolean isDesktopVisible() {
		return permissions.canSeeAudit(AuditType.DESKTOP);
	}

	public boolean isOfficeVisible() {
		return permissions.canSeeAudit(AuditType.OFFICE);
	}

	public boolean isOperator() {
		return permissions.isOperator();
	}

	public boolean isCorporate() {
		return permissions.isCorporate();
	}
}
