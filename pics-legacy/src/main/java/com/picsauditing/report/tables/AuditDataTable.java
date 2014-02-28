package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditDataTable extends AbstractTable {

    public static final String Audit = "Audit";
    public static final String Question = "Question";

    public AuditDataTable() {
        super("pqfData");
        addFields(AuditData.class);

        Field questionDate = new Field("AnswerAsDate","answer", FieldType.Date);
        addField(questionDate);
        Field questionNumber = new Field("AnswerAsNumber","answer", FieldType.Number);
        addField(questionNumber);
        Field questionBoolean = new Field("AnswerAsCheckBox","(CASE WHEN " + ReportOnClause.ToAlias + ".answer = 'X' THEN 1 ELSE 0 END)", FieldType.Boolean);
        addField(questionBoolean);

        Field questionOptionGroup = new Field("AnswerAsOptionGroup",
                "(SELECT CASE WHEN (" + ReportOnClause.ToAlias +
                        ".answer IS NOT NULL AND " + ReportOnClause.ToAlias +
                        ".answer != '') THEN CONCAT(q.optionID,'.'," + ReportOnClause.ToAlias +
                        ".answer) ELSE '' END FROM audit_question q WHERE q.id = " + ReportOnClause.ToAlias +
                        ".questionID)", FieldType.String);
        questionOptionGroup.setTranslationPrefixAndSuffix("AuditOptionGroup","");
        questionOptionGroup.setFilterable(false);
        addField(questionOptionGroup);

        Field questionFile = new Field("AnswerAsFile",
                "'Download'", FieldType.String);
        questionFile.setFilterable(false);
        addField(questionFile);
    }

	public void addJoins() {
        ReportForeignKey audit = new ReportForeignKey(Audit, new ContractorAuditTable(), new ReportOnClause("auditID"));
        audit.setMinimumImportance(FieldImportance.Low);
        addRequiredKey(audit);
        ReportForeignKey question = new ReportForeignKey(Question, new AuditQuestionTable(), new ReportOnClause("questionID"));
        question.setMinimumImportance(FieldImportance.Low);
        addRequiredKey(question);
	}
}