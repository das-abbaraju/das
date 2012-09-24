package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.util.PermissionQueryBuilder;

public class AccountContractorModel extends AbstractModel {

	public AccountContractorModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec account = new ModelSpec(null, "Account");
		account.join(AccountTable.Contact).category = FieldCategory.ContactInformation;
		account.join(AccountTable.Naics);
		{
			ModelSpec contractor = account.join(ContractorAuditTable.Contractor);
			contractor.join(ContractorTable.PQF);
			contractor.join(ContractorTable.Flag);
			contractor.join(ContractorTable.CustomerService);
		}
		return account;
	}

	// TODO ensure this will work, may need to extract into util class and
	// resuse in different models
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

		Field accountName = fields.get("accountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");
		fields.remove("accountType".toUpperCase());
		return fields;
	}
}