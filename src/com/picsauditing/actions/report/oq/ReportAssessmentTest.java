package com.picsauditing.actions.report.oq;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterAccount;
import com.picsauditing.util.ReportFilterEmployee;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportAssessmentTest extends ReportActionSupport {
	private SelectSQL sql = new SelectSQL();
	private String subHeading = "Assessment Test Report";
	
	private ReportFilterEmployee filter = new ReportFilterEmployee();
	
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
		sql.addField("a.name");
		sql.addField("a.id AS accountID");
		sql.addJoin("JOIN job_task_criteria c ON c.assessmentTestID = t.id");
		sql.addJoin("JOIN job_task j ON c.taskID = j.id");
		sql.addJoin("JOIN employee_qualification q ON c.taskID = q.taskID");
		sql.addJoin("JOIN employee e ON q.employeeID = e.id");
		sql.addJoin("LEFT JOIN accounts a ON a.id = e.accountID");
		
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
		
		addFilterToSQL();
	}
	
	private void addFilterToSQL() {
		ReportFilterEmployee f = getFilter();
		
		if (filterOn(f.getAccountName(), ReportFilterAccount.DEFAULT_NAME)) {
			String accountName = f.getAccountName().trim();
			report.addFilter(new SelectFilter("accountName", "a.nameIndex LIKE '%" + Strings.indexName(accountName)
					+ "%' OR a.name LIKE '%?%' OR a.dbaName LIKE '%" + Utilities.escapeQuotes(accountName)
					+ "%' OR a.id = '" + Utilities.escapeQuotes(accountName) + "'", accountName));
			sql.addField("a.dbaName");
		}
		
		if (filterOn(f.getFirstName())) {
			String firstName = Utilities.escapeQuotes(f.getFirstName().trim());
			sql.addWhere("e.firstName LIKE '%" + firstName + "%'");
		}
		
		if (filterOn(f.getLastName())) {
			String lastName = Utilities.escapeQuotes(f.getLastName().trim());
			sql.addWhere("e.lastName LIKE '%" + lastName + "%'");
		}
		
		if (filterOn(f.getEmail())) {
			String email = Utilities.escapeQuotes(f.getEmail().trim());
			sql.addWhere("e.email LIKE '%" + email + "%'");
		}
		
		if (f.isLimitEmployees())
			sql.addWhere("e.accountID = " + permissions.getAccountId());
		
		if (filterOn(f.getAssessmentCenters()))
			sql.addWhere("t.assessmentCenterID IN (" + Strings.implode(f.getAssessmentCenters()) + ")");
	}
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		getFilter().setDestinationAction("ReportAssessmentTests");
		// Do we need to look up employees by SSN?
		getFilter().setShowSsn(false);
		getFilter().setShowLimitEmployees(true);
		getFilter().setShowAssessmentCenter(true);
		getFilter().setPermissions(permissions);
		
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
	
	public ReportFilterEmployee getFilter() {
		return filter;
	}
}
