package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountGroupDAO;
import com.picsauditing.employeeguard.daos.AccountSkillDAO;
import com.picsauditing.employeeguard.daos.ProjectDAO;
import com.picsauditing.employeeguard.daos.ProjectRoleDAO;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.operator.ProjectCompaniesForm;
import com.picsauditing.employeeguard.forms.operator.ProjectNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.ProjectRolesForm;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ProjectService {
	@Autowired
	private AccountGroupDAO accountGroupDAO;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ProjectRoleDAO projectRoleDAO;

	public Project getProject(final String id, final int accountId) {
		AccountModel account = accountService.getAccountById(accountId);
		if (account.isCorporate()) {
			List<Integer> childIds = accountService.getChildOperatorIds(accountId);
			return projectDAO.findProjectByAccounts(NumberUtils.toInt(id), childIds);
		} else {
			return projectDAO.findProjectByAccount(NumberUtils.toInt(id), accountId);
		}
	}

	public List<Project> getProjectsForAccount(int accountId) {
		AccountModel accountModel = accountService.getAccountById(accountId);

		if (accountModel.isCorporate()) {
			List<Integer> childIds = new ArrayList<>();
			List<AccountModel> children = accountService.getChildOperators(accountId);

			for (AccountModel child : children) {
				childIds.add(child.getId());
			}

			return projectDAO.findByAccounts(childIds); // a list of site ids
		} else {
			return projectDAO.findByAccount(accountId);
		}
	}

	public List<Project> getProjectsForContractor(int accountId) {
		return projectDAO.findByContractorAccount(accountId);
	}

	public ProjectRole getProjectGroupByProjectAndRoleId(final String projectId, final int roleId) {
		return projectRoleDAO.findByProjectAndRoleId(NumberUtils.toInt(projectId), roleId);
	}

	public Project save(Project project, final int accountId, final int appUserId) {
		linkRolesToProject(accountId, project);

		Date now = new Date();
		EntityHelper.setCreateAuditFields(project, appUserId, now);
		EntityHelper.setCreateAuditFields(project.getSkills(), appUserId, now);
		EntityHelper.setCreateAuditFields(project.getRoles(), appUserId, now);

		return projectDAO.save(project);
	}

	private void linkRolesToProject(final int accountId, final Project project) {
		List<String> names = extractRoleNames(project.getRoles());
		project.getRoles().clear();

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(accountId);
		List<AccountGroup> rolesInDatabase = accountGroupDAO.findGroupByAccountIdsAndNames(corporateIds, names);
		for (AccountGroup role : rolesInDatabase) {
			project.getRoles().add(new ProjectRole(project, role));
		}
	}

	private List<String> extractRoleNames(List<ProjectRole> roles) {
		List<String> names = new ArrayList<>();

		for (ProjectRole role : roles) {
			names.add(role.getRole().getName());
		}

		return names;
	}

	public Project update(Project originalProject, final ProjectNameSkillsForm projectNameSkillsForm, final int appUserId) {
		Date now = new Date();
		Project updatedProject = projectNameSkillsForm.buildProject(originalProject.getAccountId());

		originalProject.setName(projectNameSkillsForm.getName());
		originalProject.setLocation(projectNameSkillsForm.getLocation());
		originalProject.setStartDate(updatedProject.getStartDate());
		originalProject.setEndDate(updatedProject.getEndDate());

		copyOriginalDataToUpdatedProject(originalProject, updatedProject);
		linkSkillsToProject(originalProject.getAccountId(), updatedProject);

		List<ProjectSkill> updatedSkills = IntersectionAndComplementProcess.intersection(
				updatedProject.getSkills(),
				originalProject.getSkills(),
				ProjectSkill.COMPARATOR,
				new BaseEntityCallback<ProjectSkill>(appUserId, now)
		);

		originalProject.setSkills(updatedSkills);

		EntityHelper.setUpdateAuditFields(originalProject, appUserId, now);
		EntityHelper.setUpdateAuditFields(originalProject.getSkills(), appUserId, now);

		return projectDAO.save(originalProject);
	}

	private void linkSkillsToProject(final int accountId, Project project) {
		List<Integer> skillIds = extractSkillIds(project.getSkills());
		project.getSkills().clear();

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(accountId);
		List<AccountSkill> skillsInDatabase = accountSkillDAO.findSkillsByAccountsAndIds(corporateIds, skillIds);
		for (AccountSkill skill : skillsInDatabase) {
			project.getSkills().add(new ProjectSkill(project, skill));
		}
	}

	private List<Integer> extractSkillIds(List<ProjectSkill> skills) {
		List<Integer> skillIds = new ArrayList<>();

		for (ProjectSkill projectSkill : skills) {
			skillIds.add(projectSkill.getSkill().getId());
		}

		return skillIds;
	}

	private void copyOriginalDataToUpdatedProject(Project originalProject, Project updatedProject) {
		updatedProject.setId(originalProject.getId());
		updatedProject.setAccountId(originalProject.getAccountId());
		updatedProject.setName(originalProject.getName());

		for (ProjectCompany projectCompany : updatedProject.getCompanies()) {
			projectCompany.setProject(originalProject);
		}

		for (ProjectRole projectRole : updatedProject.getRoles()) {
			projectRole.setProject(originalProject);
		}

		for (ProjectSkill projectSkill : updatedProject.getSkills()) {
			projectSkill.setProject(originalProject);
		}
	}

	public Project update(Project originalProject, final ProjectRolesForm projectRolesForm, final int appUserId) {
		Date now = new Date();

		Project updatedProject = projectRolesForm.buildProject(originalProject.getAccountId());
		copyOriginalDataToUpdatedProject(originalProject, updatedProject);
		linkRolesToProject(originalProject.getAccountId(), updatedProject);

		List<ProjectRole> updatedRoles = IntersectionAndComplementProcess.intersection(
				updatedProject.getRoles(),
				originalProject.getRoles(),
				ProjectRole.COMPARATOR,
				new BaseEntityCallback<ProjectRole>(appUserId, now)
		);

		originalProject.setRoles(updatedRoles);

		EntityHelper.setUpdateAuditFields(originalProject, appUserId, now);
		EntityHelper.setUpdateAuditFields(originalProject.getRoles(), appUserId, now);

		return projectDAO.save(originalProject);
	}

	public Project update(Project originalProject, final ProjectCompaniesForm projectCompaniesForm, final int appUserId) {
		Date now = new Date();

		Project updatedProject = projectCompaniesForm.buildProject(originalProject.getAccountId());
		copyOriginalDataToUpdatedProject(originalProject, updatedProject);

		List<ProjectCompany> updatedCompanies = IntersectionAndComplementProcess.intersection(
				updatedProject.getCompanies(),
				originalProject.getCompanies(),
				ProjectCompany.COMPARATOR,
				new BaseEntityCallback<ProjectCompany>(appUserId, now));

		originalProject.setCompanies(updatedCompanies);

		EntityHelper.setUpdateAuditFields(originalProject, appUserId, now);
		EntityHelper.setUpdateAuditFields(originalProject.getCompanies(), appUserId, now);

		return projectDAO.save(originalProject);
	}

	public void delete(final String id, final int accountId, final int appUserId) {
		Date now = new Date();
		Project project = getProject(id, accountId);
		EntityHelper.softDelete(project, appUserId, now);
		EntityHelper.softDelete(project.getRoles(), appUserId, now);
		EntityHelper.softDelete(project.getSkills(), appUserId, now);

		projectDAO.save(project);
	}

	public List<Project> search(String searchTerm, int accountId) {
		if (Strings.isNotEmpty(searchTerm)) {
			return projectDAO.search(searchTerm, accountId);
		}

		return Collections.emptyList();
	}
}