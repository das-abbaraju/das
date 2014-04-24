package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.factory.RoleFactory;
import com.picsauditing.employeeguard.viewmodel.factory.SkillFactory;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModelFactory;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectRoleAssignment;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AssignmentAction extends PicsRestActionSupport {

	private static final long serialVersionUID = 1288428610452669599L;

	@Autowired
	private AccountService accountService;

	// Old services
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRoleService projectRoleService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private SkillService skillService;

	private OperatorProjectAssignment operatorProjectAssignment;
	private OperatorProjectRoleAssignment operatorProjectRoleAssignment;

	private Project project;

	private int assignmentId;
	private int employeeId;
	private int projectId;
	private int roleId;
	private int siteId;

	public String assignments() {
		assignmentId = NumberUtils.toInt(id);
		project = projectService.getProject(String.valueOf(projectId), NumberUtils.toInt(id));

		operatorProjectAssignment = ViewModelFactory.getOperatorProjectAssignmentFactory()
				.create(RoleFactory.createFromProjectRoles(project.getRoles()),
						getEmployeeProjectAssignments(project));

		return "project";
	}

	private List<EmployeeProjectAssignment> getEmployeeProjectAssignments(final Project project) {
		return ViewModelFactory.getEmployeeProjectAssignmentFactory()
				.create(getContractorEmployeeMap(project),
						projectRoleService.getEmployeeProjectRoleAssignment(project),
						projectRoleService.getRolesAndSkillsForProject(project),
						getAccountSkillsFromProjectSkills(project.getSkills()),
						skillService.getRequiredSkillsForSite(project.getAccountId()),
						skillService.getParentSiteRequiredSkills(project.getAccountId()));
	}

	private List<AccountSkill> getAccountSkillsFromProjectSkills(final List<ProjectSkill> projectSkills) {
		List<AccountSkill> accountSkills = new ArrayList<>();
		for (ProjectSkill projectSkill : projectSkills) {
			accountSkills.add(projectSkill.getSkill());
		}

		return accountSkills;
	}

	public String role() {
		project = projectService.getProject(String.valueOf(projectId), assignmentId);
		Role role = roleService.getRole(id);
		List<AccountSkill> jobRoleSkills = getJobRoleSkills(role.getSkills());

		operatorProjectRoleAssignment = ViewModelFactory.getOperatorProjectRoleAssignmentFactory()
				.create(RoleFactory.createFromProjectRoles(project.getRoles()),
						SkillFactory
								.createSortedOperatorProjectAssignmentSkillHeader(
										jobRoleSkills,
										getAccountSkillsFromProjectSkills(project.getSkills()),
										skillService.getRequiredSkillsForSite(project.getAccountId()),
										skillService.getParentSiteRequiredSkills(project.getAccountId())),
						getEmployeeProjectRoleAssignmentList(project, role, jobRoleSkills));

		return "role";
	}

	private List<AccountSkill> getJobRoleSkills(final List<AccountSkillRole> accountSkillRoles) {
		List<AccountSkill> accountSkills = new ArrayList<>();
		for (AccountSkillRole accountSkillRole : accountSkillRoles) {
			accountSkills.add(accountSkillRole.getSkill());
		}

		return accountSkills;
	}

	private List<EmployeeProjectRoleAssignment> getEmployeeProjectRoleAssignmentList(final Project project,
	                                                                                 final Role role,
	                                                                                 final List<AccountSkill> jobRoleSkills) {
		Map<Integer, AccountModel> contractors = accountService
				.getIdToAccountModelMap(projectService.getContractorIdsForProject(project));
		Map<AccountModel, Set<Employee>> contractorEmployeeMap = projectRoleService
				.getEmployeesAssignedToProjectRole(project, role, contractors);


		return ViewModelFactory.getEmployeeProjectRoleAssignmentFactory()
				.create(contractorEmployeeMap,
						SkillFactory
								.createSortedOperatorProjectAssignmentAccountSkillsHeader(
										jobRoleSkills,
										getAccountSkillsFromProjectSkills(project.getSkills()),
										skillService.getRequiredSkillsForSite(project.getAccountId()),
										skillService.getParentSiteRequiredSkills(project.getAccountId())));
	}

	private Map<AccountModel, Set<Employee>> getContractorEmployeeMap(final Project project) {
		List<Integer> contractorIds = getContractorIds(project.getCompanies());
		List<AccountModel> contractors = accountService.getAccountsByIds(contractorIds);

		Map<AccountModel, Set<Employee>> contractorEmployeeMap = new HashMap<>();
		for (AccountModel accountModel : contractors) {
			if (!contractorEmployeeMap.containsKey(accountModel)) {
				contractorEmployeeMap.put(accountModel, new HashSet<Employee>());
			}

			contractorEmployeeMap.get(accountModel)
					.addAll(employeeService.getEmployeesForAccount(accountModel.getId()));
		}

		return contractorEmployeeMap;
	}

	private List<Integer> getContractorIds(final List<ProjectCompany> companies) {
		List<Integer> contractorIds = new ArrayList<>();
		for (ProjectCompany projectCompany : companies) {
			contractorIds.add(projectCompany.getAccountId());
		}

		return contractorIds;
	}

	/* getters and setters */

	public OperatorProjectAssignment getOperatorProjectAssignment() {
		return operatorProjectAssignment;
	}

	public OperatorProjectRoleAssignment getOperatorProjectRoleAssignment() {
		return operatorProjectRoleAssignment;
	}

	public Project getProject() {
		return project;
	}

	public int getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(int assignmentId) {
		this.assignmentId = assignmentId;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
}
