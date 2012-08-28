package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ContractorAuditOperatorTable;
import com.picsauditing.util.Strings;

public class AccountContractorAuditOperatorModel extends AccountContractorAuditModel {
	public AccountContractorAuditOperatorModel() {
		super();

		ContractorAuditOperatorTable caoTable = new ContractorAuditOperatorTable();
		rootTable.addAllFieldsAndJoins(caoTable);
		
		parentTable = caoTable;
	}
	
	@Override
	public String getWhereClause(Permissions permissions) {
		String where = super.getWhereClause(permissions);

		// TODO: This looks like it can be further improved. Find a way to do this without having to implode all of the ids.
		if (permissions.isOperatorCorporate()) {
			String opIDs = permissions.getAccountIdString();
			if (permissions.isCorporate())
				opIDs = Strings.implode(permissions.getOperatorChildren());

			where += "\n AND cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs
					+ "))";
		}
		
		return where;
	}
}