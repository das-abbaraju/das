package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.RoleEmployee;

import java.util.Date;

public class RoleEmployeeBuilder {

    private RoleEmployee roleEmployee;

    public RoleEmployeeBuilder() {
        this.roleEmployee = new RoleEmployee();
    }

    public RoleEmployeeBuilder id(final int id) {
        roleEmployee.setId(id);
        return this;
    }

    public RoleEmployeeBuilder role(final Role role) {
        roleEmployee.setRole(role);
        return this;
    }

    public RoleEmployeeBuilder employee(final Employee employee) {
        roleEmployee.setEmployee(employee);
        return this;
    }

    public RoleEmployeeBuilder createdBy(final int createdBy) {
        roleEmployee.setCreatedBy(createdBy);
        return this;
    }

    public RoleEmployeeBuilder updatedBy(final int updatedBy) {
        roleEmployee.setUpdatedBy(updatedBy);
        return this;
    }

    public RoleEmployeeBuilder deletedBy(final int deletedBy) {
        roleEmployee.setDeletedBy(deletedBy);
        return this;
    }

    public RoleEmployeeBuilder createdDate(final Date createdDate) {
        roleEmployee.setCreatedDate(createdDate);
        return this;
    }

    public RoleEmployeeBuilder updatedDate(final Date updatedDate) {
        roleEmployee.setUpdatedDate(updatedDate);
        return this;
    }

    public RoleEmployeeBuilder deletedDate(final Date deletedDate) {
        roleEmployee.setDeletedDate(deletedDate);
        return this;
    }

    public RoleEmployee build() {
        return roleEmployee;
    }
}
