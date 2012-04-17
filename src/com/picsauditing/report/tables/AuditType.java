package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class AuditType extends BaseReportTable {

	public AuditType() {
		super("audit_type", "auditType", "atype", "atype.id = ca.auditTypeID");
	}

	public AuditType(String prefix, String alias, String foreignKey) {
		super("audit_type", prefix, alias, alias + ".id = " + foreignKey);
	}

	public AuditType(String alias, String foreignKey) {
		super("audit_type", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer);

		addFields(com.picsauditing.jpa.entities.AuditType.class);
	}

	public void addJoins() {
	}
}
