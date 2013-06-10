package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class ContractorOperatorsModel extends AbstractModel {

	public static final String CONTRACTOR_OPERATOR = "ContractorOperator";

	public ContractorOperatorsModel(Permissions permissions) {
		super(permissions, new ContractorOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, CONTRACTOR_OPERATOR);
		ModelSpec opAccount = spec.join(ContractorOperatorTable.Operator);
		ModelSpec operator = opAccount.join(AccountTable.Operator);
		operator.alias = "Operator";

		ModelSpec contractor = spec.join(ContractorOperatorTable.Contractor);
		contractor.alias = "Contractor";
		contractor.minimumImportance = FieldImportance.Average;

        contractor.join(ContractorTable.Tag).category = FieldCategory.AccountInformation;

		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = "Account";
		account.minimumImportance = FieldImportance.Average;
		account.join(AccountTable.Contact);

        ModelSpec percentForcedFlag = spec.join(ContractorOperatorTable.ForcedFlagPercent);
        percentForcedFlag.alias = "ForcedFlag";
        percentForcedFlag.category = FieldCategory.AccountInformation;

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

        Field accountType = new Field("ContractorOperatorOperatorType", "ContractorOperatorOperator.type", FieldType.AccountType);
        accountType.setCategory(FieldCategory.ReportingClientSite);
        accountType.setTranslationPrefixAndSuffix("AccountType", "");
        fields.put("ContractorOperatorOperatorType".toUpperCase(), accountType);

        Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        if (permissions.isOperatorCorporate()) {
            Field operatorName = fields.get("ContractorOperatorOperatorName".toUpperCase());
            operatorName.setUrl("FacilitiesEdit.action?operator={ContractorOperatorOperatorID}");
        }

        return fields;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		// TODO This should be eventually moved into PQB
		if (permissions.isAdmin()) {
			return "";
		}

		if (permissions.isContractor()) {
			return CONTRACTOR_OPERATOR + ".subID = " + permissions.getAccountId();
		}

		if (permissions.isOperator()) {
			return CONTRACTOR_OPERATOR + ".workStatus = 'Y' AND " + CONTRACTOR_OPERATOR + ".genID = " + permissions.getAccountId();
		}

		if (permissions.isCorporate()) {
			return CONTRACTOR_OPERATOR + ".workStatus = 'Y' AND " + CONTRACTOR_OPERATOR + ".genID IN (" + Strings.implodeForDB(permissions.getOperatorChildren()) + ")";
		}

		return "1 = 0";
	}
}