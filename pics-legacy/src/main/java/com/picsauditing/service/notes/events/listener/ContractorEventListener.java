package com.picsauditing.service.notes.events.listener;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.account.events.SpringContractorEvent;
import com.picsauditing.service.notes.NoteService;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class ContractorEventListener implements ApplicationListener<SpringContractorEvent> {

    @Autowired
    private NoteService noteService;

    @Override
    public void onApplicationEvent(SpringContractorEvent event) {
        final ContractorAccount contractor = event.getContractor();

        switch (event.getEvent()) {
            case Registration:
                if (Strings.isEmpty(contractor.getRegistrationHash())) {
                    noteService.addNote(contractor,
                            "Contractor '" + contractor.getName() + "' requested by "
                            + contractor.getRequestedBy().getName() + " has registered.");
                } else {
                    noteService.addNote(contractor, contractor.getName() + "registered.");
                }
                break;
        }
    }
}
