package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.forms.operator.OperatorProjectAssignmentMatrix;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ProjectService;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignmentAction extends PicsRestActionSupport {
	private static final long serialVersionUID = 1288428610452669599L;

	@Autowired
	private AssignmentService assignmentService;
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
		assignmentId = NumberUtils.toInt(id);
		project = projectService.getProject(String.valueOf(projectId), assignmentId);
		operatorProjectAssignmentMatrix = assignmentService.buildOperatorProjectAssignmentMatrix(project, assignmentId, 0);

		return "project";
	}

	public String role() {
		project = projectService.getProject(String.valueOf(projectId), assignmentId);
		roleId = NumberUtils.toInt(id);
		operatorProjectAssignmentMatrix = assignmentService.buildOperatorProjectAssignmentMatrix(project, assignmentId, roleId);

		return "role";
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
