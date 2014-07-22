package com.picsauditing.struts.validator;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.struts.validator.constraints.CNPJValidationConstraint;
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

public class CNPJValidationConstraintTest {

    private CNPJValidationConstraint cnpjValidationConstraint;

    @Mock
    private RegistrationForm.CNPJPair cnpjPair;
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
        cnpjValidationConstraint = new CNPJValidationConstraint();
        MockitoAnnotations.initMocks(this);

        Whitebox.setInternalState(cnpjValidationConstraint, "dao", countryDAO);
        Whitebox.setInternalState(cnpjValidationConstraint, "validatorFactory", taxIdValidatorFactory);
    }

    @Test
    public void testIsValid_nullCountry() {
        when(cnpjPair.getCountry()).thenReturn(null);

        assertTrue(cnpjValidationConstraint.isValid(cnpjPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_noCNPJCode() {
        when(cnpjPair.getCountry()).thenReturn("");
        when(cnpjPair.getCnpjCode()).thenReturn("");

        assertTrue(cnpjValidationConstraint.isValid(cnpjPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_blankCountry() {
        when(cnpjPair.getCountry()).thenReturn("");
        when(cnpjPair.getCnpjCode()).thenReturn("1");

        assertFalse(cnpjValidationConstraint.isValid(cnpjPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_noCountryObject() {
        when(cnpjPair.getCountry()).thenReturn("CA");
        when(cnpjPair.getCnpjCode()).thenReturn("1");

        assertFalse(cnpjValidationConstraint.isValid(cnpjPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_noValidator() {
        when(cnpjPair.getCountry()).thenReturn("CA");
        when(cnpjPair.getCnpjCode()).thenReturn("1");
        when(countryDAO.findByISO("CA")).thenReturn(new Country("CA"));

        assertTrue(cnpjValidationConstraint.isValid(cnpjPair, constraintValidatorContext));
    }

    @Test
    public void testIsValid_Validated() {
        when(cnpjPair.getCountry()).thenReturn("CA");
        when(cnpjPair.getCnpjCode()).thenReturn("1");
        Country country = new Country("CA");
        when(countryDAO.findByISO("CA")).thenReturn(country);
        when(taxIdValidatorFactory.buildTaxIdValidator(country)).thenReturn(taxIdValidator);

        assertFalse(cnpjValidationConstraint.isValid(cnpjPair, constraintValidatorContext));
    }
}