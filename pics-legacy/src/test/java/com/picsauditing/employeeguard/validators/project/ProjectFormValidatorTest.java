package com.picsauditing.employeeguard.validators.project;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.forms.operator.ProjectForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ProjectFormValidatorTest extends PicsActionTest {
	private ProjectFormValidator projectFormValidator;

	@Mock
	private ValueStack valueStack;
	@Mock
	private ValidatorContext validatorContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectFormValidator = new ProjectFormValidator();

		super.setupMocks();

		when(request.getMethod()).thenReturn("POST");
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
