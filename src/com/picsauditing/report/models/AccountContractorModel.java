package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorTable;

public class AccountContractorModel extends AccountModel {
	public AccountContractorModel() {
		super();
		primaryTable.removeField("accountName");
		primaryTable.removeField("accountType");

		ContractorTable contractorTable = new ContractorTable(primaryTable.getPrefix(), primaryTable.getAlias());
		primaryTable.addAllFieldsAndJoins(contractorTable);
		// TODO: Find a better way of passing down the parent table
		parentTable = contractorTable;
	}

}
