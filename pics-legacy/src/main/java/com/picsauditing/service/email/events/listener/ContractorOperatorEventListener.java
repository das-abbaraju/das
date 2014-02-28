package com.picsauditing.service.email.events.listener;

import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.actions.contractors.RequestNewContractorAccount;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.model.i18n.ThreadLocalLocale;
import com.picsauditing.service.account.events.ContractorOperatorEventType;
import com.picsauditing.service.account.events.SpringContractorOperatorEvent;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.service.notes.NoteService;
import com.picsauditing.util.FileUtils;
import com.spun.util.persistence.Loader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Date;
import java.util.Locale;

public class ContractorOperatorEventListener implements ApplicationListener<SpringContractorOperatorEvent> {
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
                OperatorAccount clientSiteAccount = contractorOperator.getOperatorAccount();
                int clientSiteId = (clientSiteAccount != null) ? clientSiteAccount.getId() : 1;
                noteService.addNote(contractor, "Sent initial contact email.", "", NoteCategory.Registration, LowMedHigh.Low, true, clientSiteId);
            } catch (Exception e) {
                // TODO: figure out what to do on error
                e.printStackTrace();
            }
        }
    }
}
