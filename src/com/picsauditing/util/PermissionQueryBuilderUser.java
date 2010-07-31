package com.picsauditing.util;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class PermissionQueryBuilderUser extends PermissionQueryBuilder {

	public PermissionQueryBuilderUser(Permissions permissions) {
		this(permissions, SQL);
	}

	public PermissionQueryBuilderUser(Permissions permissions, int queryLanguage) {
		super(permissions, queryLanguage);
		accountAlias = "u";
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
		
		if(permissions.isOperator()){
			return "";			
		}

		if (permissions.isOperator()) {
			if (queryLanguage == HQL)
				return "AND " + accountAlias + ".account.id = " + permissions.getAccountId();
			else
				return "AND " + accountAlias + ".accountID = " + permissions.getAccountId();
		}
		
		if (permissions.isCorporate()) {
			if (queryLanguage == HQL)
				subquery = "SELECT f.operator FROM Facility f WHERE f.corporate.id = " + permissions.getAccountId();
			else
				subquery = "SELECT f.opID FROM facilities f WHERE f.corporateID = " + permissions.getAccountId();
		}

		// /////////////////////////
		if (subquery.length() == 0)
			// If we never set the query, then show no contractors
			return "AND 1=0";

		String query = "AND " + accountAlias + ".isActive = 1 AND " + accountAlias;

		if (queryLanguage == HQL)
			return query += " IN (" + subquery + ")";
		else
			return query += ".accountID IN (" + subquery + ")";
	}

	public void setQueryLanguage(int queryLanguage) {
		if (queryLanguage == HQL)
			this.accountAlias = "user";
		this.queryLanguage = queryLanguage;
	}

}
