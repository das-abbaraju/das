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
        percentForcedFlag.category = FieldCategory.AccountInformation;

        ModelSpec reportingClient = operator.join(OperatorTable.Reporting);
        reportingClient.alias = "ReportingClient";
        reportingClient.category = FieldCategory.ReportingClientSite;

        ModelSpec accountManager = opAccount.join(AccountTable.AccountManager);
        accountManager.alias = "AccountManager";
        accountManager.category = FieldCategory.CustomerService;

        ModelSpec accountManagerUser = accountManager.join(AccountUserTable.User);
        accountManagerUser.alias = "AccountManagerUser";
        accountManagerUser.category = FieldCategory.CustomerService;

        ModelSpec contractor = spec.join(ContractorOperatorTable.Contractor);
		contractor.alias = "Contractor";
		contractor.minimumImportance = FieldImportance.Average;

        contractor.join(ContractorTable.Tag).category = FieldCategory.AccountInformation;

        ModelSpec csr = contractor.join(ContractorTable.CustomerService);
        csr.alias = "CustomerService";
        ModelSpec csrUser = csr.join(AccountUserTable.User);
        csrUser.alias = "CustomerServiceUser";

		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = "Account";
		account.minimumImportance = FieldImportance.Average;
		account.join(AccountTable.Contact);

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
        if (!permissions.isPicsEmployee()) {
            fields.remove("ContractorOperatorBaselineFlag".toUpperCase());
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
            approvedWorkStatus = CONTRACTOR_OPERATOR + ".workStatus = 'Y' AND ";
        }

		if (permissions.isContractor()) {
			return CONTRACTOR_OPERATOR + ".subID = " + permissions.getAccountId();
		}

		if (permissions.isOperator()) {
			return approvedWorkStatus + CONTRACTOR_OPERATOR + ".genID = " + permissions.getAccountId();
		}

		if (permissions.isCorporate()) {
            return approvedWorkStatus + CONTRACTOR_OPERATOR + ".genID IN (" + Strings.implodeForDB(permissions.getOperatorChildren()) + ")";
		}

		return "1 = 0";
	}
}