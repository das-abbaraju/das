package com.picsauditing.model.viewmodel.builder;

import com.picsauditing.model.viewmodel.RegistrationSuccessResponse;
import com.picsauditing.model.viewmodel.User;

import java.util.ArrayList;

public class RegistrationSuccessResponseBuilder {

    private RegistrationSuccessResponse registrationSuccessResponse = new RegistrationSuccessResponse();


    public RegistrationSuccessResponseBuilder user(User user) {
        if (registrationSuccessResponse.getUsers() == null) {
            registrationSuccessResponse.setUsers(new ArrayList<User>());
        }

        registrationSuccessResponse.getUsers().add(user);
        return this;
    }

    public RegistrationSuccessResponse build() {
        return registrationSuccessResponse;
    }
}
