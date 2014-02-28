package com.picsauditing.service.notes;

import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Note;
import org.springframework.beans.factory.annotation.Autowired;

public class NoteService {
    @Autowired
    private NoteDAO noteDAO;

    public Note create(ContractorAccount contractor, String text) {
        Note note = new Note();
        note.setAccount(contractor);
        note.setBody(text);

        return note;
    }

    public void persist(Note note) {
        noteDAO.save(note);
    }
}
