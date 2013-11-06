package com.picsauditing.actions.cron;

import com.picsauditing.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CronTaskService {
    protected final Logger logger = LoggerFactory.getLogger(CronTaskService.class);
    protected long startTime = 0L;
    protected long endTime = 0L;
    private CronTask cronTask;

    public CronTaskService(CronTask cronTask) {
        this.cronTask = cronTask;
    }

    public CronTaskService(String cronTaskName) throws CronTaskException {
        if (!getAllTasks().contains(cronTaskName))
            throw new CronTaskException(cronTaskName + " is not a registered CronTask");
        cronTask = (CronTask) SpringUtils.getBean(cronTaskName);
    }

    static public Collection<String> getAllTasks() {
        Set<String> classes = new TreeSet<>();
        classes.add("AddLateFees");
        classes.add("AuditBuilderAddAuditRenewalsTask");
        classes.add("CheckRegistrationRequestsHoldDates");
        classes.add("ClearForcedFlagsTask");
        classes.add("DeactivateNonRenewalAccounts");
        classes.add("DeclineOldPendingAccounts");
        classes.add("EbixLoader");
        classes.add("EmailDelinquentContractors");
        classes.add("EmailPendingContractorsTask");
        classes.add("EmailRegistrationRequestsTask");
        classes.add("EmailUpcomingImplementationAudits");
        classes.add("ExpireFlagChangesTask");
        classes.add("FlagChangesEmailTask");
        classes.add("IndexerTask");
        classes.add("RecalculateAuditsTask");
        classes.add("ReportSuggestionsTask");
        classes.add("RunConCronForOldAccountsWithBalances");
        classes.add("SendEmailToBidOnlyAccounts");
        classes.add("TestTask");
        return classes;
    }

    public CronTaskResult run() {
        try {
            startTask();
            return cronTask.run();
        } catch (Throwable t) {
            return handleException(t);
        } finally {
            endTask();
        }
    }

    protected void startTask() {
        startTime = System.currentTimeMillis();
    }

    private void endTask() {
        endTime = System.currentTimeMillis();
    }

    public String getRunTime() {
        return new Long(endTime - startTime).toString();
    }

    private CronTaskResult handleException(Throwable t) {
        CronTaskResult result = new CronTaskResult();
        result.setSuccess(false);

        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        result.getLogger().append(t.getMessage());
        result.getLogger().append("\n");
        result.getLogger().append(sw.toString());

        return result;
    }

    public String getDescription() {
        return cronTask.getDescription();
    }

    public List<String> getSteps() {
        return cronTask.getSteps();
    }
}