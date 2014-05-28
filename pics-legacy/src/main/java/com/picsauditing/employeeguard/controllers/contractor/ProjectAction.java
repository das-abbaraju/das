package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.ContractorDetailProjectForm;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.process.ContractorAssignmentData;
import com.picsauditing.employeeguard.process.ContractorAssignmentProcess;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.ContractorProjectService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.forms.binding.FormBinding;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ProjectAction extends PicsRestActionSupport {

	@Autowired
	private AccountService accountService;
	@Autowired
	private ContractorProjectService contractorProjectService;
	@Autowired
	private ContractorAssignmentProcess contractorAssignmentProcess;
	@Autowired
	private ProjectEntityService projectEntityService;

	@Autowired
	private FormBuilderFactory formBuilderFactory;

	@FormBinding("contractor_project_search")
	private SearchForm searchForm;

	private ContractorDetailProjectForm contractorDetailProjectForm;
	private ProjectAssignmentBreakdown projectAssignmentBreakdown;
	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> siteAssignmentsAndProjects;

    /* pages */

	public String index() {
		siteAssignmentsAndProjects = Collections.unmodifiableMap(buildSiteAssignmentsAndProjects());

		return LIST;
	}

	private Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> buildSiteAssignmentsAndProjects() {
		int contractorId = permissions.getAccountId();
		Map<AccountModel, Set<AccountModel>> siteHierarchy = accountService.getSiteParentAccounts(
				accountService.getOperatorIdsForContractor(contractorId));

		ContractorAssignmentData contractorAssignmentData = contractorAssignmentProcess
				.buildContractorAssignmentData(contractorId,
						new HashSet<>(accountService.getOperatorsForContractors(Arrays.asList(contractorId))),
						siteHierarchy);

		Map<Project, Map<SkillStatus, Integer>> projectStatistics = contractorAssignmentProcess
				.buildProjectAssignmentStatistics(contractorAssignmentData);

		Map<AccountModel, Map<SkillStatus, Integer>> assignmentStatistics = contractorAssignmentProcess
				.buildSiteAssignmentStatistics(siteHierarchy, contractorAssignmentData);

		return ModelFactory.getSiteAssignmentsAndProjectsFactory()
				.create(projectStatistics, assignmentStatistics, contractorAssignmentData.getAccountProjects(),
						contractorAssignmentData.getContractorSiteAssignments());
	}

	public String show() {
		int contractorId = permissions.getAccountId();
		Project project = projectEntityService.find(getIdAsInt());

		ProjectCompany projectCompany = contractorProjectService.getProject(id, contractorId);
		AccountModel accountModel = accountService.getAccountById(projectCompany.getProject().getAccountId());
		contractorDetailProjectForm = formBuilderFactory.getContratorDetailProjectFormBuilder().build(projectCompany, accountModel);

		Map<AccountModel, Set<AccountModel>> siteHierarchy = accountService.getSiteParentAccounts(
				accountService.getOperatorIdsForContractor(contractorId));

		ContractorAssignmentData contractorAssignmentData = contractorAssignmentProcess
				.buildContractorAssignmentData(contractorId,
						new HashSet<>(accountService.getOperatorsForContractors(Arrays.asList(contractorId))),
						siteHierarchy);

		Map<Project, Map<SkillStatus, Integer>> projectStatistics = contractorAssignmentProcess
				.buildProjectAssignmentStatistics(contractorAssignmentData);

		projectAssignmentBreakdown = ModelFactory.getProjectAssignmentBreakdownFactory()
				.create(projectStatistics.get(project));

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
		return contractorDetailProjectForm;
	}

	public ProjectAssignmentBreakdown getProjectAssignmentBreakdown() {
		return projectAssignmentBreakdown;
	}

	public Map<SiteAssignmentStatisticsModel, List<ProjectStatisticsModel>> getSiteAssignmentsAndProjects() {
		return siteAssignmentsAndProjects;
	}
}
