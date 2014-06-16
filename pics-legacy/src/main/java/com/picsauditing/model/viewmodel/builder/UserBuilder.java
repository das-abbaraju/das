package com.picsauditing.model.viewmodel.builder;

import com.picsauditing.model.viewmodel.User;

public class UserBuilder {
    private User user = new User();

    public UserBuilder loggedIn() {
        user.setLoggedIn(true);
        return this;
    }

    public User build() {
        return user;
    }
}
