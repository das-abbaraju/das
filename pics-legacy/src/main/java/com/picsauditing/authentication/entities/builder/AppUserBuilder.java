package com.picsauditing.authentication.entities.builder;

import com.picsauditing.authentication.entities.AppUser;

public class AppUserBuilder {
    private AppUser appUser = new AppUser();

    public AppUser build() {
        return appUser;
    }

    public AppUserBuilder id(int id) {
        appUser.setId(id);
        return this;
    }
}
