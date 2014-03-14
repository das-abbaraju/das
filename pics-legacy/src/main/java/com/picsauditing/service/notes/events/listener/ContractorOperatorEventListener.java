package com.picsauditing.service.notes.events.listener;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.service.account.events.SpringContractorOperatorEvent;
import com.picsauditing.service.notes.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

public class ContractorOperatorEventListener  implements ApplicationListener<SpringContractorOperatorEvent> {
    @Autowired
    private NoteService noteService;

    @Override
    public void onApplicationEvent(SpringContractorOperatorEvent event) {
        ContractorAccount contractor = event.getContractorOperator().getContractorAccount();
        OperatorAccount operator = event.getContractorOperator().getOperatorAccount();

        switch (event.getEvent()) {
            case RegistrationRequest:
                noteService.addNote(contractor, "Registration Request");
                break;
            case addConnection:
                noteService.addNote(contractor, "Added connection to " + operator.getName());
                break;
            case removeConnection:
                noteService.addNote(contractor, "Removed connection from " + operator.getName());
                break;
        }
    }
}