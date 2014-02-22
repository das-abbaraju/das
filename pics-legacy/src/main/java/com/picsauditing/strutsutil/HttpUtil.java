package com.picsauditing.strutsutil;

import org.apache.struts2.rest.RestActionMapper;

import javax.servlet.http.HttpServletRequest;

public class HttpUtil {

    // HTTP Methods
    public static final String HTTP_GET_METHOD = "GET";
    public static final String HTTP_POST_METHOD = "POST";
    public static final String HTTP_PUT_METHOD = "PUT";
    public static final String HTTP_DELETE_METHOD = "DELETE";
    public static final String HTTP_OPTIONS_METHOD = "OPTIONS";

    public static final String HTTP_EXPECT_HEADER = "Expect";
    public static final String HTTP_100_CONTINUE = "100-continue";

	public static final int HTTP_OK = 200;

    public static boolean isGet(HttpServletRequest request) {
        return HTTP_GET_METHOD.equalsIgnoreCase(request.getMethod());
    }

    public static boolean isPost(HttpServletRequest request) {
        return HTTP_POST_METHOD.equalsIgnoreCase(request.getMethod());
    }

    public static boolean isPut(HttpServletRequest request) {
        if (HTTP_PUT_METHOD.equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return isPost(request) && HTTP_PUT_METHOD.equalsIgnoreCase(request.getParameter(RestActionMapper.HTTP_METHOD_PARAM));
        }
    }

    public static boolean isDelete(HttpServletRequest request) {
        if (HTTP_DELETE_METHOD.equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return HTTP_DELETE_METHOD.equalsIgnoreCase(request.getParameter(RestActionMapper.HTTP_METHOD_PARAM));
        }
    }

    public static boolean isOptions(HttpServletRequest request) {
        return HTTP_OPTIONS_METHOD.equalsIgnoreCase(request.getMethod());
    }

    public static boolean isExpectContinue(HttpServletRequest request) {
        String expect = request.getHeader(HTTP_EXPECT_HEADER);
        return (expect != null && expect.toLowerCase().contains(HTTP_100_CONTINUE));
    }

}
