package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditOperatorTable extends AbstractTable {
	public static final String Audit = "Audit";
	public static final String Operator = "Operator";

	public ContractorAuditOperatorTable() {
		super("contractor_audit_operator");
		addPrimaryKey();

		Field statusSubstatus = new Field("StatusSubstatus", "CONCAT(" + ReportOnClause.ToAlias
				+ ".status,IFNULL(CONCAT(':'," + ReportOnClause.ToAlias + ".auditSubStatus),''))", FieldType.String);
		addField(statusSubstatus);

		addFields(ContractorAuditOperator.class);
	}

	protected void addJoins() {
		ReportOnClause auditOnClause = new ReportOnClause("auditID");
		addOptionalKey(new ReportForeignKey(Audit, new ContractorAuditTable(), auditOnClause));
		ReportForeignKey operatorKey = new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID"));
		operatorKey.setCategory(FieldCategory.ClientSiteMonitoringAnAudit);
		operatorKey.setMinimumImportance(FieldImportance.Required);
		addOptionalKey(operatorKey);
	}
}