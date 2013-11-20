package com.picsauditing.i18n.model;

import java.util.Locale;

public class DefaultUsageContext implements UsageContext {
    private static final String UNKNOWN_CONTEXT = "UNKNOWN";

    @Override
    public String environment() {
        return UNKNOWN_CONTEXT;
    }

    @Override
    public String pageName() {
        return UNKNOWN_CONTEXT;
    }

    @Override
    public Locale locale() {
        return new Locale(UNKNOWN_CONTEXT);
    }
}
