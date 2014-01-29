package com.picsauditing.employeeguard.services;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.employeeguard.daos.*;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.entities.helper.EntityHelper;
import com.picsauditing.employeeguard.forms.contractor.ContractorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.util.Extractor;
import com.picsauditing.employeeguard.util.ExtractorUtil;
import com.picsauditing.employeeguard.util.ListUtil;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorProjectAssignmentMatrix;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModeFactory;
import com.picsauditing.util.Strings;
import com.picsauditing.util.generic.GenericPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class ContractorProjectService {
	private static final Logger LOG = LoggerFactory.getLogger(ContractorProjectService.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeDAO accountSkillEmployeeDAO;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private EmployeeDAO employeeDAO;
	@Autowired
	private FormBuilderFactory formBuilderFactory;
	@Autowired
	private ProjectCompanyDAO projectCompanyDAO;
	@Autowired
	private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
	@Autowired
	private SiteSkillDAO siteSkillDAO;
	@Autowired
	private SkillUsageLocator skillUsageLocator;

	public ProjectCompany getProject(String id, int accountId) {
		return projectCompanyDAO.findProject(NumberUtils.toInt(id), accountId);
	}

	public List<ProjectCompany> getProjectsForContractor(int accountId) {
		return projectCompanyDAO.findByContractorAccount(accountId);
	}

	public List<ProjectCompany> search(String searchTerm, int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return projectCompanyDAO.search(searchTerm, accountId);
	}

	public void assignEmployeeToProjectRole(final Employee employee, final ProjectRole projectRole, final int appUserId) {
		Date now = new Date();

		ProjectRoleEmployee projectRoleEmployee = new ProjectRoleEmployee(projectRole, employee);
		EntityHelper.setCreateAuditFields(projectRoleEmployee, appUserId, now);
		projectRoleEmployeeDAO.save(projectRoleEmployee);

		accountSkillEmployeeService.linkEmployeeToSkills(employee, appUserId, now);
	}

	public void unassignEmployeeFromProjectRole(Employee employee, ProjectRole projectRole, int appUserId) {
		Date now = new Date();

		ProjectRoleEmployee projectRoleEmployee = projectRoleEmployeeDAO.findByEmployeeAndProjectRole(employee, projectRole);
		EntityHelper.softDelete(projectRoleEmployee, appUserId);
		projectRoleEmployeeDAO.delete(projectRoleEmployee);

		accountSkillEmployeeService.linkEmployeeToSkills(employee, appUserId, now);
	}

	public ContractorProjectAssignmentMatrix buildAssignmentMatrix(final Project project, final int accountId) {
		List<AccountSkill> requiredSkills = getRequiredSkills(project);

		ContractorProjectAssignmentMatrix matrix = new ContractorProjectAssignmentMatrix();
		matrix.setRoles(buildRoleInfos(project));
		matrix.setAssignments(buildAssignments(project, requiredSkills, accountId));
		matrix.setSkillNames(buildSkillNames(requiredSkills));

		return matrix;
	}

	private List<RoleInfo> buildRoleInfos(Project project) {
		List<Role> groups = ExtractorUtil.extractList(project.getRoles(), ProjectRole.ROLE_EXTRACTOR);
		return ViewModeFactory.getRoleInfoFactory().build(groups);
	}

	private List<ContractorEmployeeProjectAssignment> buildAssignments(final Project project, final List<AccountSkill> requiredSkills, final int accountId) {
		List<Employee> employees = getAssignedEmployees(project, accountId);
		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO.findByEmployeesAndSkills(employees, requiredSkills);

		return formBuilderFactory.getContractorEmployeeProjectAssignmentFactory()
				.buildList(employees, accountSkillEmployees, requiredSkills, Collections.<Group>emptyList());
	}

	private List<String> buildSkillNames(List<AccountSkill> requiredSkills) {
		return ExtractorUtil.extractList(requiredSkills, new Extractor<AccountSkill, String>() {
            @Override
            public String extract(AccountSkill accountSkill) {
                return accountSkill.getName();
            }
        });
	}

	private List<Employee> getAssignedEmployees(final Project project, final int accountId) {
		List<Employee> employees = employeeDAO.findByProject(project);

		CollectionUtils.filter(employees, new GenericPredicate<Employee>() {
			@Override
			public boolean evaluateEntity(Employee employee) {
				return employee.getAccountId() == accountId;
			}
		});

		return employees;
	}

	private List<AccountSkill> getRequiredSkills(final Project project) {
		int accountId = project.getAccountId();
		// Find required skills by operator site and corporates
		List<Integer> accountIds = accountService.getTopmostCorporateAccountIds(accountId);
		accountIds.add(accountId);

		List<AccountSkill> requiredSkills = new ArrayList<>();
		List<SiteSkill> siteSkills = siteSkillDAO.findByAccountIds(accountIds);
		requiredSkills.addAll(ExtractorUtil.extractList(siteSkills, SiteSkill.SKILL_EXTRACTOR));
		requiredSkills.addAll(ExtractorUtil.extractList(project.getSkills(), ProjectSkill.SKILL_EXTRACTOR));

		return ListUtil.removeDuplicatesAndSort(requiredSkills);
	}

	public ContractorProjectAssignmentMatrix buildAssignmentMatrix(final Project project, final int roleId, final int accountId) {
		ProjectRole projectRole = getProjectRole(project, roleId);

		ContractorProjectAssignmentMatrix matrix = new ContractorProjectAssignmentMatrix();
		List<AccountSkill> requiredSkills = getRequiredSkills(projectRole);

		matrix.setRoles(buildRoleInfos(project));
		matrix.setAssignments(buildAssignments(projectRole, requiredSkills, accountId));
		matrix.setSkillNames(buildSkillNames(requiredSkills));

		return matrix;
	}

	private ProjectRole getProjectRole(final Project project, final int roleId) throws IllegalArgumentException {
		for (ProjectRole projectRole : project.getRoles()) {
			if (projectRole.getRole().getId() == roleId) {
				return projectRole;
			}
		}

		LOG.error("Tried to find role {} in project {}", roleId, project.getId());
		throw new IllegalArgumentException("Could not find role within project");
	}

	private List<ContractorEmployeeProjectAssignment> buildAssignments(final ProjectRole projectRole, final List<AccountSkill> requiredSkills, final int accountId) {
		List<Employee> employees = employeeDAO.findByAccount(accountId);
		Collections.sort(employees);

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeDAO.findByEmployeesAndSkills(employees, requiredSkills);
		List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByEmployeesAndProjectRole(employees, projectRole);

		return formBuilderFactory.getContractorEmployeeProjectAssignmentFactory().buildListForRole(employees, requiredSkills, accountSkillEmployees, projectRoleEmployees);
	}

	private List<AccountSkill> getRequiredSkills(final ProjectRole projectRole) {
		return ExtractorUtil.extractList(projectRole.getRole().getSkills(), AccountSkillRole.SKILL_EXTRACTOR);
	}

	public Map<AccountModel, Set<Project>> getSiteToProjectMapping(final List<ProjectCompany> projectCompanies) {
		if (CollectionUtils.isEmpty(projectCompanies)) {
			return Collections.emptyMap();
		}

		List<Integer> siteIds = getSiteIdsFromProjectCompanies(projectCompanies);
		Map<Integer, AccountModel> siteModels = accountService.getIdToAccountModelMap(siteIds);

		Map<AccountModel, Set<Project>> siteToProjectMap = new HashMap<>();

		for (ProjectCompany projectCompany : projectCompanies) {
			Utilities.addToMapOfKeyToSet(siteToProjectMap, siteModels.get(projectCompany.getProject().getAccountId()), projectCompany.getProject());
		}

		return siteToProjectMap;
	}

	private List<Integer> getSiteIdsFromProjectCompanies(List<ProjectCompany> projectCompanies) {
		return ExtractorUtil.extractList(projectCompanies, new Extractor<ProjectCompany, Integer>() {
			@Override
			public Integer extract(ProjectCompany projectCompany) {
				return projectCompany.getProject().getAccountId();
			}
		});
	}
}
