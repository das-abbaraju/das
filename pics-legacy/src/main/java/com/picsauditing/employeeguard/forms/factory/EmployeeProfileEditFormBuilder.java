package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.forms.employee.EmployeeProfileEditForm;
import com.picsauditing.employeeguard.entities.Profile;

public class EmployeeProfileEditFormBuilder {

    public EmployeeProfileEditForm build(Profile profile) {
        EmployeeProfileEditForm employeeProfileEditForm = new EmployeeProfileEditForm();
        employeeProfileEditForm.setEmail(profile.getEmail());
        employeeProfileEditForm.setFirstName(profile.getFirstName());
        employeeProfileEditForm.setLastName(profile.getLastName());
        employeeProfileEditForm.setPhone(profile.getPhone());
        employeeProfileEditForm.setSlug(profile.getSlug());
        return employeeProfileEditForm;
    }
}
