package com.picsauditing.employeeguard.validators.login;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;
import com.picsauditing.util.Strings;

public class LoginFormValidator extends AbstractBasicValidator<LoginForm> {

    public static final String LOGIN_FORM = "loginForm";

    protected LoginForm getFormFromValueStack(ValueStack valueStack) {
        return (LoginForm) valueStack.findValue(LOGIN_FORM, LoginForm.class);
    }

    @Override
    protected void doFormValidation(LoginForm loginForm) {
        if (Strings.isEmpty(loginForm.getUsername())) {
            addFieldErrorIfMessage(fieldKeyBuilder(LOGIN_FORM, "username"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.LOGIN.USERNAME"));
        }

        if (Strings.isEmpty(loginForm.getPassword())) {
            addFieldErrorIfMessage(fieldKeyBuilder(LOGIN_FORM, "password"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.LOGIN.PASSWORD"));
        }
    }
}
