package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectSkill;

import java.util.Date;

public class ProjectSkillBuilder {

    private ProjectSkill projectSkill;

    public ProjectSkillBuilder() {
        this.projectSkill = new ProjectSkill();
    }

    public ProjectSkillBuilder id(int id) {
        projectSkill.setId(id);
        return this;
    }

    public ProjectSkillBuilder skill(AccountSkill skill) {
        projectSkill.setSkill(skill);
        return this;
    }

    public ProjectSkillBuilder project(Project project) {
        projectSkill.setProject(project);
        return this;
    }

    public ProjectSkillBuilder createdBy(int createdBy) {
        projectSkill.setCreatedBy(createdBy);
        return this;
    }

    public ProjectSkillBuilder updatedBy(int updatedBy) {
        projectSkill.setUpdatedBy(updatedBy);
        return this;
    }

    public ProjectSkillBuilder deletedBy(int deletedBy) {
        projectSkill.setDeletedBy(deletedBy);
        return this;
    }

    public ProjectSkillBuilder createdDate(Date createdDate) {
        projectSkill.setCreatedDate(createdDate);
        return this;
    }

    public ProjectSkillBuilder updatedDate(Date updatedDate) {
        projectSkill.setUpdatedDate(updatedDate);
        return this;
    }

    public ProjectSkillBuilder deletedDate(Date deletedDate) {
        projectSkill.setDeletedDate(deletedDate);
        return this;
    }

    public ProjectSkill build() {
        return projectSkill;
    }
}
