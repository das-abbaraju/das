package com.picsauditing.actions.cron;

import com.picsauditing.dao.ContractorAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RunConCronForOldAccountsWithBalances implements CronTask {
    @Autowired
    ContractorAccountDAO contractorAccountDAO;

    @Override
    public String getDescription() {
        return "Bump Dead Accounts that still have balances";
    }

    @Override
    public List<String> getSteps() {
        return null;
    }

    public CronTaskResult run() {
        contractorAccountDAO.updateRecalculationForDeadAccountsWithBalances();
        return new CronTaskResult(true, "success");
    }
}
