package com.picsauditing.report.tables;

import com.picsauditing.report.fields.FilterType;

public class ContractorAuditOperator extends BaseTable {

	public ContractorAuditOperator() {
		super("contractor_audit_operator", "cao", "cao.auditID = ca.id AND cao.visible = 1");
	}

	protected void addDefaultFields() {
		addField("caoID", "cao.id", FilterType.Number);
		addField("caoStatus", "cao.status", FilterType.AuditStatus);
		// caoVisible should always be 1...should we just hard code this or make the user specify?
		// addField("caoVisible", "cao.visible", FilterType.Boolean);
	}

	public void addFields() {
		addField("caoStatusChangedDate", "cao.statusChangedDate", FilterType.Date);
		addField("caoPercentComplete", "cao.percentComplete", FilterType.Number);
	}

	public void addJoins() {
		addJoin(new Account("caoAccount", "cao.opID"));
	}
}
