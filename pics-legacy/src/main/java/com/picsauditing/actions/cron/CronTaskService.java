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
    protected String name;
    protected StringBuilder report = new StringBuilder();
    protected long startTime = 0L;
    private CronTask cronTask;

    public CronTaskService(CronTask cronTask) {
        this.cronTask = cronTask;
    }

    public CronTaskService(String cronTaskName) throws CronTaskException {
        if (!getAllTasks().contains(cronTaskName))
            throw new CronTaskException(cronTaskName + " is not a registered CronTask");
        Object cronTaskImpl = SpringUtils.getBean(cronTaskName);
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

    @Deprecated
    public StringBuilder execute() {
        return report;
    }

    protected void startTask() {
        startTime = System.currentTimeMillis();
        report.append("Starting " + name + "\n\t");
    }

    private void endTask() {
        report.append("SUCCESS...(");
        report.append(new Long(System.currentTimeMillis() - startTime).toString());
        report.append(" millis )");
        report.append("\n\n");
    }

    private CronTaskResult handleException(Throwable t) {
        CronTaskResult result = new CronTaskResult();
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        report.append("\n\n\n");
        report.append(t.getMessage());
        report.append(sw.toString());
        report.append("\n\n\n");
        return result;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return cronTask.getDescription();
    }

    public List<String> getSteps() {
        return cronTask.getSteps();
    }
}
