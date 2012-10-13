package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldImportance;

public class ContractorSearchModel extends AbstractModel {

	public ContractorSearchModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		// 3. Default sorting: Network Level, Confidence Rating, Best Match
		// (location, trade, name)

		ModelSpec contractor = new ModelSpec(null, "Contractor");
		contractor.minimumImportance = FieldImportance.Required;

		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = "Account";
		account.minimumImportance = FieldImportance.Required;

		ModelSpec contractorOperator = account.join(AccountTable.ContractorOperator);
		contractorOperator.alias = "ContractorOperator";

		return contractor;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		Field networkLevel = fields.get("ContractorOperatorNetworkLevel".toUpperCase());
		networkLevel.setVisible(true);
		networkLevel.setFilterable(true);

		return fields;
	}
}
