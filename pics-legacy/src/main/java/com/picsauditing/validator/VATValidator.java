package com.picsauditing.validator;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.Country;

public class VATValidator {

    private final static String GREECE = "GR";
    // for test injection
    private VATWebValidator webValidator;

    public String validatedVATfromAudit(AuditData data) throws ValidationException {
        Country contractorCountry = data.getAudit().getContractorAccount().getCountry();
        String potentialVATcode = data.getAnswer().trim().toUpperCase();

        return validated(contractorCountry, potentialVATcode);
    }

    public String validated(Country country, String vatCode) throws ValidationException {
        String prefix = vatPrefixFor(country);
        String finalCode = (vatCode.startsWith(prefix)) ? vatCode : prefix + vatCode;

        if (!demoVatNumber(vatCode)) {
            VATWebValidator webValidator = webValidator(finalCode);
            if (!webValidator.execute()) {
                throw new ValidationException("The VAT code is not valid");
            };
        }

        return finalCode;
    }

    private String vatPrefixFor(Country country) {
        if (country.getIsoCode().equalsIgnoreCase(GREECE)) {
			return "EL";
		} else if (country.isEuropeanUnion()) {
			return country.getIsoCode().toUpperCase();
		} else {
			return "EU";
		}
    }

    public boolean shouldValidate(Country registrationCountry) {
        if (registrationCountry != null && registrationCountry.isEuropeanUnion() && !registrationCountry.isUK()) {
            return true;
        }

        return false;
    }

    // PICS-11482
    private boolean demoVatNumber(String vatCode) {
        return vatCode.equalsIgnoreCase("999999999") ||
                vatCode.equalsIgnoreCase("4111111111111111");
    }

    private VATWebValidator webValidator(String finalCode) {
        if (webValidator != null) {
            return webValidator;
        } else {
            return new VATWebValidator(finalCode);
        }
    }
}
