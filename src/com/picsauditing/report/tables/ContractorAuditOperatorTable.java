package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.report.fields.FilterType;

public class ContractorAuditOperatorTable extends AbstractTable {
	public static final String Audit = "Audit";
	public static final String Operator = "Operator";

	public ContractorAuditOperatorTable() {
		super("contractor_audit_operator");

		// , "auditOperator", "cao", "cao.auditID = ca.id AND cao.visible = 1"

		// FieldCategory.ClientSiteMonitoringAnAudit
		addPrimaryKey(FilterType.Integer);

		// addField(prefix + "StatusSubstatus", "CONCAT(" + alias +
		// ".status,IFNULL(CONCAT(':'," + alias + ".auditSubStatus),''))",
		// FilterType.String, FieldCategory.ClientSiteMonitoringAnAudit);

		addFields(ContractorAuditOperator.class);
	}

	protected void addJoins() {
		addOptionalKey(new ReportForeignKey(Audit, new ContractorAuditTable(), new ReportOnClause("auditID")));
		addOptionalKey(new ReportForeignKey(Operator, new AccountTable(), new ReportOnClause("opID")));
		// FieldCategory.ClientSiteMonitoringAnAudit
	}
}