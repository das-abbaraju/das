package com.picsauditing.forms;

import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.togglz.junit.TogglzRule;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Locale;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

public class RegistrationFormTest {
    private static Validator validator;

    @Mock private ContractorAccount input;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

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

    @Test
    public void testFromContractor() {
        ContractorAccount registrationRequest = buildRegistrationAccount();

        RegistrationForm outputForm = RegistrationForm.fromContractor(registrationRequest);

        assertEquals(AccountStatus.Requested, outputForm.getStatus());
        assertEquals("17701 Cowan", outputForm.getAddress());
        assertEquals("Suite 140", outputForm.getAddress2());
        assertEquals("Irvine", outputForm.getCity());
        assertEquals("92612", outputForm.getZip());
        assertEquals("US-CA", outputForm.getCountrySubdivision().toString());
        assertEquals("PICS Auditing, LLC.", outputForm.getLegalName());
        assertEquals("US", outputForm.getCountryISOCode().toString());
        assertEquals("en-us", outputForm.getLocale().toString());
        assertEquals("pics@example.com", outputForm.getEmail());
        assertEquals("Bob", outputForm.getFirstName());
        assertEquals("Bobberson", outputForm.getLastName());
        assertEquals("714-555-1234", outputForm.getPhone());
        assertNull(outputForm.getAddressBlob());
    }

    @Test
    public void testFromContractor_AddressVerificationServiceEnabled() {
        togglzRule.enable(Features.USE_STRIKEIRON_ADDRESS_VERIFICATION_SERVICE);
        ContractorAccount registrationRequest = buildRegistrationAccount();

        RegistrationForm outputForm = RegistrationForm.fromContractor(registrationRequest);

        assertNull(outputForm.getAddress());
        assertNull(outputForm.getAddress2());
        assertNull(outputForm.getCity());
        assertEquals("", outputForm.getCountrySubdivision());
        assertEquals("US", outputForm.getCountryISOCode().toString());
        assertEquals("92612", outputForm.getZip());
        assertEquals("17701 Cowan\nSuite 140\nIrvine CA", outputForm.getAddressBlob());
    }

    private ContractorAccount buildRegistrationAccount() {
        return ContractorAccount.builder()
                    .status(AccountStatus.Requested)
                    .address("17701 Cowan")
                    .address2("Suite 140")
                    .city("Irvine")
                    .zip("92612")
                    .countrySubdivision(new CountrySubdivision("US-CA"))
                    .legalName("PICS Auditing, LLC.")
                    .country(new Country("US"))
                    .locale(new Locale("en-ca"))
                    .primaryContact(User.builder()
                            .email("pics@example.com")
                            .locale(new Locale("en-us"))
                            .firstName("Bob")
                            .lastName("Bobberson")
                            .phone("714-555-1234")
                            .build())
                    .build();
    }

    @After
    public void tearDown() throws Exception {

    }
}
