package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;

public class AccountContractorModel extends AbstractModel {

	public AccountContractorModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractor = new ModelSpec(null, "Contractor");
		{
			ModelSpec account = contractor.join(ContractorTable.Account);
			account.alias = "Account";
			account.join(AccountTable.Contact).category = FieldCategory.ContactInformation;
			account.join(AccountTable.Naics);
		}
		contractor.join(ContractorTable.PQF);
		if (permissions.isOperatorCorporate()) {
			contractor.join(ContractorTable.Flag);
		}
		contractor.join(ContractorTable.CustomerService);
		contractor.join(ContractorTable.Watch).category = FieldCategory.AccountInformation;
		contractor.join(ContractorTable.Tag).category = FieldCategory.AccountInformation;
		return contractor;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
		permissionQueryBuilder.setContractorOperatorAlias("ContractorFlag");

		Filter accountStatusFilter = getValidAccountStatusFilter(filters);

		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.isVisibleTo(permissions)) {
					permissionQueryBuilder.addVisibleStatus(filterStatus);
				}
			}
		}

		return permissionQueryBuilder.buildWhereClause();
	}

	private Filter getValidAccountStatusFilter(List<Filter> filters) {
		for (Filter filter : filters) {
			if (filter.getFieldName().equalsIgnoreCase("AccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}
}