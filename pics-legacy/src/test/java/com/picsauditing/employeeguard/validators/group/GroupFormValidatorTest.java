package com.picsauditing.employeeguard.validators.group;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.picsauditing.employeeguard.daos.DuplicateEntityChecker;
import com.picsauditing.employeeguard.forms.contractor.GroupNameSkillsForm;
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
import static org.mockito.Mockito.when;

public class GroupFormValidatorTest {

    private GroupFormValidator groupFormValidator;

    @Mock
    HttpServletRequest request;
    @Mock
    private DuplicateEntityChecker duplicateEntityChecker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        groupFormValidator = new GroupFormValidator();

        Whitebox.setInternalState(groupFormValidator, "duplicateEntityChecker", duplicateEntityChecker);
    }

    @Test
    public void testValidationSuccess() {
        when(request.getMethod()).thenReturn("POST");
        ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(GroupFormValidator.GROUP_NAME_SKILLS_FORM, getGroupSkillsFormPassValidation()));
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
        when(request.getMethod()).thenReturn("POST");
        ValueStack valueStack = ValueStackFactory.getValueStack(request, new KeyValue<String, Object>(GroupFormValidator.GROUP_NAME_SKILLS_FORM, getGroupSkillsFormFailValidation()));
        ValidatorContext validatorContext = ValidatorContextFactory.getValidatorContext();

        groupFormValidator.validate(valueStack, validatorContext);

        assertEquals("Name is missing", validatorContext.getFieldErrors().get(GroupFormValidator.GROUP_NAME_SKILLS_FORM + ".name").get(0));
    }

    public GroupNameSkillsForm getGroupSkillsFormFailValidation() {
        return new GroupNameSkillsForm();
    }
}
