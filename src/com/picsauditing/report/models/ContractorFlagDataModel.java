package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.ContractorOperatorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.FlagCriteriaTable;
import com.picsauditing.report.tables.FlagDataTable;

public class ContractorFlagDataModel extends AbstractModel {

	public ContractorFlagDataModel(Permissions permissions) {
		super(permissions, new FlagDataTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "FlagData");

		{
			ModelSpec contractorOperator = spec.join(FlagDataTable.ContractorOperator);
			contractorOperator.alias = "ContractorFlag";
			contractorOperator.minimumImportance = FieldImportance.Average;
			ModelSpec coOperator = contractorOperator.join(ContractorOperatorTable.Operator);
			coOperator.alias = "ContractorOperatorOperator";
			coOperator.minimumImportance = FieldImportance.Required;
		}

		ModelSpec operatorCriteria = spec.join(FlagDataTable.OperatorCriteria);
		operatorCriteria.alias = "OperatorCriteria";
		operatorCriteria.minimumImportance = FieldImportance.Average;

		ModelSpec flagCriteria = spec.join(FlagDataTable.FlagCriteria);
		flagCriteria.alias = "FlagCriteria";
		flagCriteria.minimumImportance = FieldImportance.Average;
		
		ModelSpec contractor = spec.join(FlagDataTable.Contractor);
		contractor.alias = "Account";
		contractor.minimumImportance = FieldImportance.Required;
		spec.join(FlagDataTable.Override);

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		Field flagCriteriaLabel = new Field("FlagCriteriaLabel", "FlagCriteria.id", FieldType.String);
		flagCriteriaLabel.setCategory(FieldCategory.CompanyStatistics);
		flagCriteriaLabel.setTranslationPrefixAndSuffix("FlagCriteria", "label");
		fields.put(flagCriteriaLabel.getName().toUpperCase(), flagCriteriaLabel);

		Field flagCriteriaDescription = new Field("FlagCriteriaDescription", "FlagCriteria.id", FieldType.String);
		flagCriteriaDescription.setCategory(FieldCategory.CompanyStatistics);
		flagCriteriaDescription.setTranslationPrefixAndSuffix("FlagCriteria", "description");
		flagCriteriaDescription.setWidth(500);
		fields.put(flagCriteriaDescription.getName().toUpperCase(), flagCriteriaDescription);

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
			if (filter.getFieldName().equalsIgnoreCase("AccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}
}