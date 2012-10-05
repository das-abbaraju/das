package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorOperatorTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldImportance;

public class ContractorOperatorModel extends AbstractModel {

	public static final String CONTRACTOR_OPERATOR = "ContractorOperator";

	public ContractorOperatorModel(Permissions permissions) {
		super(permissions, new ContractorOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, CONTRACTOR_OPERATOR);
		spec.join(ContractorOperatorTable.Operator);

		{
			ModelSpec contractor = spec.join(ContractorOperatorTable.Contractor);
			contractor.alias = "Contractor";
			contractor.minimumImportance = FieldImportance.Average;
			{
				ModelSpec account = contractor.join(ContractorTable.Account);
				account.alias = "Account";
				account.minimumImportance = FieldImportance.Average;
				account.join(AccountTable.Contact);
			}
		}

		return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
		permissionQueryBuilder.setContractorOperatorAlias(CONTRACTOR_OPERATOR);

		return permissionQueryBuilder.buildWhereClause();
	}
}