package com.picsauditing.employeeguard.controllers;

import com.picsauditing.access.Anonymous;
import com.picsauditing.controller.PicsRestActionSupport;

public class ResetPassword extends PicsRestActionSupport {
    private String password;

    @Anonymous
    public String index() throws Exception {
        return SUCCESS;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
