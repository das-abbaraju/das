package com.picsauditing.actions.cron;

public class CronToggle {
    static public boolean isCronTaskTextEnabled(String currentValue) {
        if (currentValue == null)
            return true;
        if (currentValue.equals("0"))
            return false;
        if (currentValue.equalsIgnoreCase("false"))
            return false;

        return true;
    }
}
