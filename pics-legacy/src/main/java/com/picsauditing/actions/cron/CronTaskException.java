package com.picsauditing.actions.cron;

public class CronTaskException extends Exception {
    public CronTaskException(CronTask task) {
        super(task.getName());
    }
}
