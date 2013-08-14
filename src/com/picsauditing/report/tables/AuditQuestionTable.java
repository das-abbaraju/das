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
		id.setCategory(FieldCategory.DocumentsAndAudits);
		
		Field auditQuestionName;
		auditQuestionName = new Field("Name", "id", FieldType.AuditQuestion);
		auditQuestionName.setTranslationPrefixAndSuffix("AuditQuestion", "name");
		auditQuestionName.setImportance(FieldImportance.Required);
		auditQuestionName.setCategory(FieldCategory.DocumentsAndAudits);
		auditQuestionName.setWidth(200);
		addField(auditQuestionName);
	}

	public void addJoins() {
        ReportForeignKey category = new ReportForeignKey(Category, new AuditCategoryTable(), new ReportOnClause("categoryID"));
        category.setMinimumImportance(FieldImportance.Low);
        addOptionalKey(category);
	}
}