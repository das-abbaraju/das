package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditTypeTable extends AbstractTable {

	public AuditTypeTable() {
		super("audit_type");
		addPrimaryKey().setCategory(FieldCategory.Audits);
		addFields(AuditType.class);

		Field auditTypeName;
		auditTypeName = new Field("Name", "id", FieldType.String);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Required);
		auditTypeName.setCategory(FieldCategory.Audits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);
	}

	public void addJoins() {
	}
}
