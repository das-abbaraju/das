package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorOperatorTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.Strings;

public class ContractorOperatorModel extends AbstractModel {

	public ContractorOperatorModel(Permissions permissions) {
		super(permissions, new ContractorOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "ContractorOperator");
		spec.join(ContractorOperatorTable.Operator);

		{
			ModelSpec contractor = spec.join(ContractorOperatorTable.Contractor);
			contractor.alias = "Contractor";
			contractor.minimumImportance = FieldImportance.Average;
			{
				ModelSpec account = contractor.join(ContractorTable.Account);
				account.alias = "Account";
				account.minimumImportance = FieldImportance.Average;
			}
		}

		return spec;
	}

	@Override
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		String whereSql = super.getWhereClause(permissions, filters);
		if (permissions.isOperatorCorporate()) {
			String operatorVisibility = getOperatorsThisPersonCanSee(permissions);
			whereSql += " AND ReportingSite.genID IN (" + operatorVisibility + ")";
		}

		return whereSql;
	}

	private String getOperatorsThisPersonCanSee(Permissions permissions) {
		String operatorVisibility = permissions.getAccountIdString();

		if (permissions.isGeneralContractor()) {
			operatorVisibility += "," + Strings.implode(permissions.getLinkedClients());
		} else if (permissions.isCorporate()) {
			operatorVisibility += "," + Strings.implode(permissions.getOperatorChildren());
		}
		return operatorVisibility;
	}
}
