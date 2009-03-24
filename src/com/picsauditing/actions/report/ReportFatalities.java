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
		sql.addJoin("JOIN osha_audit os ON os.auditID = ca.id");
		sql.addWhere("os.fatalities > 0");
		sql.addField("os.fatalities");
		
		getFilter().setPendingPqfAnnualUpdate(false);
	}
}
