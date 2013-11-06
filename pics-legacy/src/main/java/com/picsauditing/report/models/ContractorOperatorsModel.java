package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class ContractorOperatorsModel extends AbstractModel {

	public static final String CONTRACTOR_OPERATOR = "ContractorOperator";
    public static final String ACCOUNT = "Account";

	public ContractorOperatorsModel(Permissions permissions) {
		super(permissions, new ContractorOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, CONTRACTOR_OPERATOR);
		ModelSpec opAccount = spec.join(ContractorOperatorTable.Operator);
        opAccount.minimumImportance = FieldImportance.Average;
		ModelSpec operator = opAccount.join(AccountTable.Operator);
		operator.alias = "Operator";

        ModelSpec percentForcedFlag = operator.join(OperatorTable.ForcedFlagPercent);
        percentForcedFlag.alias = "ForcedFlag";

        ModelSpec reportingClient = operator.join(OperatorTable.Reporting);
        reportingClient.alias = "ReportingClient";

        ModelSpec accountManager = opAccount.join(AccountTable.AccountManager);
        accountManager.alias = "AccountManager";

        ModelSpec accountManagerUser = accountManager.join(AccountUserTable.User);
        accountManagerUser.alias = "AccountManagerUser";

        ModelSpec salesRep = opAccount.join(AccountTable.SalesRep);
        salesRep.join(AccountUserTable.User);

        ModelSpec contractor = spec.join(ContractorOperatorTable.Contractor);
		contractor.alias = "Contractor";
		contractor.minimumImportance = FieldImportance.Average;

        contractor.join(ContractorTable.Watch);
        contractor.join(ContractorTable.Tag);

        ModelSpec csr = contractor.join(ContractorTable.CustomerService);
        csr.alias = "CustomerService";
        ModelSpec csrUser = csr.join(AccountUserTable.User);
        csrUser.alias = "CustomerServiceUser";

		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = ACCOUNT;
		account.minimumImportance = FieldImportance.Average;
		account.join(AccountTable.Contact);

        ModelSpec contractorTrade = contractor.join(ContractorTable.ContractorTrade);
        ModelSpec directTrade = contractorTrade.join(ContractorTradeTable.Trade);
        directTrade.join(TradeTable.Children);

        account.join(AccountTable.ContractorRenewalPredictor);
		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

        Field accountType = new Field("ContractorOperatorOperatorType", "ContractorOperatorOperator.type", FieldType.AccountType);
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
        super.getWhereClause(filters);
        permissionQueryBuilder.setContractorOperatorAlias(CONTRACTOR_OPERATOR);

        String whereClause = permissionQueryBuilder.buildWhereClause();

		if (permissions.isCorporate()) {
            return whereClause + " AND " + CONTRACTOR_OPERATOR + ".opID IN (" + Strings.implodeForDB(permissions.getOperatorChildren()) + ")";
		}

		return whereClause;
	}
}