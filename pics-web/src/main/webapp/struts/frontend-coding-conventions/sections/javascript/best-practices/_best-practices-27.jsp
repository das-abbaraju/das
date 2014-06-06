<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Feature-detect rather than browser-detect
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-27</s:param>

    <s:param name="example_code">
BAD:
if (window.navigator.userAgent.indexOf('MSIE') != -1) {
    document.attachEvent(...);
}

GOOD:
if (!document.addEventListener) {
    document.attachEvent(...);
}
    </s:param>
</s:include>