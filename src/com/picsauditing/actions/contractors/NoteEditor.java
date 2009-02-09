package com.picsauditing.actions.contractors;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Note;

@SuppressWarnings("serial")
public class NoteEditor extends PicsActionSupport implements Preparable {
	Note note;
	NoteDAO noteDAO;

	public NoteEditor(NoteDAO noteDao) {
		this.noteDAO = noteDao;
	}

	@Override
	public void prepare() throws Exception {
		// TODO Auto-generated method stub
		int noteID = this.getParameter("note.id");
		if (noteID > 0) {
			note = noteDAO.find(noteID);
		}
	}

	@Override
	public String execute() throws Exception {
		
		if ("save".equals(button)) {
			noteDAO.save(note);
		}
		
		return SUCCESS;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

}
