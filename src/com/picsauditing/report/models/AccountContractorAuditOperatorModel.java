package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAuditOperator;

public class AccountContractorAuditOperatorModel extends AccountContractorAuditModel {
	public AccountContractorAuditOperatorModel() {
		super();

		ContractorAuditOperator cao = new ContractorAuditOperator();
		primaryTable.addAllFieldsAndJoins(cao);
	}
}
