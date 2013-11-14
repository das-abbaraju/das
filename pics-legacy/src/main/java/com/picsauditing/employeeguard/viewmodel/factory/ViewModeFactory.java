package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.viewmodel.contractor.SkillModel;

public class ViewModeFactory {

    private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
    private static SkillModelFactory skillModelFactory = new SkillModelFactory();
    private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();

    public static EmployeeAssignmentModelFactory getEmployeeAssignmentModelFactory() {
        return employeeAssignmentModelFactory;
    }

    public static SkillModelFactory getSkillModelFactory() {
        return skillModelFactory;
    }

    public static ProjectAssignmentBreakdownFactory getProjectAssignmentBreakdownFactory() {
        return projectAssignmentBreakdownFactory;
    }
}
