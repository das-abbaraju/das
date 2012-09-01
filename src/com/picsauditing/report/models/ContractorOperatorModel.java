package com.picsauditing.report.models;

import java.util.List;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.ContractorOperatorTable;
import com.picsauditing.util.Strings;

public class ContractorOperatorModel extends AccountContractorModel {

	public ContractorOperatorModel() {
		super();
		
		ContractorOperatorTable contractorOperatorTable = new ContractorOperatorTable(rootTable.getPrefix(), rootTable.getAlias());
		rootTable.addAllFieldsAndJoins(contractorOperatorTable);
		
		parentTable = contractorOperatorTable;
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

			whereSql += " AND gc.genID IN (" + operatorVisibility + ")";
		}
		
		return whereSql;
	}
}
