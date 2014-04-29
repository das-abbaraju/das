package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAssignmentModelFactory {

    public List<EmployeeAssignmentModel> create(List<ProjectRole> projectRoles, Map<Integer, AccountModel> accountModels) {
        List<EmployeeAssignmentModel> assignments = new ArrayList<>();
        Map<Integer, List<ProjectModel>> projectAssignments = getCompanyProjectAssignments(projectRoles);
        for (Map.Entry<Integer, List<ProjectModel>> projectAssignment : projectAssignments.entrySet()) {
            EmployeeAssignmentModel employeeAssignmentModel = new EmployeeAssignmentModel();
            employeeAssignmentModel.setSiteId(projectAssignment.getKey());
            employeeAssignmentModel.setSiteName(accountModels.get(projectAssignment.getKey()).getName());
            employeeAssignmentModel.setProjects(projectAssignment.getValue());
            assignments.add(employeeAssignmentModel);
        }

        return assignments;
    }

    private Map<Integer, List<ProjectModel>> getCompanyProjectAssignments(List<ProjectRole> projectRoles) {
        Map<Integer, List<ProjectModel>> assignments = new HashMap<>();
        for (ProjectRole projectRole : projectRoles) {
            int siteId = projectRole.getProject().getAccountId();
            if (!assignments.containsKey(siteId)) {
                assignments.put(siteId, new ArrayList<ProjectModel>());
            }

            Project project = projectRole.getProject();
            assignments.get(siteId).add(new ProjectModel(project.getId(), project.getName()));
        }

        return assignments;
    }
}
