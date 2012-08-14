package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class AuditTypeTable extends AbstractTable {

	public AuditTypeTable() {
		super("audit_type", "auditType", "atype", "atype.id = ca.auditTypeID");
	}

	public AuditTypeTable(String prefix, String alias, String foreignKey) {
		super("audit_type", prefix, alias, alias + ".id = " + foreignKey);
	}

	public AuditTypeTable(String alias, String foreignKey) {
		super("audit_type", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer).setCategory(FieldCategory.Audits);

		addFields(com.picsauditing.jpa.entities.AuditType.class);
	}

	public void addJoins() {
	}
}
