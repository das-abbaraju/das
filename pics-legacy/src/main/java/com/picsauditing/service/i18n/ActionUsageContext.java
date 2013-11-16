package com.picsauditing.service.i18n;

import com.picsauditing.i18n.model.UsageContext;
import com.picsauditing.model.i18n.ThreadLocalLocale;
import com.picsauditing.util.Strings;
import com.spun.util.persistence.Loader;
import org.apache.struts2.ServletActionContext;

import java.util.Locale;

public class ActionUsageContext implements UsageContext {
    private static final String environment = System.getProperty("pics.env");
    private static Loader<Locale> localeProvider = ThreadLocalLocale.INSTANCE;
    public static final String DEFAULT_PAGENAME = "UNKNOWN";
    public static final String DEFAULT_ENVIRONMENT = "UNKNOWN";
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public String environment() {
        return (environment == null) ? DEFAULT_ENVIRONMENT : environment;
    }

    public String pageName() {
        try {
            String pageName = ServletActionContext.getContext().getName();
            if (Strings.isEmpty(pageName)) {
                pageName = DEFAULT_PAGENAME;
            }
            return pageName;
        } catch (Exception e) {
            return DEFAULT_PAGENAME;
        }
    }

    public Locale locale() {
        try {
            return localeProvider.load();
        } catch (Exception e) {
            return DEFAULT_LOCALE;
        }
    }
}
