package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.Note;

public class NoteBuilder {
    private Note note = new Note();
    public Note build() {
        return note;
    }
}
