package com.picsauditing.employeeguard.util;

public final class EmployeeGUARDUrlUtils {

	/* Corporate and Operator Links */
	public static final String OPERATOR_SUMMARY = "/employee-guard/operators/dashboard";
	public static final String OPERATOR_ASSIGNMENTS = "/employee-guard/operators/assignments";
	public static final String OPERATOR_PROJECTS = "/employee-guard/operators/projects";
	public static final String OPERATOR_JOB_ROLES = "/employee-guard/operators/role";
	public static final String OPERATOR_SKILLS = "/employee-guard/operators/skill";
	public static final String OPERATOR_PROJECT_CREATE = "/employee-guard/operators/project/create";

	/* Contractor Links */
	public static final String CONTRACTOR_SUMMARY = "/employee-guard/contractor/dashboard";
	public static final String CONTRACTOR_ASSIGNMENTS = "/employee-guard/contractor/project";
	public static final String CONTRACTOR_GROUPS = "/employee-guard/contractor/employee-group";
	public static final String CONTRACTOR_SKILLS = "/employee-guard/contractor/skill";
	public static final String CONTRACTOR_EMPLOYEES = "/employee-guard/contractor/employee";
	public static final String CONTRACTOR_EMPLOYEE_IMPORT = "/contractor/employee/import-export";

	/* Employee Links */
	public static final String IMAGE_LINK = "/employee-guard/employee/contractor/%d/employee-photo/%d";
	public static final String EMPLOYEE_PROFILE = "/employee-guard/employee/profile/%d";
	public static final String EMPLOYEE_SUMMARY = "/employee-guard/employee/dashboard";
	public static final String EMPLOYEE_MY_FILES = "/employee-guard/employee/file";
	public static final String EMPLOYEE_SKILLS = "/employee-guard/employee/skills";

	public static String buildUrl(final String url, final Object... params) {
		return String.format(url, params);
	}

}
