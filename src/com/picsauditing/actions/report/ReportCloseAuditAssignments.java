package com.picsauditing.actions.report;

import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectContractorAudit;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportCloseAuditAssignments extends ReportContractorAuditOperator {
	@Autowired
	protected ContractorAuditDAO contractorAuditDAO;
	@Autowired
	protected NoteDAO noteDAO;

	protected String notes;
	protected ContractorAudit audit;
	protected User closingAuditor;
	protected int[] auditIDs;

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

		if (permissions.isOperatorCorporate()) {
			String groupIds = Strings.implode(permissions.getAllInheritedGroupIds());
			sql.addWhere("atype.assignAudit in (" + groupIds + ")");
		} else {
			sql.addWhere("ca.auditTypeID IN (2,3)");
		}
		sql.addWhere("cao.status = 'Submitted'");
		sql.addField("ca.closingAuditorID");

		getFilter().setShowCcOnFile(false);
		getFilter().setShowConWithPendingAudits(false);
		getFilter().setShowAuditType(false);
		getFilter().setShowAuditFor(false);
	}

	@Override
    public Set<User> getAuditorList() {
		if (permissions.isAdmin())
			return super.getAuditorList();
        Set<User> auditorList = new TreeSet<User>();
        auditorList.addAll(userDAO.findAuditors(permissions.getAllInheritedGroupIds()));
        return auditorList;
    }

    public String save() throws Exception {
		if (audit != null) {
			Note note = new Note();

			if (closingAuditor != null) {
				note.setSummary("Assigned " + closingAuditor.getName() + " as Closing Auditor for "
						+ audit.getAuditType().getName().toString());
			} else {
				note.setSummary("Unassigned closing auditor for " + audit.getAuditType().getName().toString());
			}

			audit.setClosingAuditor(closingAuditor);
			audit.setAuditColumns(permissions);
			contractorAuditDAO.save(audit);

			note.setAccount(audit.getContractorAccount());
			note.setAuditColumns(permissions);
			note.setBody(notes);
			note.setNoteCategory(NoteCategory.Audits);
			if (audit.getAuditType().getAccount() != null)
				note.setViewableBy(audit.getAuditType().getAccount());
			else
				note.setViewableById(Account.EVERYONE);

			noteDAO.save(note);
		}

		return BLANK;
	}
	
	public String saveAll() throws Exception {
		if (auditIDs != null && auditIDs.length > 0) {
			for (Integer auditID : auditIDs) {
				ContractorAudit audit = contractorAuditDAO.find(auditID);
				
				Note note = new Note();
				
				if (closingAuditor != null) {
					note.setSummary("Assigned " + closingAuditor.getName() + " as Closing Auditor for "
							+ audit.getAuditType().getName().toString());
				} else {
					note.setSummary("Unassigned closing auditor for " + audit.getAuditType().getName().toString());
				}
				
				audit.setClosingAuditor(closingAuditor);
				audit.setAuditColumns(permissions);
				contractorAuditDAO.save(audit);
				
				note.setAccount(audit.getContractorAccount());
				note.setAuditColumns(permissions);
				note.setBody(notes);
				note.setNoteCategory(NoteCategory.Audits);
				if (audit.getAuditType().getAccount() != null)
					note.setViewableBy(audit.getAuditType().getAccount());
				else
					note.setViewableById(Account.EVERYONE);
				
				noteDAO.save(note);
			}
		}
		
		notes = "";
		return super.execute();
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public User getClosingAuditor() {
		return closingAuditor;
	}

	public void setClosingAuditor(User closingAuditor) {
		this.closingAuditor = closingAuditor;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int[] getAuditIDs() {
		return auditIDs;
	}

	public void setAuditIDs(int[] auditIDs) {
		this.auditIDs = auditIDs;
	}
}
