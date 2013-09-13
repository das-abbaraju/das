package com.picsauditing.actions.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Note;

@SuppressWarnings("serial")
public class OpenNotesWidget extends PicsActionSupport {
	@Autowired
	private NoteDAO noteDAO;
	
	private List<Note> openNotes;

	public String execute() throws Exception {
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
