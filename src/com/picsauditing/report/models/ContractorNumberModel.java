package com.picsauditing.report.models;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorNumberTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.Strings;


public class ContractorNumberModel extends AbstractModel {

	public ContractorNumberModel(Permissions permissions) {
		super(permissions, new ContractorNumberTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractorNumber = new ModelSpec(null, "ContractorNumber");

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
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		Set<AccountStatus> statuses = new HashSet<AccountStatus>();
		Filter accountStatusFilter = getValidAccountStatusFilter(filters);
		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.isVisibleTo(permissions)) {
					statuses.add(filterStatus);
				}
			}
		}
		if (statuses.size() == 0) {
			statuses.add(AccountStatus.Active);
		}
		
		String whereClause = "Account.status IN (" + Strings.implodeForDB(statuses, ",") + ")";

		// TODO ensure this will work, may need to extract into util class and
		// resuse in different models
		// PermissionQueryBuilder permQuery = new
		if (permissions.isOperator() && permissions.isApprovesRelationships()) {
			whereClause += " AND ContractorFlag.workStatus = 'Y'";
		}
		
		return whereClause;
	}

	private Filter getValidAccountStatusFilter(List<Filter> filters) {
		for (Filter filter : filters) {
			if (filter.getFieldName().equals("AccountStatus") && filter.isValid()) {
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