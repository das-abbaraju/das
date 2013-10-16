package com.picsauditing.employeeguard.validators;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidatorContext;

public interface ValidationStrategy {
	void validate(ValueStack valueStack, ValidatorContext validatorContext);
}
