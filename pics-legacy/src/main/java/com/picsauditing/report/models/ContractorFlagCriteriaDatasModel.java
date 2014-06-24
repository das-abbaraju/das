package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class ContractorFlagCriteriaDatasModel extends AbstractModel {

	public static final String FLAG_CRITERIA_LABEL = "FlagCriteriaLabel";
	public static final String FLAG_CRITERIA_DESCRIPTION = "FlagCriteriaDescription";
    public static final String CONTRACTOR_OPERATOR = "ContractorFlag";

	public ContractorFlagCriteriaDatasModel(Permissions permissions) {
		super(permissions, new FlagDataTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "FlagData");

		ModelSpec contractorOperator = spec.join(FlagDataTable.ContractorOperator);
		contractorOperator.alias = CONTRACTOR_OPERATOR;
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
        customerService.minimumImportance = FieldImportance.Required;

        ModelSpec customerServiceUser = customerService.join(AccountUserTable.User);
        customerServiceUser.alias = "CustomerServiceUser";

        ModelSpec override = spec.join(FlagDataTable.Override);
		override.minimumImportance = FieldImportance.Required;

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        Field flagCriteriaID = new Field("FlagCriteriaID", "FlagCriteria.id", FieldType.Integer);
        flagCriteriaID.setFilterable(false);
        flagCriteriaID.setVisible(false);
        flagCriteriaID.setImportance(FieldImportance.Required);
        fields.put(flagCriteriaID.getName().toUpperCase(), flagCriteriaID);

		Field flagCriteriaLabel = new Field(FLAG_CRITERIA_LABEL, "FlagCriteria.id", FieldType.FlagCriteria);
		flagCriteriaLabel.setTranslationPrefixAndSuffix("FlagCriteria", "label");
        flagCriteriaLabel.setDrillDownField(flagCriteriaID.getName());
		fields.put(FLAG_CRITERIA_LABEL.toUpperCase(), flagCriteriaLabel);

		Field flagCriteriaDescription = new Field(FLAG_CRITERIA_DESCRIPTION, "FlagCriteria.id", FieldType.String);
		flagCriteriaDescription.setTranslationPrefixAndSuffix("FlagCriteria", "description");
        flagCriteriaDescription.setDrillDownField(flagCriteriaID.getName());
		flagCriteriaDescription.setWidth(500);
		fields.put(FLAG_CRITERIA_DESCRIPTION.toUpperCase(), flagCriteriaDescription);

		return fields;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
        permissionQueryBuilder.setContractorOperatorAlias(CONTRACTOR_OPERATOR);

		Filter accountStatusFilter = getValidAccountStatusFilter(filters);

		if (accountStatusFilter != null) {
			for (String filterValue : accountStatusFilter.getValues()) {
				AccountStatus filterStatus = AccountStatus.valueOf(filterValue);
				if (filterStatus.isVisibleTo(permissions)) {
					permissionQueryBuilder.addVisibleStatus(filterStatus);
				}
			}
		}

        String whereClause = permissionQueryBuilder.buildWhereClause();

        if (permissions.isCorporate()) {
            return whereClause + " AND " + CONTRACTOR_OPERATOR + ".opID IN (" + Strings.implodeForDB(permissions.getOperatorChildren()) + ")";
        }

        return whereClause;
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