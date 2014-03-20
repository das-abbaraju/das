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

    @Autowired
    private CountryDAO dao;

    @Override
    public void initialize(ValidateSubdivision constraintAnnotation) {  }

    @Override
    public boolean isValid(RegistrationForm.CountrySubdivisionPair value, ConstraintValidatorContext context) {

        if (value == null || Strings.isEmpty(value.getCountry())) return true;
        final Country country = dao.findbyISO(value.getCountry());
        if (country == null) return true;

        final String subdivisionISO = value.getSubdivision();
        final String zipCode = value.getZip();

        return !country.isHasCountrySubdivisions() || isAppropriateSubdivision(country, subdivisionISO);

    }

    // FIXME: This should probably be moved to the Country object logic.
    private boolean isAppropriateSubdivision(Country c, String subdivision) {
        if (Strings.isEmpty(subdivision)) return false;

        for (CountrySubdivision s : c.getCountrySubdivisions()) {
            if (s.getIsoCode().equals(subdivision)) return true;
        }
        return false;
    }

}
