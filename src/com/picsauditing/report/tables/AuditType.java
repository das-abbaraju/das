package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class AuditType extends BaseTable {
	public AuditType(String alias, String foreignKey) {
		super("audit_type", alias, alias + ".id = " + foreignKey);
		setLeftJoin();
	}

	protected void addDefaultFields() {
//		addQueryField(joinAlias + "ID", foreignKey, FilterType.Number, joinAlias, true);
//		QueryField auditTypeName = addQueryField(joinAlias + "Name", foreignKey, FilterType.String, joinAlias, true);
//		auditTypeName.translate("AuditType", "name");
//
//		addQueryField(joinAlias + "ClassType", joinAlias + ".classType", FilterType.Enum, joinAlias);
//		addQueryField(joinAlias + "IsScheduled", joinAlias + ".isScheduled", FilterType.Boolean, joinAlias);
//		addQueryField(joinAlias + "HasAuditor", joinAlias + ".hasAuditor", FilterType.Boolean, joinAlias);
//		addQueryField(joinAlias + "Scorable", joinAlias + ".scoreable", FilterType.Boolean, joinAlias);
		
	}

	public void addFields() {
	}

	public void addJoins() {
	}

}
