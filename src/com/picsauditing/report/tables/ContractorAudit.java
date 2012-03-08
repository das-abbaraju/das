package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class ContractorAudit extends BaseTable {

	public ContractorAudit() {
		super("contractor_audit", "audit", "ca", "ca.conID = a.id");
	}

	public ContractorAudit(String prefix, String alias, String foreignKey) {
		super("contractor_audit", prefix, alias, alias + ".id = " + foreignKey);
	}

	public ContractorAudit(String alias, String foreignKey) {
		super("contractor_audit", alias, alias, alias + ".id = " + foreignKey);
	}

	protected void addDefaultFields() {
		addField(prefix + "ID", alias + ".id", FilterType.Integer).setSuggested();
		addField(prefix + "For", alias + ".auditFor", FilterType.String).setSuggested();
	}

	public void addFields() {
		addField(prefix + "CreationDate", alias + ".creationDate", FilterType.Date);
		addField(prefix + "ExpirationDate", alias + ".expiresDate", FilterType.Date);
		addField(prefix + "ScheduledDate", alias + ".scheduledDate", FilterType.Date);
		addField(prefix + "AssignedDate", alias + ".assignedDate", FilterType.Date);
		addField(prefix + "Location", alias + ".auditLocation", FilterType.String);
		addField(prefix + "Score", alias + ".score", FilterType.Integer);
		addField(prefix + "AuditorID", alias + ".auditorID", FilterType.Integer);
		addField(prefix + "ClosingAuditorID", alias + ".closingAuditorID", FilterType.Integer);
		addField(prefix + "ContractorConfirmation", alias + ".contractorConfirm", FilterType.Date);
		addField(prefix + "AuditorConfirmation", alias + ".auditorConfirm", FilterType.Date);
	}

	public void addJoins() {
		addLeftJoin(new AuditType(prefix + "Type", alias + ".auditTypeID"));
		addLeftJoin(new User(prefix + "Auditor", alias + ".auditorID"));
		addLeftJoin(new User(prefix + "ClosingAuditor", alias + ".closingAuditorID"));
		// joinToOshaAudit("oshaAudit", alias + ".id");
	}
}
