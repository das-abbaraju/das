package com.picsauditing.actions.cron;

import java.util.ArrayList;
import java.util.List;

public class TestTask implements CronTask {

    public String getDescription() {
        return "This is a default test for Cron Tasks";
    }

    public List<String> getSteps() {
        List<String> list = new ArrayList<>();
        list.add("This will wait for 1000 and then return success");
        return list;
    }

    public CronTaskResult run() {
        CronTaskResult results = new CronTaskResult(true, "");

        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return results;
    }
}
