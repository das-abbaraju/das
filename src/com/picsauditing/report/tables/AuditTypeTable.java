package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;

public class AuditTypeTable extends AbstractTable {

	public AuditTypeTable() {
		super("audit_type");
		addPrimaryKey().setCategory(FieldCategory.Audits);
		addFields(AuditType.class);
	}

	public void addJoins() {
	}
}
