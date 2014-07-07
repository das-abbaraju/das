<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
Never declare global variables, and know the ways in which they can be accidentally declared.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-17</s:param>

    <s:param name="example_code">
BAD:
// Outside a function
var myGlobalVar = 10;

// Binding directly to the window object
window.myGlobalVar = 10;

// Without using the var keyword (implicit global declaration)
myGlobalVar = 10;
    </s:param>
</s:include>