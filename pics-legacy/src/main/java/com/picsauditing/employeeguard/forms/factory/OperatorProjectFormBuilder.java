package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.forms.operator.OperatorProjectForm;
import com.picsauditing.employeeguard.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class OperatorProjectFormBuilder {
	public List<OperatorProjectForm> build(List<ProjectCompany> projectCompanies, List<AccountModel> accountModels) {
		Map<Integer, AccountModel> accountModelMap = getAccountModelMap(accountModels);

		List<OperatorProjectForm> operatorProjectForms = new ArrayList<>();
		for (ProjectCompany projectCompany : projectCompanies) {
			operatorProjectForms.add(build(projectCompany,
					accountModelMap.get(projectCompany.getProject().getAccountId())));
		}

		return operatorProjectForms;
	}

	private Map<Integer, AccountModel> getAccountModelMap(List<AccountModel> accountModels) {
		if (CollectionUtils.isEmpty(accountModels)) {
			return Collections.emptyMap();
		}

		Map<Integer, AccountModel> accountModelMap = new HashMap<>();
		for (AccountModel accountModel : accountModels) {
			accountModelMap.put(accountModel.getId(), accountModel);
		}

		return accountModelMap;
	}

	public OperatorProjectForm build(ProjectCompany projectCompany, AccountModel accountModel) {
		Project project = projectCompany.getProject();

		OperatorProjectForm projectForm = new OperatorProjectForm();
		projectForm = addAccountInfo(projectForm, accountModel, project);
		projectForm = addProjectInfo(project, projectForm);

		return projectForm;
	}

	private OperatorProjectForm addAccountInfo(final OperatorProjectForm projectForm, final AccountModel accountModel,
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

	private OperatorProjectForm addProjectInfo(final Project project, final OperatorProjectForm projectForm) {
		projectForm.setProjectId(project.getId());
		projectForm.setProjectName(project.getName());
		projectForm.setLocation(project.getLocation());
		projectForm.setStartDate(project.getStartDate());
		projectForm.setEndDate(project.getEndDate());

		return projectForm;
	}
}
