package com.picsauditing.model.viewmodel;

import com.picsauditing.model.viewmodel.builder.UserBuilder;

/**
 * Created by dalvarado on 4/3/14.
 */
public class User {
    private boolean loggedIn;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public static com.picsauditing.model.viewmodel.builder.UserBuilder builder() {
        return new UserBuilder();
    }
}
