package com.picsauditing.actions.cron;

import com.picsauditing.dao.AppPropertyDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class CronTaskOld {
    protected final Logger logger = LoggerFactory.getLogger(CronTaskOld.class);
    protected String name;
    protected StringBuilder report = new StringBuilder();
    private AppPropertyDAO enabled;

    public CronTaskOld(String name) {

    }

    public StringBuilder execute() {
        return report;
    }


    public String getName() {
        return name;
    }

    abstract protected void run() throws Throwable;

    public void setEnabled(AppPropertyDAO enabled) {
        this.enabled = enabled;
    }
}
