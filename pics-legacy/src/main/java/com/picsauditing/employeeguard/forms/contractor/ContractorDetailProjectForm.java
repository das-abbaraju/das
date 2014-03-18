package com.picsauditing.employeeguard.forms.contractor;

import com.picsauditing.employeeguard.entities.ProjectSkill;

import java.util.Date;
import java.util.List;

public class ContractorDetailProjectForm {

	private int siteId;
	private String siteName;
	private int projectId;
	private String projectName;
	private String location;
	private Date startDate;
	private Date endDate;
	private List<ProjectSkill> skills;
	private List<JobRoleInfo> jobRoles;

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String site) {
		this.siteName = site;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<ProjectSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<ProjectSkill> skills) {
		this.skills = skills;
	}

	public List<JobRoleInfo> getJobRoles() {
		return jobRoles;
	}

	public void setJobRoles(List<JobRoleInfo> jobRoles) {
		this.jobRoles = jobRoles;
	}
}
