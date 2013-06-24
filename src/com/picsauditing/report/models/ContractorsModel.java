package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

public class ContractorsModel extends AbstractModel {

	public ContractorsModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractor = new ModelSpec(null, "Contractor");
		{
			ModelSpec account = contractor.join(ContractorTable.Account);
			account.alias = "Account";
			account.join(AccountTable.Contact).category = FieldCategory.ContactInformation;
			account.join(AccountTable.Naics);
			account.join(AccountTable.Country);
            account.join(AccountTable.LastLogin);
		}
		contractor.join(ContractorTable.PQF);
        ModelSpec contractorTrade = contractor.join(ContractorTable.ContractorTrade);
        contractorTrade.alias = "ContractorTrade";
        ModelSpec directTrade = contractorTrade.join(ContractorTradeTable.Trade);
        directTrade.alias = "DirectTrade";
        ModelSpec trade = directTrade.join(TradeTable.Children);
        trade.alias = "Trade";

        if (permissions.isAdmin()) {
			ModelSpec welcomeCall = contractor.join(ContractorTable.WelcomeCall);
			welcomeCall.join(ContractorAuditTable.SingleCAO);
		}


		if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
			flag.join(ContractorOperatorTable.ForcedByUser);
		}
        else {
            ModelSpec contractorOperator = contractor.join(ContractorTable.ContractorOperator);
            contractorOperator.minimumImportance = FieldImportance.None;
            ModelSpec opAccount = contractorOperator.join(ContractorOperatorTable.Operator);
            opAccount.alias = "ContractorOperatorOperator";
            opAccount.category = FieldCategory.ReportingClientSite;
        }

		ModelSpec csr = contractor.join(ContractorTable.CustomerService);
        csr.minimumImportance = FieldImportance.Required;
        ModelSpec csrUser = csr.join(AccountUserTable.User);
        csrUser.category = FieldCategory.CustomerService;
        csrUser.minimumImportance = FieldImportance.Required;

        ModelSpec insideSales = contractor.join(ContractorTable.InsideSales);
        insideSales.minimumImportance = FieldImportance.Required;
        ModelSpec insideSalesUser = insideSales.join(AccountUserTable.User);
        insideSalesUser.category = FieldCategory.CustomerService;
        insideSalesUser.minimumImportance = FieldImportance.Required;

        contractor.join(ContractorTable.RecommendedCSR);
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
			if (filter.getName().equalsIgnoreCase("AccountStatus") && filter.isValid()) {
				return filter;
			}
		}
		return null;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        if (permissions.isOperatorCorporate()) {
            Field flagColor = fields.get("ContractorFlagFlagColor".toUpperCase());
            flagColor.setUrl("ContractorFlag.action?id={AccountID}");
        }
        else {
            Field contractorOperatorOperatorID = fields.get("ContractorOperatorOperatorID".toUpperCase());
            contractorOperatorOperatorID.setVisible(false);
            contractorOperatorOperatorID.setType(FieldType.Operator);
            Field contractorOperatorOperatorName = fields.get("ContractorOperatorOperatorName".toUpperCase());
            contractorOperatorOperatorName.setVisible(false);
            contractorOperatorOperatorName.setFilterable(false);
        }

        return fields;
	}
}