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
		sql.addJoin("JOIN pqfdata pd on pd.auditID = ca.id");

		String oshaLog = "(os.SHAType = 'OSHA' && pd.QuestionID = 2064 && pd.answer = 'Yes')";
		String MshaLog = "(os.SHAType = 'MSHA' && pd.QuestionID = 2065 && pd.answer = 'Yes')";
		String CanadianLog = "(os.SHAType = 'COHS' && pd.QuestionID = 2066 && pd.answer = 'Yes')";
		sql.addWhere(oshaLog + " OR " + MshaLog +" OR " + CanadianLog);
		
		sql.addWhere("os.fatalities > 0");
		sql.addField("os.fatalities");
		sql.addField("os.SHAType");
		
		getFilter().setPendingPqfAnnualUpdate(false);
	}
}
