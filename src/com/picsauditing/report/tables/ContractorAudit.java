package com.picsauditing.report.tables;

import com.picsauditing.report.fields.QueryField;
import com.picsauditing.report.fields.Renderer;
import com.picsauditing.report.fieldtypes.FilterType;

public class ContractorAudit extends BaseTable {

	public ContractorAudit() {
		super("contractor_audit", "ca", "ca.conID = a.id");
	}

	protected void addDefaultFields() {
//		addQueryField("auditID", "ca.id", FilterType.Number, true);
//		addQueryField("auditCreationDate", "ca.creationDate", FilterType.Date);
//		addQueryField("auditExpirationDate", "ca.expiresDate", FilterType.Date);
//		addQueryField("auditScheduledDate", "ca.scheduledDate", FilterType.Date);
//		addQueryField("auditAssignedDate", "ca.assignedDate", FilterType.Date);
//		addQueryField("auditLocation", "ca.auditLocation", FilterType.String);
//		addQueryField("auditFor", "ca.auditFor", FilterType.String, true);
//		addQueryField("auditScore", "ca.score", FilterType.Number);
//		addQueryField("auditAuditorID", "ca.auditorID", FilterType.Number);
//		addQueryField("auditClosingAuditorID", "ca.closingAuditorID", FilterType.Number);
//		addQueryField("auditContractorConfirmation", "ca.contractorConfirm", FilterType.Date);
//		addQueryField("auditAuditorConfirmation", "ca.auditorConfirm", FilterType.Date);

//		QueryField auditTypeName = joinToAuditType("auditType", "ca.auditTypeID");
//		auditTypeName.addRenderer("Audit.action?auditID={0}\">{1} {2}", new String[] { "auditID", "auditTypeName",
//				"auditFor" });

	}

	public void addFields() {
	}

	public void addJoins() {
		addLeftJoin(new JoinUser("auditor", "ca.auditorID"));
		addLeftJoin(new JoinUser("closingAuditor", "ca.closingAuditorID"));
		// joinToOshaAudit("oshaAudit", "ca.id");
	}
}
