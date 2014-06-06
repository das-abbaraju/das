<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Prefer dot notation over array notation for accessing properties, unless accessing a property whose name contains special characters.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-21</s:param>

    <s:param name="example_code">
BAD:
myObj['myProp'] = 10;

GOOD:
myObj.myProp = 10;
    </s:param>
</s:include>