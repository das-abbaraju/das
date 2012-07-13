package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAuditOperatorTable;

public class AccountContractorAuditOperatorModel extends AccountContractorAuditModel {
	public AccountContractorAuditOperatorModel() {
		super();

		ContractorAuditOperatorTable caoTable = new ContractorAuditOperatorTable();
		rootTable.addAllFieldsAndJoins(caoTable);
		
		parentTable = caoTable;
	}
}
