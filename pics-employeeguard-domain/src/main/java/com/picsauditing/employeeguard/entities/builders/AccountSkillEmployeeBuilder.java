package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.ProfileDocument;

import java.util.Date;

public class AccountSkillEmployeeBuilder {

    private AccountSkillEmployee accountSkillEmployee;

    public AccountSkillEmployeeBuilder() {
        accountSkillEmployee = new AccountSkillEmployee();
    }

    public AccountSkillEmployeeBuilder id(int id) {
        accountSkillEmployee.setId(id);
        return this;
    }

    public AccountSkillEmployeeBuilder accountSkill(AccountSkill accountSkill) {
        accountSkillEmployee.setSkill(accountSkill);
        return this;
    }

    public AccountSkillEmployeeBuilder employee(Employee employee) {
        accountSkillEmployee.setEmployee(employee);
        return this;
    }

    public AccountSkillEmployeeBuilder profileDocument(ProfileDocument profileDocument) {
        accountSkillEmployee.setProfileDocument(profileDocument);
        return this;
    }

    public AccountSkillEmployeeBuilder startDate(Date startDate) {
        accountSkillEmployee.setStartDate(startDate);
        return this;
    }

    public AccountSkillEmployeeBuilder endDate(Date endDate) {
        accountSkillEmployee.setEndDate(endDate);
        return this;
    }

    public AccountSkillEmployeeBuilder createdBy(int createdBy) {
        accountSkillEmployee.setCreatedBy(createdBy);
        return this;
    }

    public AccountSkillEmployeeBuilder updatedBy(int updatedBy) {
        accountSkillEmployee.setUpdatedBy(updatedBy);
        return this;
    }

    public AccountSkillEmployeeBuilder deletedBy(int deletedBy) {
        accountSkillEmployee.setDeletedBy(deletedBy);
        return this;
    }

    public AccountSkillEmployeeBuilder createdDate(Date createdDate) {
        accountSkillEmployee.setCreatedDate(createdDate);
        return this;
    }

    public AccountSkillEmployeeBuilder updatedDate(Date updatedDate) {
        accountSkillEmployee.setUpdatedDate(updatedDate);
        return this;
    }

    public AccountSkillEmployeeBuilder deletedDate(Date deletedDate) {
        accountSkillEmployee.setDeletedDate(deletedDate);
        return this;
    }

    public AccountSkillEmployee build() {
        return accountSkillEmployee;
    }
}
