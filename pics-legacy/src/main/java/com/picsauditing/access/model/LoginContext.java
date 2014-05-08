package com.picsauditing.access.model;

import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.jpa.entities.User;

public class LoginContext {

    private AppUser appUser;
    private Profile profile;
    private String cookie;
    private User user;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
