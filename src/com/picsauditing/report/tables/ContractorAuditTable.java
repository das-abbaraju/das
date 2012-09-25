package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorAuditTable extends AbstractTable {
	
	public static final String Contractor = "Contractor";
	public static final String Type = "Type";
	public static final String Auditor = "Auditor";
	public static final String ClosingAuditor = "ClosingAuditor";

	public ContractorAuditTable() {
		super("contractor_audit");
		addFields(ContractorAudit.class);
		
		Field auditTypeName;
		auditTypeName = new Field("Name", "auditTypeID", FilterType.String);
		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + ReportOnClause.ToAlias + "ID}");
		auditTypeName.setImportance(FieldImportance.Required);
		auditTypeName.setCategory(FieldCategory.Audits);
		auditTypeName.setWidth(200);
		addField(auditTypeName);
	}

	public void addJoins() {
		addJoinKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID")));
		addJoinKey(new ReportForeignKey(Type, new AuditTypeTable(), new ReportOnClause("auditTypeID")));
//		auditType.includeRequiredAndAverageColumns();
		addOptionalKey(new ReportForeignKey(Auditor, new UserTable(), new ReportOnClause("auditorID")));
//		auditor.setOverrideCategory(FieldCategory.Auditors);
//		auditor.includeOnlyRequiredColumns();
		
		addOptionalKey(new ReportForeignKey(ClosingAuditor, new UserTable(), new ReportOnClause("closingAuditorID")));
//		closingAuditor.setOverrideCategory(FieldCategory.Auditors);
//		closingAuditor.includeOnlyRequiredColumns();
	}
}