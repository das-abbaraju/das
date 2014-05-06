package com.picsauditing.struts.controller.eula;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.LoginService;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.persistence.model.EulaAgreement;
import com.picsauditing.eula.model.LoginEulaResponse;
import com.picsauditing.eula.service.EulaService;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.persistence.model.Eula;
import com.picsauditing.service.user.UserService;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EulaController extends PicsActionSupport implements ServletRequestAware {
    @Autowired
    private UserService userService;
    @Autowired
    private EulaService eulaService;

    private String userName;
    private Country country;
    private String eulaBody;
    private HttpServletRequest request;
    private String password;

    @Anonymous
    public String loginEulaUserInfo() {
        try {
            eulaService.doPreloginVerification(userName, password);
        } catch (LoginException e) {
            ServletActionContext.getResponse().setStatus(HttpStatus.SC_UNAUTHORIZED);
            return BLANK;
        }

        User user = userService.loadUserByUsername(userName);

        if (user == null) {
            jsonString = "{}";
            ServletActionContext.getResponse().setStatus(HttpStatus.SC_NOT_ACCEPTABLE);
        }
        else {
            String httpRequestMethod = request.getMethod();
            if (httpRequestMethod.equals("PUT")) {
                try {
                    LoginEulaResponse loginEulaRequest = getModelFromJsonRequest();
                    if (userAcceptsLoginEula(loginEulaRequest)) {
                        String country = eulaService.extractCountryIso(loginEulaRequest.getEulaUrl());
                        eulaService.acceptLoginEula(user, country);
                    }
                } catch (Exception e) {
                    ServletActionContext.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
                }
            } else {
                jsonString = buildLoginEulaResponse(user);
            }
        }

        return JSON_STRING;
    }

    private boolean userAcceptsLoginEula(LoginEulaResponse loginEulaRequest) {
        return loginEulaRequest.getStatus().equals(EulaAgreement.ACCEPTED());
    }

    private String buildLoginEulaResponse(User user) {
        LoginEulaResponse response = LoginEulaResponse.builder()
                .userName(userName)
                .eulaUrl("/eulas/login/" + user.getAccount().getCountry().getIsoCode() + ".action")
                .status(getLoginEulaAgreementStatus(user))
                .build();

        return new Gson().toJson(response);
    }

    @Anonymous
    public String loginEula() {
        Eula loginEula = eulaService.getLoginEula(country);
        eulaBody = loginEula.eulaBody();
        return SUCCESS;
    }

    private String getLoginEulaAgreementStatus(User user) {
        String eulaAgreementStatus;

        if (Features.USE_EULA.isActive()) {
            Eula loginEula = eulaService.getLoginEula(user.getAccount().getCountry());
            EulaAgreement eulaAgreement = eulaService.getLoginEulaAgreement(user, loginEula);
            eulaAgreementStatus = eulaAgreement == null ? EulaAgreement.NOT_ACCEPTED() : EulaAgreement.ACCEPTED();
        } else {
            eulaAgreementStatus = EulaAgreement.ACCEPTED();
        }

        return eulaAgreementStatus;
    }

    protected LoginEulaResponse getModelFromJsonRequest() throws Exception {
        String body = getBodyFromRequest();

        LoginEulaResponse loginEulaRequest;

        try {
            loginEulaRequest = new Gson().fromJson(body, LoginEulaResponse.class);
        } catch (JsonSyntaxException e) {
            throw new Exception(HttpStatus.getStatusText(HttpStatus.SC_BAD_REQUEST) + ": " + body, e);
        }

        return loginEulaRequest;
    }

    private String getBodyFromRequest() throws IOException {
        HttpServletRequest request = getRequest();
        return getBody(request);
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return stringBuilder.toString();
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getEulaBody() {
        return eulaBody;
    }

    public void setEulaBody(String eulaBody) {
        this.eulaBody = eulaBody;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}
