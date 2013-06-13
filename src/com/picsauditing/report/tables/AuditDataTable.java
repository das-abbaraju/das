package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class AuditDataTable extends AbstractTable {

	public static final String Audit = "Audit";
	public static final String Question = "Question";

	public AuditDataTable() {
		super("pqfData");
		addFields(AuditData.class);
	}

	public void addJoins() {
        ReportForeignKey audit = new ReportForeignKey(Audit, new ContractorAuditTable(), new ReportOnClause("auditID"));
        audit.setMinimumImportance(FieldImportance.Low);
        addOptionalKey(audit);
        ReportForeignKey question = new ReportForeignKey(Question, new AuditQuestionTable(), new ReportOnClause("questionID"));
        question.setMinimumImportance(FieldImportance.Low);
        addOptionalKey(question);
	}
}