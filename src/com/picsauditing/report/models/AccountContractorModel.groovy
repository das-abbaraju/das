package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.jpa.entities.AccountStatus
import com.picsauditing.report.Filter
import com.picsauditing.report.tables.ContractorTable
import com.picsauditing.report.tables.FieldCategory
import com.picsauditing.util.PermissionQueryBuilder

public class AccountContractorModel extends AbstractModel {
	static Map joinSpec = [
		alias: "Contractor",
		joins: [
			[
				key: ContractorTable.Account,
				alias: "Account",
				joins: [
					[
						key: "Contact",
						category: FieldCategory.ContactInformation
					],[
						key: "Naics"
					]
				]
			],[
				key: ContractorTable.PQF
			],[
				key: ContractorTable.Flag
			],[
				key: ContractorTable.CustomerService
			]
		]
	]
	
	public Map getJoinSpec() {
		return joinSpec;
	}

	public AccountContractorModel(Permissions permissions) {
		super(permissions, new ContractorTable())
		
		// TODO adjust these columns
		// hideAccountID();
		// rootTable.removeField("accountName");
		// rootTable.removeField("accountType");
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

		return permQuery.buildWhereClause();
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
