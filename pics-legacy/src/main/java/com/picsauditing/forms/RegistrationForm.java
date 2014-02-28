package com.picsauditing.forms;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.service.registration.RegistrationService;
import com.picsauditing.service.registration.RegistrationSubmission;

public class RegistrationForm {

    public static RegistrationForm fromContractor(ContractorAccount input) {
        final RegistrationForm form = new RegistrationForm();

        return form;
    }

    public RegistrationSubmission createSubmission(RegistrationService service) {
        final RegistrationSubmission submission = service.newRegistration();

        return submission;
    }
}
