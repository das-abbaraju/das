package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class AuditType extends BaseTable {
	
	public AuditType(String alias, String foreignKey) {
		super("audit_type", alias, alias + ".id = " + foreignKey);
		// setLeftJoin();
	}

	protected void addDefaultFields() {
		addField("id", FilterType.Number);
		// QueryField auditTypeName = addField(alias + "Name", foreignKey FilterType.String);
		// auditTypeName.translate("AuditType", "name");
	}

	public void addFields() {
		addField("classType", FilterType.Number);
		addField("isScheduled", FilterType.Boolean);
		addField("hasAuditor", FilterType.Boolean);
		addField("scoreable", FilterType.Boolean);
	}

	public void addJoins() {
	}
}
