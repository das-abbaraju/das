package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.factory.*;
import com.picsauditing.employeeguard.viewmodel.operator.factory.OperatorSiteAssignmentModelFactory;

public class ViewModelFactory {

	private static ContractorEmployeeRoleAssignmentMatrixFactory contractorEmployeeRoleAssignmentMatrixFactory =
			new ContractorEmployeeRoleAssignmentMatrixFactory();
	private static ContractorEmployeeRoleAssignmentFactory contractorEmployeeRoleAssignmentFactory =
			new ContractorEmployeeRoleAssignmentFactory();
	private static ContractorProjectFormFactory contractorProjectFormFactory = new ContractorProjectFormFactory();
	private static EmployeeAssignmentModelFactory employeeAssignmentModelFactory = new EmployeeAssignmentModelFactory();
	private static EmployeeModelFactory employeeModelFactory = new EmployeeModelFactory();
	private static EmployeeProjectAssignmentFactory employeeProjectAssignmentFactory = new EmployeeProjectAssignmentFactory();
	private static EmployeeProjectRoleAssignmentFactory employeeProjectRoleAssignmentFactory = new EmployeeProjectRoleAssignmentFactory();
	private static EmployeeSiteAssignmentModelFactory employeeSiteAssignmentModelFactory = new EmployeeSiteAssignmentModelFactory();
	private static EntityInfoFactory entityInfoFactory = new EntityInfoFactory();
	private static OperatorProjectAssignmentFactory operatorProjectAssignmentFactory = new OperatorProjectAssignmentFactory();
	private static OperatorProjectRoleAssignmentFactory operatorProjectRoleAssignmentFactory = new OperatorProjectRoleAssignmentFactory();
	private static OperatorSiteAssignmentModelFactory operatorSiteAssignmentModelFactory = new OperatorSiteAssignmentModelFactory();
	private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
	private static RoleInfoFactory roleInfoFactory = new RoleInfoFactory();
	private static RoleEmployeeCountFactory roleEmployeeCountFactory = new RoleEmployeeCountFactory();
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

	public static EmployeeSiteAssignmentModelFactory getEmployeeSiteAssignmentModelFactory() {
		return employeeSiteAssignmentModelFactory;
	}

	public static EntityInfoFactory getEntityInfoFactory() {
		return entityInfoFactory;
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

	public static RoleInfoFactory getRoleInfoFactory() {
		return roleInfoFactory;
	}

	public static RoleEmployeeCountFactory getRoleEmployeeCountFactory() {
		return roleEmployeeCountFactory;
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
