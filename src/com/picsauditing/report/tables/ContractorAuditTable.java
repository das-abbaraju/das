package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorAuditTable extends AbstractTable {

	public ContractorAuditTable(String prefix, String alias, String toForeignKey, String fromForeignKey) {
		super("contractor_audit", prefix, alias, alias + "." + toForeignKey + " = " + fromForeignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer).setCategory(FieldCategory.Audits);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date).setCategory(FieldCategory.Audits);

		addFields(com.picsauditing.jpa.entities.ContractorAudit.class);

		Field auditTypeName;
		auditTypeName = addField(prefix + "Name", alias + ".auditTypeID", FilterType.String).setCategory(FieldCategory.Audits);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + prefix + "ID}");
		auditTypeName.setWidth(200);
	}

	public void addJoins() {
		AuditTypeTable auditType = new AuditTypeTable(prefix + "Type", alias + ".auditTypeID");
		auditType.includeRequiredAndAverageColumns();
		addLeftJoin(auditType);

		UserTable auditor = new UserTable(prefix + "Auditor", alias + ".auditorID");
		auditor.setOverrideCategory(FieldCategory.Auditors);
		auditor.includeOnlyRequiredColumns();
		addLeftJoin(auditor);

		UserTable closingAuditor = new UserTable(prefix + "ClosingAuditor", alias + ".closingAuditorID");
		closingAuditor.setOverrideCategory(FieldCategory.Auditors);
		closingAuditor.includeOnlyRequiredColumns();
		addLeftJoin(closingAuditor);

		// TODO: Add auditDataTable
	}
}