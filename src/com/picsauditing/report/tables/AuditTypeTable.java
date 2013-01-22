package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditTypeTable extends AbstractTable {

	public static final String Operator = "Operator";
	public static final String CreatedBy = "CreatedBy";

	public AuditTypeTable() {
		super("audit_type");
		addPrimaryKey().setCategory(FieldCategory.Audits);
		addFields(AuditType.class);

		Field auditTypeName;
		auditTypeName = new Field("Name", "id", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("ManageAuditType.action?id={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Low);
		auditTypeName.setCategory(FieldCategory.Audits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);

		Field createdBy;
		createdBy = new Field("CreatedBy", "createdBy", FieldType.UserID);
		createdBy.setImportance(FieldImportance.Low);
		createdBy.setCategory(FieldCategory.Audits);
		addField(createdBy);

		Field creationDate;
		creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		creationDate.setImportance(FieldImportance.Low);
		creationDate.setCategory(FieldCategory.Audits);
		addField(creationDate);
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
