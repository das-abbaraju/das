package com.picsauditing.struts.validator;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.struts.validator.constraints.VATValidationConstraint;
import com.picsauditing.validator.TaxIdValidator;
import com.picsauditing.validator.TaxIdValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import javax.validation.ConstraintValidatorContext;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class VATValidationConstraintTest {

    private VATValidationConstraint vatValidationConstraint;

    @Mock
    private RegistrationForm.VATPair vatPair;
    @Mock
    private ConstraintValidatorContext constraintValidatorContext;
    @Mock
    private CountryDAO countryDAO;
    @Mock
    private TaxIdValidatorFactory taxIdValidatorFactory;
    @Mock
    private TaxIdValidator taxIdValidator;

    @Before
    public void setup() throws Exception {
        vatValidationConstraint = new VATValidationConstraint();
        MockitoAnnotations.initMocks(this);

        Whitebox.setInternalState(vatValidationConstraint, "dao", countryDAO);
        Whitebox.setInternalState(vatValidationConstraint, "validatorFactory", taxIdValidatorFactory);
    }

    @Test
    public void testIsValid_nullCountry() {
        when(vatPair.getCountry()).thenReturn(null);

        assertTrue(vatValidationConstraint.isValid(vatPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_noCNPJCode() {
        when(vatPair.getCountry()).thenReturn("");
        when(vatPair.getVatCode()).thenReturn("");

        assertTrue(vatValidationConstraint.isValid(vatPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_blankCountry() {
        when(vatPair.getCountry()).thenReturn("");
        when(vatPair.getVatCode()).thenReturn("1");

        assertFalse(vatValidationConstraint.isValid(vatPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_noCountryObject() {
        when(vatPair.getCountry()).thenReturn("CA");
        when(vatPair.getVatCode()).thenReturn("1");

        assertFalse(vatValidationConstraint.isValid(vatPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_noValidator() {
        when(vatPair.getCountry()).thenReturn("CA");
        when(vatPair.getVatCode()).thenReturn("1");
        when(countryDAO.findByISO("CA")).thenReturn(new Country("CA"));

        assertTrue(vatValidationConstraint.isValid(vatPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_Validated() {
        when(vatPair.getCountry()).thenReturn("CA");
        when(vatPair.getVatCode()).thenReturn("1");
        Country country = new Country("CA");
        when(countryDAO.findByISO("CA")).thenReturn(country);
        when(taxIdValidatorFactory.buildTaxIdValidator(country)).thenReturn(taxIdValidator);

        assertFalse(vatValidationConstraint.isValid(vatPair, constraintValidatorContext));
    }
}