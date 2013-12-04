package com.picsauditing.actions.cron;

public class CronTaskResult {
    private boolean success;
    private StringBuilder log = new StringBuilder();

    public CronTaskResult() {
    }

    public CronTaskResult(boolean success, String log) {
        this.success = success;
        this.log.append(log);
    }

    public boolean wasSuccessful() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public StringBuilder getLogger() {
        return log;
    }

    public String getLog() {
        return log.toString();
    }
}
