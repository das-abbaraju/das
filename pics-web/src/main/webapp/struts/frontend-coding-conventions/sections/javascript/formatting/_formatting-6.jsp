<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Avoid quoting property names unless they are reserved words or contain special characters.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-formatting-6</s:param>

    <s:param name="example_code">
BAD:
myObj['myProperty'];

GOOD:
myObj.myProperty
myObj['my property']
    </s:param>
</s:include>