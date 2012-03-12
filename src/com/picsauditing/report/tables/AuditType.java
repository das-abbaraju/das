package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class AuditType extends BaseTable {

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
		// TODO: We need to find a way to pass the parent prefix/alias to here for us to use.
		addField(prefix + "ID", alias + ".id", FilterType.Integer);
		QueryField auditTypeName = addField(prefix + "Name", alias + ".id", FilterType.String);
		auditTypeName.translate("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={auditID}");
		auditTypeName.setWidth(300);

		addField(prefix + "ClassType", alias + ".classType", FilterType.Integer);
		addField(prefix + "IsScheduled", alias + ".isScheduled", FilterType.Boolean);
		addField(prefix + "HasAuditor", alias + ".hasAuditor", FilterType.Boolean);
		addField(prefix + "Scoreable", alias + ".scorable", FilterType.Boolean);
	}

	public void addJoins() {
	}
}
