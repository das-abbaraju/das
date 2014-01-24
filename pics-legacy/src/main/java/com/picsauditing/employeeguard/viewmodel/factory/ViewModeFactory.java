package com.picsauditing.employeeguard.viewmodel.factory;

public class ViewModeFactory {

    private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
    private static SkillModelFactory skillModelFactory = new SkillModelFactory();
    private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
    private static EmployeeProjectAssignmentFactory employeeProjectAssignmentFactory = new EmployeeProjectAssignmentFactory();
    private static EmployeeProjectRoleAssignmentFactory employeeProjectRoleAssignmentFactory = new EmployeeProjectRoleAssignmentFactory();
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
}
