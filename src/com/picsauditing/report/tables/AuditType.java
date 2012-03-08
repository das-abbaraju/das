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

	protected void addDefaultFields() {
		// TODO: We need to find a way to pass the parent prefix to here for us to use.
		addField(prefix + "ID", alias + ".id", FilterType.Integer).setSuggested();
		QueryField auditTypeName = addField(prefix + "Name", alias + ".id", FilterType.String).setSuggested();
		auditTypeName.translate("AuditType", "name");
		auditTypeName.addRenderer("Audit.action?auditID={0}\">{1} {2}", new String[] { "auditID", prefix + "Name",
				"auditFor" });
	}

	public void addFields() {
		addField(prefix + "ClassType", alias + ".classType", FilterType.Integer);
		addField(prefix + "IsScheduled", alias + ".isScheduled", FilterType.Boolean);
		addField(prefix + "HasAuditor", alias + ".hasAuditor", FilterType.Boolean);
		addField(prefix + "Scoreable", alias + ".scorable", FilterType.Boolean);
	}

	public void addJoins() {
	}
}
