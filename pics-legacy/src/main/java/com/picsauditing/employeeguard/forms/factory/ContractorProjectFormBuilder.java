package com.picsauditing.employeeguard.forms.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ContractorProjectFormBuilder {

    public List<ContractorProjectForm> build(List<ProjectCompany> projectCompanies, List<AccountModel> accountModels) {
        Map<Integer, AccountModel> accountModelMap = getAccountModelMap(accountModels);

        List<ContractorProjectForm> contractorProjectForms = new ArrayList<>();
        for (ProjectCompany projectCompany : projectCompanies) {
            contractorProjectForms.add(build(projectCompany,
                    accountModelMap.get(projectCompany.getProject().getAccountId())));
        }

        return contractorProjectForms;
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

    public ContractorProjectForm build(ProjectCompany projectCompany, AccountModel accountModel) {
        Project project = projectCompany.getProject();

        ContractorProjectForm projectForm = new ContractorProjectForm();
        projectForm = addAccountInfo(projectForm, accountModel, project);
        projectForm = addProjectInfo(project, projectForm);

        return projectForm;
    }

    private ContractorProjectForm addAccountInfo(final ContractorProjectForm projectForm, final AccountModel accountModel,
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
}
