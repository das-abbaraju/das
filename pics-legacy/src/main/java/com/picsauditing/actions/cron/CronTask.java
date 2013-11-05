package com.picsauditing.actions.cron;

import com.picsauditing.dao.AppPropertyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

abstract public class CronTask {
    private static String CRON_TASK_PREFIX = "Cron.Task.";
    protected final Logger logger = LoggerFactory.getLogger(CronTask.class);
    protected String name;
    protected StringBuilder report = new StringBuilder();
    protected long startTime = 0L;
    protected boolean enabled = true;

    public CronTask(String name) {
        this.name = CRON_TASK_PREFIX + name;
    }

    public StringBuilder execute() {
        if (!enabled) {
            skipTask();
            return report;
        }
        try {
            startTask();
            run();
        } catch (Throwable t) {
            handleException(t);
        } finally {
            endTask();
        }
        return report;
    }

    protected abstract void run() throws Throwable;

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

    private void skipTask() {
        report.append("Skipping " + name + "\n\n");
    }

    private void handleException(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        report.append("\n\n\n");
        report.append(t.getMessage());
        report.append(sw.toString());
        report.append("\n\n\n");
    }

    public void setEnabled(AppPropertyDAO propertyDAO) {
        String property = propertyDAO.getProperty(name);
        enabled = CronToggle.isCronTaskTextEnabled(property);
    }

    public String getName() {
        return name;
    }
}
