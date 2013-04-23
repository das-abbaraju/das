package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditCategoryTable extends AbstractTable {

	public AuditCategoryTable() {
		super("audit_category");
		addFields(AuditCategory.class);
		Field id = addPrimaryKey();
		id.setCategory(FieldCategory.DocumentsAndAudits);
		
		Field auditQuestionName;
		auditQuestionName = new Field("Name", "id", FieldType.AuditCategory);
		auditQuestionName.setTranslationPrefixAndSuffix("AuditCategory", "name");
		auditQuestionName.setUrl("ManageCategory.action?id={" + ReportOnClause.ToAlias + "ID}");
		auditQuestionName.setImportance(FieldImportance.Required);
		auditQuestionName.setCategory(FieldCategory.DocumentsAndAudits);
		auditQuestionName.setWidth(200);
		addField(auditQuestionName);
	}

	public void addJoins() {
	}
}