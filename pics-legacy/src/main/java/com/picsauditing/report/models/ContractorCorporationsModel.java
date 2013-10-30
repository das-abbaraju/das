package com.picsauditing.report.models;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class ContractorCorporationsModel extends AbstractModel {

	public static final String CONTRACTOR_CORP = "ContractorCorporation";

	public ContractorCorporationsModel(Permissions permissions) {
		super(permissions, new ContractorOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, CONTRACTOR_CORP);
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
		account.alias = "Account";
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

        Field accountType = new Field("ContractorCorporationOperatorType", "ContractorCorporationOperator.type", FieldType.AccountType);
        accountType.setTranslationPrefixAndSuffix("AccountType", "");
        fields.put("ContractorCorporationOperatorType".toUpperCase(), accountType);

        Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        if (permissions.isOperatorCorporate()) {
            Field operatorName = fields.get("ContractorCorporationOperatorName".toUpperCase());
            operatorName.setUrl("FacilitiesEdit.action?operator={ContractorCorporationOperatorID}");
        }

        return fields;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		// TODO This should be eventually moved into PQB
		if (permissions.isAdmin()) {
			return "";
		}

        String approvedWorkStatus = "";

        if (!permissions.hasPermission(OpPerms.ViewUnApproved)) {
            approvedWorkStatus = CONTRACTOR_CORP + ".workStatus = 'Y' AND ";
        }

		if (permissions.isContractor()) {
			return CONTRACTOR_CORP + ".conID = " + permissions.getAccountId();
		}

		if (permissions.isOperatorCorporate()) {
            return approvedWorkStatus + "Operator.reportingID IN (" + permissions.getPrimaryCorporateAccountID() + ")";
		}

		return "1 = 0";
	}
}