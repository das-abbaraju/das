package com.picsauditing.service.registration;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;

public abstract class RegistrationResult {

    public static class RegistrationFailure extends RegistrationResult {
        RegistrationFailure(Exception e) { this.problem = e; }

        final private Exception problem;

        public Exception getProblem() {
            return problem;
        }
    }

    public static class RegistrationSuccess extends RegistrationResult {
        RegistrationSuccess(ContractorAccount c, User u) {
            this.contractor = c;
            this.user = u;
        }

        private final ContractorAccount contractor;
        private final User user;

        public ContractorAccount getContractor() {
            return contractor;
        }

        public User getUser() {
            return user;
        }
    }
}
