package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Note;

@SuppressWarnings("serial")
public class OpenNotesWidget extends PicsActionSupport {
	private NoteDAO noteDAO;
	private List<Note> openNotes;

	public OpenNotesWidget(NoteDAO noteDAO) {
		this.noteDAO = noteDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<Note> getOpenNotes() {
		if (openNotes == null)
			openNotes = noteDAO.getTasksForUser(permissions.getShadowedUserID());
		return openNotes;
	}
}
