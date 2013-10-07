package com.picsauditing.jpa.entities.builders;

import java.util.Date;
import java.util.Locale;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.YesNo;

public class UserBuilder {
    private final User user = new User();

    public User build() {
        user.setUsingVersion7Menus(false);
        return user;
    }

    public UserBuilder account(Account account) {
        user.setAccount(account);
        user.setUsingVersion7MenusDate(new Date());
        return  this;
    }

    public UserBuilder permission(OpPerms perms) {
        UserAccess access = new UserAccess();
        access.setOpPerm(perms);
        access.setUser(user);
        access.setViewFlag(true);
        user.getOwnedPermissions().add(access);
        return this;
}

    public UserBuilder id(int i) {
        user.setId(i);
        return this;
    }

    public UserBuilder locale(Locale locale) {
        user.setLocale(locale);
        return this;
    }

    public UserBuilder denyPermission(OpPerms contractorApproval) {
        // nothing to do here, just being explicit about this
        return this;
    }

    public UserBuilder group() {
        user.setIsGroup(YesNo.Yes);
        return this;
    }

    public UserBuilder name(String name) {
        user.setName(name);
        return this;
    }
}
