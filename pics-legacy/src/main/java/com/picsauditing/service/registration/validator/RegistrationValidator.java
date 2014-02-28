package com.picsauditing.service.registration.validator;

import com.picsauditing.service.registration.RegistrationSubmission;

public interface RegistrationValidator {
    public void validate(RegistrationSubmission registrationSubmission);
}
