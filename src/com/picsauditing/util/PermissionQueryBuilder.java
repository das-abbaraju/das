package com.picsauditing.util;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class PermissionQueryBuilder {
	static final public int SQL = 1;
	static final public int HQL = 2;
	private int queryLanguage = SQL;
	private String accountAlias = "a"; // contractorAccount
	private boolean activeContractorsOnly = true;
	private boolean onlyPendingAudits = true; // if auditor, then only show the pending or submitted audits
	private Permissions permissions;
	
	public PermissionQueryBuilder(Permissions permissions) {
		this.permissions = permissions;
	}
	
	public PermissionQueryBuilder(Permissions permissions, int queryLanguage) {
		this.permissions = permissions;
		this.queryLanguage = queryLanguage;
	}
	
	public String toString() {
		// For Nobody
		if (permissions == null || !permissions.isLoggedIn())
			return "AND 1<>1";
		
		// For Admins and Contractors (easy ones)
		if (permissions.hasPermission(OpPerms.AllContractors))
			return "";
		
		if (permissions.isContractor()) {
			return "AND "+accountAlias + ".id = " + permissions.getAccountId();
		}
		
		// For Operators, Corporate, Audits (hard ones)
		String subquery = ""; // sorry, String was easier to read than a StringBuffer
		
		if (permissions.isOperator()) {
			if (queryLanguage == HQL)
				subquery = "SELECT contractorAccount FROM ContractorOperator " +
						"WHERE operatorAccount.id = " + permissions.getAccountId();
			else
				subquery = "SELECT gc.subID FROM generalcontractors gc WHERE gc.genID = "+permissions.getAccountId();
			
			if (permissions.isApprovesRelationships()
					&& !permissions.hasPermission(OpPerms.ViewUnApproved)) {
					subquery += " AND workStatus = 'Y'";
			}
		}
		
		if (permissions.isCorporate()) {
			if (queryLanguage == HQL)
				subquery = "SELECT contractorAccount FROM ContractorOperator " +
						"WHERE operatorAccount.facilities.corporate.id = " + permissions.getAccountId();
			else
				subquery = "SELECT gc.subID FROM generalcontractors gc " +
						"JOIN facilities f ON f.opID = gc.genID AND f.corporateID = "+permissions.getAccountId();
		}
		
		if (permissions.isOnlyAuditor()) {
			if (queryLanguage == HQL)
				subquery = "SELECT contractorAccount FROM ContractorAudit WHERE auditor.id = "+permissions.getUserId();
			else
				subquery = "SELECT conID FROM contractor_audit WHERE auditorID = "+permissions.getUserId();
			if (this.onlyPendingAudits)
				subquery += " AND auditStatus IN ('Pending','Submitted')";
		}
		
		///////////////////////////
		if (subquery.length() == 0)
			// If we never set the query, then show no contractors
			return "AND 1=0";
		
		String query = "";
		if (activeContractorsOnly)
			query = "AND " +accountAlias+".active = 'Y' ";
		
		if (queryLanguage == HQL)
			return query += "AND "+accountAlias+" IN ("+subquery+")";
		else
			return query += "AND "+accountAlias+".id IN ("+subquery+")";
	}

	public int getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(int queryLanguage) {
		this.queryLanguage = queryLanguage;
	}

	public String getAccountAlias() {
		return accountAlias;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	public boolean isActiveContractorsOnly() {
		return activeContractorsOnly;
	}

	public void setActiveContractorsOnly(boolean activeContractorsOnly) {
		this.activeContractorsOnly = activeContractorsOnly;
	}

	public boolean isOnlyPendingAudits() {
		return onlyPendingAudits;
	}

	public void setOnlyPendingAudits(boolean onlyPendingAudits) {
		this.onlyPendingAudits = onlyPendingAudits;
	}
	
}
