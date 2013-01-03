package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorNumberTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;


public class ContractorNumberModel extends AbstractModel {

	public ContractorNumberModel(Permissions permissions) {
		super(permissions, new ContractorNumberTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractorNumber = new ModelSpec(null, "OperatorContractorNumber");

		{
			ModelSpec contractor = contractorNumber.join(ContractorNumberTable.Contractor);
			contractor.alias = "Contractor";
			{
				ModelSpec account = contractor.join(ContractorTable.Account);
				account.alias = "Account";
				account.join(AccountTable.Contact).category = FieldCategory.ContactInformation;
				account.join(AccountTable.Naics);
			}
			if (permissions.isOperatorCorporate()) {
				contractor.join(ContractorTable.Flag);
			}
			contractor.join(ContractorTable.CustomerService);
		}
		return contractorNumber;
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
			if (filter.getId().equals("AccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");
		return fields;
	}
}