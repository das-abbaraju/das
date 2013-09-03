package com.picsauditing.report.models;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ForcedFlagsModel extends AbstractModel {

	public ForcedFlagsModel(Permissions permissions) {
		super(permissions, new ForcedFlagView());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "ContractorFlag");

		ModelSpec contractor = spec.join(ForcedFlagView.Contractor);
		ModelSpec account = contractor.join(ContractorTable.Account);
        account.alias = "Account";
        account.minimumImportance = FieldImportance.Required;

        ModelSpec operator = spec.join(ForcedFlagView.Operator);
        operator.alias = "Operator";
        ModelSpec opAccount = operator.join(OperatorTable.Account);
        opAccount.alias = "ContractorOperatorOperator";
        opAccount.minimumImportance = FieldImportance.Required;

        ModelSpec forcedFlagPercent = operator.join(OperatorTable.ForcedFlagPercent);
        forcedFlagPercent.alias = "ForcedFlagPercent";

        ModelSpec accountManager = opAccount.join(AccountTable.AccountManager);
        accountManager.alias = "AccountManager";
        ModelSpec accountManagerUser = accountManager.join(AccountUserTable.User);
        accountManagerUser.alias = "AccountManagerUser";

        ModelSpec forcedBy = spec.join(ForcedFlagView.ForcedByUser);
        forcedBy.alias = "ContractorOperatorForcedByUser";
        forcedBy.minimumImportance = FieldImportance.Required;

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        Field accountType = new Field("ContractorOperatorOperatorType", "ContractorOperatorOperator.type", FieldType.AccountType);
        accountType.setTranslationPrefixAndSuffix("AccountType", "");
        fields.put("ContractorOperatorOperatorType".toUpperCase(), accountType);

        return fields;
	}

    @Override
    public String getWhereClause(List<Filter> filters) {
        super.getWhereClause(filters);
        String where = permissionQueryBuilder.buildWhereClause();

        if (permissions.isContractor()) {
            return "ContractorFlag.conID = " + permissions.getAccountId();
        }

        if (permissions.isOperator()) {
            return where + " AND ContractorFlag.opID = " + permissions.getAccountId();
        }

        if (permissions.isCorporate()) {
            return where + " AND (ContractorFlag.opID = " + permissions.getAccountId() + " OR ContractorFlag.opID IN (SELECT opID FROM facilities WHERE corporateID = " + permissions.getAccountId() + "))";
        }

        return where;
    }
}