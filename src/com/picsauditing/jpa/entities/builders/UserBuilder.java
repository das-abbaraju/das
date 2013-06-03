package com.picsauditing.jpa.entities.builders;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.builders.PermissionsBuilder;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccess;
import com.picsauditing.jpa.entities.YesNo;

import java.util.Date;
import java.util.Locale;

public class UserBuilder {
    private User user = new User();

    public User build() {
        user.setUsingVersion7Menus(false);
        return user;
    }

    public UserBuilder account(OperatorAccount account) {
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
