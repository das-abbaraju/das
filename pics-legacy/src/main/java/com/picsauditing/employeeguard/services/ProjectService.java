package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.BaseEntityCallback;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.operator.ProjectCompaniesForm;
import com.picsauditing.employeeguard.forms.operator.ProjectNameSkillsForm;
import com.picsauditing.employeeguard.forms.operator.ProjectRolesForm;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.services.external.AccountService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.employeeguard.util.PicsCollectionUtil;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.GenericPredicate;
import com.picsauditing.util.generic.IntersectionAndComplementProcess;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Deprecated
public class ProjectService {

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillDAO accountSkillDAO;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private ProjectRoleDAO projectRoleDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;

	public Project getProject(final int id, final int accountId) {
		AccountModel account = accountService.getAccountById(accountId);

		if (account.isCorporate()) {
			List<Integer> childIds = accountService.getChildOperatorIds(accountId);
			return getProjectByAccounts(id, childIds);
		} else {
			return projectDAO.findProjectByAccount(id, accountId);
		}
	}

	public List<Project> getProjects(final List<Integer> projectIds, final int accountId) {
		AccountModel account = accountService.getAccountById(accountId);
		if (account.isCorporate()) {
			List<Integer> childIds = accountService.getChildOperatorIds(accountId);
			return getProjectsByAccounts(projectIds, childIds);
		} else {
			return getProjectsByAccount(projectIds, accountId);
		}
	}

	private Project getProjectByAccounts(final int projectId, final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(accountIds)) {
			return null;
		}

