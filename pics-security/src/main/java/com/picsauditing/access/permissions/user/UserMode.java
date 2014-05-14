package com.picsauditing.access.permissions.user;

public enum UserMode {

	ADMIN/*("/Home.action", "admin")*/,                             // Default mode for PICSORG Users
	EMPLOYEE/*("/employee-guard/employee/dashboard", "employee")*/; // For EmployeeGUARD Employee

//	private String homeUrl;
//	private String mode;
//
//	private UserMode(final String homeUrl, final String mode) {
//		this.homeUrl = homeUrl;
//		this.mode = mode;
//	}
//
//	public String getHomeUrl() {
//		return homeUrl;
//	}
//
//	public String getModeParameterValue() {
//		return mode;
//	}
//
//	public static UserMode mapUserModeFromValue(final String value) {
//		for (UserMode userMode : UserMode.values()) {
//			if (userMode.mode.equals(value)) {
//				return userMode;
//			}
//		}
//
//		throw new RuntimeException("No UserMode found");
//	}
//
}
