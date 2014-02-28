package com.picsauditing.service.registration;

import com.picsauditing.service.registration.validator.RegistrationValidator;

public class RegistrationService {

    private final RegistrationValidator validator;

    public RegistrationService(
            RegistrationValidator rv
    ) {
        this.validator = rv;
    }

    public RegistrationSubmission newRegistration() {
        return new RegistrationSubmission(validator, this);
    }

    void doRegistration(RegistrationSubmission registrationSubmission) {
        //TODO
    }
}
