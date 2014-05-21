package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.ContractorDetailProjectForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.models.factories.SiteAssignmentsAndProjectsFactory;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.ContractorProjectService;
import com.picsauditing.employeeguard.services.ProjectRoleService;
import com.picsauditing.employeeguard.services.SiteSkillService;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.forms.binding.FormBinding;
import com.picsauditing.jpa.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private SiteSkillService siteSkillService;

	@Autowired
	private FormBuilderFactory formBuilderFactory;

	@FormBinding("contractor_project_search")
	private SearchForm searchForm;

	private ContractorDetailProjectForm project;
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

		buildSiteAssignmentsAndProjects(projectCompanies);

		buildbuildSiteAssignmentNotAttachedToProjects(permissions.getAccountId());

		siteAssignmentsAndProjects=Collections.unmodifiableMap(siteAssignmentsAndProjects);

		return LIST;
	}


	private void buildbuildSiteAssignmentNotAttachedToProjects(int contractorId){
		List<Integer> contractorClientSitesAttachedToProjs= contractorProjectService.findClientSitesByContractorAccount(contractorId);

		List<AccountModel> contractorClientSitesNotAttachedToProjects=accountService.findContractorClientSitesNotAttachedToProjects(contractorId, contractorClientSitesAttachedToProjs);

		SiteAssignmentsAndProjectsFactory saapf=ModelFactory.getSiteAssignmentsAndProjectsFactory();

		List<SiteAssignmentStatisticsModel> sasmForClientSitesNotAttchdToProjs=saapf.buildSiteAssignStatsForClientSitesUnattachedToProjs(contractorClientSitesNotAttachedToProjects);

		for(SiteAssignmentStatisticsModel siteAssignmentStatisticsModel:sasmForClientSitesNotAttchdToProjs){
			siteAssignmentsAndProjects.put(siteAssignmentStatisticsModel, Collections.<ProjectStatisticsModel>emptyList());
		}

	}



	private void buildSiteAssignmentsAndProjects(List<ProjectCompany> projectCompanies) {
		Map<AccountModel, Set<Project>> siteProjects = contractorProjectService.getSiteToProjectMapping(projectCompanies);
		Map<AccountModel, Set<AccountSkill>> siteRequiredSkills = siteSkillService.getRequiredSkillsForProjects(projectCompanies);
		Map<Employee, Set<Role>> employeeRoles = projectRoleService.getEmployeeProjectAndSiteRolesByAccount(permissions.getAccountId());
		List<AccountSkillEmployee> employeeSkills = accountSkillEmployeeService.getSkillsForAccount(permissions.getAccountId());

		siteAssignmentsAndProjects = ModelFactory.getSiteAssignmentsAndProjectsFactory()
				.create(siteProjects, siteRequiredSkills, employeeRoles, employeeSkills);
	}

	public String show() {
		ProjectCompany projectCompany = contractorProjectService.getProject(id, permissions.getAccountId());
		AccountModel accountModel = accountService.getAccountById(projectCompany.getProject().getAccountId());
		project = formBuilderFactory.getContratorDetailProjectFormBuilder().build(projectCompany, accountModel);

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService
				.getAccountSkillEmployeeForProjectAndContractor(projectCompany.getProject(), permissions.getAccountId());
		List<ProjectRoleEmployee> projectRoleEmployees = projectRoleService.getProjectRolesForContractor
				(projectCompany.getProject(), permissions.getAccountId());

		projectAssignmentBreakdown = ModelFactory.getProjectAssignmentBreakdownFactory()
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

	public ProjectAssignmentBreakdown getProjectAssignmentBreakdown() {
		return projectAssignmentBreakdown;
	}

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> getSiteAssignmentsAndProjects() {
		return siteAssignmentsAndProjects;
	}
}
