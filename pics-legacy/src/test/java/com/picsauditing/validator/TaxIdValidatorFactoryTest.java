package com.picsauditing.validator;

import com.picsauditing.jpa.entities.Country;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

public class TaxIdValidatorFactoryTest {

    private  TaxIdValidatorFactory taxIdValidatorFactory;

    @Before
    public void setup() {
        taxIdValidatorFactory = new TaxIdValidatorFactory();
    }

    @Test
    public void test_buildTaxIdValidator_Vat() {
        Country country = new Country(Country.GERMANY_ISO_CODE);

        TaxIdValidator result = taxIdValidatorFactory.buildTaxIdValidator(country);

        assertTrue(result instanceof VATValidator);
    }

    @Test
    public void test_buildTaxIdValidator_Cnpj() {
        Country country = new Country(Country.BRAZIL_ISO_CODE);

        TaxIdValidator result = taxIdValidatorFactory.buildTaxIdValidator(country);

        assertTrue(result instanceof CnpjTaxIdValidator);
    }

    @Test
    public void test_buildTaxIdValidator_Uk() {
        Country country = new Country(Country.UK_ISO_CODE);

        TaxIdValidator result = taxIdValidatorFactory.buildTaxIdValidator(country);

        assertNull(result);
    }

    @Test
    public void test_buildTaxIdValidator_Null() {
        Country country = new Country(Country.UK_ISO_CODE);

        TaxIdValidator result = taxIdValidatorFactory.buildTaxIdValidator(country);

        assertNull(result);
    }
}
