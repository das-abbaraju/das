package com.picsauditing.employeeguard.validators.employee;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.PersonalInformationForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePersonalForm;
import com.picsauditing.employeeguard.validators.AbstractValidator;

public class EmployeeFormValidator extends AbstractValidator<PersonalInformationForm> {

	public static final String EMPLOYEE_CREATE_FORM = "employeeForm";
	public static final String EMPLOYEE_PERSONAL_FORM = "employeePersonalForm";

	@Override
	public void performValidation(PersonalInformationForm personalInformationForm) {
		String formName = EMPLOYEE_CREATE_FORM;
		if (personalInformationForm instanceof EmployeePersonalForm) {
			formName = EMPLOYEE_PERSONAL_FORM;
		}

		if (!EmployeeValidationUtil.valid(personalInformationForm.getFirstName(), EmployeeValidationUtil.EmployeeField.FIRST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "firstName"), "First name is missing");
		}

		if (!EmployeeValidationUtil.valid(personalInformationForm.getLastName(), EmployeeValidationUtil.EmployeeField.LAST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "lastName"), "Last name is missing");
		}

		if (!EmployeeValidationUtil.valid(personalInformationForm.getEmail(), EmployeeValidationUtil.EmployeeField.EMAIL)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "email"), "Email is missing or already used");
		}
	}

	@Override
	protected PersonalInformationForm getFormFromValueStack(ValueStack valueStack) {
		EmployeePersonalForm employeePersonalForm = (EmployeePersonalForm) valueStack.findValue(EMPLOYEE_PERSONAL_FORM, EmployeePersonalForm.class);

		if (employeePersonalForm == null) {
			return (EmployeeForm) valueStack.findValue(EMPLOYEE_CREATE_FORM, EmployeeForm.class);
		}

		return employeePersonalForm;
	}
}
