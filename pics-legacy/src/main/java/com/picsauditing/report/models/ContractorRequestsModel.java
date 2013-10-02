package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorRequestsModel extends AbstractModel {

	public ContractorRequestsModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
        ModelSpec contractor = new ModelSpec(null, "Contractor");
        {
            ModelSpec account = contractor.join(ContractorTable.Account);
            account.alias = "Account";
            account.join(AccountTable.Contact);
        }

        ModelSpec contractorTrade = contractor.join(ContractorTable.ContractorTrade);
        contractorTrade.alias = "ContractorTrade";
        ModelSpec directTrade = contractorTrade.join(ContractorTradeTable.Trade);
        directTrade.alias = "DirectTrade";
        ModelSpec trade = directTrade.join(TradeTable.Children);
        trade.alias = "Trade";

        ModelSpec insideSales = contractor.join(ContractorTable.InsideSales);
        insideSales.minimumImportance = FieldImportance.Required;
        ModelSpec insideSalesUser = insideSales.join(AccountUserTable.User);
        insideSalesUser.minimumImportance = FieldImportance.Required;

        contractor.join(ContractorTable.Tag);

        return contractor;
    }

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

        permissionQueryBuilder.setExcludeWorkStatus(true);
        permissionQueryBuilder.setIgnoreDefaultStatuses(true);
        permissionQueryBuilder.addVisibleStatus(AccountStatus.Requested);
        permissionQueryBuilder.addVisibleStatus(AccountStatus.Declined);

		return permissionQueryBuilder.buildWhereClause();
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
        Field accountStatus = new Field("AccountStatus","Account.status",FieldType.RequestedDeclinedAccountStatus);
        fields.put(accountStatus.getName().toUpperCase(), accountStatus);

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        Field accountManager = new Field("AccountManager","Account.id",FieldType.AccountUser);
        accountManager.setVisible(false);
        accountManager.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "JOIN account_user au ON au.accountID = co.genID " +
                "JOIN users u ON au.userID = u.id " +
                "WHERE u.id IN ");
        accountManager.setSuffixValue("");
        fields.put(accountManager.getName().toUpperCase(), accountManager);

        Field clientSite = new Field("ContractorWorksAtClientSite","Account.id",FieldType.Operator);
        clientSite.setVisible(false);
        clientSite.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "WHERE co.genID IN ");
        clientSite.setSuffixValue("");
        fields.put(clientSite.getName().toUpperCase(), clientSite);

        Field reportingClient = new Field("ContractorWorksForReportingClient","Account.id",FieldType.Operator);
        reportingClient.setVisible(false);
        reportingClient.setPrefixValue("SELECT co.subID " +
                "FROM generalcontractors co " +
                "JOIN operators o ON o.id = co.genID " +
                "WHERE o.reportingID IN ");
        reportingClient.setSuffixValue("");
        fields.put(reportingClient.getName().toUpperCase(), reportingClient);

       return fields;
	}
}