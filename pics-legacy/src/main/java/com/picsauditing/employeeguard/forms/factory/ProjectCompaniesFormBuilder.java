package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.forms.operator.ProjectCompaniesForm;
import com.picsauditing.employeeguard.services.models.AccountModel;

import java.util.List;

public class ProjectCompaniesFormBuilder {
	public ProjectCompaniesForm build(final Project project) {
		ProjectCompaniesForm projectCompaniesForm = new ProjectCompaniesForm();

		int[] companyIds = new int[project.getCompanies().size()];
		int counter = 0;
		for (ProjectCompany projectCompany : project.getCompanies()) {
			companyIds[counter++] = projectCompany.getAccountId();
		}

		projectCompaniesForm.setCompanies(companyIds);

		return projectCompaniesForm;
	}

	public ProjectCompaniesForm build(final List<AccountModel> accounts) {
		ProjectCompaniesForm projectCompaniesForm = new ProjectCompaniesForm();

		int[] companyIds = new int[accounts.size()];
		int counter = 0;
		for (AccountModel accountModel : accounts) {
			companyIds[counter++] = accountModel.getId();
		}

		projectCompaniesForm.setCompanies(companyIds);

		return projectCompaniesForm;
	}
}
