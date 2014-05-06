package com.picsauditing.eula.model.builder;

import com.picsauditing.eula.model.LoginEulaResponse;

public class LoginEulaResponseBuilder {
    private LoginEulaResponse loginEulaResponse = new LoginEulaResponse();
    public LoginEulaResponseBuilder userName(String userName) {
        loginEulaResponse.setUserName(userName);
        return this;
    }

    public LoginEulaResponseBuilder eulaUrl(String eulaUrl) {
        loginEulaResponse.setEulaUrl(eulaUrl);
        return this;
    }

    public LoginEulaResponseBuilder status(String status) {
        loginEulaResponse.setStatus(status);
        return this;
    }

    public LoginEulaResponse build() {
        return loginEulaResponse;
    }
}
