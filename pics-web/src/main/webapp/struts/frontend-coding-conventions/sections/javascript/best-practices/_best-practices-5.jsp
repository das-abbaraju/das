<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
if / else / for / while / try should always have braces (even if the body is only one line).
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-5</s:param>

    <s:param name="example_code">
BAD:
if (condition) doSomething();

if (condition)
    doSomething();

GOOD:
if (condition) {
    doSomething();
}
    </s:param>
</s:include>