package com.picsauditing.actions.report;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.search.SelectContractorAudit;

@SuppressWarnings("serial")
public class ReportCloseAuditAssignments extends ReportContractorAudits {
	protected NoteDAO noteDAO;

	public ReportCloseAuditAssignments(NoteDAO noteDAO) {
		sql = new SelectContractorAudit();
		orderByDefault = "ca.completedDate DESC";
		this.noteDAO = noteDAO;
	}

	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.ManageAudits);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		
		sql.addWhere("ca.auditTypeID IN (1,3)");
		sql.addWhere("ca.auditStatus = 'Submitted'");

		sql.addField("ca.creationDate createdDate");
		sql.addField("ca.completedDate");
		sql.addField("ca.assignedDate");
		sql.addField("ca.auditorID");
		sql.addField("ca.auditFor");

		getFilter().setShowCcOnFile(false);
	}
}
