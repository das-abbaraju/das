<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
For anonymous functions, separate the function name from its parameters with a space.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-9</s:param>

    <s:param name="example_code">
BAD:
var myFn = function() {
    ...
}

GOOD:
var myFn = function () {
    ...
}
    </s:param>
</s:include>