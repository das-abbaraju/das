package com.picsauditing.employeeguard.validators.project;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.ResourceBundleMocking;
import com.picsauditing.employeeguard.daos.DuplicateEntityChecker;
import com.picsauditing.employeeguard.forms.operator.ProjectForm;
import org.junit.After;
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
	private ValueStack valueStack;

	private ResourceBundleMocking resourceBundleMocking;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		projectFormValidator = new ProjectFormValidator();

		super.setupMocks();

		Whitebox.setInternalState(projectFormValidator, "duplicateEntityChecker", duplicateEntityChecker);

		when(request.getMethod()).thenReturn("POST");

		resourceBundleMocking = new ResourceBundleMocking();
		resourceBundleMocking.setUp();
	}

	@After
	public void tearDown() {
		resourceBundleMocking.tearDown();
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
