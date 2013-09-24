package com.picsauditing.security;

import java.util.Collection;

public interface UserSession {

    public int getUserId();

    public int getAccountId();

    public int getAdminID();

    public boolean isLoggedIn();

    public boolean isOperator();

    public boolean isCorporate();

    public boolean hasPermission(String permission);

    public Collection<Integer> getVisibleAccounts();

    public Collection<Integer> getCorporateParent();

    public Collection<Integer> getAllInheritedGroupIds();
}
