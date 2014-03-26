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
	private static ProfileEmployerModelFactory profileEmployerModelFactory = new ProfileEmployerModelFactory();
	private static ProfileModelFactory profileModelFactory = new ProfileModelFactory();
	private static ProfileProjectModelFactory profileProjectModelFactory = new ProfileProjectModelFactory();
	private static ProfileSiteModelFactory profileSiteModelFactory = new ProfileSiteModelFactory();
	private static ProjectAssignmentModelFactory projectAssignmentModelFactory = new ProjectAssignmentModelFactory();
	private static ProjectModelFactory projectModelFactory = new ProjectModelFactory();
	private static ProjectStatusModelFactory projectStatusModelFactory = new ProjectStatusModelFactory();
	private static RoleModelFactory roleModelFactory = new RoleModelFactory();
	private static RoleStatusModelFactory roleStatusModelFactory = new RoleStatusModelFactory();
	private static SkillModelFactory skillModelFactory = new SkillModelFactory();
	private static SkillStatusModelFactory skillStatusModelFactory = new SkillStatusModelFactory();
	private static StatusSummaryFactory statusSummaryFactory = new StatusSummaryFactory();

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

	public static ProfileEmployerModelFactory getProfileEmployerModelFactory() {
		return profileEmployerModelFactory;
	}

	public static ProfileModelFactory getProfileModelFactory() {
		return profileModelFactory;
	}

	public static ProfileProjectModelFactory getProfileProjectModelFactory() {
		return profileProjectModelFactory;
	}

	public static ProfileSiteModelFactory getProfileSiteModelFactory() {
		return profileSiteModelFactory;
	}

	public static ProjectAssignmentModelFactory getProjectAssignmentModelFactory() {
		return projectAssignmentModelFactory;
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

	public static SkillModelFactory getSkillModelFactory() {
		return skillModelFactory;
	}

	public static SkillStatusModelFactory getSkillStatusModelFactory() {
		return skillStatusModelFactory;
	}

	public static StatusSummaryFactory getStatusSummaryFactory() {
		return statusSummaryFactory;
	}
}
