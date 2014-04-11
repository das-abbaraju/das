package com.picsauditing.employeeguard.viewmodel.factory;

import com.picsauditing.employeeguard.forms.factory.*;
import com.picsauditing.employeeguard.models.factories.CompanyEmployeeModelFactory;
import com.picsauditing.employeeguard.models.factories.ProjectModelFactory;

public class ViewModelFactory {

	private static CompanyEmployeeModelFactory companyEmployeeModelFactory = new CompanyEmployeeModelFactory();
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
	private static OperatorProjectAssignmentFactory operatorProjectAssignmentFactory = new OperatorProjectAssignmentFactory();
	private static OperatorProjectRoleAssignmentFactory operatorProjectRoleAssignmentFactory = new OperatorProjectRoleAssignmentFactory();
	private static OperatorSiteAssignmentModelFactory operatorSiteAssignmentModelFactory = new OperatorSiteAssignmentModelFactory();
	private static ProjectModelFactory projectModelFactory = new ProjectModelFactory();
	private static RoleInfoFactory roleInfoFactory = new RoleInfoFactory();
	private static RoleEmployeeCountFactory roleEmployeeCountFactory = new RoleEmployeeCountFactory();
	private static RoleModelFactory roleModelFactory = new RoleModelFactory();
	private static SkillModelFactory skillModelFactory = new SkillModelFactory();
	private static SiteAssignmentModelFactory siteAssignmentModelFactory = new SiteAssignmentModelFactory();

	public static CompanyEmployeeModelFactory getCompanyEmployeeModelFactory() {
		return companyEmployeeModelFactory;
	}

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

	public static OperatorProjectAssignmentFactory getOperatorProjectAssignmentFactory() {
		return operatorProjectAssignmentFactory;
	}

	public static OperatorProjectRoleAssignmentFactory getOperatorProjectRoleAssignmentFactory() {
		return operatorProjectRoleAssignmentFactory;
	}

	public static OperatorSiteAssignmentModelFactory getOperatorSiteAssignmentModelFactory() {
		return operatorSiteAssignmentModelFactory;
	}

	public static ProjectModelFactory getProjectModelFactory() {
		return projectModelFactory;
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
}
