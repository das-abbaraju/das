package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAuditTable;

public class AccountContractorAuditModel extends AccountContractorModel {
	public AccountContractorAuditModel() {
		super();

		ContractorAuditTable conAuditTable = new ContractorAuditTable();
		rootTable.addAllFieldsAndJoins(conAuditTable);

		parentTable = conAuditTable;
	}
}
