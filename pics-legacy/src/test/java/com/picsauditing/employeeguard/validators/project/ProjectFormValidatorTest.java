package com.picsauditing.employeeguard.validators.project;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.daos.DuplicateEntityChecker;
import com.picsauditing.employeeguard.forms.operator.ProjectForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ProjectFormValidatorTest extends PicsActionTest {
	private ProjectFormValidator projectFormValidator;

	@Mock
	private DuplicateEntityChecker duplicateEntityChecker;

	@Mock
	private ValidatorContext validatorContext;

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

	private static final String DUMMY_RESOURCE_BUNDLE_STRING= "DUMMY RESOURCE BUNDLE STRING";

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectFormValidator = new ProjectFormValidator();

		super.setupMocks();

		Whitebox.setInternalState(projectFormValidator, "duplicateEntityChecker", duplicateEntityChecker);

		when(request.getMethod()).thenReturn("POST");

		initResourceBundleMocking();

	}

	private void initResourceBundleMocking(){
		Whitebox.setInternalState(ActionContext.class, threadLocalActionContext);
		when(threadLocalActionContext.get()).thenReturn(actionContext);
		when(actionContext.getActionInvocation()).thenReturn(actionInvocation);
		when(actionInvocation.getAction()).thenReturn(actionSupport);
		when(actionContext.getValueStack()).thenReturn(valueStack);
		when(actionSupport.getText(any(String.class), any(String.class), anyList(),any(ValueStack.class))).thenReturn("DUMMY RESOURCE BUNDLE STRING");

	}

	@Test
	public void testPerformValidation_Name_Missing() throws Exception {
		when(valueStack.findValue(ProjectFormValidator.PROJECT_FORM, ProjectForm.class)).thenReturn(new ProjectForm());

		projectFormValidator.validate(valueStack, validatorContext);

		verify(validatorContext).addFieldError(eq(ProjectFormValidator.PROJECT_FORM + ".name"), anyString());
	}

	@Test
	public void testPerformValidation_Name_Empty() throws Exception {
		ProjectForm projectForm = new ProjectForm();
		projectForm.setName("");

		when(valueStack.findValue(ProjectFormValidator.PROJECT_FORM, ProjectForm.class)).thenReturn(projectForm);

		projectFormValidator.validate(valueStack, validatorContext);

		verify(validatorContext).addFieldError(eq(ProjectFormValidator.PROJECT_FORM + ".name"), anyString());
	}

	@Test
	public void testPerformValidation_Name_Valid() throws Exception {
		ProjectForm projectForm = new ProjectForm();
		projectForm.setName("Valid");

		when(valueStack.findValue(ProjectFormValidator.PROJECT_FORM, ProjectForm.class)).thenReturn(projectForm);

		projectFormValidator.validate(valueStack, validatorContext);

		verify(validatorContext, never()).addFieldError(eq(ProjectFormValidator.PROJECT_FORM + ".name"), anyString());
	}
}
