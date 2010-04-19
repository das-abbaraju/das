package com.picsauditing.actions.report;

@SuppressWarnings("serial")
public class ReportComplianceByEmployee extends ReportAccount {
	public ReportComplianceByEmployee() {
		orderByDefault = "a.name, e.lastName, e.firstName";
	}
	
	protected void buildQuery() {
		super.buildQuery();
		
		//sql.addField("e.id");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("a.id AS accountID");
		sql.addField("COUNT(jc.competencyID) AS required");
		sql.addField("SUM(IFNULL(ec.skilled,0)) AS skilled");
		sql.addJoin("JOIN employee e on e.accountID = a.id");
		sql.addJoin("JOIN (SELECT DISTINCT er.employeeID, jc.competencyID FROM employee_role er"
				+ " JOIN job_competency jc ON jc.jobRoleID = er.jobRoleID) jc ON jc.employeeID = e.id");
		sql.addJoin("LEFT JOIN employee_competency ec ON ec.competencyID = jc.competencyID AND e.id = ec.employeeID");
		sql.addGroupBy("e.id");
		
		if (permissions.isContractor())
			sql.addWhere("a.id = " + permissions.getAccountId());
		if (permissions.isOperatorCorporate())
			sql.addField("a.name");
	}
}
