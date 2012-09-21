package com.picsauditing.report.models

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.util.PermissionQueryBuilder;

class AccountModel extends AbstractModel {
	
	public AccountModel(Permissions permissions) {
		super(permissions)
		
		fromTable = new AccountTable("account")
		availableFields = fromTable.getAvailableFields(permissions)
		addJoin(fromTable.getJoin("accountContact"), permissions)
		addJoin(fromTable.getJoin("accountNaics"), permissions)
		
		// Like this
		//	conAudit ContractorAuditTable
		//		contractor Level.Required
		//			account
		//				accountContact
		//				accountNaics
		//		auditType
		
		table AccountTable.class, "account" {
			join "accountContact"
			join "accountNaics"
		}
	}
	
	// TODO ensure this will work, may need to extract into util class and resuse in different models
	@Override
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions);
		Filter accountStatusFilter = getValidAccountStatusFilter(filters);
		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.canSee(permissions)) {
					permQuery.addVisibleStatus(filterStatus);
				}
			}
		}

		String whereClause = permQuery.buildWhereClause();
		if (permissions.isOperatorCorporate()) {
			whereClause += " AND myFlag.genID = " + permissions.getAccountId();
		}
		return whereClause;
	}

	private Filter getValidAccountStatusFilter(List<Filter> filters) {
		for (Filter filter : filters) {
			if (filter.getFieldName().equals("accountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}
}
