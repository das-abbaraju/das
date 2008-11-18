package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

public class ReportAuditDataUpdate extends ReportContractorAudits {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.DevelopmentEnvironment);
		sql.addField("pq.updateDate");
		sql.addField("pq.answer");
		sql.addField("p.question");
		sql.addJoin("JOIN pqfdata pq on pq.auditID = ca.auditID");
		sql.addJoin("JOIN pqfquestions p on p.questionID = pq.questionID");
		sql.addWhere("pq.updateDate > ca.completedDate");
		if (orderBy == null) {
			orderBy = "pq.updateDate DESC";
		}

		if (filtered == null)
			filtered = false;
		return super.execute();
	}
}
