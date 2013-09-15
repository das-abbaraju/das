package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorTradesModel extends AbstractModel {

	public ContractorTradesModel(Permissions permissions) {
		super(permissions, new ContractorTradeTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "ContractorTrade");

        ModelSpec account = spec.join(ContractorTradeTable.Contractor);
        account.alias = "Account";

        ModelSpec contractor = account.join(AccountTable.Contractor);
        contractor.minimumImportance = FieldImportance.None;

        if (permissions.isOperatorCorporate()) {
            contractor.join(ContractorTable.Flag);
        }

        ModelSpec directTrade = spec.join(ContractorTradeTable.Trade);
        directTrade.alias = "Trade";

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

		return permissionQueryBuilder.buildWhereClause();
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

        Field tradeName = fields.get("TradeName".toUpperCase());
        tradeName.setVisible(true);

		Field clientSite = new Field("ContractorWorksAtClientSite","Account.id",FieldType.Operator);
		clientSite.setVisible(false);
		clientSite.setPrefixValue("SELECT co.subID " +
				"FROM generalcontractors co " +
				"WHERE co.genID IN ");
		clientSite.setSuffixValue("");
		fields.put(clientSite.getName().toUpperCase(), clientSite);

        return fields;
	}
}