package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorNote extends ContractorActionSupport implements Preparable {
	private NoteDAO noteDAO;
	private AccountDAO accountDAO;
	private List<Note> openTasks = null;
	private List<Note> notes = null;
	protected List<Account> facilities = null;
	public int noteID;
	public Note note;

	public ContractorNote(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, NoteDAO noteDAO,
			AccountDAO accountDAO) {
		super(accountDao, auditDao);
		this.noteDAO = noteDAO;
		this.accountDAO = accountDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		findContractor();

		if ("delete".equals(button) && note != null) {
			permissions.tryPermission(OpPerms.EditNotes, OpType.Delete);
			note.setStatus(NoteStatus.Hidden);
			noteDAO.save(note);
			addActionMessage("Successfully removed the note");
		}
		if ("add".equals(button)) {
			permissions.tryPermission(OpPerms.EditNotes, OpType.Edit);
			if (Strings.isEmpty(note.getSummary()))
				addActionError("Please enter a Note");
			if (Strings.isEmpty(note.getNoteCategory().toString()))
				addActionError("Please select a Note Category");
			if (this.getActionErrors().size() > 0) {
				noteDAO.clear();
				return SUCCESS;
			}

			if (note.getId() == 0) {
				note.setCreatedBy(new User(permissions.getUserId()));
				note.setCreationDate(new Date());
				note.setAccount(new Account());
				note.getAccount().setId(permissions.getAccountId());
			}
			note.setUpdateDate(new Date());
			note.setUpdatedBy(new User(permissions.getUserId()));
			noteDAO.save(note);
			addActionMessage("Successfully added the note");
		}
		openTasks = noteDAO.findWhere(contractor.getId(), "n.status = 1");
		notes = noteDAO.findWhere(contractor.getId(), "n.status = 2");
		this.subHeading = "Contractor Notes";

		return SUCCESS;
	}

	public void prepare() throws Exception {
		if (noteID > 0) {
			note = noteDAO.find(noteID);
		}
	}

	public List<Note> getOpenTasks() {
		return openTasks;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public int getNoteID() {
		return noteID;
	}

	public void setNoteID(int noteID) {
		this.noteID = noteID;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public Map<Integer, LowMedHigh> getPriorityList() {
		return LowMedHigh.getMap();
	}

	public NoteCategory[] getNoteCategoryList() {
		return NoteCategory.values();
	}

	public Map<Integer, NoteStatus> getNoteStatus() {
		return NoteStatus.getMap();
	}

	public List<Account> getFacilities() {
		facilities = accountDAO.findWhere("a.type != 'Auditor' OR a.type != 'Contractor'");
		return facilities;
	}
}
