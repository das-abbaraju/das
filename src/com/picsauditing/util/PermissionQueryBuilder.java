package com.picsauditing.util;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

public class PermissionQueryBuilder {
	static final public int SQL = 1;
	static final public int HQL = 2;
	protected int queryLanguage = SQL;
	protected String accountAlias = "a"; // or contractorAccount
	private boolean showPendingDeactivated = false;
	private boolean onlyPendingAudits = true; // if auditor, then only show
												// the pending or submitted
												// audits
	protected Permissions permissions;
	private boolean workingFacilities = true;

	public PermissionQueryBuilder(Permissions permissions) {
		this(permissions, SQL);
	}

	public PermissionQueryBuilder(Permissions permissions, int queryLanguage) {
		this.permissions = permissions;
		setQueryLanguage(queryLanguage);
	}

	public String toString() {
		// For Nobody
		if (permissions == null || !permissions.isLoggedIn())
			return "AND 1<>1";

		// For Admins and Contractors (easy ones)
		if (permissions.hasPermission(OpPerms.AllContractors))
			return "";

		if (permissions.isContractor()) {
			return "AND " + accountAlias + ".id = " + permissions.getAccountId();
		}
		
		// Assessment Centers
		if (permissions.isAssessment()) {
			return "AND " + accountAlias + ".status IN ('Active', 'Pending', 'Deactivated')";
		}

		// For Operators, Corporate, Audits (hard ones)
		String subquery = ""; // sorry, String was easier to read than a
								// StringBuffer

		if (permissions.isOperator()) {
			if (queryLanguage == HQL)
				subquery = "SELECT t.contractorAccount FROM ContractorOperator t " + "WHERE t.operatorAccount.id = "
						+ permissions.getAccountId();
			else
				subquery = "SELECT gc.subID FROM generalcontractors gc WHERE gc.genID = " + permissions.getAccountId();

			if (workingFacilities) {
				if (permissions.isApprovesRelationships() && !permissions.hasPermission(OpPerms.ViewUnApproved)) {
					subquery += " AND workStatus = 'Y'";
				}
			}
		}

		if (permissions.isCorporate()) {
			if (queryLanguage == HQL)
				subquery = "SELECT co.contractorAccount FROM ContractorOperator co WHERE co.operatorAccount IN "
						+ "(SELECT f.operator FROM Facility f WHERE f.corporate.id = " + permissions.getAccountId()
						+ ")";
			else
				subquery = "SELECT gc.subID FROM generalcontractors gc "
						+ "JOIN facilities f ON f.opID = gc.genID AND f.corporateID = " + permissions.getAccountId();

			if (workingFacilities) {
				if (permissions.isApprovesRelationships() && !permissions.hasPermission(OpPerms.ViewUnApproved)) {
						subquery += " AND workStatus = 'Y'";
				}
			}
		}

		if (permissions.isOnlyAuditor()) {
			if (queryLanguage == HQL)
				subquery = "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.auditor.id = "
						+ permissions.getUserId();
			else
				subquery = "SELECT conID FROM contractor_audit WHERE auditorID = " + permissions.getUserId();
			if (this.onlyPendingAudits)
				subquery += " AND auditStatus IN ('Pending','Incomplete','Submitted')";
		}

		// /////////////////////////
		if (subquery.length() == 0)
			// If we never set the query, then show no contractors
			return "AND 1=0";

		String query = "AND " + accountAlias + ".status IN ('Active'";
		
		if (permissions.getAccountStatus().isDemo())
			query += ",'Demo'";
		
		if (showPendingDeactivated)
			query += ",'Pending','Deactivated'";

		query += ") AND " + accountAlias;
		
		if (queryLanguage == HQL)
			return query += " IN (" + subquery + ")";
		else
			return query += ".id IN (" + subquery + ")";
	}

	public int getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(int queryLanguage) {
		if (queryLanguage == HQL)
			this.accountAlias = "contractorAccount";
		this.queryLanguage = queryLanguage;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	public void setShowPendingDeactivated(boolean value) {
		this.showPendingDeactivated = value;
	}

	/**
	 * 
	 * @param onlyPendingAudits
	 *            Set to true (DEFAULT) if you want auditors to be restricted to
	 *            "auditStatus IN ('Pending','Submitted')"
	 */
	public void setOnlyPendingAudits(boolean onlyPendingAudits) {
		this.onlyPendingAudits = onlyPendingAudits;
	}

	public void setWorkingFacilities(boolean workingFacilities) {
		this.workingFacilities = workingFacilities;
	}

}
