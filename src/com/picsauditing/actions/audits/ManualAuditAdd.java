package com.picsauditing.actions.audits;

import com.picsauditing.actions.report.ReportContractorAudits;

public class ManualAuditAdd extends ReportContractorAudits {

	public ManualAuditAdd() {
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		return SUCCESS;
	}
}
