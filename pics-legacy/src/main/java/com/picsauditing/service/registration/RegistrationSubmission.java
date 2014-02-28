package com.picsauditing.service.registration;

import com.picsauditing.service.registration.validator.RegistrationValidator;

public class RegistrationSubmission {
    private final RegistrationValidator passedValidator;
    private final RegistrationService parentService;

    RegistrationSubmission (
            RegistrationValidator v,
            RegistrationService s
    ) {
        this.passedValidator = v;
        this.parentService = s;
    }

    public void submit() {
        passedValidator.validate(this);
        parentService.doRegistration(this);
    }


}
