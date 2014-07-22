package com.picsauditing.employeeguard.validators.profile;

import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.msgbundle.EGI18n;
import com.picsauditing.employeeguard.validators.AbstractBasicValidator;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileEditFormValidator extends AbstractBasicValidator<EmployeeProfileEditForm> {

    public static final String PROFILE_EDIT_FORM = "personalInfo";

    @Autowired
    private InputValidator inputValidator;

    @Override
    protected EmployeeProfileEditForm getFormFromValueStack(ValueStack valueStack) {
        return (EmployeeProfileEditForm) valueStack.findValue(PROFILE_EDIT_FORM, EmployeeProfileEditForm.class);
    }

    @Override
    protected void doFormValidation(EmployeeProfileEditForm form) {
        if (Strings.isEmpty(form.getFirstName())) {
            addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_EDIT_FORM, "firstName"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.FIRST_NAME"));
        }

        if (Strings.isEmpty(form.getLastName())) {
            addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_EDIT_FORM, "lastName"), EGI18n.getTextFromResourceBundle("VALIDATION.REQUIRED.LAST_NAME"));
        }

        if (!InputValidator.NO_ERROR.equals(inputValidator.validateEmail(form.getEmail()))) {
            addFieldErrorIfMessage(fieldKeyBuilder(PROFILE_EDIT_FORM, "email"), EGI18n.getTextFromResourceBundle("VALIDATION.INVALID.EMAIL"));
        }
    }
}
