package com.picsauditing.interceptors;

import com.picsauditing.access.*;
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
    public String executeApiAndRequiredPermission() {
        return SUCCESS;
    }

    @ApiOrRequiredPermission(value = OpPerms.EditNotes)
    public String executeApiOrRequiredPermission() {
        return SUCCESS;
    }

    @Api
    @ApiOrRequiredPermission(value = OpPerms.EditNotes)
    public String executeApiAndApiOrRequiredPermission() {
        return SUCCESS;
    }

    @Anonymous
    @RequiredPermission(value = OpPerms.EditNotes)
    public String executeAnonymousAndRequiredPermission() {
        return SUCCESS;
    }
    @Anonymous
    @Api
    public String executeAnonymousAndApi() {
        return JSON;
    }

}