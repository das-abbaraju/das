package com.picsauditing.model.viewmodel.builder;

import com.picsauditing.model.viewmodel.RegistrationSignupForm;
import com.picsauditing.struts.controller.forms.RegistrationForm;

public class RegistrationAccountBuilder {
    private RegistrationSignupForm registrationSignupForm = new RegistrationSignupForm();

    public RegistrationSignupForm build() {
        return registrationSignupForm;
    }

    public RegistrationAccountBuilder registrationForm(RegistrationForm registrationForm) {
        registrationSignupForm.setRegistrationForm(registrationForm);
        return this;
    }
}
