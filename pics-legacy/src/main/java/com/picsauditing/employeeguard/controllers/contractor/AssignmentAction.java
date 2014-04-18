package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.EntityAuditInfo;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.ContractorProjectService;
import com.picsauditing.employeeguard.services.ProjectService;
import com.picsauditing.employeeguard.services.entity.ProjectEntityService;
import com.picsauditing.employeeguard.viewmodel.contractor.ContractorProjectAssignmentMatrix;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AssignmentAction extends PicsRestActionSupport {

	private static final Logger LOG = LoggerFactory.getLogger(AssignmentAction.class);

	@Autowired
	private ContractorProjectService contractorProjectService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectEntityService projectEntityService;
	@Autowired
	private AssignmentService assignmentService;

	private ContractorProjectAssignmentMatrix contractorProjectAssignmentMatrix;

	private Project project;

	private int employeeId;
	private int projectId;
	private int roleId;
	private int assignmentId;

	public String project() {
		assignmentId = NumberUtils.toInt(id);
		project = projectService.getProject(String.valueOf(projectId), assignmentId);
		contractorProjectAssignmentMatrix = contractorProjectService.buildAssignmentMatrix(project, permissions.getAccountId());

		return "project";
	}

	public String role() {
		roleId = NumberUtils.toInt(id);
		project = projectService.getProject(String.valueOf(projectId), assignmentId);
		contractorProjectAssignmentMatrix = contractorProjectService.buildAssignmentMatrix(project, roleId, permissions.getAccountId());

		return "role";
	}

	public String assign() {
		try {
			Project project = projectEntityService.find(projectId);

			assignmentService.assignEmployeeToProject(project, roleId, NumberUtils.toInt(id),
					new EntityAuditInfo.Builder().appUserId(permissions.getAppUserID()).timestamp(DateBean.today()).build());

			json.put("status", "SUCCESS");
		} catch (Exception exception) {
			LOG.error("Error assigning employee {} to job role {} under project {}\n{}", new Object[]{employeeId, roleId, id, exception});
			json.put("status", "FAILURE");
		}

		return JSON;
	}

	public String unassign() {
		try {
			Project project = projectEntityService.find(projectId);

			assignmentService.unassignEmployeeFromProject(project, roleId, NumberUtils.toInt(id));

			json.put("status", "SUCCESS");
		} catch (Exception exception) {
			LOG.error("Error unassigning employee {} from job role {} under project {}\n{}", new Object[]{employeeId, roleId, id, exception});
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
