package com.picsauditing.actions.report;

import com.picsauditing.util.ReportFilterAudit;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAnnualAddendum extends ReportAccount {
	protected String auditFor;
	
	private ReportFilterAudit filter = new ReportFilterAudit();

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		sql.addJoin("JOIN contractor_audit ca ON ca.conID = a.id");
		sql.addField("ca.auditID");
		sql.addField("auditFor"); 
		sql.addWhere("pqf.auditStatus = 'Active' AND pqf.auditTypeID = 11");
		String list = Strings.implode(getfil  , delimiter) 
		sql.addWhere("pqf.auditFor IN = "+ auditFor);
		String list = Strings.implode(f.getOfficeIn(), ",");
		createPqfDataClause(sql, "AND d.questionID IN (" + list + ") AND d.answer LIKE 'Yes with Office'");

		return super.execute();
	}

	public String getAuditFor() {
		return auditFor;
	}

	public void setAuditFor(String auditFor) {
		this.auditFor = auditFor;
	}
	
	@Override
	public ReportFilterAudit getFilter() {
		return filter;
	}
}
