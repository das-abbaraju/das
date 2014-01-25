package com.picsauditing.employeeguard.viewmodel.factory;

public class ViewModeFactory {

    private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
    private static SkillModelFactory skillModelFactory = new SkillModelFactory();
    private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
	private static SiteAssignmentsAndProjectsFactory siteAssignmentsAndProjectsFactory = new SiteAssignmentsAndProjectsFactory();

    public static EmployeeAssignmentModelFactory getEmployeeAssignmentModelFactory() {
        return employeeAssignmentModelFactory;
    }

    public static SkillModelFactory getSkillModelFactory() {
        return skillModelFactory;
    }

    public static ProjectAssignmentBreakdownFactory getProjectAssignmentBreakdownFactory() {
        return projectAssignmentBreakdownFactory;
    }

	public static SiteAssignmentsAndProjectsFactory getSiteAssignmentsAndProjectsFactory() {
		return siteAssignmentsAndProjectsFactory;
	}
}
