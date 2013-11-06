package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.entities.ProjectSkill;

import java.util.Date;
import java.util.List;

public class ProjectInfo {
	private int id;
	private int accountId;
	private String site;
	private String name;
	private String location;
	private Date startDate;
	private Date endDate;
	private List<ProjectRole> roles;
	private List<ProjectSkill> skills;

	private ProjectInfo() {
	}

	public int getId() {
		return id;
	}

	public int getAccountId() {
		return accountId;
	}

	public String getSite() {
		return site;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public List<ProjectRole> getRoles() {
		return roles;
	}

	public List<ProjectSkill> getSkills() {
		return skills;
	}

	public static class Builder {
		private ProjectInfo projectInfo;

		public Builder() {
			projectInfo = new ProjectInfo();
		}

		public Builder id(int id) {
			projectInfo.id = id;
			return this;
		}

		public Builder accountId(int accountId) {
			projectInfo.accountId = accountId;
			return this;
		}

		public Builder site(String site) {
			projectInfo.site = site;
			return this;
		}

		public Builder name(String name) {
			projectInfo.name = name;
			return this;
		}

		public Builder location(String location) {
			projectInfo.location = location;
			return this;
		}

		public Builder startDate(Date startDate) {
			projectInfo.startDate = startDate;
			return this;
		}

		public Builder endDate(Date endDate) {
			projectInfo.endDate = endDate;
			return this;
		}

		public Builder roles(List<ProjectRole> roles) {
			projectInfo.roles = roles;
			return this;
		}

		public Builder skills(List<ProjectSkill> skills) {
			projectInfo.skills = skills;
			return this;
		}

		public ProjectInfo build() {
			return projectInfo;
		}
	}
}
