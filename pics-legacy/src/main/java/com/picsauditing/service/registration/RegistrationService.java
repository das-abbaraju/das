package com.picsauditing.service.registration;

public class RegistrationService {


    public RegistrationSubmission newRegistration() {
        return new RegistrationSubmission(this);
    }

    void doRegistration(RegistrationSubmission registrationSubmission) {
        //TODO
    }
}
