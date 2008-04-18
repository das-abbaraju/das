package com.picsauditing.actions.report;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectAccount;

public class ReportAccountAudits extends ReportAccount {

	public String execute() throws Exception {
		sql.addAudit(AuditType.PQF);
		sql.addAudit(AuditType.DESKTOP);
		sql.addAudit(AuditType.OFFICE);
		sql.addField("ca" + AuditType.PQF + ".percentVerified AS ca" + AuditType.PQF + "_percentVerified");
		sql.addField("ca" + AuditType.DESKTOP + ".percentVerified AS ca" + AuditType.DESKTOP + "_percentVerified");
		sql.addField("ca" + AuditType.OFFICE + ".percentVerified AS ca" + AuditType.OFFICE + "_percentVerified");
		sql.addField("c.main_trade");
		sql.addField("a.industry");
		sql.addField("c.certs");

		return super.execute();
	}
}
