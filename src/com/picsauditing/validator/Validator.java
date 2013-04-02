package com.picsauditing.validator;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;

public interface Validator {

	void validate(ValueStack valueStack, ValidatorContext validatorContext);

}
