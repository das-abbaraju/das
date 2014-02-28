package com.picsauditing.service.notes.events.listener;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.account.events.SpringContractorEvent;
import com.picsauditing.service.notes.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class ContractorEventListener implements ApplicationListener<SpringContractorEvent> {
    @Autowired
    private NoteService noteService;

    @Override
    public void onApplicationEvent(SpringContractorEvent event) {
        ContractorAccount contractor = event.getContractor();

        switch (event.getEvent()) {
            case Registration:
                noteService.create(contractor, contractor.getName() + "registered.");
                break;
        }
    }
}
