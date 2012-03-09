package com.picsauditing.report.models;

import com.picsauditing.report.tables.ContractorAudit;

public class QueryAccountContractorAudit extends QueryAccountContractor {
	public QueryAccountContractorAudit() {
		super();

		ContractorAudit conAudit = new ContractorAudit();
		from.addAllFieldsAndJoins(conAudit);
	}
}
