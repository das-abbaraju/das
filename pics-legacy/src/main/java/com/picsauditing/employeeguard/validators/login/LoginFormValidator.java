package com.picsauditing.employeeguard.validators.login;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;
import com.picsauditing.util.Strings;

public class LoginFormValidator extends AbstractValidator<LoginForm> {

    public static final String LOGIN_FORM = "loginForm";

    protected LoginForm getFormFromValueStack(ValueStack valueStack) {
        return (LoginForm) valueStack.findValue(LOGIN_FORM, LoginForm.class);
    }

    @Override
    protected void performValidation(LoginForm loginForm) {
        if (Strings.isEmpty(loginForm.getUsername())) {
            addFieldErrorIfMessage(fieldKeyBuilder(LOGIN_FORM, "username"), "Username must be provided.");
        }

        if (Strings.isEmpty(loginForm.getPassword())) {
            addFieldErrorIfMessage(fieldKeyBuilder(LOGIN_FORM, "password"), "Password must be provided.");
        }
    }
}
