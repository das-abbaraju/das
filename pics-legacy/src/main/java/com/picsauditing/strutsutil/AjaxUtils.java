package com.picsauditing.strutsutil;

import javax.servlet.http.HttpServletRequest;

public class AjaxUtils {

	public static final String AJAX_REQUEST_HEADER = "XMLHttpRequest";

	public static boolean isAjax(HttpServletRequest request) {
		return AJAX_REQUEST_HEADER.equalsIgnoreCase(request.getHeader("X-Requested-With"));
	}

}
