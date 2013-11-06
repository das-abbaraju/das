package com.picsauditing.actions.cron;

import java.util.List;

public interface CronTask {
    String getDescription();

    List<String> getSteps();

    CronTaskResult run();
}

