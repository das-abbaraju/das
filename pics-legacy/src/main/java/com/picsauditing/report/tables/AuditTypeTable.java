package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditTypeTable extends AbstractTable {

	public static final String Operator = "Operator";
	public static final String CreatedBy = "CreatedBy";

	public AuditTypeTable() {
		super("audit_type");
		Field auditTypeID = addPrimaryKey();
		addFields(AuditType.class);

		Field auditTypeName = new Field("Name", "id", FieldType.AuditType);
        auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
        auditTypeName.setDrillDownField(auditTypeID.getName());
        auditTypeName.setUrl("ManageAuditType.action?id={" + ReportOnClause.ToAlias + "ID}");
        auditTypeName.setWidth(200);
        addField(auditTypeName);

		Field createdBy;
		createdBy = new Field("CreatedBy", "createdBy", FieldType.UserID);
		addField(createdBy);
	}

	public void addJoins() {
		ReportForeignKey operator = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		addOptionalKey(operator);

		ReportForeignKey createdBy = new ReportForeignKey(CreatedBy, new UserTable(), new ReportOnClause("createdBy"));
		addOptionalKey(createdBy);
	}
}
