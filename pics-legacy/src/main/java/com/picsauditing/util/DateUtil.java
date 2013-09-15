package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

public class DateUtil {

    private final static Map<Integer, String> DAYS_OF_WEEK = new HashMap<>();

    static {
        DAYS_OF_WEEK.put(1, "Global.MON");
        DAYS_OF_WEEK.put(2, "Global.TUE");
        DAYS_OF_WEEK.put(3, "Global.WED");
        DAYS_OF_WEEK.put(4, "Global.THU");
        DAYS_OF_WEEK.put(5, "Global.FRI");
        DAYS_OF_WEEK.put(6, "Global.SAT");
        DAYS_OF_WEEK.put(7, "Global.SUN");
    }

    private final static Map<Integer, String> MONTHS_OF_YEAR = new HashMap<>();

    static {
        MONTHS_OF_YEAR.put(1, "Global.JAN");
        MONTHS_OF_YEAR.put(2, "Global.FEB");
        MONTHS_OF_YEAR.put(3, "Global.MAR");
        MONTHS_OF_YEAR.put(4, "Global.APR");
        MONTHS_OF_YEAR.put(5, "Global.MAY");
        MONTHS_OF_YEAR.put(6, "Global.JUN");
        MONTHS_OF_YEAR.put(7, "Global.JUL");
        MONTHS_OF_YEAR.put(8, "Global.AUG");
        MONTHS_OF_YEAR.put(9, "Global.SEP");
        MONTHS_OF_YEAR.put(10, "Global.OCT");
        MONTHS_OF_YEAR.put(11, "Global.NOV");
        MONTHS_OF_YEAR.put(12, "Global.DEC");
    }

    public static String getDayOfWeek(int dayOfWeek) {
        return DAYS_OF_WEEK.get(dayOfWeek);
    }

    public static String getMonthOfYear(int monthOfYear) {
        return MONTHS_OF_YEAR.get(monthOfYear);
    }


}
