package com.picsauditing.forms;

import com.intuit.developer.Authenticate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RegistrationFormTest {
    private static Validator validator;

    @Mock private ContractorAccount input;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @Test
    public void fromContractor_TestUserPhonePopulation(){
        String phoneInUser="1234567891";
        String phoneInContractorAccount="8790475849";
        User user = new User();
        user.setPhone(phoneInUser);
        when(input.getPrimaryContact()).thenReturn(user);
        when(input.getPhone()).thenReturn(phoneInContractorAccount);
        RegistrationForm registrationForm = RegistrationForm.fromContractor(input);
        assertEquals("Phone needs to populate from user", user.getPhone(), registrationForm.getPhone());
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

        assertEquals(2, constraintViolations.size());
    }

    @After
    public void tearDown() throws Exception {

    }
}
