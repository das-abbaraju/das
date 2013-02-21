package com.picsauditing.interceptors;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.Api;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsApiSupport;

public class ActionClassToTestSecurityInterceptor extends PicsApiSupport {
    @Api
    public String executeApi() {
        return JSON;
    }

    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeRequiredPermission() {
        return SUCCESS;
    }

    @Anonymous
    public String executeAnonymous() {
        return SUCCESS;
    }

    @Api
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeApiOrRequiredPermission() {
        return SUCCESS;
    }

    @Anonymous
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeAnonymousAndRequiredPermission() {
        return SUCCESS;
    }

}