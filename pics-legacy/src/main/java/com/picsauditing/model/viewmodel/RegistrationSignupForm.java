package com.picsauditing.model.viewmodel;

import com.picsauditing.model.viewmodel.builder.RegistrationAccountBuilder;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.struts.controller.forms.RegistrationLocaleForm;

public class RegistrationSignupForm {
    private RegistrationLocaleForm localeForm;
    private RegistrationForm registrationForm;
    private String registrationRequestKey;

    public RegistrationSignupForm() {
        setRegistrationForm(new RegistrationForm());
        setLocaleForm(new RegistrationLocaleForm());
    }

    public RegistrationLocaleForm getLocaleForm() {
        return localeForm;
    }

    public void setLocaleForm(RegistrationLocaleForm localeForm) {
        this.localeForm = localeForm;
    }

    public RegistrationForm getRegistrationForm() {
        return registrationForm;
    }

    public void setRegistrationForm(RegistrationForm registrationForm) {
        this.registrationForm = registrationForm;
    }

    public static RegistrationAccountBuilder builder() {
        return new RegistrationAccountBuilder();
    }

    public String getRegistrationRequestKey() {
        return registrationRequestKey;
    }

    public void setRegistrationRequestKey(String registrationRequestKey) {
        this.registrationRequestKey = registrationRequestKey;
    }
}
