package com.picsauditing.employeeguard.validators.group;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.ResourceBundleMocking;
import com.picsauditing.employeeguard.daos.DuplicateEntityChecker;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
import com.picsauditing.employeeguard.validators.factory.struts.ValidatorContextFactory;
import com.picsauditing.employeeguard.validators.factory.struts.ValueStackFactory;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.strutsutil.HttpUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.when;

public class GroupFormValidatorTest {

	private GroupFormValidator groupFormValidator;

	@Mock
	HttpServletRequest request;
	@Mock
	private DuplicateEntityChecker duplicateEntityChecker;

	private ResourceBundleMocking resourceBundleMocking;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		groupFormValidator = new GroupFormValidator();

		Whitebox.setInternalState(groupFormValidator, "duplicateEntityChecker", duplicateEntityChecker);

		resourceBundleMocking = new ResourceBundleMocking();
		resourceBundleMocking.setUp();
	}

	@After
	public void tearDown() {
		resourceBundleMocking.tearDown();
	}

	@Test
	public void testValidationSuccess() {
		when(request.getMethod()).thenReturn(HttpUtil.HTTP_POST_METHOD);
		ValueStack valueStack = ValueStackFactory.getValueStack(request,
				new KeyValue<String, Object>(GroupFormValidator.GROUP_NAME_SKILLS_FORM, getGroupSkillsFormPassValidation()));
		ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

		groupFormValidator.validate(valueStack, validatorContext);

		assertFalse(validatorContext.hasErrors());
	}

	public GroupNameSkillsForm getGroupSkillsFormPassValidation() {
		GroupNameSkillsForm groupNameSkillsForm = new GroupNameSkillsForm();
		groupNameSkillsForm.setName("Bob Group");
		return groupNameSkillsForm;
	}

	@Test
	public void testValidationFailure() {
		when(request.getMethod()).thenReturn(HttpUtil.HTTP_POST_METHOD);
		ValueStack valueStack = ValueStackFactory.getValueStack(request,
				new KeyValue<String, Object>(GroupFormValidator.GROUP_NAME_SKILLS_FORM, getGroupSkillsFormFailValidation()));
		ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

		groupFormValidator.validate(valueStack, validatorContext);

		assertEquals(ResourceBundleMocking.DEFAULT_RESOURCE_BUNDLE_STRING,
				validatorContext.getFieldErrors().get(GroupFormValidator.GROUP_NAME_SKILLS_FORM + ".name").get(0));
	}

	public GroupNameSkillsForm getGroupSkillsFormFailValidation() {
		return new GroupNameSkillsForm();
	}
}
