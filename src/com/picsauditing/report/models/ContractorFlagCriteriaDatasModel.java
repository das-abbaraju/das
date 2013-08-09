package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorFlagCriteriaDatasModel extends AbstractModel {

	public static final String FLAG_CRITERIA_LABEL = "FlagCriteriaLabel";
	public static final String FLAG_CRITERIA_DESCRIPTION = "FlagCriteriaDescription";

	public ContractorFlagCriteriaDatasModel(Permissions permissions) {
		super(permissions, new FlagDataTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "FlagData");

		ModelSpec contractorOperator = spec.join(FlagDataTable.ContractorOperator);
		contractorOperator.alias = "ContractorFlag";
		contractorOperator.minimumImportance = FieldImportance.Average;

		ModelSpec coOperator = contractorOperator.join(ContractorOperatorTable.Operator);
		coOperator.alias = "ContractorOperatorOperator";
		coOperator.minimumImportance = FieldImportance.Average;

		ModelSpec operator = coOperator.join(AccountTable.Operator);
		operator.alias = "Operator";

		ModelSpec flagCriteria = spec.join(FlagDataTable.FlagCriteria);
		flagCriteria.alias = "FlagCriteria";
		flagCriteria.minimumImportance = FieldImportance.Low;

		ModelSpec account = spec.join(FlagDataTable.Contractor);
		account.alias = "Account";
		account.minimumImportance = FieldImportance.Average;

        ModelSpec contractor = account.join(AccountTable.Contractor);
        contractor.alias = "Contractor";
        contractor.minimumImportance = FieldImportance.Required;

        ModelSpec customerService = contractor.join(ContractorTable.CustomerService);
        customerService.alias = "CustomerService";
        customerService.category = FieldCategory.CustomerService;
        customerService.minimumImportance = FieldImportance.Required;

        ModelSpec customerServiceUser = customerService.join(AccountUserTable.User);
        customerServiceUser.alias = "CustomerServiceUser";
        customerServiceUser.category = FieldCategory.CustomerService;

        ModelSpec override = spec.join(FlagDataTable.Override);
		override.minimumImportance = FieldImportance.Required;

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		Field flagCriteriaLabel = new Field(FLAG_CRITERIA_LABEL, "FlagCriteria.id", FieldType.String);
		flagCriteriaLabel.setCategory(FieldCategory.CompanyStatistics);
		flagCriteriaLabel.setTranslationPrefixAndSuffix("FlagCriteria", "label");
		fields.put(FLAG_CRITERIA_LABEL.toUpperCase(), flagCriteriaLabel);

		Field flagCriteriaDescription = new Field(FLAG_CRITERIA_DESCRIPTION, "FlagCriteria.id", FieldType.String);
		flagCriteriaDescription.setCategory(FieldCategory.CompanyStatistics);
		flagCriteriaDescription.setTranslationPrefixAndSuffix("FlagCriteria", "description");
		flagCriteriaDescription.setWidth(500);
		fields.put(FLAG_CRITERIA_DESCRIPTION.toUpperCase(), flagCriteriaDescription);

		return fields;
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
			if (filter.getName().equalsIgnoreCase("AccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}
}