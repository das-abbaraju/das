package com.picsauditing.forms;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class RegistrationFormTest {
    private static Validator validator;

    @Before
    public void setUp() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void emailIsNotNull() {
        RegistrationForm rForm = new RegistrationForm();
        //rForm.setEmail("blah");

        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validate(rForm);

        assertEquals(1, constraintViolations.size());
    }

    @After
    public void tearDown() throws Exception {

    }
}
