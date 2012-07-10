package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAuditOperator;

public class QueryAccountContractorAuditOperator extends QueryAccountContractorAudit {
	public QueryAccountContractorAuditOperator() {
		super();

		ContractorAuditOperator cao = new ContractorAuditOperator();
		from.addAllFieldsAndJoins(cao);
	}
}
