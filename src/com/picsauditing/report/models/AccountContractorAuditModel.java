package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAudit;

public class AccountContractorAuditModel extends AccountContractorModel {
	public AccountContractorAuditModel() {
		super();

		ContractorAudit conAudit = new ContractorAudit();
		primaryTable.addAllFieldsAndJoins(conAudit);
	}
}
