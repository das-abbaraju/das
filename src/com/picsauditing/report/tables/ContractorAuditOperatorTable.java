package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class ContractorAuditOperatorTable extends AbstractTable {

	public ContractorAuditOperatorTable() {
		super("contractor_audit_operator", "auditOperator", "cao", "cao.auditID = ca.id AND cao.visible = 1");
	}

	public ContractorAuditOperatorTable(String prefix, String alias, String foreignKey) {
		super("contractor_audit_operator", prefix, alias, alias + ".id = " + foreignKey);
	}

	public ContractorAuditOperatorTable(String alias, String foreignKey) {
		super("contractor_audit_operator", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer, FieldCategory.ClientSiteMonitoringAnAudit);

		addField(prefix + "StatusSubstatus", "CONCAT(" + alias + ".status,IFNULL(CONCAT(':'," + alias + ".auditSubStatus),''))",
				FilterType.String, FieldCategory.ClientSiteMonitoringAnAudit);

		addFields(com.picsauditing.jpa.entities.ContractorAuditOperator.class);
	}

	public void addJoins() {
		AccountTable auditingSite = new AccountTable(prefix + "Account", alias + ".opID");
		auditingSite.setOverrideCategory(FieldCategory.ClientSiteMonitoringAnAudit);
		addLeftJoin(auditingSite);
	}
}