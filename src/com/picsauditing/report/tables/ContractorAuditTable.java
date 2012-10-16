package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditTable extends AbstractTable {

	public static final String Contractor = "Contractor";
	public static final String Type = "Type";
	public static final String Auditor = "Auditor";
	public static final String ClosingAuditor = "ClosingAuditor";

	public ContractorAuditTable() {
		super("contractor_audit");
		addFields(ContractorAudit.class);
		Field id = addPrimaryKey();
		id.setCategory(FieldCategory.Audits);

		Field auditTypeName;
		auditTypeName = new Field("TypeName", "auditTypeID", FieldType.AuditType);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Average);
		auditTypeName.setCategory(FieldCategory.Audits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);
	}

	public void addJoins() {
		{
			ReportForeignKey contractorJoin = new ReportForeignKey(Contractor, new ContractorTable(),
					new ReportOnClause("conID"));
			// contractorJoin.setMinimumImportance(FieldImportance.Average);
			// We may not need this either if the Entity Fields are set correctly
			// contractorJoin.setCategory(FieldCategory.AccountInformation);
			addJoinKey(contractorJoin);
		}
		
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
	}
}