package com.picsauditing.validator;

import com.picsauditing.jpa.entities.Country;

/**
 * CNPJ is a tax ID used in Brazil
 * */
public class CnpjTaxIdValidator extends TaxIdValidator {
    private static final String LABEL = "CNPJ";
    public static final String INVALID_CNPJ_ERROR_MESSAGE = "Invalid CNPJ: ";
    private String REGEX_CNPJ_TAX_id = "\\d{2}.?\\d{3}.?\\d{3}/?\\d{4}-?\\d{2}";

    @Override
    public String validated(Country country, String taxId) throws ValidationException {
        if (taxId.matches(REGEX_CNPJ_TAX_id)) {
            return taxId;
        }
        throw new ValidationException(INVALID_CNPJ_ERROR_MESSAGE + taxId);
    }

    @Override
    public String getLabel() {
        return LABEL;
    }
}
