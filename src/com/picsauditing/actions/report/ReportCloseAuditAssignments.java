package com.picsauditing.actions.report;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectContractorAudit;

@SuppressWarnings("serial")
public class ReportCloseAuditAssignments extends ReportContractorAuditOperator {
	protected int auditID;
	protected int closeAuditor;
	protected String notes;

	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	protected UserDAO userDAO;

	public ReportCloseAuditAssignments() {
		super();
		sql = new SelectContractorAudit();
		orderByDefault = "cao.statusChangedDate DESC";
	}

	@Override
	protected void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AssignAudits);
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();

		sql.addWhere("ca.auditTypeID IN (2,3)");
		sql.addWhere("cao.status = 'Submitted'");
		sql.addField("ca.closingAuditorID");

		getFilter().setShowCcOnFile(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditFor(false);
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (auditID > 0 && closeAuditor > 0) {
			ContractorAudit cAudit = contractorAuditDAO.find(auditID);
			cAudit.setClosingAuditor(new User(closeAuditor));
			cAudit.setAuditColumns(permissions);
			contractorAuditDAO.save(cAudit);

			User user = userDAO.find(closeAuditor);
			Note note = new Note();
			note.setAccount(cAudit.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setSummary("Assigned " + user.getName() + " as Closing Auditor for "
					+ cAudit.getAuditType().getName().toString());
			note.setBody(notes);
			note.setNoteCategory(NoteCategory.Audits);
			if (cAudit.getAuditType().getAccount() != null)
				note.setViewableBy(cAudit.getAuditType().getAccount());
			else
				note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);
		}

		return super.execute();
	}

	public int getAuditID() {
		return auditID;
	}

	public void setAuditID(int auditID) {
		this.auditID = auditID;
	}

	public int getCloseAuditor() {
		return closeAuditor;
	}

	public void setCloseAuditor(int closeAuditor) {
		this.closeAuditor = closeAuditor;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
