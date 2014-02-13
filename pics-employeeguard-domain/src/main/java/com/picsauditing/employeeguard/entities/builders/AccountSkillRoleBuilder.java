package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;

import java.util.Date;

public class AccountSkillRoleBuilder {

    private AccountSkillRole accountSkillRole;

    public AccountSkillRoleBuilder() {
        this.accountSkillRole = new AccountSkillRole();
    }

    public AccountSkillRoleBuilder id(int id) {
        accountSkillRole.setId(id);
        return this;
    }

    public AccountSkillRoleBuilder skill(AccountSkill skill) {
        accountSkillRole.setSkill(skill);
        return this;
    }

    public AccountSkillRoleBuilder role(Role role) {
        accountSkillRole.setRole(role);
        return this;
    }

    public AccountSkillRoleBuilder createdBy(int createdBy) {
        accountSkillRole.setCreatedBy(createdBy);
        return this;
    }

    public AccountSkillRoleBuilder updatedBy(int updatedBy) {
        accountSkillRole.setUpdatedBy(updatedBy);
        return this;
    }

    public AccountSkillRoleBuilder deletedBy(int deletedBy) {
        accountSkillRole.setDeletedBy(deletedBy);
        return this;
    }

    public AccountSkillRoleBuilder createdDate(Date createdDate) {
        accountSkillRole.setCreatedDate(createdDate);
        return this;
    }

    public AccountSkillRoleBuilder updatedDate(Date updatedDate) {
        accountSkillRole.setUpdatedDate(updatedDate);
        return this;
    }

    public AccountSkillRoleBuilder deletedDate(Date deletedDate) {
        accountSkillRole.setDeletedDate(deletedDate);
        return this;
    }

    public AccountSkillRole build() {
        return accountSkillRole;
    }
}
