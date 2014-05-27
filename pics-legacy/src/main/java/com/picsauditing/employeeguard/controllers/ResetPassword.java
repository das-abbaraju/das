package com.picsauditing.employeeguard.controllers;

import com.picsauditing.controller.PicsRestActionSupport;

public class ResetPassword extends PicsRestActionSupport {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
