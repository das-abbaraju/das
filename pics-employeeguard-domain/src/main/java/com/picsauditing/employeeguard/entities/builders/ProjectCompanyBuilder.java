package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;

import java.util.Date;

public class ProjectCompanyBuilder {

    private ProjectCompany projectCompany;

    public ProjectCompanyBuilder() {
        this.projectCompany = new ProjectCompany();
    }

    public ProjectCompanyBuilder id(int id) {
        projectCompany.setId(id);
        return this;
    }

    public ProjectCompanyBuilder accountId(int companyId) {
        projectCompany.setAccountId(companyId);
        return this;
    }

    public ProjectCompanyBuilder project(Project project) {
        projectCompany.setProject(project);
        return this;
    }

    public ProjectCompanyBuilder createdBy(int createdBy) {
        projectCompany.setCreatedBy(createdBy);
        return this;
    }

    public ProjectCompanyBuilder updatedBy(int updatedBy) {
        projectCompany.setUpdatedBy(updatedBy);
        return this;
    }

    public ProjectCompanyBuilder deletedBy(int deletedBy) {
        projectCompany.setDeletedBy(deletedBy);
        return this;
    }

    public ProjectCompanyBuilder createdDate(Date createdDate) {
        projectCompany.setCreatedDate(createdDate);
        return this;
    }

    public ProjectCompanyBuilder updatedDate(Date updatedDate) {
        projectCompany.setUpdatedDate(updatedDate);
        return this;
    }

    public ProjectCompanyBuilder deletedDate(Date deletedDate) {
        projectCompany.setDeletedDate(deletedDate);
        return this;
    }

    public ProjectCompany build() {
        return projectCompany;
    }
}
