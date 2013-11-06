package com.picsauditing.employeeguard.validators.login;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.model.i18n.KeyValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class LoginFormValidatorTest {

    private LoginFormValidator loginFormValidator;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        loginFormValidator = new LoginFormValidator();
    }

    @Test
    public void testValidation_Success() {
        when(request.getMethod()).thenReturn("POST");
        ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(LoginFormValidator.LOGIN_FORM, getLoginFormSuccessfulValidation()));
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

        loginFormValidator.validate(valueStack, validatorContext);

        assertFalse(validatorContext.hasErrors());
    }

    private LoginForm getLoginFormSuccessfulValidation() {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("testuser");
        loginForm.setPassword("Abc123");
        return loginForm;
    }

    @Test
    public void testValidation_Failure() {
        when(request.getMethod()).thenReturn("POST");
        ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(LoginFormValidator.LOGIN_FORM, getLoginFormFailsValidation()));
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

        loginFormValidator.validate(valueStack, validatorContext);

        assertTrue(validatorContext.hasErrors());
        assertEquals(2, validatorContext.getFieldErrors().size());
    }

    private LoginForm getLoginFormFailsValidation() {
        return new LoginForm();
    }
}
