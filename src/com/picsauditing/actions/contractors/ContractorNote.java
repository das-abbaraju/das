package com.picsauditing.actions.contractors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.util.ReportFilterNote;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorNote extends ContractorActionSupport implements Preparable {
	private List<Note> openTasks = null;
	private List<Note> notes = null;
	private List<EmailQueue> emailList = null;
	protected List<Account> facilities = null;
	public Note note;
	private String returnType = SUCCESS;
	
	private ReportFilterNote filter = new ReportFilterNote();
	
	private NoteDAO noteDAO;
	private AccountDAO accountDAO;
	private EmailQueueDAO emailDAO;

	public ContractorNote(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, NoteDAO noteDAO,
			AccountDAO accountDAO, EmailQueueDAO emailDAO) {
		super(accountDao, auditDao);
		this.noteDAO = noteDAO;
		this.accountDAO = accountDAO;
		this.emailDAO = emailDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		findContractor();

		if ("addFilter".equals(button)) {
			return "tasks";
		}
		
		if ("noteEdit".equals(button) && note != null && note.getId() > 0) {
			return returnType;
		}
		if ("saveNote".equals(button)) {
			if (note.getStatus().equals(NoteStatus.Hidden))
				permissions.tryPermission(OpPerms.EditNotes, OpType.Delete);
			
			permissions.tryPermission(OpPerms.EditNotes, OpType.Edit);
			if (Strings.isEmpty(note.getSummary()))
				addActionError("Please enter a Note");
			if (this.getActionErrors().size() > 0) {
				noteDAO.clear();
				return SUCCESS;
			}
			
			if (note.getId() == 0) {
				note.setAccount(contractor);
			}
			note.setAuditColumns(getUser());
			noteDAO.save(note);
			addActionMessage("Successfully added the note");
		}
		
		note = new Note(contractor, getUser(), "");
		
		this.subHeading = "Notes/Emails";

		return returnType;
	}

	public void prepare() throws Exception {
		int noteID = getParameter("note.id");
		if (noteID > 0) {
			note = noteDAO.find(noteID);
		}
	}

	public List<Note> getOpenTasks() {
		if (openTasks == null)
			openTasks = noteDAO.findWhere(contractor.getId(), "n.status = 1" + getFilters("tasks"), 25);
		
		return openTasks;
	}

	public List<Note> getNotes() {
		if (notes == null)
			notes = noteDAO.findWhere(contractor.getId(), "n.status = 2" + getFilters("notes"), 25);
		
		return notes;
	}
	
	public List<EmailQueue> getEmailList() {
		if (emailList == null)
			emailList = emailDAO.findByContractorId(contractor.getId());
		
		return emailList;
	}

	private String getFilters(String listType) {
		String filterString = "";
		if (filter.getUserID() != null && filter.getUserID().length > 0) {
			filterString += " AND createdBy.id IN (" + Strings.implode(filter.getUserID(), ",") + ")";
		}
		if (filter.getUserAccountID() != null && filter.getUserAccountID().length > 0) {
			filterString += " AND createdBy.account.id IN (" + Strings.implode(filter.getUserAccountID(), ",") + ")";
		}
		if (filter.getCategory() != null && filter.getCategory().length > 0) {
			Set<String> set = new HashSet<String>();
			for(NoteCategory e : filter.getCategory())
				set.add(e.name());
			
			filterString += " AND noteCategory.id IN (" + Strings.implodeForDB((String[])set.toArray(), ",") + ")";
		}
		if (filter.getPriority() != null && filter.getPriority().length > 0) {
			Set<Integer> set = new HashSet<Integer>();
			for(LowMedHigh e : filter.getPriority())
				set.add(e.ordinal());
			
			filterString += " AND priority IN (" + Strings.implodeForDB((String[])set.toArray(), ",") + ")";
		}
		return filterString;
	}
	
	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public Note getNote() {
		if (note == null) {
			note = new Note();
		}
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public List<Account> getFacilities() {
		facilities = accountDAO.findViewableOperators(permissions);
		return facilities;
	}

	public ReportFilterNote getFilter() {
		return filter;
	}
	
}
