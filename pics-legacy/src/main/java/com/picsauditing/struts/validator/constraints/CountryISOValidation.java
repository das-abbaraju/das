package com.picsauditing.struts.validator.constraints;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CountryISOValidation implements ConstraintValidator<CountryExists, String> {
    @Autowired
    private CountryDAO dao;

    @Override
    public void initialize(CountryExists constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Strings.isEmpty(value) || dao.findbyISO(value) != null;
    }

}
