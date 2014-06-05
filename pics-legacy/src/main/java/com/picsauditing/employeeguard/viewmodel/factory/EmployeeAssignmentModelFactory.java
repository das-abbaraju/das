package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.entities.ProjectRole;
import com.picsauditing.employeeguard.models.AccountModel;
import com.picsauditing.employeeguard.viewmodel.contractor.EmployeeAssignmentModel;
import com.picsauditing.employeeguard.viewmodel.contractor.ProjectModel;

import java.util.*;

public class EmployeeAssignmentModelFactory {

    public List<EmployeeAssignmentModel> create(final Set<Project> projects, Map<Integer, AccountModel> accountModels) {
        List<EmployeeAssignmentModel> assignments = new ArrayList<>();
        Map<Integer, List<ProjectModel>> projectAssignments = getCompanyProjectAssignments(projects);
        for (Map.Entry<Integer, List<ProjectModel>> projectAssignment : projectAssignments.entrySet()) {
            EmployeeAssignmentModel employeeAssignmentModel = new EmployeeAssignmentModel();
            employeeAssignmentModel.setSiteId(projectAssignment.getKey());
            employeeAssignmentModel.setSiteName(accountModels.get(projectAssignment.getKey()).getName());
            employeeAssignmentModel.setProjects(projectAssignment.getValue());
            assignments.add(employeeAssignmentModel);
        }

        return assignments;
    }

    private Map<Integer, List<ProjectModel>> getCompanyProjectAssignments(final Set<Project> projects) {
        Map<Integer, List<ProjectModel>> assignments = new HashMap<>();
        for (Project project : projects) {
            int siteId = project.getAccountId();
            if (!assignments.containsKey(siteId)) {
                assignments.put(siteId, new ArrayList<ProjectModel>());
            }

            assignments.get(siteId).add(new ProjectModel(project.getId(), project.getName()));
        }

        return assignments;
    }
}
