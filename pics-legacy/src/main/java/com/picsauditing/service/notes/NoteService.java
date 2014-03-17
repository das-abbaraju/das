package com.picsauditing.service.notes;

import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.security.IdentityProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class NoteService {
    private final NoteCategory noteCategory = NoteCategory.General;
    private IdentityProvider identityProvider;

    @Autowired
    private NoteDAO noteDAO;

    public NoteService (IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    public Note addNote(Account account, String newNote) {
        return addNote(account, newNote, noteCategory);
    }

    public Note addNote(Account account, String newNote, NoteCategory noteCategory) {
        return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, null, null);
    }

    public Note addNote(Account account, String newNote, NoteCategory noteCategory, int viewableBy) {
        return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, viewableBy, null, null);
    }

    public Note addNote(Account account, String newNote, NoteCategory noteCategory, LowMedHigh priority,
                           boolean canContractorView, int viewableBy, User user) {
        return addNote(account, newNote, noteCategory, LowMedHigh.Low, true, viewableBy, user, null);
    }

    public Note addNote(Account account, String summary, String newNote, NoteCategory category, LowMedHigh priority,
                           boolean canContractorView, int viewableBy) {
        Note note = new Note();
        note.setAccount(account);
        note.setAuditColumns(new User(identityProvider.identity().getLoggedInUserId()));
        note.setSummary(summary);
        note.setBody(newNote);
        note.setPriority(priority);
        note.setNoteCategory(category);
        note.setViewableById(viewableBy);
        note.setCanContractorView(canContractorView);
        note.setStatus(NoteStatus.Closed);
        noteDAO.save(note);
        return note;
    }

    public Note addNote(Account account, String newNote, NoteCategory category, LowMedHigh priority,
                           boolean canContractorView, int viewableBy, User user, Employee employee) {
        Note note = new Note();
        note.setAccount(account);
        note.setAuditColumns(new User(identityProvider.identity().getLoggedInUserId()));
        note.setSummary(newNote);
        note.setPriority(priority);
        note.setNoteCategory(category);
        note.setViewableById(viewableBy);
        note.setCanContractorView(canContractorView);
        note.setStatus(NoteStatus.Closed);
        note.setEmployee(employee);
        noteDAO.save(note);
        return note;
    }

}
