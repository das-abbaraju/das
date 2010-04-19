package com.picsauditing.util;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class PermissionQueryBuilderEmployee extends PermissionQueryBuilder {

	public PermissionQueryBuilderEmployee(Permissions permissions) {
		this(permissions, SQL);
	}

	public PermissionQueryBuilderEmployee(Permissions permissions, int queryLanguage) {
		super(permissions, queryLanguage);
		accountAlias = "e";
	}

	@Override
	public String toString() {
		// For Nobody
		if (permissions == null || !permissions.isLoggedIn())
			return "AND 1<>1";

		// For Admins and Contractors (easy ones)
		if (permissions.hasPermission(OpPerms.AllContractors))
			return "";

		if (permissions.isOnlyAuditor())
			return "";

		if (permissions.isContractor())
			return "";

		// For Operators and Corporate (hard ones)
		String subquery = "";

		if (permissions.isOperator()) {
			if (queryLanguage == HQL)
				subquery = "SELECT t.employee FROM EmployeeSite t WHERE t.operator.id = " + permissions.getAccountId();
			else
				subquery = "SELECT es.employeeID FROM employee_site es WHERE es.opID = " + permissions.getAccountId();
		}

		if (permissions.isCorporate()) {
			if (queryLanguage == HQL)
				subquery = "SELECT co.contractorAccount FROM EmployeeSite t WHERE t.operator IN "
						+ "(SELECT f.operator FROM Facility f WHERE f.corporate.id = " + permissions.getAccountId()
						+ ")";
			else
				subquery = "SELECT es.employeeID FROM employee_site es "
						+ "JOIN facilities f ON f.opID = es.opID AND f.corporateID = " + permissions.getAccountId();
		}

		// /////////////////////////
		if (subquery.length() == 0)
			// If we never set the query, then show no contractors
			return "AND 1=0";

		String query = "AND " + accountAlias + ".active = 1 AND " + accountAlias;

		if (queryLanguage == HQL)
			return query += " IN (" + subquery + ")";
		else
			return query += ".id IN (" + subquery + ")";
	}

	public void setQueryLanguage(int queryLanguage) {
		if (queryLanguage == HQL)
			this.accountAlias = "employee";
		this.queryLanguage = queryLanguage;
	}

}
