package com.picsauditing.employeeguard.util;

public final class EmployeeGUARDUrlUtils {

	public static final String IMAGE_LINK = "/employee-guard/employee/contractor/%d/employee-photo/%d";

	public static String buildUrl(final String url, final Object... params) {
		return String.format(url, params);
	}

}
