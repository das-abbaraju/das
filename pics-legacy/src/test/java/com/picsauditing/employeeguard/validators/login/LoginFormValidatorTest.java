package com.picsauditing.employeeguard.validators.login;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.forms.LoginForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.model.i18n.KeyValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

public class LoginFormValidatorTest {

	private LoginFormValidator loginFormValidator;

	@Mock
	private HttpServletRequest request;

	@Mock
	private ThreadLocal<ActionContext> threadLocalActionContext;

	@Mock
	private ActionContext actionContext;

	@Mock
	private ActionInvocation actionInvocation;

	@Mock
	private ActionSupport actionSupport;

	@Mock
	private ValueStack valueStack;

	private static final String DUMMY_RESOURCE_BUNDLE_STRING = "DUMMY RESOURCE BUNDLE STRING";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		loginFormValidator = new LoginFormValidator();

		initResourceBundleMocking();

	}

	private void initResourceBundleMocking() {
		Whitebox.setInternalState(ActionContext.class, threadLocalActionContext);
		when(threadLocalActionContext.get()).thenReturn(actionContext);
		when(actionContext.getActionInvocation()).thenReturn(actionInvocation);
		when(actionInvocation.getAction()).thenReturn(actionSupport);
		when(actionContext.getValueStack()).thenReturn(valueStack);
		when(actionSupport.getText(any(String.class), any(String.class), anyList(), any(ValueStack.class))).thenReturn("DUMMY RESOURCE BUNDLE STRING");
	}

	@After
	public void tearDown() {
		Whitebox.setInternalState(ActionContext.class, "actionContext", new ThreadLocal<ActionContext>());
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
