package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditQuestionTable extends AbstractTable {
	
	public static final String Category = "Category";

	public AuditQuestionTable() {
		super("audit_question");
		addFields(AuditQuestion.class);
		Field id = addPrimaryKey();
		id.setCategory(FieldCategory.Audits);
		
		Field auditQuestionName;
		auditQuestionName = new Field("Name", "id", FieldType.AuditQuestion);
		auditQuestionName.setTranslationPrefixAndSuffix("AuditQuestion", "name");
		auditQuestionName.setUrl("ManageQuestion.action?id={" + ReportOnClause.ToAlias + "ID}");
		auditQuestionName.setImportance(FieldImportance.Required);
		auditQuestionName.setCategory(FieldCategory.Audits);
		auditQuestionName.setWidth(200);
		addField(auditQuestionName);
	}

	public void addJoins() {
		addOptionalKey(new ReportForeignKey(Category, new AuditCategoryTable(), new ReportOnClause("categoryID")));
	}
}