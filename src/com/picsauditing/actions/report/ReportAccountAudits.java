package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.Report;
import com.picsauditing.search.SelectAccount;

public class ReportAccountAudits extends ReportAccount {

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addAudit(AuditType.PQF);
		sql.addField("c.main_trade");
		sql.addField("a.industry");
		sql.addField("c.certs");

		toggleFilters();
		
		if(filtered == null) 
			filtered = true;
		
		return super.execute();
	}

	public boolean isPqfVisible() {
		return permissions.canSeeAudit(AuditType.PQF);
	}

	protected void toggleFilters() {
		if (permissions.hasPermission(OpPerms.StatusOnly)) {
			filterOperator = false;
			filterAddress = false;
			filterIndustry = false;
			filterCerts = false;
			filterVisible = false;
			filterAuditor = false;
			filterTaxID = false;
			filterLicensedIn = false;
			filterWorksIn = false;
		}
	}

}
