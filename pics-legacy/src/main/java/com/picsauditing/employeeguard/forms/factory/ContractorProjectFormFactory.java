package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ContractorProjectFormFactory {

	public List<ContractorProjectForm> build(List<ProjectCompany> projectCompanies, List<AccountModel> accountModels) {
		Map<Integer, AccountModel> accountModelMap = PicsCollectionUtil.convertToMap(accountModels, new PicsCollectionUtil.MapConvertable<Integer, AccountModel>() {
			@Override
			public Integer getKey(AccountModel entity) {
				return entity.getId();
			}
		});

		List<ContractorProjectForm> contractorProjectForms = new ArrayList<>();
		for (ProjectCompany projectCompany : projectCompanies) {
			contractorProjectForms.add(build(projectCompany,
					accountModelMap.get(projectCompany.getProject().getAccountId())));
		}

		return contractorProjectForms;
	}

	public ContractorProjectForm build(ProjectCompany projectCompany, AccountModel accountModel) {
		Project project = projectCompany.getProject();

		ContractorProjectForm projectForm = new ContractorProjectForm();
		projectForm = addAccountInfo(projectForm, accountModel, project);
		projectForm = addProjectInfo(project, projectForm);

		return projectForm;
	}

	private ContractorProjectForm addAccountInfo(final ContractorProjectForm projectForm,
	                                             final AccountModel accountModel,
	                                             final Project project) {
		if (accountModel == null) {
			projectForm.setSiteId(project.getAccountId());
			projectForm.setSiteName(Integer.toString(project.getAccountId()));
		} else {
			projectForm.setSiteId(accountModel.getId());
			projectForm.setSiteName(accountModel.getName());
		}

		return projectForm;
	}

	private ContractorProjectForm addProjectInfo(final Project project, final ContractorProjectForm projectForm) {
		projectForm.setProjectId(project.getId());
		projectForm.setProjectName(project.getName());
		projectForm.setLocation(project.getLocation());
		projectForm.setStartDate(project.getStartDate());
		projectForm.setEndDate(project.getEndDate());

		return projectForm;
	}

	public List<ContractorProjectForm> build(final Set<AccountModel> sites, final Set<Project> projects) {
		Map<Integer, AccountModel> siteById = PicsCollectionUtil.convertToMap(sites, new PicsCollectionUtil.MapConvertable<Integer, AccountModel>() {
			@Override
			public Integer getKey(AccountModel entity) {
				return entity.getId();
			}
		});

		List<ContractorProjectForm> contractorProjects = new ArrayList<>();

		for (Project project : projects) {
			ContractorProjectForm contractorProject = new ContractorProjectForm();
			addAccountInfo(contractorProject, siteById.get(project.getAccountId()), project);
			addProjectInfo(project, contractorProject);

			contractorProjects.add(contractorProject);
		}

		return contractorProjects;
	}
}
