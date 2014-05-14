package com.picsauditing.strutsutil;

import javax.servlet.http.HttpServletRequest;

public class AjaxUtils {

    public static final String HTTP_HEADER_X_REQUESTED_WITH = "X-Requested-With";

	public static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";

    public static boolean isAjax(HttpServletRequest request) {
		return AJAX_REQUEST_HEADER_VALUE.equalsIgnoreCase(request.getHeader(HTTP_HEADER_X_REQUESTED_WITH));
	}

}
