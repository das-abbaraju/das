package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class CreditMemosModel extends AbstractModel {

	public CreditMemosModel(Permissions permissions) {
		super(permissions, new CreditMemoTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "CreditMemo");

		ModelSpec account = spec.join(InvoiceTable.Account);
		account.alias = "Account";
		account.join(AccountTable.Contact);

		ModelSpec contractor = account.join(AccountTable.Contractor);
		contractor.alias = "Contractor";

        ModelSpec contractorTrade = contractor.join(ContractorTable.ContractorTrade);
        ModelSpec directTrade = contractorTrade.join(ContractorTradeTable.Trade);
        directTrade.join(TradeTable.Children);

        contractor.join(ContractorTable.Tag);

        if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
            flag.join(ContractorOperatorTable.ForcedByUser);
        }

        ModelSpec insideSales = contractor.join(ContractorTable.InsideSales);
        insideSales.minimumImportance = FieldImportance.Required;
        ModelSpec insideSalesUser = insideSales.join(AccountUserTable.User);
        insideSalesUser.minimumImportance = FieldImportance.Required;

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		return "CreditMemo.tableType = 'C'";
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
        if (permissions.isAdmin())
		    accountName.setUrl("BillingDetail.action?id={AccountID}");
        else
            accountName.setUrl("ContractorView.action?id={AccountID}");

		return fields;
	}
}
