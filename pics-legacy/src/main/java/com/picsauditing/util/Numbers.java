package com.picsauditing.util;

import java.text.DecimalFormat;

public class Numbers {

    public static String printDouble(Double d) {
        return printDouble(d, "##.##");
    }

    public static String printDouble(Double d, String format) {
        DecimalFormat df = new DecimalFormat(format);
        return df.format(d);
    }
}
