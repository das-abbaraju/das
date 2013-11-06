package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.*;
import com.picsauditing.employeeguard.forms.factory.FormBuilderFactory;
import com.picsauditing.employeeguard.forms.operator.OperatorEmployeeProjectAssignment;
import com.picsauditing.employeeguard.forms.operator.OperatorProjectAssignmentMatrix;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.*;
import com.picsauditing.employeeguard.services.models.AccountModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class AssignmentAction extends PicsRestActionSupport {
    private static final long serialVersionUID = 1288428610452669599L;
	private static final Logger LOG = LoggerFactory.getLogger(AssignmentAction.class);

	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountSkillEmployeeService accountSkillEmployeeService;
	@Autowired
	private OperatorProjectService operatorProjectService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private FormBuilderFactory formBuilderFactory;
	@Autowired
	private ProjectService projectService;

	private OperatorProjectAssignmentMatrix operatorProjectAssignmentMatrix;

	private Project project;

	private int assignmentId;
	private int employeeId;
	private int projectId;
	private int roleId;
	private int siteId;

	public String assignments() {
		prepareMatrix(String.valueOf(projectId), NumberUtils.toInt(id), 0);
		return "project";
	}

	public String role() {
		prepareMatrix(String.valueOf(projectId), assignmentId, NumberUtils.toInt(id));
		return "role";
	}

	private void prepareMatrix(String projectId, int assignmentId, int roleId) {
		project = projectService.getProject(projectId, assignmentId);
		List<AccountGroup> jobRoles = extractRolesFromProject(project);
		List<AccountSkill> accountSkills = extractSkillsFromProject(project, roleId);
		List<AccountModel> contractors = accountService.getContractors(permissions.getAccountId());
		List<Employee> employees = employeeService.getEmployeesForAccounts(accountService.extractIdFromAccountModel(contractors.toArray(new AccountModel[0])));;

		List<AccountSkillEmployee> accountSkillEmployees = accountSkillEmployeeService.findByEmployeesAndSkills(employees, accountSkills);
		List<RoleInfo> roleInfos = formBuilderFactory.getRoleInfoFactory().build(jobRoles);

		Map<Integer, AccountModel> accountModels = getIdToAccountModel(contractors);

		List<OperatorEmployeeProjectAssignment> operatorEmployeeProjectAssignments =
				formBuilderFactory.getOperatorEmployeeProjectAssignmentFactory().buildList(employees, accountSkillEmployees, accountSkills, accountModels, jobRoles);
		Collections.sort(operatorEmployeeProjectAssignments);

		buildAssignmentMatrix(accountSkills, roleInfos, operatorEmployeeProjectAssignments);
	}

	private Map<Integer, AccountModel> getIdToAccountModel(List<AccountModel> contractors) {
		Map<Integer, AccountModel> accountModels = new HashMap<>();
		for (AccountModel accountModel : contractors) {
			accountModels.put(accountModel.getId(), accountModel);
		}
		return accountModels;
	}

	private void buildAssignmentMatrix(List<AccountSkill> accountSkills, List<RoleInfo> roleInfos, List<OperatorEmployeeProjectAssignment> operatorEmployeeProjectAssignments) {
		operatorProjectAssignmentMatrix = new OperatorProjectAssignmentMatrix();
		operatorProjectAssignmentMatrix.setAssignments(operatorEmployeeProjectAssignments);
		operatorProjectAssignmentMatrix.setRoles(roleInfos);
		operatorProjectAssignmentMatrix.setSkillNames(extractNamesFromSkills(accountSkills));
//		operatorProjectAssignmentMatrix.setEmployeeRoles(operatorProjectService.sumEmployeeRolesForProject(permissions.getAccountId(), project));
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
			Employee employee = employeeService.findEmployee(Integer.toString(employeeId), permissions.getAccountId());
			ProjectRole projectRole = projectService.getProjectGroupByProjectAndRoleId(id, roleId);
			operatorProjectService.assignEmployeeToProjectRole(employee, projectRole, permissions.getAppUserID());
			json.put("status", "SUCCESS");
		} catch (Exception exception) {
			LOG.error("Error assigning employee {} to job role {} under project {}\n{}", new Object[]{employeeId, roleId, id, exception});
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	public String unassign() {
		try {
			Employee employee = employeeService.findEmployee(Integer.toString(employeeId), permissions.getAccountId());
			ProjectRole projectRole = projectService.getProjectGroupByProjectAndRoleId(id, roleId);
			operatorProjectService.unassignEmployeeFromProjectRole(employee, projectRole, permissions.getAppUserID());
			json.put("status", "SUCCESS");
		} catch (Exception exception) {
			LOG.error("Error assigning employee {} to job role {} under project {}\n{}", new Object[]{employeeId, roleId, id, exception});
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	/* getters and setters */

	public OperatorProjectAssignmentMatrix getOperatorProjectAssignmentMatrix() {
		return operatorProjectAssignmentMatrix;
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
