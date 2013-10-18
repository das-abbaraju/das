package com.picsauditing.employeeguard.validators.factory.struts;

import com.opensymphony.xwork2.validator.ValidatorContext;

public class ValidatorContextFactory {

    public static ValidatorContext getValidatorContext() {
        return new ValidatorContextForTest();
    }
}
