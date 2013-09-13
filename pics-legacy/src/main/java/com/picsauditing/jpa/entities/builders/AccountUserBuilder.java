package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAccountRole;

import java.util.Date;

public class AccountUserBuilder {
    private AccountUser accountUser = new AccountUser();

    public AccountUserBuilder user(User user) {
        accountUser.setUser(user);
        return this;
    }

    public AccountUserBuilder startDate(Date startDate) {
        accountUser.setStartDate(startDate);
        return this;
    }

    public AccountUserBuilder endDate(Date endDate) {
        accountUser.setEndDate(endDate);
        return this;
    }

    public AccountUserBuilder role(UserAccountRole role) {
        accountUser.setRole(role);
        return this;
    }

    public AccountUser build() {
        return  accountUser;
    }
}
