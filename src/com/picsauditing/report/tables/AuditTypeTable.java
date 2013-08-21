package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditTypeTable extends AbstractTable {

	public static final String Operator = "Operator";
	public static final String CreatedBy = "CreatedBy";

	public AuditTypeTable() {
		super("audit_type");
		addPrimaryKey();
		addFields(AuditType.class);

		Field auditTypeName;
		auditTypeName = new Field("Name", "id", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("ManageAuditType.action?id={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Low);
		auditTypeName.setWidth(200);
		addField(auditTypeName);

		Field createdBy;
		createdBy = new Field("CreatedBy", "createdBy", FieldType.UserID);
		createdBy.setImportance(FieldImportance.Low);
		addField(createdBy);

        Field creationDate = addCreationDate();
        creationDate.setImportance(FieldImportance.Low);
	}

	public void addJoins() {
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		addOptionalKey(operator);

		ReportForeignKey createdBy = new ReportForeignKey(CreatedBy, new UserTable(), new ReportOnClause("createdBy"));
		addOptionalKey(createdBy);
	}
}
