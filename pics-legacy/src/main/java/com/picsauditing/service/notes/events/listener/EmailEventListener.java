package com.picsauditing.service.notes.events.listener;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.email.events.publisher.SpringEmailEvent;
import com.picsauditing.service.notes.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class EmailEventListener  implements ApplicationListener<SpringEmailEvent> {
    @Autowired
    private NoteService noteService;

    @Override
    public void onApplicationEvent(SpringEmailEvent event) {
        User user = event.getUser();

        //TODO what if it's an operator user?!
        noteService.addNote((ContractorAccount) user.getAccount(), "email sent to " + user.getName());

    }
}
