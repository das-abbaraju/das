package com.picsauditing.service.email.events.listener;

import com.picsauditing.PICS.RegistrationRequestEmailHelper;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.account.events.ContractorOperatorEventType;
import com.picsauditing.service.account.events.SpringContractorOperatorEvent;
import com.picsauditing.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.util.Date;

public class ContractorOperatorEventListener implements ApplicationListener<SpringContractorOperatorEvent> {
    @Autowired
    private RegistrationRequestEmailHelper registrationRequestEmailHelper;
    @Autowired
    private ContractorAccountDAO contractorAccountDAO;


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
            } catch (Exception e) {
                // TODO: figure out what to do on error
                e.printStackTrace();
            }
        }
    }
}
