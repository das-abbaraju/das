package com.picsauditing.employeeguard.web;

public class SessionInfoProviderFactory {

    private static SessionInfoProvider sessionInfoProvider = new StrutsSessionInfoProvider();

    public static SessionInfoProvider getSessionInfoProvider() {
        return sessionInfoProvider;
    }
}
