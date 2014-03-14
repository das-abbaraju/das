package com.picsauditing.service.email.events.listener;

import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.account.events.ContractorOperatorEventType;
import com.picsauditing.service.account.events.SpringContractorOperatorEvent;
import com.picsauditing.service.notes.NoteService;
import com.picsauditing.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Date;

public class ContractorOperatorEventListener implements ApplicationListener<SpringContractorOperatorEvent> {
    private Logger logger = LoggerFactory.getLogger(ContractorOperatorEventListener.class);

    static final String EMAIL_SUCCESS_NOTE_TEXT = "Sent initial contact email.";
    static final String EMAIL_FAILURE_NOTE_TEXT = "Failed to send initial contact email.";
    static final String CONTRACTOR_SAVE_FAILURE_MESSAGE = "Unable to update contacted properties on contractor after registration request email was sent: {}";
    static final LowMedHigh DEFAULT_NOTE_PRIORITY = LowMedHigh.Low;

    @Autowired
    private RegistrationRequestEmailHelper registrationRequestEmailHelper;
    @Autowired
    private ContractorAccountDAO contractorAccountDAO;
    @Autowired
    private NoteService noteService;

    @Override
    public void onApplicationEvent(SpringContractorOperatorEvent event) {
        ContractorOperator contractorOperator = event.getContractorOperator();
        if (event.getEvent() == ContractorOperatorEventType.RegistrationRequest) {
            ContractorAccount contractor = contractorOperator.getContractorAccount();
            User primaryContact = contractor.getPrimaryContact();
            try {
                registrationRequestEmailHelper.sendInitialEmail(contractor, primaryContact, contractorOperator, FileUtils.getFtpDir());
                addNote(contractorOperator, EMAIL_SUCCESS_NOTE_TEXT);
            } catch (Exception e) {
                addNote(contractorOperator, EMAIL_FAILURE_NOTE_TEXT);
                return;
            }
            try {
                updateContractorContactedInformation(event, contractorOperator, contractor);
            } catch (Exception e) {
                logger.error(CONTRACTOR_SAVE_FAILURE_MESSAGE, e.getMessage());
            }
        }
    }

    private void updateContractorContactedInformation(SpringContractorOperatorEvent event, ContractorOperator contractorOperator, ContractorAccount contractor) {
        if (contractorOperator.getRequestedBy() != null) {
            contractor.setLastContactedByInsideSales(contractorOperator.getRequestedBy());
        } else {
            contractor.setLastContactedByInsideSales(event.getGeneratingEventUserID());
        }
        Date now = new Date();
        contractor.contactByEmail();
        contractor.setLastContactedByInsideSalesDate(now);
        contractor.setLastContactedByAutomatedEmailDate(now);
        contractorAccountDAO.save(contractor);
    }

    private void addNote(ContractorOperator contractorOperator, String noteText) {
        ContractorAccount contractor = contractorOperator.getContractorAccount();
        OperatorAccount clientSiteAccount = contractorOperator.getOperatorAccount();
        int clientSiteId = (clientSiteAccount != null) ? clientSiteAccount.getId() : 1;
        noteService.addNote(contractor, noteText, "", NoteCategory.Registration, DEFAULT_NOTE_PRIORITY, true, clientSiteId);
    }
}
