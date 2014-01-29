package com.picsauditing.employeeguard.controllers.operator;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.forms.IdentifierAndNameCompositeForm;
import com.picsauditing.employeeguard.forms.operator.ContractorRoleInfo;
import com.picsauditing.employeeguard.forms.operator.ProjectRoleAssignment;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import com.picsauditing.employeeguard.services.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.model.Skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SiteAssignmentAction extends PicsRestActionSupport {

    private static final long serialVersionUID = 1288428610452669599L;

    int roleId;

    private List<ProjectRoleAssignment> projectRoleAssignments;

	public String index() {
		return LIST;
	}

    public String show() {
        projectRoleAssignments = buildFakeProjectRoleAssignments();
        return SHOW;
    }

    private List<ProjectRoleAssignment> buildFakeProjectRoleAssignments() {
        List<ProjectRoleAssignment> projectRoleAssignments = new ArrayList<>();
        projectRoleAssignments.add(buildProjectRoleAssignment(1, "Required Skills"));
        projectRoleAssignments.add(buildProjectRoleAssignment(2, "Master Welder"));
        projectRoleAssignments.add(buildProjectRoleAssignment(3, "Plumber"));
        return projectRoleAssignments;
    }

    private ProjectRoleAssignment buildProjectRoleAssignment(int roleId, String roleName) {
        ProjectRoleAssignment projectRoleAssignment = new ProjectRoleAssignment();
        projectRoleAssignment.setRole(buildRole(roleId, roleName));
        projectRoleAssignment.setContractorRoleInfo(buildContractorRoleInfoList());

        return projectRoleAssignment;
    }

    private IdentifierAndNameCompositeForm buildRole(int roleId, String roleName) {
        IdentifierAndNameCompositeForm identifierAndNameCompositeForm = new IdentifierAndNameCompositeForm();
        identifierAndNameCompositeForm.setId(roleId);
        identifierAndNameCompositeForm.setName(roleName);
        return identifierAndNameCompositeForm;
    }

    private List<ContractorRoleInfo> buildContractorRoleInfoList() {
        List<ContractorRoleInfo> contractorRoleInfo = new ArrayList<>();
        contractorRoleInfo.add(buildContractorRoleInfo(1, "Bob Jackson"));
        contractorRoleInfo.add(buildContractorRoleInfo(2, "Jim Smith"));
        contractorRoleInfo.add(buildContractorRoleInfo(3, "Jill Johnson"));
        return contractorRoleInfo;
    }

    private ContractorRoleInfo buildContractorRoleInfo(int employeeId, String employeeName) {
        ContractorRoleInfo contractorRoleInfo = new ContractorRoleInfo();
        contractorRoleInfo.setAccountModel(new AccountModel.Builder().id(1).name("PICS").build());
        contractorRoleInfo.setEmployeeInfo(new IdentifierAndNameCompositeForm(employeeId, employeeName));
        contractorRoleInfo.setSkillList(Arrays.asList(buildSkill(1, "Mechanic", SkillStatus.Complete),
                buildSkill(2, "Fireman", SkillStatus.Expired), buildSkill(3, "Manager", SkillStatus.Expiring)));
        return contractorRoleInfo;
    }

    private Skill buildSkill(int id, String name, SkillStatus skillStatus) {
        return new Skill.Builder()
                .id(id)
                .name(name)
                .skillStatus(skillStatus)
                .build();
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public List<ProjectRoleAssignment> getProjectRoleAssignments() {
        return projectRoleAssignments;
    }
}
