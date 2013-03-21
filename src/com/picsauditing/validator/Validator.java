package com.picsauditing.validator;

import com.opensymphony.xwork2.validator.ValidatorContext;

public interface Validator {

	void validate();

	void setValidatorContext(ValidatorContext validatorContext);

	void setAction(Object action);

}
