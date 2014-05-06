package com.picsauditing.eula.model;

import com.picsauditing.eula.model.builder.LoginEulaResponseBuilder;

public class LoginEulaResponse {
    private String userName;
    private String eulaUrl;
    private String status;

    public static LoginEulaResponseBuilder builder() {
        return new LoginEulaResponseBuilder();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEulaUrl() {
        return eulaUrl;
    }

    public void setEulaUrl(String eulaUrl) {
        this.eulaUrl = eulaUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
