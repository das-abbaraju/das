package com.picsauditing.actions.report;

import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAnnualAddendum extends ReportAccount {
	private ReportFilterAudit filter = new ReportFilterAudit();

	@Override
	public void buildQuery() {
		super.buildQuery();
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.auditID");
		sql.addField("ca.auditFor");
		sql.addWhere("ca.auditTypeID = 11");
		
		String auditFor = Strings.implodeForDB(getFilter().getAuditFor(), ",");
		if (!Strings.isEmpty(auditFor))
			sql.addWhere("ca.auditFor IN ("+ auditFor + ")");
	}

	public String execute() throws Exception {
		return super.execute2();
	}

	@Override
	public ReportFilterAudit getFilter() {
		return filter;
	}
}
