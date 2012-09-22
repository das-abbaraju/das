package com.picsauditing.report.tables;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.FilterType;

public class AuditTypeTable extends ReportTable {

	public AuditTypeTable() {
		super("audit_type");
	}

	public void fill(Permissions permissions) {
		// addField(prefix + "ID", alias + ".id", FilterType.Integer,
		// FieldCategory.Audits);
		// addFields(com.picsauditing.jpa.entities.AuditType.class);
	}

}
