package com.picsauditing.actions.cron;

import com.picsauditing.EntityFactory;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.mail.EmailBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailDelinquentContractorsTest {
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private EmailQueueDAO emailQueueDAO;
    @Mock
    private EmailBuilder emailBuilder;
    private EmailDelinquentContractors cron;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cron = new EmailDelinquentContractors(contractorAccountDAO, emailQueueDAO);
    }

    @Test
    public void testSplitPendingAndDeliquentInvoices() throws Exception {
        ContractorAccount deactContractor = EntityFactory.makeContractor();
        ContractorAccount delinContractor = EntityFactory.makeContractor();

        Calendar date = Calendar.getInstance();

        Invoice deactInvoice = new Invoice();
        deactInvoice.setAccount(deactContractor);
        date.add(Calendar.DATE, -1);
        deactInvoice.setDueDate(date.getTime());
        Invoice delinInvoice = new Invoice();
        delinInvoice.setAccount(delinContractor);
        date.add(Calendar.DATE, 6);
        delinInvoice.setDueDate(date.getTime());

        List<Invoice> invoices = new ArrayList<>();
        invoices.add(deactInvoice);
        invoices.add(delinInvoice);

        List<Integer> idList = new ArrayList<>();
        idList.add(deactContractor.getId());
        idList.add(delinContractor.getId());

        when(emailQueueDAO.findContractorsWithRecentEmails(org.mockito.Matchers.anyString(), org.mockito.Matchers.anyInt())).thenReturn(idList);

        Map<ContractorAccount, Integer> map = cron.splitPendingAndDeliquentInvoices(invoices);
        assertTrue(map.size() == 0);
    }

    @Test
    public void testSendEmailsTo_SingleContractorAccountGetsSingleEmail() throws Exception {
        ContractorAccount cAccount = EntityFactory.makeContractor();
        Map<ContractorAccount, Integer> fakeContractors = new TreeMap<>();
        fakeContractors.put(cAccount, 48);

        EmailQueue email = new EmailQueue();
        email.setContractorAccount(EntityFactory.makeContractor());

        when(emailBuilder.build()).thenReturn(email);

        cron.sendEmailsTo(fakeContractors);

        verify(emailQueueDAO).save(any(EmailQueue.class));
        verify(contractorAccountDAO).save(any(Note.class));
    }

    @Test
    public void testSendInvalidEmailsToBilling_SingleContractorAccountGetsSingleEmail() throws Exception {
        ContractorAccount cAccount = EntityFactory.makeContractor();
        Map<ContractorAccount, Integer> fakeContractors = new TreeMap<>();
        fakeContractors.put(cAccount, 48);

        EmailQueue email = new EmailQueue();
        email.setContractorAccount(EntityFactory.makeContractor());

        when(emailBuilder.build()).thenReturn(email);

        cron.sendEmailsTo(fakeContractors);

        verify(emailQueueDAO).save(any(EmailQueue.class));
        verify(contractorAccountDAO).save(any(Note.class));
    }

}
