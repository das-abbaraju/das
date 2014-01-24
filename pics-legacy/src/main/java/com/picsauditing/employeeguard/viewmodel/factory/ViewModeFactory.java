package com.picsauditing.employeeguard.viewmodel.factory;

public class ViewModeFactory {

    private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
    private static SkillModelFactory skillModelFactory = new SkillModelFactory();
    private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
    private static EmployeeSiteAssignmentFactory employeeSiteAssignmentFactory = new EmployeeSiteAssignmentFactory();
    private static EmployeeSiteRoleAssignmentFactory employeeSiteRoleAssignmentFactory = new EmployeeSiteRoleAssignmentFactory();
    private static OperatorProjectAssignmentFactory operatorProjectAssignmentFactory = new OperatorProjectAssignmentFactory();
    private static OperatorProjectRoleAssignmentFactory operatorProjectRoleAssignmentFactory = new OperatorProjectRoleAssignmentFactory();

    public static EmployeeAssignmentModelFactory getEmployeeAssignmentModelFactory() {
        return employeeAssignmentModelFactory;
    }

    public static SkillModelFactory getSkillModelFactory() {
        return skillModelFactory;
    }

    public static ProjectAssignmentBreakdownFactory getProjectAssignmentBreakdownFactory() {
        return projectAssignmentBreakdownFactory;
    }

    public static EmployeeSiteAssignmentFactory getEmployeeSiteAssignmentFactory() {
        return employeeSiteAssignmentFactory;
    }

    public static EmployeeSiteRoleAssignmentFactory getEmployeeSiteRoleAssignmentFactory() {
        return employeeSiteRoleAssignmentFactory;
    }

    public static OperatorProjectAssignmentFactory getOperatorProjectAssignmentFactory() {
        return operatorProjectAssignmentFactory;
    }

    public static OperatorProjectRoleAssignmentFactory getOperatorProjectRoleAssignmentFactory() {
        return operatorProjectRoleAssignmentFactory;
    }
}
