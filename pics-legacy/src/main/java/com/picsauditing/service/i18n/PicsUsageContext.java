package com.picsauditing.service.i18n;

import com.picsauditing.i18n.model.UsageContext;
import com.picsauditing.model.i18n.ThreadLocalLocale;
import com.spun.util.persistence.Loader;

import java.util.Locale;

abstract public class PicsUsageContext implements UsageContext {
    public static final String ENVIRONMENT_ENV_VAR_NAME = "pics.env";
    private static final String environment = System.getProperty(ENVIRONMENT_ENV_VAR_NAME);
    private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final String DEFAULT_ENVIRONMENT = "UNKNOWN";

    public String environment() {
        return (environment == null) ? DEFAULT_ENVIRONMENT : environment;
    }

    public Locale locale() {
        try {
            return localeProvider.load();
        } catch (Exception e) {
            return DEFAULT_LOCALE;
        }
    }

}
