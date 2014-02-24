package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.models.factories.*;

public class ModelFactory {

	private static CompanyEmployeeModelFactory companyEmployeeStatusModelFactory = new CompanyEmployeeModelFactory();
	private static CompanyModelFactory companyModelFactory = new CompanyModelFactory();
	private static ProjectModelFactory projectModelFactory = new ProjectModelFactory();
	private static RoleModelFactory roleModelFactory = new RoleModelFactory();
	private static SkillModelFactory skillModelFactory = new SkillModelFactory();
	private static SkillStatusModelFactory skillStatusModelFactory = new SkillStatusModelFactory();

	public static CompanyEmployeeModelFactory getCompanyEmployeeStatusModelFactory() {
		return companyEmployeeStatusModelFactory;
	}

	public static CompanyModelFactory getCompanyModelFactory() {
		return companyModelFactory;
	}

	public static ProjectModelFactory getProjectModelFactory() {
		return projectModelFactory;
	}

	public static RoleModelFactory getRoleModelFactory() {
		return roleModelFactory;
	}

	public static SkillModelFactory getSkillModelFactory() {
		return skillModelFactory;
	}

	public static SkillStatusModelFactory getSkillStatusModelFactory() {
		return skillStatusModelFactory;
	}
}
