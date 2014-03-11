package com.picsauditing.struts.controller.forms;

import com.picsauditing.util.Strings;

import java.util.Locale;

public class RegistrationLocaleForm {

    private Locale locale;
    private String dialect;
    private String language;

    public Locale getLocale() {
        if (locale != null) {
            return locale;
        } else if (Strings.isNotEmpty(language)) {
            return new Locale(language, Strings.isEmpty(dialect) ? null : dialect);
        } else {
            return Locale.US;
        }
    }

    public void setLocale(String locale) {
        this.locale = new Locale(locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
