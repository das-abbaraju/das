package com.picsauditing.report.models;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.Strings;

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
		contractor.join(ContractorTable.Watch);
		contractor.join(ContractorTable.Tag);
		return contractor;
	}

	@Override
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		Set<AccountStatus> statuses = new HashSet<AccountStatus>();
		Filter accountStatusFilter = getValidAccountStatusFilter(filters);
		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.canSee(permissions)) {
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
		{
			Field contractorEdit = new Field("ContractorEdit", "'Edit'", FilterType.String);
			contractorEdit.setUrl("ContractorEdit.action?id={AccountID}");
			contractorEdit.setWidth(70);
			fields.put(contractorEdit.getName().toUpperCase(), contractorEdit);
		}

		{
			Field contractorAudits = new Field("ContractorAudits", "'Audits'", FilterType.String);
			contractorAudits.setUrl("ContractorDocuments.action?id={AccountID}");
			contractorAudits.setWidth(70);
			fields.put(contractorAudits.getName().toUpperCase(), contractorAudits);
		}

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");
		return fields;
	}
}