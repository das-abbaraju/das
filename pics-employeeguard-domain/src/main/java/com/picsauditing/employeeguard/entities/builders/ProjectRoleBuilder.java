package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.*;

import java.util.Date;
import java.util.List;

public class ProjectRoleBuilder {

    private ProjectRole projectRole;

    public ProjectRoleBuilder() {
        this.projectRole = new ProjectRole();
    }

    public ProjectRoleBuilder id(int id) {
        projectRole.setId(id);
        return this;
    }

    public ProjectRoleBuilder role(Role role) {
        projectRole.setRole(role);
        return this;
    }

    public ProjectRoleBuilder project(Project project) {
        projectRole.setProject(project);
        return this;
    }

    public ProjectRoleBuilder createdBy(int createdBy) {
        projectRole.setCreatedBy(createdBy);
        return this;
    }

    public ProjectRoleBuilder updatedBy(int updatedBy) {
        projectRole.setUpdatedBy(updatedBy);
        return this;
    }

    public ProjectRoleBuilder deletedBy(int deletedBy) {
        projectRole.setDeletedBy(deletedBy);
        return this;
    }

    public ProjectRoleBuilder createdDate(Date createdDate) {
        projectRole.setCreatedDate(createdDate);
        return this;
    }

    public ProjectRoleBuilder updatedDate(Date updatedDate) {
        projectRole.setUpdatedDate(updatedDate);
        return this;
    }

    public ProjectRoleBuilder deletedDate(Date deletedDate) {
        projectRole.setDeletedDate(deletedDate);
        return this;
    }

    public ProjectRoleBuilder employees(List<ProjectRoleEmployee> employees) {
        projectRole.setEmployees(employees);
        return this;
    }

    public ProjectRoleBuilder skills(List<ProjectSkillRole> skills) {
        projectRole.setSkills(skills);
        return this;
    }

    public ProjectRole build() {
        return projectRole;
    }
}
