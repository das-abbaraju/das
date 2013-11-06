package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import org.apache.commons.lang3.ArrayUtils;

public class ProjectCompaniesForm {
	private int[] companies;

	public int[] getCompanies() {
		return companies;
	}

	public void setCompanies(int[] companies) {
		this.companies = companies;
	}

	public Project buildProject(final int accountId) {
		Project project = new Project();

		project.setAccountId(accountId);

		if (ArrayUtils.isNotEmpty(companies)) {
			for (int companyId : companies) {
				project.getCompanies().add(new ProjectCompany(project, companyId));
			}
		}

		return project;
	}
}
