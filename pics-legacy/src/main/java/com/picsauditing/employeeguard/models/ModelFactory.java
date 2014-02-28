package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.models.factories.*;

public class ModelFactory {

	private static CompanyEmployeeModelFactory companyEmployeeModelFactory = new CompanyEmployeeModelFactory();
	private static CompanyEmployeeStatusModelFactory companyEmployeeStatusModelFactory = new CompanyEmployeeStatusModelFactory();
	private static CompanyModelFactory companyModelFactory = new CompanyModelFactory();
	private static EmploymentInfoModelFactory employmentInfoModelFactory = new EmploymentInfoModelFactory();
	private static ProjectModelFactory projectModelFactory = new ProjectModelFactory();
	private static ProjectStatusModelFactory projectStatusModelFactory = new ProjectStatusModelFactory();
	private static RoleModelFactory roleModelFactory = new RoleModelFactory();
	private static RoleStatusModelFactory roleStatusModelFactory = new RoleStatusModelFactory();
	private static SkillModelFactory skillModelFactory = new SkillModelFactory();
	private static SkillStatusModelFactory skillStatusModelFactory = new SkillStatusModelFactory();

	public static CompanyEmployeeModelFactory getCompanyEmployeeModelFactory() {
		return companyEmployeeModelFactory;
	}

	public static CompanyEmployeeStatusModelFactory getCompanyEmployeeStatusModelFactory() {
		return companyEmployeeStatusModelFactory;
	}

	public static CompanyModelFactory getCompanyModelFactory() {
		return companyModelFactory;
	}

	public static EmploymentInfoModelFactory getEmploymentInfoModelFactory() {
		return employmentInfoModelFactory;
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
}
