package com.picsauditing.service.notes.events.listener;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.service.notes.NoteService;
import com.picsauditing.service.user.events.publisher.SpringUserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class UserEventListener  implements ApplicationListener<SpringUserEvent> {
    @Autowired
    private NoteService noteService;

    @Override
    public void onApplicationEvent(SpringUserEvent event) {
        User user = event.getUser();

        switch (event.getUserEventType()) {
            case Creation:
                noteService.addNote(user.getAccount(), "user created for " + user.getName());
                break;
        }

    }
}
