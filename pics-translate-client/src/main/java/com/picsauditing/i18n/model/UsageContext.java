package com.picsauditing.i18n.model;

import java.util.Locale;

public interface UsageContext {
    String DEFAULT_PAGENAME = "UNKNOWN";

    String environment();
    String pageName();
    Locale locale();
}
