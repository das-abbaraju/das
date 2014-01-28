package com.picsauditing.employeeguard.viewmodel.operator;

import com.picsauditing.employeeguard.viewmodel.model.Role;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class OperatorProjectRoleAssignment {

    private final List<Role> roles;
    private final List<Skill> skills;
    private final List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments;

    public OperatorProjectRoleAssignment(final Builder builder) {
        this.roles = CollectionUtils.isEmpty(builder.roles)
                ? Collections.<Role>emptyList() : Collections.unmodifiableList(builder.roles);

        this.skills = CollectionUtils.isEmpty(builder.skills)
                ? Collections.<Skill>emptyList() : Collections.unmodifiableList(builder.skills);

        this.employeeProjectRoleAssignments = CollectionUtils.isEmpty(builder.employeeProjectRoleAssignments)
                ? Collections.<EmployeeProjectRoleAssignment>emptyList()
                : Collections.unmodifiableList(builder.employeeProjectRoleAssignments);
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<EmployeeProjectRoleAssignment> getEmployeeProjectRoleAssignments() {
        return employeeProjectRoleAssignments;
    }

    public static class Builder {

        private List<Role> roles;
        private List<Skill> skills;
        private List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments;

        public Builder roles(final List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder skills(final List<Skill> skills) {
            this.skills = skills;
            return this;
        }

        public Builder employeeSiteRoleAssignments(final List<EmployeeProjectRoleAssignment> employeeProjectRoleAssignments) {
            this.employeeProjectRoleAssignments = employeeProjectRoleAssignments;
            return this;
        }

        public OperatorProjectRoleAssignment build() {
            return new OperatorProjectRoleAssignment(this);
        }
    }
}
