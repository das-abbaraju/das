package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.ContractorAuditTable
import com.picsauditing.report.tables.FieldCategory

public class AccountContractorAuditModel extends AbstractModel {
	static def joinSpec = [
		alias: "Audit",
		joins: [
			[
				alias: "Account",
				joins: [
					[
						key: "Contact",
						category: FieldCategory.ContactInformation
					],[
						key: "Naics"
					]
				]
			],[
				key: "Naics"
			]
		]
	]

	public Map getJoinSpec() {
		return joinSpec;
	}

	public AccountContractorAuditModel(Permissions permissions) {
		super(permissions, new ContractorAuditTable())
//		rootTable.removeJoin("contractorCustomerService");
//		rootTable.removeJoin("contractorPQF");
	}
}