		return projectDAO.findProjectByAccounts(projectId, accountIds);
	}

	private List<Project> getProjectsByAccount(final List<Integer> projectIds, final int accountId) {
		return getProjectsByAccounts(projectIds, Arrays.asList(accountId));
	}

	private List<Project> getProjectsByAccounts(final List<Integer> projectIds, final Collection<Integer> accountIds) {
		if (CollectionUtils.isEmpty(projectIds) || CollectionUtils.isEmpty(accountIds)) {
			return Collections.emptyList();
		}

		return projectDAO.findProjectsByAccounts(projectIds, accountIds);
	}

	public List<Project> getProjectsForAccount(int accountId) {
		AccountModel accountModel = accountService.getAccountById(accountId);

		if (accountModel.isCorporate()) {
			List<Integer> childIds = new ArrayList<>();
			List<AccountModel> children = accountService.getChildOperators(accountId);

			for (AccountModel child : children) {
				childIds.add(child.getId());
			}

			if (CollectionUtils.isEmpty(childIds)) {
				return Collections.emptyList();
			}

			return projectDAO.findByAccounts(childIds); // a list of site ids
		} else {
			return projectDAO.findByAccount(accountId);
		}
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
		List<Role> rolesInDatabase = roleDAO.findRoleByAccountIdsAndNames(corporateIds, names);
		for (Role role : rolesInDatabase) {
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

		copyUpdatedProjectFieldsToOriginal(originalProject, updatedProject);
		copyOriginalDataToUpdatedProject(originalProject, updatedProject);
		linkSkillsToProject(originalProject.getAccountId(), updatedProject);

		BaseEntityCallback<ProjectSkill> callback = new BaseEntityCallback<>(appUserId, now);
		List<ProjectSkill> updatedSkills = IntersectionAndComplementProcess.intersection(
				updatedProject.getSkills(),
				originalProject.getSkills(),
				ProjectSkill.COMPARATOR,
				callback
		);

    originalProject.getSkills().clear();
    originalProject.getSkills().addAll(updatedSkills);
		EntityHelper.setUpdateAuditFields(originalProject, appUserId, now);
		EntityHelper.setUpdateAuditFields(originalProject.getSkills(), appUserId, now);

		return projectDAO.save(originalProject);
	}

	private void copyUpdatedProjectFieldsToOriginal(Project originalProject, Project updatedProject) {
		originalProject.setName(updatedProject.getName());
		originalProject.setLocation(updatedProject.getLocation());
		originalProject.setStartDate(updatedProject.getStartDate());
		originalProject.setEndDate(updatedProject.getEndDate());
	}

	private void linkSkillsToProject(final int accountId, final Project project) {
		List<Integer> skillIds = extractSkillIds(project.getSkills());
		project.getSkills().clear();

		List<Integer> corporateIds = accountService.getTopmostCorporateAccountIds(accountId);
		if (CollectionUtils.isEmpty(corporateIds) || CollectionUtils.isEmpty(skillIds)) {
			return;
		}

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

		originalProject.getRoles().clear();
		originalProject.getRoles().addAll(updatedRoles);

		EntityHelper.setUpdateAuditFields(originalProject, appUserId, now);
		EntityHelper.setUpdateAuditFields(originalProject.getRoles(), appUserId, now);

		return projectDAO.save(originalProject);
	}

	public Project update(Project originalProject, final ProjectCompaniesForm projectCompaniesForm, final int appUserId) {
		Date now = new Date();

		Project updatedProject = projectCompaniesForm.buildProject(originalProject.getAccountId());
		copyOriginalDataToUpdatedProject(originalProject, updatedProject);

		BaseEntityCallback<ProjectCompany> projectCompanyCallback = new BaseEntityCallback<>(appUserId, now);
		List<ProjectCompany> updatedCompanies = IntersectionAndComplementProcess.intersection(
				updatedProject.getCompanies(),
				originalProject.getCompanies(),
				ProjectCompany.COMPARATOR,
				projectCompanyCallback);

    originalProject.getCompanies().clear();
    originalProject.getCompanies().addAll(updatedCompanies);

		EntityHelper.setUpdateAuditFields(originalProject, appUserId, now);
		EntityHelper.setUpdateAuditFields(originalProject.getCompanies(), appUserId, now);
		originalProject = projectDAO.save(originalProject);

		removeEmployeesNoLongerAssignedToProject(originalProject, appUserId, projectCompanyCallback);

		return originalProject;
	}

	private void removeEmployeesNoLongerAssignedToProject(Project originalProject, int appUserId, BaseEntityCallback<ProjectCompany> projectCompanyCallback) {
		final List<Integer> removedCompanies = getRemovedCompanyIds(projectCompanyCallback);
		List<Employee> employeesAssignedToProject = getEmployeesAssignedToProjectFromRemovedCompanies(originalProject, removedCompanies);

		for (Employee employee : employeesAssignedToProject) {
			EntityHelper.softDelete(employee.getProjectRoles(), appUserId);
			projectRoleEmployeeDAO.delete(employee.getProjectRoles());
		}
	}

	private List<Employee> getEmployeesAssignedToProjectFromRemovedCompanies(Project originalProject, final List<Integer> removedCompanies) {
		List<Employee> employeesAssignedToProject = new ArrayList<>(employeeDAO.findByProject(originalProject));
		CollectionUtils.filter(employeesAssignedToProject, new GenericPredicate<Employee>() {
			@Override
			public boolean evaluateEntity(Employee employee) {
				return removedCompanies.contains(employee.getAccountId());
			}
		});
		return employeesAssignedToProject;
	}

	private List<Integer> getRemovedCompanyIds(BaseEntityCallback<ProjectCompany> projectCompanyCallback) {
		return ExtractorUtil.extractList(projectCompanyCallback.getRemovedEntities(), new Extractor<ProjectCompany, Integer>() {
			@Override
			public Integer extract(ProjectCompany projectCompany) {
				return projectCompany.getAccountId();
			}
		});
	}

	public void delete(final int id, final int accountId) {
		Project project = getProject(id, accountId);
		projectDAO.delete(project);
	}

	public List<Project> search(String searchTerm, int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return projectDAO.search(searchTerm, accountId);
	}

	public List<AccountSkill> getRequiredSkills(final Project project) {
		List<AccountSkill> requiredSkills = new ArrayList<>();

		for (ProjectSkill projectSkill : project.getSkills()) {
			requiredSkills.add(projectSkill.getSkill());
		}

		for (ProjectRole projectRole : project.getRoles()) {
			for (AccountSkillRole accountSkillRole : projectRole.getRole().getSkills()) {
				requiredSkills.add(accountSkillRole.getSkill());
			}
		}

		int accountId = project.getAccountId();
		List<Integer> accounts = accountService.getTopmostCorporateAccountIds(accountId);
		accounts.add(accountId);

		List<SiteSkill> siteSkills = getSiteRequiredSkills(accounts);
		requiredSkills.addAll(ExtractorUtil.extractList(siteSkills, SiteSkill.SKILL_EXTRACTOR));

		return ListUtil.removeDuplicatesAndSort(requiredSkills);
	}

	private List<SiteSkill> getSiteRequiredSkills(final List<Integer> accounts) {
		if (CollectionUtils.isEmpty(accounts)) {
			return Collections.emptyList();
		}

		return siteSkillDAO.findByAccountIds(accounts);
	}

	@Deprecated
	public Set<Project> getProjectsForEmployee(final Employee employee) {
		return projectEntityService.getProjectsForEmployee(employee);
	}

	@Deprecated
	public Project getProjectByRoleAndAccount(final String roleId, final int accountId) {
		return projectEntityService.getProjectByRoleAndAccount(roleId, accountId);
	}

	@Deprecated
	public Set<Integer> getContractorIdsForProject(final Project project) {
		return projectEntityService.getContractorIdsForProject(project);
	}

	public Map<Project, Set<Role>> getProjectRolesForEmployee(final int siteId, final Employee employee) {
		return PicsCollectionUtil.convertToMapOfSets(projectRoleDAO.findBySiteAndEmployee(siteId, employee),

				new PicsCollectionUtil.EntityKeyValueConvertable<ProjectRole, Project, Role>() {

					@Override
					public Project getKey(ProjectRole projectRole) {
						return projectRole.getProject();
					}

					@Override
					public Role getValue(ProjectRole projectRole) {
						return projectRole.getRole();
					}
				});
	}
}
