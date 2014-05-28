package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Anonymous;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.controller.PicsRestActionSupport;
import org.springframework.beans.factory.annotation.Autowired;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY;

public class ResetPassword extends PicsRestActionSupport {
    @Autowired
    private AppUserService appUserService;
    private String password;

    @Anonymous
    public String index() throws Exception {
        return SUCCESS;
    }

    @Anonymous
    public String resetPassword() throws Exception {
        appUserService.encodeAndSavePassword(permissions.getAppUserID(), password);
        return setUrlForRedirect(EMPLOYEE_SUMMARY);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
