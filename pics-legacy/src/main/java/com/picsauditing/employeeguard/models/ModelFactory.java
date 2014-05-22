package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.models.factories.*;

public class ModelFactory {

	private static CompanyEmployeeModelFactory companyEmployeeModelFactory = new CompanyEmployeeModelFactory();
	private static CompanyEmployeeStatusModelFactory companyEmployeeStatusModelFactory = new CompanyEmployeeStatusModelFactory();
	private static CompanyModelFactory companyModelFactory = new CompanyModelFactory();
	private static ContractorSummaryFactory contractorSummaryFactory = new ContractorSummaryFactory();
	private static EmploymentInfoModelFactory employmentInfoModelFactory = new EmploymentInfoModelFactory();
	private static OperatorSiteAssignmentStatusFactory operatorSiteAssignmentStatusFactory = new OperatorSiteAssignmentStatusFactory();
	private static ProfileAssignmentModelFactory profileAssignmentModelFactory = new ProfileAssignmentModelFactory();
	private static ProfileModelFactory profileModelFactory = new ProfileModelFactory();
	private static ProjectAssignmentModelFactory projectAssignmentModelFactory = new ProjectAssignmentModelFactory();
	private static ProjectAssignmentBreakdownFactory projectAssignmentBreakdownFactory = new ProjectAssignmentBreakdownFactory();
	private static ProjectModelFactory projectModelFactory = new ProjectModelFactory();
	private static ProjectStatusModelFactory projectStatusModelFactory = new ProjectStatusModelFactory();
	private static RoleModelFactory roleModelFactory = new RoleModelFactory();
	private static RoleStatusModelFactory roleStatusModelFactory = new RoleStatusModelFactory();
	private static SiteAssignmentsAndProjectsFactory siteAssignmentsAndProjectsFactory = new SiteAssignmentsAndProjectsFactory();
	private static SkillModelFactory skillModelFactory = new SkillModelFactory();
	private static SkillStatusModelFactory skillStatusModelFactory = new SkillStatusModelFactory();
	private static StatusSummaryFactory statusSummaryFactory = new StatusSummaryFactory();
	private static UserModelFactory userModelFactory = new UserModelFactory();
	private static LiveIDEmployeeModelFactory liveIDEmployeeModelFactory = new LiveIDEmployeeModelFactory();
	private static OperatorEmployeeModelFactory operatorEmployeeModelFactory = new OperatorEmployeeModelFactory();
	private static EmployeeSkillsModelFactory employeeSkillsModelFactory = new EmployeeSkillsModelFactory();
	private static CompanyStatusModelFactory companyStatusModelFactory = new CompanyStatusModelFactory();
	private static CompanyProjectModelFactory companyProjectModelFactory = new CompanyProjectModelFactory();

	public static LiveIDEmployeeModelFactory getLiveIDEmployeeModelFactory() {
		return liveIDEmployeeModelFactory;
	}

	public static CompanyEmployeeModelFactory getCompanyEmployeeModelFactory() {
		return companyEmployeeModelFactory;
	}

	public static CompanyEmployeeStatusModelFactory getCompanyEmployeeStatusModelFactory() {
		return companyEmployeeStatusModelFactory;
	}

	public static CompanyModelFactory getCompanyModelFactory() {
		return companyModelFactory;
	}

	public static ContractorSummaryFactory getContractorSummaryFactory() {
		return contractorSummaryFactory;
	}

	public static EmploymentInfoModelFactory getEmploymentInfoModelFactory() {
		return employmentInfoModelFactory;
	}

	public static OperatorSiteAssignmentStatusFactory getOperatorSiteAssignmentStatusFactory() {
		return operatorSiteAssignmentStatusFactory;
	}

	public static ProfileAssignmentModelFactory getProfileAssignmentModelFactory() {
		return profileAssignmentModelFactory;
	}

	public static ProfileModelFactory getProfileModelFactory() {
		return profileModelFactory;
	}

	public static ProjectAssignmentModelFactory getProjectAssignmentModelFactory() {
		return projectAssignmentModelFactory;
	}

	public static ProjectAssignmentBreakdownFactory getProjectAssignmentBreakdownFactory() {
		return projectAssignmentBreakdownFactory;
	}

	public static ProjectModelFactory getProjectModelFactory() {
		return projectModelFactory;
	}

	public static ProjectStatusModelFactory getProjectStatusModelFactory() {
		return projectStatusModelFactory;
	}

	public static RoleModelFactory getRoleModelFactory() {
		return roleModelFactory;
	}

	public static RoleStatusModelFactory getRoleStatusModelFactory() {
		return roleStatusModelFactory;
	}

	public static SiteAssignmentsAndProjectsFactory getSiteAssignmentsAndProjectsFactory() {
		return siteAssignmentsAndProjectsFactory;
	}

	public static SkillModelFactory getSkillModelFactory() {
		return skillModelFactory;
	}

	public static SkillStatusModelFactory getSkillStatusModelFactory() {
		return skillStatusModelFactory;
	}

	public static StatusSummaryFactory getStatusSummaryFactory() {
		return statusSummaryFactory;
	}

	public static UserModelFactory getUserModelFactory() {
		return userModelFactory;
	}

	public static OperatorEmployeeModelFactory getOperatorEmployeeModelFactory() {
		return operatorEmployeeModelFactory;
	}

	public static EmployeeSkillsModelFactory getEmployeeSkillsModelFactory() {
		return employeeSkillsModelFactory;
	}

	public static CompanyStatusModelFactory getCompanyStatusModelFactory() {
		return companyStatusModelFactory;
	}

	public static CompanyProjectModelFactory getCompanyProjectModelFactory() {
		return companyProjectModelFactory;
	}
}
