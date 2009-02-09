package com.picsauditing.actions.contractors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.util.ReportFilterNote;

@SuppressWarnings("serial")
public class NoteEditor extends ContractorActionSupport implements Preparable {
	private String mode = "edit";
	private Note note;
	private int viewableBy;
	private int viewableByOther;
	private ReportFilterNote filter = new ReportFilterNote();

	private AccountDAO accountDAO;
	private NoteDAO noteDAO;

	public NoteEditor(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, NoteDAO noteDAO,
			AccountDAO accountDAO) {
		super(accountDao, auditDao);
		this.noteDAO = noteDAO;
		this.accountDAO = accountDAO;
	}

	@Override
	public void prepare() throws Exception {
		// TODO Auto-generated method stub
		int noteID = this.getParameter("note.id");
		if (noteID > 0) {
			note = noteDAO.find(noteID);
			viewableBy = note.getViewableBy().getId();
			if (viewableBy > 2) {
				viewableByOther = viewableBy;
				viewableBy = 3;
			}
		} else
			note = new Note();
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		findContractor();
		
		if ("save".equals(button)) {
			if (viewableBy > 2)
				note.setViewableById(viewableBy);
			else
				note.setViewableById(viewableByOther);
			
			noteDAO.save(note);
			addActionMessage("Successfully saved Note");
		}

		if (viewableByOther == 0)
			viewableByOther = permissions.getAccountId();
		
		return mode;
	}

	// ///////////////////////////

	public List<Account> getFacilities() {
		List<Account> facilities = accountDAO.findViewableOperators(permissions);
		return facilities;
	}

	public ReportFilterNote getFilter() {
		return filter;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public Map<Integer, String> getViewableByList() {
		Map<Integer, String> list = new HashMap<Integer, String>();
		list.put(Account.EVERYONE, "Everyone");
		list.put(Account.PRIVATE, "Only Me");
		list.put(3, "Restricted to:");
		return list;
	}

	public int getViewableBy() {
		return viewableBy;
	}

	public void setViewableBy(int viewableBy) {
		this.viewableBy = viewableBy;
	}

	public int getViewableByOther() {
		return viewableByOther;
	}

	public void setViewableByOther(int viewableByOther) {
		this.viewableByOther = viewableByOther;
	}

}
