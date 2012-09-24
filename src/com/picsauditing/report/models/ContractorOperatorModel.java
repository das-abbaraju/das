package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.Filter
import com.picsauditing.report.tables.ContractorOperatorTable
import com.picsauditing.report.tables.FieldCategory
import com.picsauditing.util.Strings

public class ContractorOperatorModel extends AbstractModel {
	static def joinSpec = [
		alias: "ReportingSite",
		joins: [
			[
				key: "Contact",
				category: FieldCategory.ContactInformation
			],[
				key: "Naics"
			]
		]
	]

	public Map getJoinSpec() {
		return joinSpec;
	}

	public ContractorOperatorModel(Permissions permissions) {
		super(permissions, new ContractorOperatorTable())
	}


	@Override
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		String whereSql = super.getWhereClause(permissions, filters);
		if (permissions.isOperatorCorporate()) {
			String operatorVisibility = permissions.getAccountIdString();

			if (permissions.isGeneralContractor()) {
				operatorVisibility += "," + Strings.implode(permissions.getLinkedClients());
			}
			else if (permissions.isCorporate()) {
				operatorVisibility += "," + Strings.implode(permissions.getOperatorChildren());
			}

			whereSql += " AND reportingSite.genID IN (" + operatorVisibility + ")";
		}
		
		return whereSql;
	}
}
