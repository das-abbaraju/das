package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.QueryField;

public class ContractorAudit extends BaseReportTable {

	public ContractorAudit() {
		super("contractor_audit", "audit", "ca", "ca.conID = a.id");
	}

	public ContractorAudit(String prefix, String alias, String toForeignKey, String fromForeignKey) {
		super("contractor_audit", prefix, alias, alias + "."+ toForeignKey +" = " + fromForeignKey);
	}

	public ContractorAudit(String prefix, String alias, String foreignKey) {
		super("contractor_audit", prefix, alias, alias + ".id = " + foreignKey);
	}

	public ContractorAudit(String alias, String foreignKey) {
		super("contractor_audit", alias, alias, alias + ".id = " + foreignKey);
	}

	public void addFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer);
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);

		addFields(com.picsauditing.jpa.entities.ContractorAudit.class);

		QueryField auditTypeName; 
		auditTypeName = addField(prefix + "Name", alias + ".auditTypeID", FilterType.String);
		auditTypeName.translate("AuditType", "name");
		auditTypeName.setUrl("Audit.action?auditID={" + prefix + "ID}");
		auditTypeName.setWidth(300);
	}

	public void addJoins() {
		AuditType auditTypeLeftJoin = new AuditType(prefix + "Type", alias + ".auditTypeID");
		auditTypeLeftJoin.setParentPrefix(prefix);
		auditTypeLeftJoin.setParentPrefix(alias);
		addLeftJoin(auditTypeLeftJoin);

		addLeftJoin(new User(prefix + "Auditor", alias + ".auditorID"));
		addLeftJoin(new User(prefix + "ClosingAuditor", alias + ".closingAuditorID"));
	}
}
