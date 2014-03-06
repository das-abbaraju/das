package com.picsauditing.struts.validator.constraints;

import com.picsauditing.authentication.dao.AppUserDAO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserNotTakenConstraintValidator implements ConstraintValidator<UniqueUserName, String> {

    @Autowired
    private AppUserDAO dao;

    @Override
    public void initialize(UniqueUserName notTaken) {  }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return dao.duplicateUsername(s, 0);
    }
}
