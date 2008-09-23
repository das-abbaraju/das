package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

public class EmailSelector extends ReportContractorAudits {
	public String execute() throws Exception {
		if(!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.EmailTemplates);

		if (filtered == null)
			filtered = false;

		this.filterAuditType = false;

		return super.execute();
	}
}
