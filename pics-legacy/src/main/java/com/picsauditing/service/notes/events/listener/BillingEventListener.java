package com.picsauditing.service.notes.events.listener;

import com.picsauditing.service.notes.NoteService;
import org.springframework.beans.factory.annotation.Autowired;

public class BillingEventListener {
    @Autowired
    private NoteService noteService;
}
