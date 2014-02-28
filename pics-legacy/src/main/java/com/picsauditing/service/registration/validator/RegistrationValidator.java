package com.picsauditing.service.registration.validator;

import com.picsauditing.service.registration.RegistrationSubmission;

public interface RegistrationValidator {
    public void validate(RegistrationSubmission registrationSubmission);

    public static abstract class ValidationResult {}
    public static class ValidationSuccess extends ValidationResult {}
    public static class ValidationFailure extends ValidationResult {
        public ValidationFailure(String reason) { this.reason = reason; }
        private final String reason;
        public String getReason() { return reason; }
    }

}
