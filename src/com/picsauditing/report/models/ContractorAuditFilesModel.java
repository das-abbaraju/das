package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class ContractorAuditFilesModel extends AbstractModel {

    private static final String AUDIT_OPERATOR = "AuditOperator";
    private static final String CONTRACTOR_OPERATOR = "ContractorOperator";

	public static final String ContractorAuditFile = "ContractorAuditFile";

	public ContractorAuditFilesModel(Permissions permissions) {
		super(permissions, new ContractorAuditFileTable());
	}

	public ModelSpec getJoinSpec() {
		// join() param is the name of the ReportForeignKey in the "from" table that points to the table we want to join to

		ModelSpec contractorAuditFile = new ModelSpec(null, ContractorAuditFile, FieldCategory.DocumentsAndAudits);

		// join from ContractorAuditFileTable to ContractorAuditTable
		ModelSpec contractorAudit = contractorAuditFile.join(ContractorAuditFileTable.ContractorAudit);
		contractorAudit.alias = "Audit";    // Needs to agree with "AuditExpiresDate" in order by
        contractorAudit.minimumImportance = FieldImportance.Average;

		// We need to join from ContractorAuditTable to ContractorAccountTable, but since that join is not available in
		// ContractorAuditTable, we'll join to ContractorTable instead (which is actually the contractor_info db table)
		// as an intermediate step.
		ModelSpec contractorInfo = contractorAudit.join(ContractorAuditTable.Contractor);
		contractorInfo.alias = "ContractorInfo";
        contractorInfo.minimumImportance = FieldImportance.Required;

        // Now, join from ContractorTable to ContractorAccountTable
		ModelSpec contractorAccount = contractorInfo.join(ContractorTable.Account);
		contractorAccount.alias = "Account"; // Needs to agree to the param in the setUrl method below
        contractorAccount.minimumImportance = FieldImportance.Required;

		ModelSpec auditor = contractorAudit.join(ContractorAuditTable.Auditor);
        auditor.minimumImportance = FieldImportance.Required;

		ModelSpec closingAuditor = contractorAudit.join(ContractorAuditTable.ClosingAuditor);
        closingAuditor.minimumImportance = FieldImportance.Required;

        ModelSpec cao = contractorAudit.join(ContractorAuditTable.SingleCAO);
        cao.alias = AUDIT_OPERATOR;
        cao.minimumImportance = FieldImportance.Required;

        ModelSpec caoOperator = cao.join(ContractorAuditOperatorTable.Operator);
        caoOperator.alias = "AuditOperatorAccount";
        caoOperator.minimumImportance = FieldImportance.Required;

        return contractorAuditFile;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
        super.getWhereClause(filters);
        permissionQueryBuilder.setContractorOperatorAlias(CONTRACTOR_OPERATOR);

        String where = permissionQueryBuilder.buildWhereClause();

        if (!where.isEmpty()) {
            where += " AND ";
        }

        where += "AuditOperator.visible = 1";

        if (permissions.isOperatorCorporate()) {
            // TODO: This looks like it can be further improved. Find a way to
            // do this without having to implode all of the ids.
            String opIDs = permissions.getAccountIdString();
            if (permissions.isCorporate()) {
                opIDs = Strings.implode(permissions.getOperatorChildren());
            }

            where += "\n AND " + AUDIT_OPERATOR
                    + ".id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs + "))";
        }

        return where;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");
		return fields;
	}
}
