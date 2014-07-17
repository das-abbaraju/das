package com.picsauditing.employeeguard.validators.employee;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.contractor.EmployeeEmploymentForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class EmployeeEmploymentFormValidator extends AbstractBasicAndDuplicateValidator<EmployeeEmploymentForm> {
	public static final String EMPLOYEE_EMPLOYMENT_FORM = "employeeEmploymentForm";

	@Override
	protected EmployeeEmploymentForm getFormFromValueStack(ValueStack valueStack) {
		return (EmployeeEmploymentForm) valueStack.findValue(EMPLOYEE_EMPLOYMENT_FORM, EmployeeEmploymentForm.class);
	}

	@Override
	protected void doFormValidation(EmployeeEmploymentForm form) {
		if (!EmployeeValidationUtil.valid(form.getEmployeeId(), EmployeeValidationUtil.EmployeeField.EMPLOYEE_ID)) {
			addFieldErrorIfMessage(fieldKeyBuilder(EMPLOYEE_EMPLOYMENT_FORM, "employeeId"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMPLOYEE.ID_ALREADY_USED"));
		}
	}

    @Override
    protected String getDuplicateErrorMessage() {
        return EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMPLOYEE.DUPLICATE_ID");
    }
}
