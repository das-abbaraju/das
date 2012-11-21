package com.picsauditing.PICS;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.Country;

import java.util.regex.Pattern;

public class VATValidator {

    private final static String GREECE = "GR";

    public String validatedVATfromAudit(AuditData data) throws ValidationException {
        Country contractorCountry = data.getAudit().getContractorAccount().getCountry();
        String potentialVATcode = data.getAnswer().trim().toUpperCase();

        return validated(contractorCountry, potentialVATcode);
    }

    public String validated(Country country, String vatCode) throws ValidationException {
        String prefix = vatPrefixFor(country);
        String finalCode = (vatCode.startsWith(prefix)) ? vatCode : prefix + vatCode;
        return validated(finalCode);
    }

    public String validated(String vatCode) throws ValidationException {
        locallyValidate(vatCode);
        webValidate(vatCode);
        return vatCode;
    }

    private void locallyValidate(String vatCode) throws ValidationException {
        getValidator(vatCode).validate();
    }

    private Validator getValidator(String vatCode) {
        return new Validator(vatCode);
    }

    private void webValidate(String vatCode) throws ValidationException {
        //TODO: A potential API for implementing this can be found at: http://isvat.appspot.com/
    }

    private String vatPrefixFor(Country country) {
        if (country.getIsoCode().equalsIgnoreCase(GREECE))
            return "EL";
        else if (country.isEuropeanUnion())
            return country.getIsoCode().toUpperCase();
        else
            return "EU";
    }

    public boolean shouldValidate(Country registrationCountry) {
        if (registrationCountry.isEuropeanUnion() && !registrationCountry.isUK()) {
            return true;
        }

        return false;
    }

    public class ValidationException extends Exception {

    }

    class Validator {
        private final Pattern GENERIC_VAT_REGEX = Pattern.compile("[A-Z]{2}[- ]?[\\d\\w -]{2,15}\\z", Pattern.CASE_INSENSITIVE);
        private final Pattern CHECK_FOR_DUPLICATES = Pattern.compile("[A-Z]{2}(\\d)\\1+\\z");
        private final Pattern CHECK_FOR_OBFUSCATION = Pattern.compile("[A-Z]{2}(\\w)\\1{3,}\\w+\\z");
        private final Pattern CHECK_FOR_YESNO = Pattern.compile("[A-Z]{2}((yes)|(no)|(n/?a))\\z", Pattern.CASE_INSENSITIVE);

        String vatNumber;

        Validator(String vatNumber) {
            this.vatNumber = vatNumber;
        }

        void validate() throws ValidationException {
            if (!GENERIC_VAT_REGEX.matcher(vatNumber).matches())
                throw new ValidationException();
            if (CHECK_FOR_DUPLICATES.matcher(vatNumber).matches())
                throw new ValidationException();
            if (CHECK_FOR_OBFUSCATION.matcher(vatNumber).matches())
                throw new ValidationException();
            if (CHECK_FOR_YESNO.matcher(vatNumber).matches())
                throw new ValidationException();
        }
    }

}
