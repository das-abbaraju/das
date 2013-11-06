package com.picsauditing.actions.cron;

import com.picsauditing.PICS.BillingService;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.account.AccountStatusChanges;
import com.picsauditing.util.IndexerEngine;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DeactivateNonRenewalAccounts implements CronTask {
    protected Permissions permissions = null;
    @Autowired
    IndexerEngine indexer;
    @Autowired
    ContractorAccountDAO contractorAccountDAO;
    @Autowired
    BillingService billingService;
    @Autowired
    AccountStatusChanges accountStatusChanges;

    public String getDescription() {
        return "Do not change the payment expires when deactivating accounts. " +
                "This is actually Canceling an account, not a deactivation.";
    }

    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        String where = "a.status = 'Active' AND a.renew = 0 AND paymentExpires < NOW()";
        List<ContractorAccount> conAcctList = contractorAccountDAO.findWhere(where);
        for (ContractorAccount contractor : conAcctList) {
            final String reason = contractor.getAccountLevel().isBidOnly() ? AccountStatusChanges
                    .BID_ONLY_ACCOUNT_REASON : AccountStatusChanges.DEACTIVATED_NON_RENEWAL_ACCOUNT_REASON;
            billingService.syncBalance(contractor);
            contractor.setAuditColumns(new User(User.SYSTEM));
            accountStatusChanges.deactivateContractor(contractor, permissions, reason,
                    "Automatically inactivating account based on expired membership");
        }
        return results;
    }
}
