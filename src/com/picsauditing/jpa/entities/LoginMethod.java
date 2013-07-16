package com.picsauditing.jpa.entities;

public enum LoginMethod implements Translatable {
    Credentials, SwitchTo, RememberMeCookie;

    public static final String I18N_PREFIX = "UserLoginLog.LoginMethod.";

    @Override
    public String getI18nKey() {
        return I18N_PREFIX + toString();
    }

    @Override
    public String getI18nKey(String property) {
        return getI18nKey() + "." + property;
    }

}
