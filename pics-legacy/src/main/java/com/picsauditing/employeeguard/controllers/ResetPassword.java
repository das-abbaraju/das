package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.PermissionBuilder;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.model.LoginContext;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.service.authentication.AuthenticationService;
import com.picsauditing.web.SessionInfoProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static com.picsauditing.employeeguard.util.EmployeeGUARDUrlUtils.EMPLOYEE_SUMMARY;

public class ResetPassword extends PicsRestActionSupport {
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PermissionBuilder permissionBuilder;

    private String username;
    private String password;

    @Anonymous
    public String index() throws Exception {
        return SUCCESS;
    }

    @Anonymous
    public String resetPassword() throws Exception {
        AppUser appUser = appUserService.findByUsername(username);
        if (appUser != null) {
            appUserService.encodeAndSavePassword(appUser, password);
            LoginContext loginContext = authenticationService.doPreLoginVerificationEG(username, password);
            return doLoginEG(loginContext);
        } else {
            return ERROR;
        }
    }

    private String doLoginEG(LoginContext loginContext) throws IOException {
        doSetCookie(loginContext.getCookie(), 10);
        permissions = permissionBuilder.employeeUserLogin(loginContext.getAppUser(), loginContext.getProfile());
        SessionInfoProviderFactory.getSessionInfoProvider()
                .putInSession(Permissions.SESSION_PERMISSIONS_COOKIE_KEY, permissions);

        return setUrlForRedirect(EMPLOYEE_SUMMARY);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
