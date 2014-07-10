package com.picsauditing.employeeguard.validators.skill;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.daos.DuplicateEntityChecker;
import com.picsauditing.employeeguard.entities.SkillType;
import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.model.i18n.KeyValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

public class SkillFormValidatorTest {

	private SkillFormValidator skillFormValidator;

	@Mock
	private DuplicateEntityChecker duplicateEntityChecker;
	@Mock
	HttpServletRequest request;


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
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		skillFormValidator = new SkillFormValidator();

		Whitebox.setInternalState(skillFormValidator, "duplicateEntityChecker", duplicateEntityChecker);

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
	public void testNoValidationFailure() {
		when(request.getMethod()).thenReturn("POST");
		ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(SkillFormValidator.SKILL_FORM, buildSkillFormNoValidationFailure()));
		ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

		skillFormValidator.validate(valueStack, validatorContext);

		assertFalse(validatorContext.hasErrors());
	}

	private SkillForm buildSkillFormNoValidationFailure() {
		SkillForm skillForm = new SkillForm();
		skillForm.setName("Bob");
		skillForm.setSkillType(SkillType.Certification);
		return skillForm;
	}

	@Test
	public void testNameAndExpirationValidationFailures() {
		when(request.getMethod()).thenReturn("POST");
		ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(SkillFormValidator.SKILL_FORM,
						buildSkillFormNameAndExpirationValidationFailure()));
		ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

		skillFormValidator.validate(valueStack, validatorContext);

		assertEquals(DUMMY_RESOURCE_BUNDLE_STRING, validatorContext.getFieldErrors().get(SkillFormValidator.SKILL_FORM
						+ ".name").get(0));
	}

	private SkillForm buildSkillFormNameAndExpirationValidationFailure() {
		SkillForm skillForm = new SkillForm();
		skillForm.setSkillType(SkillType.Training);
		return skillForm;
	}

	@Test
	public void testMissingSkillTypeValidationFailure() {
		when(request.getMethod()).thenReturn("POST");
		ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(SkillFormValidator.SKILL_FORM,
						buildSkillFormSkillTypeValidationFailure()));
		ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

		skillFormValidator.validate(valueStack, validatorContext);

		assertEquals(DUMMY_RESOURCE_BUNDLE_STRING, validatorContext.getFieldErrors().get(SkillFormValidator.SKILL_FORM
						+ ".skillType").get(0));
	}

	private SkillForm buildSkillFormSkillTypeValidationFailure() {
		SkillForm skillForm = new SkillForm();
		skillForm.setName("Bob");
		return skillForm;
	}
}
