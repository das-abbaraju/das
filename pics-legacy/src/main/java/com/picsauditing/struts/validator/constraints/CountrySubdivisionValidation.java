package com.picsauditing.struts.validator.constraints;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.util.DataScrubber;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountrySubdivisionValidation implements ConstraintValidator<ValidateSubdivision, RegistrationForm.CountrySubdivisionPair> {

    public static final String UK_POST_CODE_REGEX = "(GIR 0AA|STHL 1ZZ|ASCN 1ZZ|AI-2640|TDCU 1ZZ|BBND 1ZZ|BIQQ 1ZZ|FIQQ 1ZZ|GX11 1AA|PCRN 1ZZ|SIQQ 1ZZ|TKCA 1ZZ)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})";
    public static final String SPECIAL_CHAR_REGEX = "(?s).*[;<>`\"].*";

    @Autowired
    private CountryDAO dao;

    @Override
    public void initialize(ValidateSubdivision constraintAnnotation) {  }

    @Override
    public boolean isValid(RegistrationForm.CountrySubdivisionPair value, ConstraintValidatorContext context) {

        if (value == null || Strings.isEmpty(value.getCountry())) return false;

        final Country country = dao.findbyISO(value.getCountry());
        if (country == null) return false;

        final String subdivisionISO = value.getSubdivision();
        final String zipCode = value.getZip();

        if (country.isHasCountrySubdivisions() && ! isAppropriateSubdivision(country, subdivisionISO)) return false;

        if (requiresZipCode(country) && ! validZipCode(country, zipCode)) return false;

        return true;
    }

    private boolean validZipCode(Country country, String zipCode) {
        if (Strings.isEmpty(zipCode)) return false;

        if (country.isUK()) {
            zipCode = DataScrubber.cleanUKPostcode(zipCode).toUpperCase();
            return zipCode.matches(UK_POST_CODE_REGEX) && ! zipCode.matches(SPECIAL_CHAR_REGEX);
        } else {
            return ! zipCode.matches(SPECIAL_CHAR_REGEX);
        }

    }

    // FIXME: This should probably be moved to the country object logic.
    private boolean requiresZipCode(Country country) {
        return ! country.isUAE();
    }

    // FIXME: This too.
    private boolean isAppropriateSubdivision(Country c, String subdivision) {
        if (Strings.isEmpty(subdivision)) return false;

        for (CountrySubdivision s : c.getCountrySubdivisions()) {
            if (s.getIsoCode().equals(subdivision)) return true;
        }
        return false;
    }

}
