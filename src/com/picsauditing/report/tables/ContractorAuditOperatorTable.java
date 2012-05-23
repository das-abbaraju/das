package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class ContractorAuditOperatorTable extends BaseTable {

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
		addField(prefix + "ID", alias + ".id", FilterType.Integer);
		addField(prefix + "Status", alias + ".status", FilterType.Enum);
		addField(prefix + "StatusChangedDate", alias + ".statusChangedDate", FilterType.Date);
		addField(prefix + "PercentComplete", alias + ".percentComplete", FilterType.Integer);
	}

	public void addJoins() {
		addJoin(new AccountTable(prefix + "Account", alias + ".opID"));
	}
}
