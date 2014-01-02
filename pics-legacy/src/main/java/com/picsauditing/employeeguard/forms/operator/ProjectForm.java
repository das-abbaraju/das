package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.AddAnotherForm;
import com.picsauditing.employeeguard.util.DateUtil;
import org.apache.commons.lang3.ArrayUtils;

public class ProjectForm extends ProjectNameLocationForm implements AddAnotherForm {
	private int siteId;
	private int startYear;
	private int startMonth;
	private int startDay;
	private int endYear;
	private int endMonth;
	private int endDay;
	private int[] skills;
	private String[] roles;

	private boolean addAnother;

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public int getStartDay() {
		return startDay;
	}

	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public int[] getSkills() {
		return skills;
	}

	public void setSkills(int[] skills) {
		this.skills = skills;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	@Override
	public boolean isAddAnother() {
		return addAnother;
	}

	@Override
	public void setAddAnother(boolean addAnother) {
		this.addAnother = addAnother;
	}

	public Project buildProject() {
		Project project = new Project();

		project.setAccountId(siteId);
		project.setName(name);
		project.setLocation(location);
		project.setStartDate(DateUtil.explodedToDate(startYear, startMonth, startDay));
		project.setEndDate(DateUtil.explodedToDate(endYear, endMonth, endDay));

		if (ArrayUtils.isNotEmpty(skills)) {
			for (int skillId : skills) {
				AccountSkill skill = new AccountSkill(skillId, siteId);
				ProjectSkill projectSkill = new ProjectSkill(project, skill);
				project.getSkills().add(projectSkill);
			}
		}

		if (ArrayUtils.isNotEmpty(roles)) {
			for (String roleName : roles) {
				Group group = new Group();
				group.setName(roleName);

				ProjectRole projectRole = new ProjectRole(project, group);
				project.getRoles().add(projectRole);
			}
		}

		return project;
	}

	public static class Builder {
		private Project project;

		public Builder project(Project project) {
			this.project = project;

			return this;
		}

		public ProjectForm build() {
			ProjectForm projectForm = new ProjectForm();

			if (project != null) {
				int[] explodedStart = DateUtil.dateToExploded(project.getStartDate());
				int[] explodedEnd = DateUtil.dateToExploded(project.getEndDate());

				projectForm.name = project.getName();
				projectForm.location = project.getLocation();

				if (ArrayUtils.isNotEmpty(explodedStart)) {
					projectForm.startYear = explodedStart[0];
					projectForm.startMonth = explodedStart[1];
					projectForm.startDay = explodedStart[2];
				}

				if (ArrayUtils.isNotEmpty(explodedEnd)) {
					projectForm.endYear = explodedEnd[0];
					projectForm.endMonth = explodedEnd[1];
					projectForm.endDay = explodedEnd[2];
				}

				int counter = 0;

				projectForm.skills = new int[project.getSkills().size()];
				for (ProjectSkill projectSkill : project.getSkills()) {
					projectForm.skills[counter++] = projectSkill.getSkill().getId();
				}

				counter = 0;

				projectForm.roles = new String[project.getRoles().size()];
				for (ProjectRole projectRole : project.getRoles()) {
					projectForm.roles[counter++] = projectRole.getRole().getName();
				}
			}

			return projectForm;
		}
	}
}
