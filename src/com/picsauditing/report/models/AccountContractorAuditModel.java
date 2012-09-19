package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAuditTable;

public class AccountContractorAuditModel extends AccountContractorModel {
	public AccountContractorAuditModel() {
		super();

		ContractorAuditTable conAuditTable = new ContractorAuditTable("audit", "ca", "conID", "a.id");
		conAuditTable.includeAllColumns();
		rootTable.addAllFieldsAndJoins(conAuditTable);

		parentTable = conAuditTable;

		rootTable.removeJoin("contractorCustomerService");
		rootTable.removeJoin("contractorPQF");

		// TODO I think we should remove this join to eliminate confusion
		// But let's leave it until after we can support joining on GC table
		// rootTable.removeJoin("contractorRequestedBy");
	}
}
