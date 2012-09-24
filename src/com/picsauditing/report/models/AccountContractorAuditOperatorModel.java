package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.Filter
import com.picsauditing.report.tables.ContractorAuditOperatorTable
import com.picsauditing.report.tables.FieldCategory
import com.picsauditing.util.Strings

public class AccountContractorAuditOperatorModel extends AbstractModel {
	static def joinSpec = [
		alias: "Account",
		joins: [
			[
				key: "Contact",
				category: FieldCategory.ContactInformation
			],[
				key: "Naics"
			]
		]
	]
//	rootTable.getTable("account").includeRequiredAndAverageColumns();
//	rootTable.getTable("contractor").includeRequiredAndAverageColumns();
//	rootTable.removeJoin("accountContact");

	public Map getJoinSpec() {
		return joinSpec;
	}

	public AccountContractorAuditOperatorModel(Permissions permissions) {
		super(permissions, new ContractorAuditOperatorTable())
	}

	@Override
	public String getWhereClause(Permissions permissions, List<Filter> filters) {
		String where = super.getWhereClause(permissions, filters);

		if (permissions.isOperatorCorporate()) {
			// TODO: This looks like it can be further improved. Find a way to
			// do this without having to implode all of the ids.
			String opIDs = permissions.getAccountIdString();
			if (permissions.isCorporate())
				opIDs = Strings.implode(permissions.getOperatorChildren());

			where += "\n AND cao.id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs
					+ "))";
		}

		return where;
	}
}