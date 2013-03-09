package com.picsauditing.interceptors;

import com.picsauditing.access.*;
import com.picsauditing.actions.PicsApiSupport;

public class ActionClassToTestSecurityInterceptor extends PicsApiSupport {
    // must have api, permissions don't matter one way or the other
    @ApiRequired
    public String executeApiRequired() {
        return JSON;
    }

    // must have the required permission, api doesn't matter one way or the other
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeRequiredPermission() {
        return SUCCESS;
    }

    // always allowed, doesn't matter if have any perms, api, or even logged in
    @Anonymous
    public String executeAnonymous() {
        return SUCCESS;
    }

    // must have api AND must have the required permission
    @ApiRequired
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeApiRequiredAndRequiredPermission() {
        return SUCCESS;
    }

    // must have api OR must have the required permission
    @ApiAllowed
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeApiAllowedOrRequiredPermission() {
        return SUCCESS;
    }

    // must have api since there's no other access policy to "or" with
    @ApiAllowed
    public String executeApiAllowed() {
        return SUCCESS;
    }

    // must have api, though this is redundant, there's no real reason to disallow it
    @ApiRequired
    @ApiAllowed
    public String executeApiOnlyAndApiAllowed() {
        return SUCCESS;
    }

    // conflicting access policy, will result in exception
    @Anonymous
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeAnonymousAndRequiredPermission() {
        return SUCCESS;
    }

    // conflicting access policy will result in exception
    @Anonymous
    @ApiRequired
    public String executeAnonymousAndApiRequired() {
        return JSON;
    }

    // a not-very-logical but really not harmful policy declaration - same as Anonymous
    @Anonymous
    @ApiAllowed
    public String executeAnonymousAndApiAllowed() {
        return JSON;
    }

}