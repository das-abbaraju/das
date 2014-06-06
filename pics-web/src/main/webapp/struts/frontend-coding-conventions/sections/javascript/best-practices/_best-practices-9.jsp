<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/struts/frontend-coding-conventions/sections/_section.jsp">
    <s:param name="description">
When possible, initialize variables at the same time that you declare them.
    </s:param>

    <%-- The value of accordian_parent_id should be {section-id-prefix}-{file name} --%>
    <s:param name="accordian_parent_id">javascript-best-practices-9</s:param>

    <s:param name="example_code">
BAD:
var foo;

doSomething();

foo = 10;

GOOD:
var foo = 10;

doSomething();
    </s:param>
</s:include>