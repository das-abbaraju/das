package com.picsauditing.employeeguard.validators.employee;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.PersonalInformationForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeeForm;
import com.picsauditing.employeeguard.forms.contractor.EmployeePersonalForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicAndDuplicateValidator;

public class EmployeeFormValidator extends AbstractBasicAndDuplicateValidator<PersonalInformationForm> {

	public static final String EMPLOYEE_CREATE_FORM = "employeeForm";
	public static final String EMPLOYEE_PERSONAL_FORM = "employeePersonalForm";

	@Override
	public void doFormValidation(PersonalInformationForm personalInformationForm) {
		String formName = EMPLOYEE_CREATE_FORM;
		if (personalInformationForm instanceof EmployeePersonalForm) {
			formName = EMPLOYEE_PERSONAL_FORM;
		}

		if (!EmployeeValidationUtil.valid(personalInformationForm.getFirstName(), EmployeeValidationUtil.EmployeeField.FIRST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "firstName"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMPLOYEE.FIRST_NAME"));
		}

		if (!EmployeeValidationUtil.valid(personalInformationForm.getLastName(), EmployeeValidationUtil.EmployeeField.LAST_NAME)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "lastName"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMPLOYEE.LAST_NAME"));
		}

		if (!EmployeeValidationUtil.valid(personalInformationForm.getEmail(), EmployeeValidationUtil.EmployeeField.EMAIL)) {
			addFieldErrorIfMessage(fieldKeyBuilder(formName, "email"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMPLOYEE.EMAIL"));
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

    @Override
    protected String getDuplicateErrorMessage() {
        return EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.EMPLOYEE.DUPLICATE");
    }
}
