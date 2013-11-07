package com.picsauditing.actions.cron;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.model.account.AccountStatusChanges;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DeclineOldPendingAccounts implements CronTask {
    protected Permissions permissions = null;
    @Autowired
    ContractorAccountDAO contractorAccountDAO;
    @Autowired
    AccountStatusChanges accountStatusChanges;

    public String getDescription() {
        return "Decline Contractors who have not paid in 90 days of quote";
    }

    public List<String> getSteps() {
        List<String> steps = new ArrayList<>();
        List<ContractorAccount> deactivateList = contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus();
        for (ContractorAccount contractorAccount : deactivateList) {
            steps.add("Will decline " + contractorAccount.getName() + " (" + contractorAccount.getId() + ")");
        }
        return steps;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");
        List<ContractorAccount> deactivateList = contractorAccountDAO.findPendingAccountsToMoveToDeclinedStatus();
        results.getLogger().append("Found " + deactivateList.size() + " contractor(s) to deactivate");

        if (CollectionUtils.isEmpty(deactivateList)) {
            return results;
        }

        for (ContractorAccount contractor : deactivateList) {
            accountStatusChanges.declineContractor(contractor, permissions,
                    AccountStatusChanges.DID_NOT_COMPLETE_PICS_PROCESS_REASON,
                    AccountStatusChanges.NOTE_DID_NOT_COMPLETE_PICS_PROCESS_REASON);
            results.getLogger().append(",  " + contractor.getId());
        }
        return results;
    }
}
