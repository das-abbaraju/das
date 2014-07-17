package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.models.operations.MOperations;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SiteProjectService {
	private static Logger log = LoggerFactory.getLogger(SiteProjectService.class);

	@Autowired
	private ProjectEntityService projectEntityService;

	@Autowired
	private SkillEntityService skillEntityService;

	@Autowired
	private AccountService accountService;
	

	public MProjectsManager.MProject findProject(int projectId) throws ReqdInfoMissingException {
		MModels.fetchProjectManager().operations().copyId().copyName();
		Project project = projectEntityService.find(projectId);
		return MModels.fetchProjectManager().copyProject(project);
	}

	public MProjectsManager.MProject findProjectRoles(int projectId) throws ReqdInfoMissingException {
		MModels.fetchProjectManager().operations().copyId().copyName().attachRoles();
		MModels.fetchRolesManager().operations().copyId().copyName();
		Project project = projectEntityService.find(projectId);

		return MModels.fetchProjectManager().copyProject(project);

	}

	public MAssignments calculateProjectRoleEmployeeAssignments(int projectId, int roleId) throws ReqdInfoMissingException {
		ProjectRole projectRole = projectEntityService.findProjectRole(projectId, roleId);
		MModels.fetchStatusManager().operations().evalAllSkillsStatus();
		MModels.fetchContractorEmployeeManager().operations().copyId().copyFirstName().copyLastName().attachContractor().attachDocumentations();

		return findProjectRoleEmployees(projectId, Arrays.asList(projectRole));
	}

	public MAssignments calculateProjectEmployeeAssignments(int projectId) throws ReqdInfoMissingException {
		List<ProjectRole> projectRoles = projectEntityService.findProjectRoles(projectId);
		MModels.fetchStatusManager().operations().evalOverallStatusOnly();
		MModels.fetchContractorEmployeeManager().operations().copyId().copyFirstName().copyLastName().copyTitle().attachContractor().attachDocumentations();

		return findProjectRoleEmployees(projectId, projectRoles);
	}

	private MAssignments findProjectRoleEmployees(int projectId, List<ProjectRole> projectRoles)  throws ReqdInfoMissingException {
		Project project = projectEntityService.find(projectId);
		int siteAccountId = project.getAccountId();
		AccountModel accountModel = accountService.getAccountById(siteAccountId);

		List<AccountSkill> corpReqdSkills = skillEntityService.findAllParentCorpSiteRequiredSkills(siteAccountId);
		List<AccountSkill> siteReqdSkills = skillEntityService.findReqdSkillsForAccount(siteAccountId);

		MModels.fetchSkillsManager().operations().copyName().copyId();

		MModels.fetchRolesManager().operations().copyId().copyName().attachSkills();

		MModels.fetchContractorManager().operations().copyId().copyName();


		MModels.fetchCorporateManager().operations().copyId().copyName().attachReqdSkills();
		MModels.fetchCorporateManager().attachReqdSkills(siteAccountId, corpReqdSkills);
		MModels.fetchCorporateManager().copySite(siteAccountId, accountModel);


		MModels.fetchSitesManager().operations().copyId().copyName().attachReqdSkills();
		MModels.fetchSitesManager().attachReqdSkills(siteAccountId, siteReqdSkills);
		MModels.fetchSitesManager().copySite(siteAccountId, accountModel);

		MModels.fetchProjectManager().operations().copyId().copyName().copyAccountId().attachRoles().attachReqdSkills();
		MModels.fetchProjectManager().evalProjectAssignments(projectRoles);

		MAssignments mAssignments = MModels.fetchProjectManager().fetchModel(project.getId()).getAssignments();

		return mAssignments;

	}

}
