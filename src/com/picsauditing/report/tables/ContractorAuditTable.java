package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Type = "Type";
	public static final String Auditor = "Auditor";
	public static final String ClosingAuditor = "ClosingAuditor";
	public static final String Data = "Data";

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
		id.setCategory(FieldCategory.Audits);

		Field creationDate = new Field("CreationDate", "creationDate", FieldType.Date);
		addField(creationDate).setCategory(FieldCategory.Audits);

		Field auditTypeName;
		auditTypeName = new Field("TypeName", "auditTypeID", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Required);
		auditTypeName.setCategory(FieldCategory.Audits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);
	}

	public void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID")));

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
		caoKey.setCategory(FieldCategory.Audits);
		caoKey.setMinimumImportance(FieldImportance.Required);

		ReportForeignKey data = addOptionalKey(new ReportForeignKey(Data, new AuditDataTable(),
				new ReportOnClause("id", "auditID")));
		data.setCategory(FieldCategory.Audits);
		data.setMinimumImportance(FieldImportance.Required);
	}
}