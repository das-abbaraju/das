package com.picsauditing.actions.cron;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.account.AccountStatusChanges;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class DeactivateNonRenewalAccountsTest {
    @Mock
    private ContractorAccountDAO contractorAccountDAO;
    @Mock
    private BillingService billingService;
    @Mock
    private AccountStatusChanges accountStatusChanges;
    @Mock
    private ContractorAccount mockContractorAccount;
    private DeactivateNonRenewalAccounts cron;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cron = new DeactivateNonRenewalAccounts();
        cron.contractorAccountDAO = contractorAccountDAO;
        cron.accountStatusChanges = accountStatusChanges;
        cron.billingService = billingService;
    }

    @Test
    public void testDeactivateNonRenewalAccounts() throws Exception {
        String where = "a.status = 'Active' AND a.renew = 0 AND paymentExpires < NOW()";
        List<ContractorAccount> contractors = buildMockContractorList();
        when(contractorAccountDAO.findWhere(where)).thenReturn(contractors);

        cron.run();

        for (ContractorAccount contractor : contractors) {
            verifyContractor(contractor, contractor.getAccountLevel().isBidOnly());
        }
    }

    private List<ContractorAccount> buildMockContractorList() {
        List<ContractorAccount> contractors = new ArrayList<>();
        contractors.add(buildMockContractorAccount(1, false));
        contractors.add(buildMockContractorAccount(2, true));

        return contractors;
    }

    private ContractorAccount buildMockContractorAccount(int id, boolean isBidOnly) {
        ContractorAccount contractor = Mockito.mock(ContractorAccount.class);
        when(contractor.getId()).thenReturn(id);

        if (isBidOnly) {
            when(contractor.getAccountLevel()).thenReturn(AccountLevel.BidOnly);
        } else {
            when(contractor.getAccountLevel()).thenReturn(AccountLevel.Full);
        }

        return contractor;
    }

    private void verifyContractor(ContractorAccount contractor, boolean isBidOnly) {
        String reason = isBidOnly ? AccountStatusChanges.BID_ONLY_ACCOUNT_REASON
                : AccountStatusChanges.DEACTIVATED_NON_RENEWAL_ACCOUNT_REASON;

        verify(billingService, times(1)).syncBalance(contractor);
        verify(contractor, times(1)).setAuditColumns(new User(User.SYSTEM));
        verify(accountStatusChanges, times(1)).deactivateContractor(contractor, null, reason,
                "Automatically inactivating account based on expired membership");
    }

}
