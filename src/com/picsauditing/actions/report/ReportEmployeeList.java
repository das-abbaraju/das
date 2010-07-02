package com.picsauditing.actions.report;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class ReportEmployeeList extends ReportAccount {
	public ReportEmployeeList() {
		skipPermissions = true;
		orderByDefault = "a.nameIndex, e.firstName, e.lastName";
	}
	
	@Override
	protected void buildQuery() {
		sql = new SelectAccount();
		
		if (permissions.isOperatorCorporate()) {
			// View all employees under corporate umbrella, view own employees.
			// Need a better way to find operator siblings and corporate umbrella for operators

			// Add own employees?
			String joinString = "JOIN (SELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location " +
					"FROM employee e JOIN generalcontractors gc ON gc.subID = e.accountID " +
					"AND gc.genID = " + permissions.getAccountId();
			
			if (permissions.isOperator()) {
				// Get siblings
				joinString += " UNION SELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location " +
						"FROM employee e JOIN generalcontractors gc ON gc.subID = e.accountID AND gc.genID IN " +
						"(SELECT o.id FROM operators o WHERE o.parentID = " +
						"(SELECT parentID FROM operators WHERE id = " + permissions.getAccountId() + "))";
				// Get parent
				joinString += " UNION SELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location " +
						"FROM employee e JOIN generalcontractors gc ON gc.subID = e.accountID AND gc.genID = " +
						"(SELECT parentID FROM operators WHERE id = " + permissions.getAccountId() + "))";
			} else { // is Corporate
				joinString += " UNION SELECT e.id, e.accountID, e.firstName, e.lastName, e.title, e.location " +
						"FROM employee e JOIN generalcontractors gc ON gc.subID = e.accountID AND gc.genID IN " +
						"(SELECT id FROM operators WHERE parentID = " + permissions.getAccountId() + "))";
				getFilter().setShowOperator(true);
			}
			
			joinString += " e ON e.accountID = a.id";
			
			sql.addJoin(joinString);
			sql.addGroupBy("a.id, e.id");
		} else { // PICS Administrator
			// See ALL employees
			sql.addJoin("JOIN employee e ON e.accountID = a.id");
			getFilter().setShowOperator(true);
		}

		sql.addField("e.id AS employeeID");
		sql.addField("e.firstName");
		sql.addField("e.lastName");
		sql.addField("e.title");
		sql.addField("e.location");
		sql.addField("a.dbaName");
		
		addFilterToSQL();
	}
	
	@Override
	public String execute() throws Exception {
		// Manage filters?
		getFilter().setShowStatus(false);
		getFilter().setShowAddress(false);
		getFilter().setShowWorksIn(false);
		getFilter().setShowLicensedIn(false);
		getFilter().setShowTaxID(false);
		getFilter().setShowIndustry(false);
		getFilter().setShowTrade(false);
		getFilter().setShowRiskLevel(false);
		getFilter().setShowCcOnFile(false);
		getFilter().setShowConAuditor(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowOfficeIn(false);
		getFilter().setShowRegistrationDate(false);
		
		return super.execute();
	}
	
	@Override
	protected void checkPermissions() throws Exception {
		if (!permissions.isOperatorCorporate() && !permissions.isAdmin())
			throw new NoRightsException("Operator, Corporate or PICS Administrator");
	}
}
