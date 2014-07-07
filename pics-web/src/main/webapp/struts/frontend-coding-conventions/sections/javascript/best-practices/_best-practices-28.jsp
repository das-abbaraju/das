<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid "with" statements
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-28</s:param>

    <s:param name="example_code">
BAD:
with (myObj) {
    prop1 = 10;
    prop2 = 20;
}

GOOD:
myObj.prop1 = 10;
myObj.prop2 = 20;
    </s:param>
</s:include>