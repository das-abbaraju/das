package com.picsauditing.actions;

import java.util.List;
import java.util.TreeMap;

import com.picsauditing.PICS.Inputs;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class AccountActionSupport extends PicsActionSupport {

	protected int id;
	protected Account account;
	protected String subHeading;
	protected List<Note> notes;
	protected NoteCategory noteCategory = NoteCategory.General;

	private NoteDAO noteDao;
	
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		return SUCCESS;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	public Account getAccount() {
		return account;
	}
	
	/*************  NOTES  ************/

	protected NoteDAO getNoteDao() {
		if (noteDao == null)
			noteDao = (NoteDAO) SpringUtils.getBean("NoteDAO");
		return noteDao;
	}
	
	public NoteCategory getNoteCategory() {
		return noteCategory;
	}

	public void setNoteCategory(NoteCategory noteCategory) {
		this.noteCategory = noteCategory;
	}

	/**
	 * Get a list of notes up to the limit, using the given where clause
	 * @param where should be in the format of "AND field=1", can be an empty string
	 * @param limit ie 25
	 * @return
	 */
	public List<Note> getNotes(String where, int firstLimit, int limit) {
		if (notes == null)
			notes = getNoteDao().getNotes(id, permissions, "status IN (1,2)" + where, firstLimit, limit);

		return notes;
	}

	/**
	 * Get a list of 5 embedded notes, based on noteCategory
	 * @return
	 */
	public List<Note> getNotes() {
		return getNotes(" AND noteCategory IN ('" + noteCategory.toString() + "','General')", 0, 5);
	}

	protected void addNote(Account account, String newNote) throws Exception {
		addNote(account, newNote, noteCategory);
	}

	protected void addNote(Account account, String newNote, NoteCategory noteCategory) throws Exception {
		addNote(account, newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, null);
	}

	protected void addNote(Account account, String newNote, NoteCategory category, LowMedHigh priority,
		boolean canContractorView, int viewableBy, User user) throws Exception {
		Note note = new Note();
		note.setAccount(account);
		note.setAuditColumns(permissions);
		note.setSummary(newNote);
		note.setPriority(priority);
		note.setNoteCategory(category);
		note.setViewableById(viewableBy);
		note.setCanContractorView(canContractorView);
		note.setStatus(NoteStatus.Closed);
		getNoteDao().save(note);
	}

	/***** END of NOTES *****/
	
	public String[] getCountryList() {
		return Inputs.COUNTRY_ARRAY;
	}

	public TreeMap<String, String> getStateList() {
		return State.getStates(true);
	}

	public Industry[] getIndustryList() {
		return Industry.values();
	}

}
