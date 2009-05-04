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
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class AccountActionSupport extends PicsActionSupport {

	protected int id;
	protected String subHeading;
	private List<Note> notes;
	protected NoteCategory noteCategory = NoteCategory.General;

	private NoteDAO noteDao;
	
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

	public List<Note> getNotes() {
		if (notes == null) {
			notes = getNoteDao().getNotes(id, permissions, "status != " + NoteStatus.Hidden.ordinal()
					+ " AND noteCategory IN ('" + noteCategory.toString() + "','General')", 5);
		}
		return notes;
	}

	protected void addNote(Account account, String newNote) throws Exception {
		addNote(account, newNote, NoteCategory.General);
	}

	protected void addNote(Account account, String newNote, NoteCategory noteCategory) throws Exception {
		addNote(account, newNote, noteCategory, LowMedHigh.Low, false, Account.EVERYONE);
	}

	protected void addNote(Account account, String newNote, NoteCategory category, LowMedHigh priority,
			boolean canContractorView, int viewableBy) throws Exception {
		Note note = new Note();
		note.setAccount(account);
		note.setAuditColumns(this.getUser());
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
