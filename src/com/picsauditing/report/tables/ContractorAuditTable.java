package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAudit;

public class ContractorAuditTable extends AbstractTable {
	
	public static final String Contractor = "Contractor";
	public static final String Type = "Type";
	public static final String Auditor = "Auditor";
	public static final String ClosingAuditor = "ClosingAuditor";

	public ContractorAuditTable() {
		super("contractor_audit");
		addFields(ContractorAudit.class);
		
		// addField(prefix + "ID", alias + ".id", FilterType.Integer, FieldCategory.Audits);
		// I'm not sure this field is really that important at all. With
		// Effective Date, the creationDate just becomes confusing
		// Field creationDate = addField(prefix + "CreationDate", alias +
		// ".creationDate", FilterType.Date, FieldCategory.Audits);
		// creationDate.setImportance(FieldImportance.Low);
		// creationDate.requirePermission(OpPerms.ManageAudits);


//		Field auditTypeName;
//		auditTypeName = addField(prefix + "Name", alias + ".auditTypeID", FilterType.String, FieldCategory.Audits);
//		auditTypeName.setTranslationPrefixAndSuffix("AuditType", "name");
//		auditTypeName.setUrl("Audit.action?auditID={" + prefix + "ID}");
//		auditTypeName.setImportance(FieldImportance.Required);
//		auditTypeName.setWidth(200);
	}

	public void addJoins() {
		addKey(new ReportForeignKey(Contractor, new ContractorTable(), new ReportOnClause("conID")));
		addOptionalKey(new ReportForeignKey(Type, new AuditTypeTable(), new ReportOnClause("auditTypeID")));
//		auditType.includeRequiredAndAverageColumns();
		addOptionalKey(new ReportForeignKey(Auditor, new UserTable(), new ReportOnClause("auditorID")));
//		auditor.setOverrideCategory(FieldCategory.Auditors);
//		auditor.includeOnlyRequiredColumns();
		
		addOptionalKey(new ReportForeignKey(ClosingAuditor, new UserTable(), new ReportOnClause("closingAuditorID")));
//		closingAuditor.setOverrideCategory(FieldCategory.Auditors);
//		closingAuditor.includeOnlyRequiredColumns();
	}
}