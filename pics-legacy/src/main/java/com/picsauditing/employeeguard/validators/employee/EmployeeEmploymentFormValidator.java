package com.picsauditing.employeeguard.validators.employee;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;

public class EmployeeEmploymentFormValidator extends AbstractValidator<EmployeeEmploymentForm> {
	public static final String EMPLOYEE_EMPLOYMENT_FORM = "employeeEmploymentForm";

	@Override
	protected EmployeeEmploymentForm getFormFromValueStack(ValueStack valueStack) {
		return (EmployeeEmploymentForm) valueStack.findValue(EMPLOYEE_EMPLOYMENT_FORM, EmployeeEmploymentForm.class);
	}

	@Override
	protected void performValidation(EmployeeEmploymentForm form) {
		if (!EmployeeValidationUtil.valid(form.getEmployeeId(), EmployeeValidationUtil.EmployeeField.EMPLOYEE_ID)) {
			addFieldErrorIfMessage(fieldKeyBuilder(EMPLOYEE_EMPLOYMENT_FORM, "employeeId"), "Employee ID is already used");
		}
	}
}
