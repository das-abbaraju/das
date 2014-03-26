package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditCategoryTable extends AbstractTable {

	public AuditCategoryTable() {
		super("audit_category");
		addFields(AuditCategory.class);
		Field auditCategoryID = addPrimaryKey();

        Field auditCategoryName = new Field("Name", "id", FieldType.AuditCategory);
		auditCategoryName.setTranslationPrefixAndSuffix("AuditCategory", "name");
        auditCategoryName.setDrillDownField(auditCategoryID.getName());
		auditCategoryName.setUrl("ManageCategory.action?id={" + ReportOnClause.ToAlias + "ID}");
		auditCategoryName.setImportance(FieldImportance.Required);
		auditCategoryName.setWidth(200);
		addField(auditCategoryName);
	}

	public void addJoins() {
	}
}