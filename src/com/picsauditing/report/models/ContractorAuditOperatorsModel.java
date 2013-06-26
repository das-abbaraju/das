package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class ContractorAuditOperatorsModel extends AbstractModel {
	public static final String CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL = "ContractorsRequiringAnnualUpdateEmail";
	private static final String AUDIT_OPERATOR = "AuditOperator";
	private static final String CONTRACTOR_OPERATOR = "ContractorOperator";

	public ContractorAuditOperatorsModel(Permissions permissions) {
		super(permissions, new ContractorAuditOperatorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, AUDIT_OPERATOR);

		ModelSpec operatorAccount = spec.join(ContractorAuditOperatorTable.Operator);
		operatorAccount.alias = "AuditOperatorAccount";
		operatorAccount.category = FieldCategory.MonitoringClientSite;

		ModelSpec conAudit = spec.join(ContractorAuditOperatorTable.Audit);
		conAudit.alias = "Audit";
		conAudit.join(ContractorAuditTable.Type);
		conAudit.join(ContractorAuditTable.Auditor);
		conAudit.join(ContractorAuditTable.ClosingAuditor);

        ModelSpec previousAudit = conAudit.join(ContractorAuditTable.PreviousAudit);
        previousAudit.alias = "PreviousAudit";
        previousAudit.minimumImportance = FieldImportance.Average;
        previousAudit.category = FieldCategory.DocumentsAndAudits;

		ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
		contractor.alias = "Contractor";
		contractor.minimumImportance = FieldImportance.Average;
		contractor.category = FieldCategory.AccountInformation;
		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = AbstractModel.ACCOUNT;
		account.minimumImportance = FieldImportance.Average;
		account.category = FieldCategory.AccountInformation;

        ModelSpec pqf = contractor.join(ContractorTable.PQF);
        pqf.alias = "PQF";
        pqf.minimumImportance = FieldImportance.Required;
        ModelSpec manual = pqf.join(ContractorAuditTable.SafetyManual);
        manual.alias = "SafetyManual";
        manual.minimumImportance = FieldImportance.Average;
        manual.category = FieldCategory.DocumentsAndAudits;

		if (permissions.isOperatorCorporate()) {
			ModelSpec flag = contractor.join(ContractorTable.Flag);
			flag.alias = CONTRACTOR_OPERATOR;
			flag.minimumImportance = FieldImportance.Average;
			flag.category = FieldCategory.AccountInformation;
		}

        ModelSpec fatalities = conAudit.join(ContractorAuditTable.Fatalities);
        fatalities.alias = "Fatalities";

        ModelSpec trir = conAudit.join(ContractorAuditTable.Trir);
        trir.alias = "Trir";

        ModelSpec emr = conAudit.join(ContractorAuditTable.Emr);
        emr.alias = "Emr";

        return spec;
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

        Field fatalities = fields.get("FatalitiesAnswer".toUpperCase());
        fatalities.setType(FieldType.Number);
        Field trir = fields.get("TrirAnswer".toUpperCase());
        trir.setType(FieldType.Number);
        Field emr = fields.get("EmrAnswer".toUpperCase());
        emr.setType(FieldType.Number);

        Field contractorsRequiringAnnualUpdateEmail = new Field(
				CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL,
				"(Audit.auditTypeID = 1 AND AuditOperator.status = 'Resubmit') " +
				"OR (Audit.auditTypeID = 11 AND Audit.auditFor = 2012 AND AuditOperator.status = 'Pending') " +
				"OR (AuditType.classType = 'PQF' AND DATE(Audit.expiresDate) = '2013-03-15' AND AuditOperator.status = 'Pending')",
				FieldType.Boolean);
		contractorsRequiringAnnualUpdateEmail.requirePermission(OpPerms.DevelopmentEnvironment);
		fields.put(CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL.toUpperCase(), contractorsRequiringAnnualUpdateEmail);

		return fields;
	}
}