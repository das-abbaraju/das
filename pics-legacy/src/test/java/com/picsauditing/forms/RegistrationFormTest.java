package com.picsauditing.forms;

import com.picsauditing.struts.controller.forms.RegistrationForm;
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
    public void cityIsBlank() {
        RegistrationForm rForm = new RegistrationForm();
        rForm.setCity("");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "city");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void cityTooLong() {
        RegistrationForm rForm = new RegistrationForm();
        rForm.setCity("aslkjflskjflksjlfkjslkjflasjflkasjlfkjaslkjflskjdflaksjdlfkjasldjflak");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "city");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void citySpecialCharacter() {
        RegistrationForm rForm = new RegistrationForm();
        rForm.setCity("!@#");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "city");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void firstName() {
        RegistrationForm rForm = new RegistrationForm();
        rForm.setFirstName("John Michael-Claude o'malley");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "firstName");

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void firstNameSpecialCharacter() {
        RegistrationForm rForm = new RegistrationForm();
        rForm.setFirstName("John!");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "firstName");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void firstNameOnlySpecialCharacter() {
        RegistrationForm rForm = new RegistrationForm();
        rForm.setFirstName("!&*%");
        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "firstName");

        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void emailIsNotNull() {
        RegistrationForm rForm = new RegistrationForm();
        //rForm.setEmail("blah");

        Set<ConstraintViolation<RegistrationForm>> constraintViolations = validator.validateProperty(rForm, "email");

        assertEquals(1, constraintViolations.size());
    }

    @After
    public void tearDown() throws Exception {

    }
}
