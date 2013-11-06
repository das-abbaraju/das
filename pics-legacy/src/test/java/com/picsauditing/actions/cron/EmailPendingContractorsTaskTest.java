package com.picsauditing.actions.cron;

import com.picsauditing.EntityFactory;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmailPendingContractorsTaskTest {
    private EmailPendingContractorsTask cron;
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private EmailQueueDAO emailQueueDAO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cron = new EmailPendingContractorsTask();
        cron.emailQueueDAO = emailQueueDAO;
        cron.contractorAccountDAO = contractorAccountDAO;
        cron.duplicateContractors = new EmailDuplicateContractors(contractorAccountDAO, emailQueueDAO);
    }

    @Test
    public void testSendEmailPendingAccounts_SqlHasCountryRestrictionWhenEmailExclusionsExist() throws Exception {
        doTestSqlHasCountryRestriction();
    }

    @Test
    public void testSendEmailPendingAccounts_SqlHasCountryRestrictionWhenNoEmailExclusions() throws Exception {
        cron.emailExclusionList.add("123");
        cron.emailExclusionList.add("124");
        doTestSqlHasCountryRestriction();
    }

    private void doTestSqlHasCountryRestriction() throws Exception {
        List<ContractorAccount> emptyList = new ArrayList<ContractorAccount>();
        when(contractorAccountDAO.findPendingAccounts(anyString())).thenReturn(emptyList);

        cron.sendEmailPendingAccounts();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(contractorAccountDAO, times(3)).findPendingAccounts(captor.capture());

        List<String> allSql = captor.getAllValues();

        for (String sql : allSql) {
            assertTrue(sql.contains("a.country IN ('US','CA')"));
        }
    }

    @Test
    public void testRemoveContractorsWithRecentlySentEmail() throws Exception {
        List<ContractorAccount> contractorList = new ArrayList<>();
        contractorList.add(EntityFactory.makeContractor());
        contractorList.add(EntityFactory.makeContractor());
        contractorList.add(EntityFactory.makeContractor());

        List<Integer> idList = new ArrayList<>();
        idList.add(contractorList.get(1).getId()); // get middle contractor

        when(emailQueueDAO.findContractorsWithRecentEmails(org.mockito.Matchers.anyString(), org.mockito.Matchers.anyInt())).thenReturn(idList);
        Whitebox.invokeMethod(cron, "removeContractorsWithRecentlySentEmail", contractorList, 1);
        assertTrue(contractorList.size() == 2);
    }


}
