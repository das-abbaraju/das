package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
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
	public String getWhereClause(Permissions permissions) {
		String whereSql = super.getWhereClause(permissions);
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
