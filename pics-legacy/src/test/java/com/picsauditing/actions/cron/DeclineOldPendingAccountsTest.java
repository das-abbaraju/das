package com.picsauditing.actions.cron;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.account.AccountStatusChanges;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DeclineOldPendingAccountsTest {

    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private AccountStatusChanges accountStatusChanges;
    private DeclineOldPendingAccounts cron;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        cron = new DeclineOldPendingAccounts();
        cron.contractorAccountDAO = contractorAccountDAO;
        cron.accountStatusChanges = accountStatusChanges;
    }

    @Test
    public void testDeactivatePendingAccounts_emptyList() {
        List<ContractorAccount> cList = new ArrayList<>();
        when(contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus()).thenReturn(cList);
        verify(accountStatusChanges, never()).deactivateContractor(any(ContractorAccount.class),
                any(Permissions.class), anyString(), anyString());
    }

    @Test
    public void testDeactivatePendingAccounts() throws Exception {
        ContractorAccount cAccount = new ContractorAccount(1);
        ContractorAccount cAccount2 = new ContractorAccount(2);
        cAccount.setStatus(AccountStatus.Pending);
        cAccount2.setStatus(AccountStatus.Pending);
        List<ContractorAccount> cList = new ArrayList<>();
        cList.add(cAccount);
        cList.add(cAccount2);

        when(contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus()).thenReturn(cList);

        cron.run();

        verify(accountStatusChanges).declineContractor(cAccount, null,
                AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
                AccountStatusChanges.NOTE_DID_NOT_COMPLETE_PICS_PROCESS_REASON);
        verify(accountStatusChanges).declineContractor(cAccount2, null,
                AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
                AccountStatusChanges.NOTE_DID_NOT_COMPLETE_PICS_PROCESS_REASON);
    }

}
