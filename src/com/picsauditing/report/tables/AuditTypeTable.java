package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.report.fields.FilterType;

public class AuditTypeTable extends AbstractTable {

	public AuditTypeTable() {
		super("audit_type");
		addPrimaryKey(FilterType.Integer);
		addFields(AuditType.class);
	}

	public void addJoins() {
	}

}
