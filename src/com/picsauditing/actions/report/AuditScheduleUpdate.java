package com.picsauditing.actions.report;

import java.util.Date;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AuditScheduleUpdate extends PicsActionSupport {

	protected String type = null;
	protected String key = null;

	protected ContractorAudit contractorAudit = null;

	protected ContractorAuditDAO dao = null;
	protected NoteDAO noteDAO = null;

	@Anonymous
	public String execute() {

		if (Strings.isEmpty(type)) {
			addActionError("Missing type, invalid URL");
			return SUCCESS;
		}

		if (contractorAudit == null) {
			addActionError("Missing audit, invalid URL");
			return SUCCESS;
		}

		String seed = type + contractorAudit.getContractorAccount().getId() + "id" + contractorAudit.getId();
		if (!Strings.hashUrlSafe(seed).equals(key)) {
			addActionError("Invalid URL, please confirm your link and check with customer service if the problem persists.");
			return SUCCESS;
		}
		if (type.equals("c")) {
			contractorAudit.setContractorConfirm(new Date());
			String newNote = " Confirmed the " + contractorAudit.getAuditType().getName();

			Note note = new Note();
			note.setAccount(contractorAudit.getContractorAccount());
			note.setAuditColumns(new User(contractorAudit.getContractorAccount().getUsers().get(0).getId()));
			note.setSummary(newNote);
			note.setNoteCategory(NoteCategory.Audits);
			if (contractorAudit.getAuditType().getAccount() != null)
				note.setViewableBy(contractorAudit.getAuditType().getAccount());
			else
				note.setViewableById(Account.EVERYONE);
			note.setPriority(LowMedHigh.Med);
			note.setCanContractorView(false);
			note.setStatus(NoteStatus.Closed);
			noteDAO.save(note);
		}
		if (type.equals("a"))
			contractorAudit.setAuditorConfirm(new Date());

		dao.save(contractorAudit);
		return SUCCESS;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setDao(ContractorAuditDAO dao) {
		this.dao = dao;
	}

	public void setNoteDAO(NoteDAO noteDAO) {
		this.noteDAO = noteDAO;
	}

	public ContractorAudit getContractorAudit() {
		return contractorAudit;
	}

	public void setContractorAudit(ContractorAudit contractorAudit) {
		this.contractorAudit = contractorAudit;
	}
}
