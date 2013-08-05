package com.picsauditing.report.models;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class ContractorAuditOperatorWorkflowsModel extends AbstractModel {
	private static final String CAOW = "AuditOperatorWorkflow";

    private String auditOperator = "AuditOperator";

	public ContractorAuditOperatorWorkflowsModel(Permissions permissions) {
		super(permissions, new ContractorAuditOperatorWorkflowTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, CAOW);

        spec.join(ContractorAuditOperatorWorkflowTable.User);

        ModelSpec cao = spec.join(ContractorAuditOperatorWorkflowTable.CAO);
        cao.alias = "AuditOperator";
        auditOperator = cao.alias;

        ModelSpec operatorAccount = cao.join(ContractorAuditOperatorTable.Operator);
		operatorAccount.alias = "AuditOperatorAccount";
		operatorAccount.category = FieldCategory.MonitoringClientSite;

		ModelSpec conAudit = cao.join(ContractorAuditOperatorTable.Audit);
		conAudit.alias = "Audit";
		conAudit.join(ContractorAuditTable.Type);
		conAudit.join(ContractorAuditTable.Auditor);
		conAudit.join(ContractorAuditTable.ClosingAuditor);

		ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
		contractor.alias = "Contractor";
		contractor.minimumImportance = FieldImportance.Average;
		contractor.category = FieldCategory.AccountInformation;
		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = AbstractModel.ACCOUNT;
		account.minimumImportance = FieldImportance.Average;
		account.category = FieldCategory.AccountInformation;

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

		String where = permissionQueryBuilder.buildWhereClause();

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