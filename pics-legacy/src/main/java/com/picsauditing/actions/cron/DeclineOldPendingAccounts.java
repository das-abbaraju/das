package com.picsauditing.actions.cron;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.account.AccountStatusChanges;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class DeclineOldPendingAccounts extends CronTask {
    private static String NAME = "MovePendingAccountsToDeclined";
    protected Permissions permissions = null;
    private ContractorAccountDAO contractorAccountDAO;
    private AccountStatusChanges accountStatusChanges;

    public DeclineOldPendingAccounts(ContractorAccountDAO contractorAccountDAO, AccountStatusChanges accountStatusChanges) {
        super(NAME);
        this.contractorAccountDAO = contractorAccountDAO;
        this.accountStatusChanges = accountStatusChanges;
    }

    protected void run() {
        List<ContractorAccount> deactivateList = contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus();
        logger.debug("Found {1} entries", deactivateList.size());

        if (CollectionUtils.isEmpty(deactivateList)) {
            return;
        }

        for (ContractorAccount contractor : deactivateList) {
            accountStatusChanges.declineContractor(contractor, permissions,
                    AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
                    AccountStatusChanges.NOTE_DID_NOT_COMPLETE_PICS_PROCESS_REASON);
        }
    }
}
