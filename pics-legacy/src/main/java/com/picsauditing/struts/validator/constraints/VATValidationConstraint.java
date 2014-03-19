package com.picsauditing.struts.validator.constraints;

import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.struts.controller.forms.RegistrationForm;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.TaxIdValidator;
import com.picsauditing.validator.TaxIdValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class VATValidationConstraint implements ConstraintValidator<VatValidation, RegistrationForm.VATPair>{

    @Autowired
    private CountryDAO dao;

    @Autowired
    TaxIdValidatorFactory validatorFactory;

    @Override
    public void initialize(VatValidation constraintAnnotation) { }

    @Override
    public boolean isValid(RegistrationForm.VATPair value, ConstraintValidatorContext context) {
        if (Strings.isEmpty(value.getCountry())) return false;

        try {
            final Country country = dao.findbyISO(value.getCountry());
            if (country == null) return false;
            final TaxIdValidator validator = validatorFactory.buildTaxIdValidator(country);
            return (validator == null ||  validator.validated(country, value.getVatCode()) != null);
        } catch (Exception e) {
            return false;
        }
    }
}
