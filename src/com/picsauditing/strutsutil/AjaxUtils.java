package com.picsauditing.strutsutil;

import javax.servlet.http.HttpServletRequest;

public class AjaxUtils {
	public static boolean isAjax(HttpServletRequest request) {
		return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
	}
}
