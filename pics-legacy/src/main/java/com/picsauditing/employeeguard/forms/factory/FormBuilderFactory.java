package com.picsauditing.employeeguard.forms.factory;

import org.springframework.beans.factory.annotation.Autowired;

public class FormBuilderFactory {

    @Autowired
    private CompanySkillsFormBuilder companySkillsFormBuilder;
    @Autowired
    private EmployeeProfileFormBuilder employeeProfileFormBuilder;
    @Autowired
    private EmployeeProfileEditFormBuilder employeeProfileEditFormBuilder;

    private final SkillDocumentFormBuilder skillDocumentFormBuilder = new SkillDocumentFormBuilder();
    private final SkillInfoBuilder skillInfoBuilder = new SkillInfoBuilder();

    public CompanySkillsFormBuilder getCompanySkillsFormBuilder() {
        return companySkillsFormBuilder;
    }

    public EmployeeProfileFormBuilder getEmployeeProfileFormBuilder() {
        return employeeProfileFormBuilder;
    }

    public EmployeeProfileEditFormBuilder getEmployeeProfileEditFormBuilder() {
        return employeeProfileEditFormBuilder;
    }

    public SkillDocumentFormBuilder getSkillDocumentFormBuilder() {
        return skillDocumentFormBuilder;
    }

    public SkillInfoBuilder getSkillInfoBuilder() {
        return skillInfoBuilder;
    }
}
