package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.contractor.ContractorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.forms.contractor.ContractorProjectAssignmentMatrix;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.AccountSkillEmployeeService;
import com.picsauditing.employeeguard.services.ContractorProjectService;
import com.picsauditing.employeeguard.services.EmployeeService;
import com.picsauditing.employeeguard.services.ProjectService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AssignmentAction extends PicsRestActionSupport {
	private static final Logger LOG = LoggerFactory.getLogger(AssignmentAction.class);

	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private ContractorProjectService contractorProjectService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private FormBuilderFactory formBuilderFactory;
	@Autowired
	private ProjectService projectService;

	private ContractorProjectAssignmentMatrix contractorProjectAssignmentMatrix;

	private Project project;

	private int assignmentId;
	private int employeeId;
	private int projectId;
	private int roleId;
	private int siteId;

	public String project() {
		project = projectService.getProject(String.valueOf(projectId), NumberUtils.toInt(id));
		List<AccountGroup> jobRoles = extractRolesFromProject(project);
		List<AccountSkill> accountSkills = extractSkillsFromProject(project, roleId);
		List<Employee> employees = employeeService.getEmployeesForAccount(permissions.getAccountId());

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.getAccountSkillEmployeeForAccountAndSkills(permissions.getAccountId(), accountSkills);
		List<RoleInfo> roleInfos = formBuilderFactory.getRoleInfoFactory().build(jobRoles);

		List<ContractorEmployeeProjectAssignment> contractorEmployeeProjectAssignments =
				formBuilderFactory.getContractorEmployeeProjectAssignmentFactory().buildList(employees, accountSkillEmployees, accountSkills, jobRoles);
		Collections.sort(contractorEmployeeProjectAssignments);

		buildAssignmentMatrix(accountSkills, roleInfos, contractorEmployeeProjectAssignments);

		return "project";
	}

	public String role() {
		project = projectService.getProject(String.valueOf(projectId), assignmentId);
		List<AccountGroup> jobRoles = extractRolesFromProject(project);
		List<AccountSkill> accountSkills = extractSkillsFromProject(project, NumberUtils.toInt(id));
		List<Employee> employees = employeeService.getEmployeesForAccount(permissions.getAccountId());

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.getAccountSkillEmployeeForAccountAndSkills(permissions.getAccountId(), accountSkills);
		List<RoleInfo> roleInfos = formBuilderFactory.getRoleInfoFactory().build(jobRoles);

		List<ContractorEmployeeProjectAssignment> contractorEmployeeProjectAssignments =
				formBuilderFactory.getContractorEmployeeProjectAssignmentFactory().buildList(employees, accountSkillEmployees, accountSkills, jobRoles);
		Collections.sort(contractorEmployeeProjectAssignments);

		buildAssignmentMatrix(accountSkills, roleInfos, contractorEmployeeProjectAssignments);

		return "role";
	}

	private void buildAssignmentMatrix(List<AccountSkill> accountSkills, List<RoleInfo> roleInfos, List<ContractorEmployeeProjectAssignment> contractorEmployeeProjectAssignments) {
		contractorProjectAssignmentMatrix = new ContractorProjectAssignmentMatrix();
		contractorProjectAssignmentMatrix.setAssignments(contractorEmployeeProjectAssignments);
		contractorProjectAssignmentMatrix.setRoles(roleInfos);
		contractorProjectAssignmentMatrix.setSkillNames(extractNamesFromSkills(accountSkills));
		contractorProjectAssignmentMatrix.setEmployeeRoles(contractorProjectService.sumEmployeeRolesForProject(permissions.getAccountId(), project));
	}

	private List<AccountGroup> extractRolesFromProject(final Project project) {
		if (project == null) {
			return Collections.emptyList();
		}

		List<AccountGroup> jobRoles = new ArrayList<>();
		for (ProjectRole jobRole : project.getRoles()) {
			jobRoles.add(jobRole.getRole());
		}

		Collections.sort(jobRoles);

		return jobRoles;
	}

	private List<AccountSkill> extractSkillsFromProject(final Project project, final int roleId) {
		if (project == null) {
			return Collections.emptyList();
		}

		List<AccountSkill> accountSkills = new ArrayList<>();

		if (roleId == 0) {
			extractSkillsDirectlyOnProject(project, accountSkills);
		} else {
			extractSkillsFromProjectRoles(project, accountSkills, roleId);
		}

		Collections.sort(accountSkills);

		return accountSkills;
	}

	private void extractSkillsDirectlyOnProject(final Project project, final List<AccountSkill> accountSkills) {
		for (ProjectSkill projectSkill : project.getSkills()) {
			accountSkills.add(projectSkill.getSkill());
		}
	}

	private void extractSkillsFromProjectRoles(final Project project, final List<AccountSkill> accountSkills, final int roleId) {
		for (ProjectRole jobRole : project.getRoles()) {
			if (jobRole.getRole().getId() == roleId) {
				for (AccountSkillGroup accountSkillGroup : jobRole.getRole().getSkills()) {
					accountSkills.add(accountSkillGroup.getSkill());
				}
			}
		}
	}

	private List<String> extractNamesFromSkills(final List<AccountSkill> accountSkills) {
		if (CollectionUtils.isEmpty(accountSkills)) {
			return Collections.emptyList();
		}

		List<String> skillNames = new ArrayList<>();
		for (AccountSkill accountSkill : accountSkills) {
			skillNames.add(accountSkill.getName());
		}

		return skillNames;
	}

	public String assign() {
		try {
			Employee employee = employeeService.findEmployee(id, permissions.getAccountId());
			ProjectRole projectRole = projectService.getProjectGroupByProjectAndRoleId(Integer.toString(projectId), roleId);
			contractorProjectService.assignEmployeeToProjectRole(employee, projectRole, permissions.getAppUserID());
			json.put("status", "SUCCESS");
		} catch (Exception exception) {
			LOG.error("Error assigning employee {} to job role {} under project {}\n{}", new Object[]{employeeId, roleId, id, exception});
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	public String unassign() {
		try {
			Employee employee = employeeService.findEmployee(id, permissions.getAccountId());
			ProjectRole projectRole = projectService.getProjectGroupByProjectAndRoleId(Integer.toString(projectId), roleId);
			contractorProjectService.unassignEmployeeFromProjectRole(employee, projectRole, permissions.getAppUserID());
			json.put("status", "SUCCESS");
		} catch (Exception exception) {
			LOG.error("Error assigning employee {} to job role {} under project {}\n{}", new Object[]{employeeId, roleId, id, exception});
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	/* getters and setters */

	public ContractorProjectAssignmentMatrix getContractorProjectAssignmentMatrix() {
		return contractorProjectAssignmentMatrix;
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
