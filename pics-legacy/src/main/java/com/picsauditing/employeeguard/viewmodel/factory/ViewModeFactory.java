package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.factory.ContractorProjectFormFactory;


public class ViewModeFactory {

    private static ContractorProjectFormFactory contractorProjectFormFactory = new ContractorProjectFormFactory();
    private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
    private static EmployeeProjectAssignmentFactory employeeProjectAssignmentFactory = new EmployeeProjectAssignmentFactory();
    private static EmployeeProjectRoleAssignmentFactory employeeProjectRoleAssignmentFactory = new EmployeeProjectRoleAssignmentFactory();
    private static OperatorProjectAssignmentFactory operatorProjectAssignmentFactory = new OperatorProjectAssignmentFactory();
    private static OperatorProjectRoleAssignmentFactory operatorProjectRoleAssignmentFactory = new OperatorProjectRoleAssignmentFactory();
    private static EmployeeSiteAssignmentModelFactory employeeSiteAssignmentModelFactory = new EmployeeSiteAssignmentModelFactory();
    private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
    private static SkillModelFactory skillModelFactory = new SkillModelFactory();
    private static SiteAssignmentModelFactory siteAssignmentModelFactory = new SiteAssignmentModelFactory();
    private static SiteAssignmentsAndProjectsFactory siteAssignmentsAndProjectsFactory = new SiteAssignmentsAndProjectsFactory();

    public static ContractorProjectFormFactory getContractorProjectFormFactory() {
        return contractorProjectFormFactory;
    }

    public static EmployeeAssignmentModelFactory getEmployeeAssignmentModelFactory() {
        return employeeAssignmentModelFactory;
    }

    public static EmployeeSiteAssignmentModelFactory getEmployeeSiteAssignmentModelFactory() {
        return employeeSiteAssignmentModelFactory;
    }

    public static SkillModelFactory getSkillModelFactory() {
        return skillModelFactory;
    }

    public static ProjectAssignmentBreakdownFactory getProjectAssignmentBreakdownFactory() {
        return projectAssignmentBreakdownFactory;
    }

    public static EmployeeProjectAssignmentFactory getEmployeeProjectAssignmentFactory() {
        return employeeProjectAssignmentFactory;
    }

    public static EmployeeProjectRoleAssignmentFactory getEmployeeProjectRoleAssignmentFactory() {
        return employeeProjectRoleAssignmentFactory;
    }

    public static OperatorProjectAssignmentFactory getOperatorProjectAssignmentFactory() {
        return operatorProjectAssignmentFactory;
    }

    public static OperatorProjectRoleAssignmentFactory getOperatorProjectRoleAssignmentFactory() {
        return operatorProjectRoleAssignmentFactory;
    }

    public static SiteAssignmentModelFactory getSiteAssignmentModelFactory() {
        return siteAssignmentModelFactory;
    }

    public static SiteAssignmentsAndProjectsFactory getSiteAssignmentsAndProjectsFactory() {
        return siteAssignmentsAndProjectsFactory;
    }
}
