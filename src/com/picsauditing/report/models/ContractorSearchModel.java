package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldImportance;

public class ContractorSearchModel extends AbstractModel {

	public ContractorSearchModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractor = new ModelSpec(null, "Contractor");
		contractor.minimumImportance = FieldImportance.Required;
		{
			ModelSpec account = contractor.join(ContractorTable.Account);
			account.alias = "Account";
			account.minimumImportance = FieldImportance.Required;
		}
		return contractor;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		return "";
	}
}
