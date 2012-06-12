package com.picsauditing.report.tables;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FilterType;

public class ContractorAuditTable extends AbstractTable {

	public ContractorAuditTable() {
		super("contractor_audit", "audit", "ca", "ca.conID = a.id");
	}

	public ContractorAuditTable(String prefix, String alias, String toForeignKey, String fromForeignKey) {
		super("contractor_audit", prefix, alias, alias + "."+ toForeignKey +" = " + fromForeignKey);
	}

	public ContractorAuditTable(String prefix, String alias, String foreignKey) {
		super("contractor_audit", prefix, alias, alias + ".id = " + foreignKey);
	}

	public ContractorAuditTable(String alias, String foreignKey) {
		super("contractor_audit", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);

		addFields(com.picsauditing.jpa.entities.ContractorAudit.class);

		Field auditTypeName;
		auditTypeName = addField(prefix + "Name", alias + ".auditTypeID", FilterType.String);
		auditTypeName.translate("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + prefix + "ID}");
		auditTypeName.setWidth(300);
	}

	public void addJoins() {
		addLeftJoin(new AuditTypeTable(prefix + "Type", alias + ".auditTypeID"));

		addLeftJoin(new UserTable(prefix + "Auditor", alias + ".auditorID"));
		addLeftJoin(new UserTable(prefix + "ClosingAuditor", alias + ".closingAuditorID"));
		
		// TODO: Add auditDataTable
	}
}
