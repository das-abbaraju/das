package com.picsauditing.actions.cron;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.EmailQueue;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EmailRegistrationRequestsTaskTest {

    @Test
    public void testRunCRREmailBlast() throws Exception {
        List<ContractorRegistrationRequest> list = new ArrayList<ContractorRegistrationRequest>();
        ContractorRegistrationRequest crr = new ContractorRegistrationRequest();
        crr.setName("test");
        list.add(crr);

        EmailQueue email = new EmailQueue();
        email.setContractorAccount(new ContractorAccount(3));
        // email.setEmailTemplate(any(EmailTemplate.class));
        // when(emailBuilder.build()).thenReturn(email);

        // Whitebox.invokeMethod(cron, "runCRREmailBlast", list, 1, "test");

        // verify(emailQueueDAO).save(any(EmailQueue.class));
        // verify(contractorRegistrationRequestDAO).save(any(ContractorRegistrationRequest.class));
    }


}
