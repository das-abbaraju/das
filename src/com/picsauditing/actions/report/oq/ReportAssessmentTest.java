package com.picsauditing.actions.report.oq;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilter;

@SuppressWarnings("serial")
public class ReportAssessmentTest extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL();
	private String subHeading = "Assessment Test Report";
	
	private ReportFilter filter;
	
	public ReportAssessmentTest() {
		orderByDefault = "test, task, employee";
	}
	
	protected void buildQuery() {
		sql.setFromTable("assessment_test t");
		sql.addField("CONCAT((SELECT name FROM accounts a WHERE a.id = t.assessmentCenterID), ': ', " +
				"t.qualificationType, ' - ', t.qualificationMethod) AS test");
		sql.addField("t.description");
		sql.addField("CASE (t.effectiveDate < NOW() AND t.expirationDate > now()) WHEN TRUE THEN 'Yes' " +
				"ELSE 'No' END AS testActive");
		sql.addField("CONCAT(j.label, ': ', j.name) AS task");
		sql.addField("CASE j.active WHEN 1 THEN 'Yes' ELSE 'No' END AS taskActive");
		sql.addField("e.id AS employeeID");
		sql.addField("CONCAT(e.firstName, ' ', e.lastName) AS employee");
		sql.addField("DATE_FORMAT(q.effectiveDate, '%m/%d/%Y') AS qualEff");
		sql.addField("DATE_FORMAT(q.expirationDate, '%m/%d/%Y') AS qualExp");
		sql.addField("CASE q.qualified WHEN 1 THEN 'Yes' ELSE 'No' END AS qualified");
		sql.addJoin("JOIN job_task_criteria c ON c.assessmentTestID = t.id");
		sql.addJoin("JOIN job_task j ON c.taskID = j.id");
		sql.addJoin("JOIN employee_qualification q ON c.taskID = q.taskID");
		sql.addJoin("JOIN employee e ON q.employeeID = e.id");
		
		if (permissions.isOperator()) {
			sql.addWhere("e.accountID IN (SELECT subID FROM generalcontractors WHERE genID = " + 
					permissions.getAccountId() + ") OR e.accountID = " + permissions.getAccountId());
		}
		
		if (permissions.isCorporate()) {
			sql.addWhere("e.accountID IN (SELECT subID FROM generalcontractors WHERE genID IN " +
					"(SELECT id FROM operators WHERE parentID = " + permissions.getAccountId() + 
					")) OR e.accountID = " + permissions.getAccountId());
		}
		
		sql.addOrderBy(getOrderBy());
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		// Operators, Corporate, Administrators
		if (!permissions.isAdmin() && !permissions.isOperatorCorporate())
			throw new NoRightsException("Operator, Corporate or PICS Administrator");
		
		buildQuery();
		run(sql);
		
		return SUCCESS;
	}
	
	public String getSubHeading() {
		return subHeading;
	}
	
	public ReportFilter getFilter() {
		return filter;
	}
	
	public void setFilter(ReportFilter filter) {
		this.filter = filter;
	}
}
