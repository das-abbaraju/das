package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.ContractorAuditOperatorTable;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.Strings;

public class AccountContractorAuditOperatorModel extends AbstractModel {
	// rootTable.removeJoin("accountContact");

	public AccountContractorAuditOperatorModel(Permissions permissions) {
		super(permissions, new ContractorAuditOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "CAO");
		spec.join(ContractorAuditOperatorTable.Operator);
		{
			ModelSpec conAudit = spec.join(ContractorAuditOperatorTable.Audit);
			conAudit.join(ContractorAuditTable.Type);
			{
				ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
				contractor.minimumImportance = FieldImportance.Average;
				contractor.join(ContractorTable.Account).minimumImportance = FieldImportance.Average;
			}
		}
		return spec;
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