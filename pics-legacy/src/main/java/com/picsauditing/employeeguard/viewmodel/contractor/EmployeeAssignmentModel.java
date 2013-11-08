package com.picsauditing.employeeguard.viewmodel.contractor;

import java.util.List;

public class EmployeeAssignmentModel {

    private int siteId;
    private String siteName;
    private List<ProjectModel> projects;

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<ProjectModel> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectModel> projects) {
        this.projects = projects;
    }
}
