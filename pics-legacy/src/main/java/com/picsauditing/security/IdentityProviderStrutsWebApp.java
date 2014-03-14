package com.picsauditing.security;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Permissions;

public class IdentityProviderStrutsWebApp implements IdentityProvider {

    @Override
    public Identity identity() {
        Identity identity = new Identity();
        if (ActionContext.getContext().getSession() != null) {
            Permissions permissions = (Permissions) ActionContext.getContext().getSession().get(Permissions.SESSION_PERMISSIONS_COOKIE_KEY);
            if (permissions.getAdminID() > 0) {
                identity.setLoggedInUserId(permissions.getAdminID());
            } else {
                identity.setLoggedInUserId(permissions.getUserId());
            }
        }
        return identity;
    }
}
