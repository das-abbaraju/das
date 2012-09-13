package com.picsauditing.util;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;

public class PermissionQueryBuilder {
	static final public int SQL = 1;
	static final public int HQL = 2;
	protected int queryLanguage = SQL;
	protected String accountAlias = "a"; // or contractorAccount
	private boolean showPendingDeactivated = false;
	protected Permissions permissions;
	private boolean workingFacilities = true;
	private String subquery;
	private Set<AccountStatus> visibleStatuses = new HashSet<AccountStatus>();;

	public PermissionQueryBuilder(Permissions permissions) {
		this(permissions, SQL);
	}

	public PermissionQueryBuilder(Permissions permissions, int queryLanguage) {
		this.permissions = permissions;
		setQueryLanguage(queryLanguage);
	}

	@Deprecated
	public String toString() {
		String whereClause = buildWhereClause();

		if (Strings.isEmpty(whereClause))
			return "";
		else
			return "AND " + whereClause;
	}

	public String buildWhereClause() {
		// For Nobody
		if (permissions == null || !permissions.isLoggedIn())
			return "1=0";

		// For Admins and Contractors (easy ones)
		if (permissions.hasPermission(OpPerms.AllContractors))
			return "";

		if (permissions.isContractor()) {
			return "" + accountAlias + ".id = " + permissions.getAccountId();
		}

		// Assessment Centers
		if (permissions.isAssessment()) {
			return "" + accountAlias + ".status IN ('Active', 'Pending', 'Deactivated')";
		}

		buildSubQuery();

		// /////////////////////////
		if (subquery.length() == 0)
			// If we never set the query, then show no contractors
			return "1=0";

		String query = buildStatusFilter();

		if (queryLanguage == HQL)
			return query += " AND " + accountAlias + " IN (" + subquery + ")";
		else
			return query += " AND " + accountAlias + ".id IN (" + subquery + ")";
	}

	private void buildSubQuery() {
		subquery = "";

		if (permissions.isOperator()) {
			String operatorIDs = permissions.getAccountIdString();

			if (permissions.isGeneralContractor()) {
				operatorIDs += "," + Strings.implode(permissions.getLinkedClients());
			}

			String contractors = "gc.subID";
			String contractorOperatorTable = "generalcontractors gc";
			String whereOperator = "gc.genID IN (" + operatorIDs + ")";

			if (queryLanguage == HQL) {
				contractors = "t.contractorAccount";
				contractorOperatorTable = "ContractorOperator t";
				whereOperator = "t.operatorAccount.id IN (" + operatorIDs + ")";
			}

			subquery = "SELECT " + contractors + " FROM " + contractorOperatorTable + " WHERE " + whereOperator;

			if (workingFacilities) {
				if ((permissions.isApprovesRelationships() && !permissions.hasPermission(OpPerms.ViewUnApproved))
						|| permissions.isGeneralContractor()) {
					subquery += " AND " + (queryLanguage == HQL ? "t." : "gc.") + "workStatus = 'Y'";
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
					if (queryLanguage == HQL)
						subquery += " AND co.operatorAccount IN "
								+ "(SELECT f.operator FROM Facility f WHERE f.corporate.id = "
								+ permissions.getAccountId() + ")" + " AND workStatus = 'Y'";
					else
						subquery += " JOIN operators o ON f.opID = o.id WHERE o.approvesRelationships = 'No' OR workStatus = 'Y'";
				}
			}
		}

		if (permissions.isOnlyAuditor()) {
			if (queryLanguage == HQL)
				subquery = "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.auditor.id = "
						+ permissions.getUserId();
			else
				subquery = "SELECT conID FROM contractor_audit WHERE auditorID = " + permissions.getUserId();
		}
	}

	private String buildStatusFilter() {
		defaultVisibleStatuses();
		String statusList = Strings.implodeForDB(visibleStatuses, ",");
		return accountAlias + ".status IN (" + statusList + ")";
	}

	private void defaultVisibleStatuses() {
		if (visibleStatuses.isEmpty()) {
			if (showPendingDeactivated) {
				visibleStatuses.add(AccountStatus.Pending);
				visibleStatuses.add(AccountStatus.Deactivated);
			}
		}

		visibleStatuses.add(AccountStatus.Active);

		if (permissions.getAccountStatus().isDemo())
			visibleStatuses.add(AccountStatus.Demo);
	}

	public void addVisibleStatus(AccountStatus status) {
		visibleStatuses.add(status);
	}

	public int getQueryLanguage() {
		return queryLanguage;
	}

	public void setQueryLanguage(int queryLanguage) {
		if (queryLanguage == HQL) {
			setAccountAlias("contractorAccount");
		}
		this.queryLanguage = queryLanguage;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}

	public void setWorkingFacilities(boolean workingFacilities) {
		this.workingFacilities = workingFacilities;
	}

}
