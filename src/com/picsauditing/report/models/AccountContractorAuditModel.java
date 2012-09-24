package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;

public class AccountContractorAuditModel extends AbstractModel {
	public AccountContractorAuditModel(Permissions permissions) {
		super(permissions, new ContractorAuditTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec conAudit = new ModelSpec(null, "Audit");
		{
			ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
			contractor.join(ContractorTable.Account);
			contractor.join(ContractorTable.Flag);
		}
		conAudit.join(ContractorAuditTable.Auditor);
		conAudit.join(ContractorAuditTable.ClosingAuditor);
		conAudit.join(ContractorAuditTable.Type);
		return conAudit;
	}
}
