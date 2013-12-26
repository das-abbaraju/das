package com.picsauditing.validator;

import com.picsauditing.jpa.entities.Country;

public class MockTaxIdValidator extends TaxIdValidator {

    private boolean returnSuccess = true;

    public MockTaxIdValidator(boolean returnSuccess) {
        this.returnSuccess = returnSuccess;
    }
    @Override
    public String validated(Country country, String vatCode) throws ValidationException {
        if (!returnSuccess) {
            throw new ValidationException();
        }
        return vatCode;
    }

    @Override
    public String getLabel() {
        return "MOCK VALIDATOR";
    }
}
