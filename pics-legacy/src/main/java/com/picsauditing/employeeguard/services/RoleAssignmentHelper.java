package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.ProjectRoleEmployeeDAO;
import com.picsauditing.employeeguard.daos.RoleEmployeeDAO;
import com.picsauditing.employeeguard.entities.ProjectRoleEmployee;
import com.picsauditing.employeeguard.entities.RoleEmployee;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RoleAssignmentHelper {

    @Autowired
    private ProjectRoleEmployeeDAO projectRoleEmployeeDAO;
    @Autowired
    private RoleEmployeeDAO roleEmployeeDAO;

    public void deleteProjectRolesFromEmployee(int employeeId, int siteId) {
        List<ProjectRoleEmployee> projectRoleEmployees = projectRoleEmployeeDAO.findByEmployeeAndSiteId(employeeId, siteId);
        projectRoleEmployeeDAO.delete(projectRoleEmployees);
    }

    public void deleteSiteRolesFromEmployee(int employeeId, int siteId) {
        List<RoleEmployee> roleEmployees = roleEmployeeDAO.findByEmployeeAndSiteId(employeeId, siteId);
        roleEmployeeDAO.delete(roleEmployees);
    }
}
