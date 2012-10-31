package com.picsauditing.util;

import java.util.HashSet;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;

public class PermissionQueryBuilder {
	private static final String PERMISSION_DENIED = "1=0";
	static final public int SQL = 1;
	static final public int HQL = 2;

	protected int queryLanguage = SQL;
	protected String accountAlias = "a"; // aka contractorAccount
	protected String contractorOperatorAlias = ""; // aka contractorOperator
	protected Permissions permissions;

	private boolean workingFacilities = true;

	private Set<AccountStatus> visibleStatuses = new HashSet<AccountStatus>();

	public PermissionQueryBuilder(Permissions permissions) {
		this(permissions, SQL);
	}

	public PermissionQueryBuilder(Permissions permissions, int queryLanguage) {
		this.permissions = permissions;
		setQueryLanguage(queryLanguage);
	}

	/**
	 * Use buildWhereClause instead
	 */
	@Deprecated
	public String toString() {
		String whereClause = buildWhereClause();

		if (Strings.isEmpty(whereClause))
			return "";
		else
			return "AND " + whereClause;
	}

	public String buildWhereClause() {
		if (permissions == null || !permissions.isLoggedIn())
			return PERMISSION_DENIED;

		if (permissions.hasPermission(OpPerms.AllContractors))
			return "";

		if (permissions.isContractor())
			return "" + accountAlias + ".id = " + permissions.getAccountId();

		if (permissions.isAssessment())
			return "" + accountAlias + ".status IN ('Active', 'Pending', 'Deactivated')";

		String query = buildStatusFilter();

		if (!permissions.isOperatorCorporate())
			contractorOperatorAlias = "";

		String subSQL;
		query += " AND ";

		if (!Strings.isEmpty(contractorOperatorAlias) && permissions.isOperator())
			return query + buildContractorOperatorClause(contractorOperatorAlias);

		String alias = isHQL() ? "co" : "gc";

		if (permissions.isOnlyAuditor())
			subSQL = buildAuditorSubquery();
		else if (permissions.isOperatorCorporate())
			subSQL = buildContractorOperatorSubquery(alias);
		else
			return PERMISSION_DENIED;

		query += accountAlias;

		if (!isHQL())
			query += ".id";

		return query + " IN (" + subSQL + ")";
	}

	private String buildAuditorSubquery() {
		if (isHQL())
			return "SELECT t.contractorAccount FROM ContractorAudit t WHERE t.auditor.id = "
					+ permissions.getUserId();
		else
			return "SELECT conID FROM contractor_audit WHERE auditorID = " + permissions.getUserId();
	}

	private String buildContractorOperatorSubquery(String alias) {
		String contractorColumn;
		String contractorOperatorTable;

		if (isHQL()) {
			contractorColumn = alias + ".contractorAccount";
			contractorOperatorTable = "ContractorOperator " + alias;
		} else {
			contractorColumn = alias + ".subID";
			contractorOperatorTable = "generalcontractors " + alias;
		}

		String subquery = "SELECT " + contractorColumn + " FROM " + contractorOperatorTable;

		subquery += " WHERE " + buildContractorOperatorClause(alias);

		return subquery;
	}

	private String buildContractorOperatorClause(String alias) {
		String where = alias;

		if (isHQL()) {
			where += ".operatorAccount.id";
		} else {
			where += ".genID";
		}

		where += " IN (" + getOperatorIDs() + ")";

		if (showOnlyApprovedContractors())
			where += " AND " + alias + ".workStatus = 'Y'";

		return where;
	}

	private boolean showOnlyApprovedContractors() {
		if (!permissions.hasPermission(OpPerms.ViewUnApproved))
			return true;

		if (permissions.isGeneralContractor())
			return true;

		return workingFacilities;
	}

	private String getOperatorIDs() {
		if (permissions.isCorporate())
			return Strings.implode(permissions.getOperatorChildren());

		if (permissions.isGeneralContractor())
			return permissions.getAccountId() + "," + Strings.implode(permissions.getLinkedClients());

		return permissions.getAccountIdString();
	}

	private boolean isHQL() {
		return queryLanguage == HQL;
	}

	private String buildStatusFilter() {
		// TODO we shouldn't always add these, make the user add these when they want
		// if (visibleStatuses.isEmpty()) {}
		addDefaultStatuses();
		String statusList = Strings.implodeForDB(visibleStatuses, ",");
		return accountAlias + ".status IN (" + statusList + ")";
	}

	public void addDefaultStatuses() {
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

	public void setContractorOperatorAlias(String contractorOperatorAlias) {
		this.contractorOperatorAlias = contractorOperatorAlias;
	}

	public void setWorkingFacilities(boolean workingFacilities) {
		this.workingFacilities = workingFacilities;
	}
}