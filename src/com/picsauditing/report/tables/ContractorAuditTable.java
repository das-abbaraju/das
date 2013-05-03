package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmrStatistics;
import com.picsauditing.jpa.entities.OshaStatistics;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Type = "Type";
	public static final String Auditor = "Auditor";
	public static final String ClosingAuditor = "ClosingAuditor";
	public static final String Data = "Data";
    public static final String Fatalities = "Fatalities";
    public static final String Emr = "Emr";
    public static final String Trir = "Trir";

	/**
	 * This is here ONLY for use when the audit type only has a single cao such
	 * as Welcome Calls, Manual Audits, Implementation Audits, and PQF Specfic.
	 * With any other audits, please use the ContractorAudit Model
	 */
	public static final String SingleCAO = "Cao";

	public ContractorAuditTable() {
		super("contractor_audit");
		addFields(ContractorAudit.class);
		Field id = addPrimaryKey();
		id.setCategory(FieldCategory.DocumentsAndAudits);

		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		addField(creationDate).setCategory(FieldCategory.DocumentsAndAudits);

		Field auditTypeName;
		auditTypeName = new Field("TypeName", "auditTypeID", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Required);
		auditTypeName.setCategory(FieldCategory.DocumentsAndAudits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);
	}

	public void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID"))).setMinimumImportance(FieldImportance.Average);

		addJoinKey(new ReportForeignKey(Type, new AuditTypeTable(), new ReportOnClause("auditTypeID")))
				.setMinimumImportance(FieldImportance.Average);
		
		{
			ReportForeignKey auditorKey = addOptionalKey(new ReportForeignKey(Auditor, new UserTable(),
					new ReportOnClause("auditorID")));
			auditorKey.setMinimumImportance(FieldImportance.Required);
			auditorKey.setCategory(FieldCategory.Auditors);
		}

		{
			ReportForeignKey auditorKey = addOptionalKey(new ReportForeignKey(ClosingAuditor, new UserTable(),
					new ReportOnClause("closingAuditorID")));
			auditorKey.setMinimumImportance(FieldImportance.Required);
			auditorKey.setCategory(FieldCategory.Auditors);
		}

		ReportForeignKey caoKey = addOptionalKey(new ReportForeignKey(SingleCAO, new ContractorAuditOperatorTable(),
				new ReportOnClause("id", "auditID")));
		caoKey.setCategory(FieldCategory.DocumentsAndAudits);
		caoKey.setMinimumImportance(FieldImportance.Required);

		ReportForeignKey data = addOptionalKey(new ReportForeignKey(Data, new AuditDataTable(),
				new ReportOnClause("id", "auditID")));
		data.setCategory(FieldCategory.DocumentsAndAudits);
		data.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey fatalities = addOptionalKey(new ReportForeignKey(Fatalities, new AuditDataTable(),
                new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + OshaStatistics.QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR)));
        fatalities.setCategory(FieldCategory.DocumentsAndAudits);
        fatalities.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey emr = addOptionalKey(new ReportForeignKey(Emr, new AuditDataTable(),
                new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + EmrStatistics.QUESTION_ID_EMR_FOR_THE_GIVEN_YEAR)));
        emr.setCategory(FieldCategory.DocumentsAndAudits);
        emr.setMinimumImportance(FieldImportance.Required);

        ReportForeignKey trir = addOptionalKey(new ReportForeignKey(Trir, new AuditDataTable(),
                new ReportOnClause("id", "auditID", ReportOnClause.ToAlias + ".questionID = " + OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR)));
        trir.setCategory(FieldCategory.DocumentsAndAudits);
        trir.setMinimumImportance(FieldImportance.Required);

    }
}