package com.picsauditing.employeeguard.forms.operator;

import java.util.Date;

public class OperatorProjectForm implements Comparable<OperatorProjectForm> {

	private int siteId;
	private String siteName;
	private int projectId;
	private String projectName;
	private String location;
	private Date startDate;
	private Date endDate;

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

	@Override
	public int compareTo(OperatorProjectForm that) {
		int compareSiteNames = this.getSiteName().compareTo(that.getSiteName());
		if (compareSiteNames == 0) {
			return this.getProjectName().compareTo(that.getProjectName());
		}

		return compareSiteNames;
	}
}
