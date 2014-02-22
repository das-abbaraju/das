package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.factory.*;

public class ViewModelFactory {

	private static ContractorEmployeeRoleAssignmentMatrixFactory contractorEmployeeRoleAssignmentMatrixFactory =
			new ContractorEmployeeRoleAssignmentMatrixFactory();
	private static ContractorEmployeeRoleAssignmentFactory contractorEmployeeRoleAssignmentFactory =
			new ContractorEmployeeRoleAssignmentFactory();
	private static ContractorProjectFormFactory contractorProjectFormFactory = new ContractorProjectFormFactory();
	private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
	private static EmployeeModelFactory employeeModelFactory = new EmployeeModelFactory();
	private static EmployeeNavFactory employeeNavFactory = new EmployeeNavFactory();
	private static EmployeeProjectAssignmentFactory employeeProjectAssignmentFactory = new EmployeeProjectAssignmentFactory();
	private static EmployeeProjectRoleAssignmentFactory employeeProjectRoleAssignmentFactory = new EmployeeProjectRoleAssignmentFactory();
	private static EmployeeSiteAssignmentModelFactory employeeSiteAssignmentModelFactory = new EmployeeSiteAssignmentModelFactory();
	private static EntityInfoFactory entityInfoFactory = new EntityInfoFactory();
	private static IdNameTitleModelFactory idNameTitleModelFactory = new IdNameTitleModelFactory();
	private static NavItemFactory navItemFactory = new NavItemFactory();
	private static OperatorEmployeeModelFactory operatorEmployeeModelFactory = new OperatorEmployeeModelFactory();
	private static OperatorEmployeeSkillModelFactory operatorEmployeeSkillModelFactory = new OperatorEmployeeSkillModelFactory();
	private static OperatorProjectAssignmentFactory operatorProjectAssignmentFactory = new OperatorProjectAssignmentFactory();
	private static OperatorProjectRoleAssignmentFactory operatorProjectRoleAssignmentFactory = new OperatorProjectRoleAssignmentFactory();
	private static OperatorSiteAssignmentModelFactory operatorSiteAssignmentModelFactory = new OperatorSiteAssignmentModelFactory();
	private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
	private static ProjectDetailModelFactory projectDetailModelFactory = new ProjectDetailModelFactory();
	private static RoleInfoFactory roleInfoFactory = new RoleInfoFactory();
	private static RoleEmployeeCountFactory roleEmployeeCountFactory = new RoleEmployeeCountFactory();
	private static RoleModelFactory roleModelFactory = new RoleModelFactory();
	private static SkillModelFactory skillModelFactory = new SkillModelFactory();
	private static SiteAssignmentModelFactory siteAssignmentModelFactory = new SiteAssignmentModelFactory();
	private static SiteAssignmentsAndProjectsFactory siteAssignmentsAndProjectsFactory = new SiteAssignmentsAndProjectsFactory();

	public static ContractorEmployeeRoleAssignmentMatrixFactory getContractorEmployeeRoleAssignmentMatrixFactory() {
		return contractorEmployeeRoleAssignmentMatrixFactory;
	}

	public static ContractorEmployeeRoleAssignmentFactory getContractorEmployeeRoleAssignmentFactory() {
		return contractorEmployeeRoleAssignmentFactory;
	}

	public static ContractorProjectFormFactory getContractorProjectFormFactory() {
		return contractorProjectFormFactory;
	}

	public static EmployeeAssignmentModelFactory getEmployeeAssignmentModelFactory() {
		return employeeAssignmentModelFactory;
	}

	public static EmployeeModelFactory getEmployeeModelFactory() {
		return employeeModelFactory;
	}

	public static EmployeeProjectAssignmentFactory getEmployeeProjectAssignmentFactory() {
		return employeeProjectAssignmentFactory;
	}

	public static EmployeeProjectRoleAssignmentFactory getEmployeeProjectRoleAssignmentFactory() {
		return employeeProjectRoleAssignmentFactory;
	}

	public static EmployeeNavFactory getEmployeeNavFactory() {
		return employeeNavFactory;
	}

	public static EmployeeSiteAssignmentModelFactory getEmployeeSiteAssignmentModelFactory() {
		return employeeSiteAssignmentModelFactory;
	}

	public static EntityInfoFactory getEntityInfoFactory() {
		return entityInfoFactory;
	}

	public static IdNameTitleModelFactory getIdNameTitleModelFactory() {
		return idNameTitleModelFactory;
	}

	public static NavItemFactory getNavItemFactory() {
		return navItemFactory;
	}

	public static OperatorEmployeeModelFactory getOperatorEmployeeModelFactory() {
		return operatorEmployeeModelFactory;
	}

	public static OperatorEmployeeSkillModelFactory getOperatorEmployeeSkillModelFactory() {
		return operatorEmployeeSkillModelFactory;
	}

	public static OperatorProjectAssignmentFactory getOperatorProjectAssignmentFactory() {
		return operatorProjectAssignmentFactory;
	}

	public static OperatorProjectRoleAssignmentFactory getOperatorProjectRoleAssignmentFactory() {
		return operatorProjectRoleAssignmentFactory;
	}

	public static OperatorSiteAssignmentModelFactory getOperatorSiteAssignmentModelFactory() {
		return operatorSiteAssignmentModelFactory;
	}

	public static ProjectAssignmentBreakdownFactory getProjectAssignmentBreakdownFactory() {
		return projectAssignmentBreakdownFactory;
	}

	public static ProjectDetailModelFactory getProjectDetailModelFactory() {
		return projectDetailModelFactory;
	}

	public static RoleInfoFactory getRoleInfoFactory() {
		return roleInfoFactory;
	}

	public static RoleEmployeeCountFactory getRoleEmployeeCountFactory() {
		return roleEmployeeCountFactory;
	}

	public static RoleModelFactory getRoleModelFactory() {
		return roleModelFactory;
	}

	public static SkillModelFactory getSkillModelFactory() {
		return skillModelFactory;
	}

	public static SiteAssignmentModelFactory getContractorSiteAssignmentModelFactory() {
		return siteAssignmentModelFactory;
	}

	public static SiteAssignmentsAndProjectsFactory getSiteAssignmentsAndProjectsFactory() {
		return siteAssignmentsAndProjectsFactory;
	}
}
