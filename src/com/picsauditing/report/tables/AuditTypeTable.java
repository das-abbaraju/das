package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditTypeTable extends AbstractTable {

	public static final String Operator = "Operator";
	public static final String CreatedBy = "CreatedBy";

	public AuditTypeTable() {
		super("audit_type");
		addPrimaryKey().setCategory(FieldCategory.DocumentsAndAudits);
		addFields(AuditType.class);

		Field auditTypeName;
		auditTypeName = new Field("Name", "id", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("ManageAuditType.action?id={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Low);
		auditTypeName.setCategory(FieldCategory.DocumentsAndAudits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);

		Field createdBy;
		createdBy = new Field("CreatedBy", "createdBy", FieldType.UserID);
		createdBy.setImportance(FieldImportance.Low);
		createdBy.setCategory(FieldCategory.DocumentsAndAudits);
		addField(createdBy);

        Field creationDate = addCreationDate();
        creationDate.setCategory(FieldCategory.DocumentsAndAudits);
        creationDate.setImportance(FieldImportance.Low);
	}

	public void addJoins() {
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		operator.setCategory(FieldCategory.ReportingClientSite);
		operator.setMinimumImportance(FieldImportance.Required);
		addOptionalKey(operator);

		ReportForeignKey createdBy = new ReportForeignKey(CreatedBy, new UserTable(), new ReportOnClause("createdBy"));
		createdBy.setCategory(FieldCategory.Auditors);
		createdBy.setMinimumImportance(FieldImportance.Required);
		addOptionalKey(createdBy);
	}
}
