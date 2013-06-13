package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditOperatorTable extends AbstractTable {
	public static final String Audit = "Audit";
	public static final String Operator = "Operator";

	public ContractorAuditOperatorTable() {
		super("contractor_audit_operator");
		Field primaryKey = addPrimaryKey();
		primaryKey.setCategory(FieldCategory.MonitoringClientSite);

		Field statusSubstatus = new Field("StatusSubstatus", "CONCAT(" + ReportOnClause.ToAlias
				+ ".status,IFNULL(CONCAT(':'," + ReportOnClause.ToAlias + ".auditSubStatus),''))", FieldType.AuditStatus);
		statusSubstatus.setCategory(FieldCategory.MonitoringClientSite);
		addField(statusSubstatus);

		addFields(ContractorAuditOperator.class);
	}

	protected void addJoins() {
		ReportOnClause auditOnClause = new ReportOnClause("auditID");
        // TODO Michael Do, can you tell us why this is listed as optional?
        ReportForeignKey audit = new ReportForeignKey(Audit, new ContractorAuditTable(), auditOnClause);
        audit.setMinimumImportance(FieldImportance.Low);
        addRequiredKey(audit);
		ReportForeignKey operatorKey = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		operatorKey.setCategory(FieldCategory.MonitoringClientSite);
		operatorKey.setMinimumImportance(FieldImportance.Required);
		addOptionalKey(operatorKey);
	}
}