package com.picsauditing.validator;

import com.picsauditing.jpa.entities.Country;

public class TaxIdValidatorFactory {


    public TaxIdValidator buildTaxIdValidator(Country country) {
        TaxIdValidator appropriateTaxIdValidator = null;
        if (country == null) {
            return null;
        }

        if (country.isBrazil()) {
            appropriateTaxIdValidator = new CnpjTaxIdValidator();
        } else if (country.isEuropeanUnion() && !country.isUK()) {
            appropriateTaxIdValidator = new VATValidator();
        }
        return appropriateTaxIdValidator;
    }
}
