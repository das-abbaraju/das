package com.picsauditing.i18n.util;

import java.util.Locale;

public class Strings {
    public static final String EMPTY_STRING = "";

    public static boolean isEmpty(String value) {
        if (value == null) {
            return true;
        }

        value = value.trim();
        return value.length() == 0;
    }

    public static Locale parseLocale(String locale) {
        Locale test = null;
        String[] loc = locale.split("[_-]");
        try {
            test = new Locale(loc[0], loc[1], loc[2]);
        } catch (Exception no_variant) {
            try {
                test = new Locale(loc[0], loc[1]);
            } catch (Exception no_country) {
                try {
                    test = new Locale(loc[0]);
                } catch (Exception bad_input) {
                }
            }
        }
        return test;
    }

}
