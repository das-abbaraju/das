package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
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
			Field contractorEdit = new Field("ContractorEdit", "'Edit'", FieldType.String);
			contractorEdit.setUrl("ContractorEdit.action?id={AccountID}");
			contractorEdit.setWidth(70);
			fields.put(contractorEdit.getName().toUpperCase(), contractorEdit);
		}

		{
			Field contractorAudits = new Field("ContractorAudits", "'Audits'", FieldType.String);
			contractorAudits.setUrl("ContractorDocuments.action?id={AccountID}");
			contractorAudits.setWidth(70);
			fields.put(contractorAudits.getName().toUpperCase(), contractorAudits);
		}

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");
		return fields;
	}
}