package com.picsauditing.validator;

import com.picsauditing.jpa.entities.Country;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CnpjTaxIdValidatorTest {
    private CnpjTaxIdValidator cnpjTaxIdValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        cnpjTaxIdValidator = new CnpjTaxIdValidator();
    }

    @Test
    public void testValidated_Correct() throws Exception {
        String correctCnpj = "18.791.925/0001-63";
        cnpjTaxIdValidator.validated(new Country(Country.BRAZIL_ISO_CODE), correctCnpj);
    }

    @Test
    public void testValidated_Correct_WithTrailingSpaces() throws Exception {
        String correctCnpj = "18.791.925/0001-63    ";
        cnpjTaxIdValidator.validated(new Country(Country.BRAZIL_ISO_CODE), correctCnpj);
    }

    @Test
    public void testValidated_Incorrect() throws Exception {
        String incorrectCnpj = "18.791.92/0001-63";

        thrown.expect(ValidationException.class);

        thrown.expectMessage("Invalid CNPJ: " + incorrectCnpj);

        cnpjTaxIdValidator.validated(new Country(Country.BRAZIL_ISO_CODE), incorrectCnpj);
    }
}
