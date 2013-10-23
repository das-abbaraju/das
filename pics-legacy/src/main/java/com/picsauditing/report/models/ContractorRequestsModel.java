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

        contractor.join(ContractorTable.LastContactedByInsideSales);

        {
            ModelSpec account = contractor.join(ContractorTable.Account);
            account.alias = "Account";
            account.join(AccountTable.Contact);
        }

        ModelSpec requestedBy = contractor.join(ContractorTable.ContractorOperator);
        requestedBy.alias = "ContractorRequestedBy";
        requestedBy.join(ContractorOperatorTable.Operator);
        requestedBy.join(ContractorOperatorTable.RequestedByUser);

        ModelSpec insideSales = contractor.join(ContractorTable.InsideSales);
        insideSales.minimumImportance = FieldImportance.Required;
        ModelSpec insideSalesUser = insideSales.join(AccountUserTable.User);
        insideSalesUser.minimumImportance = FieldImportance.Required;

        contractor.join(ContractorTable.Tag);

        return contractor;
    }

	@Override
	public String getWhereClause(List<Filter> filters) {
        return "Account.status IN ('Requested','Declined','Pending','Active')";
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

        fields.remove("ContractorRequestedByFlagColor".toUpperCase());

        Field accountStatus = new Field("AccountStatus","Account.status",FieldType.RegistrationRequestAccountStatus);
        fields.put(accountStatus.getName().toUpperCase(), accountStatus);

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("RequestNewContractorAccount.action?contractor={AccountID}");

        Field requestedByOtherUser = new Field("ContractorRequestedByUserOther","ContractorRequestedBy.requestedByUser",FieldType.String);
        fields.put(requestedByOtherUser.getName().toUpperCase(), requestedByOtherUser);

        Field requestedByDeadline = new Field("ContractorRequestedByDeadline","ContractorRequestedBy.deadline",FieldType.String);
        fields.put(requestedByDeadline.getName().toUpperCase(), requestedByDeadline);

        Field accountManager = new Field("AccountManager","Account.id",FieldType.AccountUser);
        accountManager.setVisible(false);
        accountManager.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "JOIN account_user au ON au.accountID = co.opID " +
                "JOIN users u ON au.userID = u.id " +
                "WHERE u.id IN ");
        accountManager.setSuffixValue("");
        fields.put(accountManager.getName().toUpperCase(), accountManager);

        Field clientSite = new Field("ContractorWorksAtClientSite","Account.id",FieldType.Operator);
        clientSite.setVisible(false);
        clientSite.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "WHERE co.opID IN ");
        clientSite.setSuffixValue("");
        fields.put(clientSite.getName().toUpperCase(), clientSite);

        Field reportingClient = new Field("ContractorWorksForReportingClient","Account.id",FieldType.Operator);
        reportingClient.setVisible(false);
        reportingClient.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "JOIN operators o ON o.id = co.opID " +
                "WHERE o.reportingID IN ");
        reportingClient.setSuffixValue("");
        fields.put(reportingClient.getName().toUpperCase(), reportingClient);

       return fields;
	}
}