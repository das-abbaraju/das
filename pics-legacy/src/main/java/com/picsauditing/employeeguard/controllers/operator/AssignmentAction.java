package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.entities.AccountGroup;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.forms.operator.OperatorProjectAssignmentMatrix;
import com.picsauditing.employeeguard.forms.operator.RoleInfo;
import com.picsauditing.employeeguard.services.AssignmentService;
import com.picsauditing.employeeguard.services.GroupService;
import com.picsauditing.employeeguard.services.ProjectService;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.factory.RoleFactory;
import com.picsauditing.employeeguard.viewmodel.factory.SkillFactory;
import com.picsauditing.employeeguard.viewmodel.factory.ViewModeFactory;
import com.picsauditing.employeeguard.viewmodel.model.Skill;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.EmployeeProjectRoleAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectAssignment;
import com.picsauditing.employeeguard.viewmodel.operator.OperatorProjectRoleAssignment;
import com.picsauditing.jpa.entities.Employee;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssignmentAction extends PicsRestActionSupport {
    private static final long serialVersionUID = 1288428610452669599L;

    @Autowired
    private AssignmentService assignmentService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SkillService skillService;

    private OperatorProjectAssignmentMatrix operatorProjectAssignmentMatrix;
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
        project = projectService.getProject(String.valueOf(projectId), assignmentId);

        operatorProjectAssignment = ViewModeFactory.getOperatorProjectAssignmentFactory()
                .create(Collections.<RoleInfo>emptyList(), Collections.<EmployeeProjectAssignment>emptyList());

        return "project";
    }

    public String role() {
        project = projectService.getProject(String.valueOf(projectId), assignmentId);
        AccountGroup role = groupService.getGroup(id);

        operatorProjectRoleAssignment = ViewModeFactory.getOperatorProjectRoleAssignmentFactory()
                .create(RoleFactory.createFromProjectRoles(project.getRoles()),
                        SkillFactory.createFromAccountSkillGroups(role.getSkills()),
                        getEmployeeProjectRoleAssignmentList());

        return "role";
    }

    private List<EmployeeProjectRoleAssignment> getEmployeeProjectRoleAssignmentList(final Project project,
                                                                                     final List<Skill> jobRoleSkills) {
        Map<AccountModel, Set<Employee>> contractorEmployeeMap;
        return ViewModeFactory.getEmployeeProjectRoleAssignmentFactory().create(contractorEmployeeMap, jobRoleSkills);
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
