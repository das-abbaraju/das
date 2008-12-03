package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;

@SuppressWarnings("serial")
public class ReportFatalities extends ReportAnnualAddendum {
	
	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.FatalitiesReport);
	}
	
	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.auditID");
		sql.addWhere("os.fatalities > 0");
		sql.addField("os.fatalities");
	}

	public String execute() throws Exception {
		return super.execute2();
	}
}
