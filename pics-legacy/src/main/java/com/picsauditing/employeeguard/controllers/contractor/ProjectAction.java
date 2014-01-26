package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountSkillEmployee;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.ContractorDetailProjectForm;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.ContractorProjectService;
import com.picsauditing.employeeguard.services.ProjectRoleService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectAssignmentBreakdown;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.contractor.SiteAssignmentStatisticsModel;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModeFactory;
import com.picsauditing.forms.binding.FormBinding;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProjectAction extends PicsRestActionSupport {

    @Autowired

    private AccountSkillEmployeeService accountSkillEmployeeService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ContractorProjectService contractorProjectService;
    @Autowired
    private ProjectRoleService projectRoleService;

    @Autowired
    private FormBuilderFactory formBuilderFactory;

    @FormBinding("contractor_project_search")
    private SearchForm searchForm;

    private ContractorDetailProjectForm project;
    private List<ContractorProjectForm> projects;
    private ProjectAssignmentBreakdown projectAssignmentBreakdown;
	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignmentsAndProjects;

    /* pages */

    public String index() {
        List<ProjectCompany> projectCompanies = null;
        if (isSearch(searchForm)) {
            projectCompanies = contractorProjectService.search(searchForm.getSearchTerm(), permissions.getAccountId());
        } else {
            projectCompanies = contractorProjectService.getProjectsForContractor(permissions.getAccountId());
        }

	    List<ProjectRoleEmployee> employeeRoles = projectRoleService.getProjectRolesForContractor(permissions.getAccountId());
	    List<AccountSkillEmployee> employeeSkills = accountSkillEmployeeService.getSkillsForAccount(permissions.getAccountId());

        projects = buildProjectsFromProjectCompanies(projectCompanies);
	    siteAssignmentsAndProjects = ViewModeFactory.getSiteAssignmentsAndProjectsFactory().create(projects, employeeRoles, employeeSkills);

        return LIST;
    }

    private List<ContractorProjectForm> buildProjectsFromProjectCompanies(List<ProjectCompany> projectCompanies) {
        List<Integer> siteIds = getSiteIds(projectCompanies);
        List<AccountModel> accountModels = accountService.getAccountsByIds(siteIds);

        return formBuilderFactory.getContractorProjectFormBuilder().build(projectCompanies, accountModels);
    }

    // TODO: Find some way to abstract this into a general utility class for Entities
    private List<Integer> getSiteIds(List<ProjectCompany> projectCompanies) {
        if (CollectionUtils.isEmpty(projectCompanies)) {
            return Collections.emptyList();
        }

        List<Integer> siteIds = new ArrayList<>();
        for (ProjectCompany projectCompany : projectCompanies) {
            siteIds.add(projectCompany.getProject().getAccountId());
        }

        return siteIds;
    }

    public String show() {
        ProjectCompany projectCompany = contractorProjectService.getProject(id, permissions.getAccountId());
        AccountModel accountModel = accountService.getAccountById(projectCompany.getProject().getAccountId());
        project = formBuilderFactory.getContratorDetailProjectFormBuilder().build(projectCompany, accountModel);

        List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService
                .getAccountSkillEmployeeForProjectAndContractor(projectCompany.getProject(), permissions.getAccountId());
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleService.getProjectRolesForContractor
                (projectCompany.getProject(), permissions.getAccountId());
        projectAssignmentBreakdown = ViewModeFactory.getProjectAssignmentBreakdownFactory()
                .create(projectRoleEmployees, accountSkillEmployees);

        return SHOW;
    }

    /* Setters and Getters */

    public SearchForm getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(SearchForm searchForm) {
        this.searchForm = searchForm;
    }

    /* Models */

    public ContractorDetailProjectForm getProject() {
        return project;
    }

    public List<ContractorProjectForm> getProjects() {
        return projects;
    }

    public ProjectAssignmentBreakdown getProjectAssignmentBreakdown() {
        return projectAssignmentBreakdown;
    }

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> getSiteAssignmentsAndProjects() {
		return siteAssignmentsAndProjects;
	}
}
