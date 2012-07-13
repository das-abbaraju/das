package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorTable;

public class AccountContractorModel extends AccountModel {
	public AccountContractorModel() {
		super();
		rootTable.removeField("accountName");
		rootTable.removeField("accountType");

		ContractorTable contractorTable = new ContractorTable(rootTable.getPrefix(), rootTable.getAlias());
		rootTable.addAllFieldsAndJoins(contractorTable);

		parentTable = contractorTable;
	}

}
