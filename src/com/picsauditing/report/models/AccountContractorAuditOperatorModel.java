package com.picsauditing.report.models;

import java.util.List;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.tables.ContractorAuditOperatorTable;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.util.Strings;

public class AccountContractorAuditOperatorModel extends AbstractModel {
	private static final String AUDIT_OPERATOR = "AuditOperator";
	private static final String CONTRACTOR_OPERATOR = "ContractorOperator";

	public AccountContractorAuditOperatorModel(Permissions permissions) {
		super(permissions, new ContractorAuditOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, AUDIT_OPERATOR);
		// Let's try to set the categories on the entity fields
		// spec.category = FieldCategory.Audits;

		ModelSpec operatorAccount = spec.join(ContractorAuditOperatorTable.Operator);
		operatorAccount.alias = "AuditOperatorAccount";
		operatorAccount.category = FieldCategory.ClientSiteMonitoringAnAudit;
		{
			ModelSpec conAudit = spec.join(ContractorAuditOperatorTable.Audit);
			conAudit.alias = "Audit";
			// Let's try to set the categories on the entity fields
			// conAudit.category = FieldCategory.Audits;
			conAudit.join(ContractorAuditTable.Type);
			conAudit.join(ContractorAuditTable.Auditor);
			conAudit.join(ContractorAuditTable.ClosingAuditor);
			{
				ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
				contractor.alias = "Contractor";
				contractor.minimumImportance = FieldImportance.Average;
				contractor.category = FieldCategory.AccountInformation;
				ModelSpec account = contractor.join(ContractorTable.Account);
				account.alias = AbstractModel.ACCOUNT;
				account.minimumImportance = FieldImportance.Average;
				account.category = FieldCategory.AccountInformation;

				if (permissions.isOperatorCorporate()) {
					ModelSpec flag = contractor.join(ContractorTable.Flag);
					flag.alias = CONTRACTOR_OPERATOR;
					flag.minimumImportance = FieldImportance.Average;
					flag.category = FieldCategory.AccountInformation;
				}
			}
		}
		return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
		permissionQueryBuilder.setContractorOperatorAlias(CONTRACTOR_OPERATOR);

		String where = permissionQueryBuilder.buildWhereClause() + " AND AuditOperator.visible = 1";

		if (permissions.isOperatorCorporate()) {
			// TODO: This looks like it can be further improved. Find a way to
			// do this without having to implode all of the ids.
			String opIDs = permissions.getAccountIdString();
			if (permissions.isCorporate())
				opIDs = Strings.implode(permissions.getOperatorChildren());

			where += "\n AND " +
					AUDIT_OPERATOR +
					".id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs
					+ "))";
		}

		return where;
	}
}