package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorOperatorTable;

public class ContractorOperatorModel extends AccountContractorModel {

	public ContractorOperatorModel() {
		super();
		
		ContractorOperatorTable contractorOperatorTable = new ContractorOperatorTable(rootTable.getPrefix(), rootTable.getAlias());
		rootTable.addAllFieldsAndJoins(contractorOperatorTable);
		
		parentTable = contractorOperatorTable;
	}
}
